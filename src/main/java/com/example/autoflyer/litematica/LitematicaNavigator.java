package com.example.autoflyer.litematica;

import com.example.autoflyer.AutoFlyerConfig;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Tu dong bay den tung block con thieu theo ban ve Litematica dang tai (chi trong lop dang hien),
 * dung ngay phia tren no (y + hoverHeight) de mod print (vd Meteor Litematica Printer) tu dat block.
 *
 * De tranh bay xuyen tuong khi dang xay dang map art 3D, duong bay chia lam 3 doan theo truc
 * thay vi bay thang (cheo) toi diem dich:
 *   1) ASCEND  - bay thang len do cao vua du de bay vong qua vat can (dò tang dan, khong len tan dinh)
 *   2) TRAVEL  - bay ngang (X/Z) toi ngay phia tren diem dich, van o do cao da chon
 *   3) DESCEND - ha thang xuong diem dich (x, y+hoverHeight, z)
 * Neu duong bay thang toi dich khong bi vuong block nao (kiem tra bang isPathClear), se bay
 * thang toi luon cho nhanh (dung yeu cau "bay nhanh nhat co the").
 *
 * Chong ket: kiem tra va thoat ket o nhieu tang - nudge tuc thi, thu lai do cao khac, watchdog
 * tong thoi gian tren 1 block, va isPathClear kiem tra ca "be rong" nguoi choi (khong chi 1 tia
 * o giua) de tranh truong hop tuong nhu thoang nhung va vao nguoi khi bay.
 */
public class LitematicaNavigator {
    private enum Phase { ASCEND, TRAVEL, DESCEND, HOVER, DIRECT }

    private static boolean running = false;
    private static BlockPos currentTarget = null;
    private static Phase phase = null;
    private static double cruiseY;

    private static Vec3d lastPos = null;
    private static int stuckTicks = 0;
    private static int hoverTicks = 0;
    private static int retries = 0;
    private static int targetTotalTicks = 0; // watchdog: tong so tick da danh cho 1 block hien tai

    // Vi tri vua "cho qua lau ma van chua duoc dat" se bi tam thoi bo qua, tranh ket o 1 cho.
    private static final Map<BlockPos, Long> skipUntilTick = new HashMap<>();
    private static long tickCounter = 0;

    private static final int HOVER_TIMEOUT_TICKS = 100;      // 5 giay cho printer dat block
    private static final int STUCK_TIMEOUT_TICKS = 24;       // 1.2 giay khong nhuc nhich -> coi la vuong (phan ung nhanh hon)
    private static final int MAX_RETRIES_BEFORE_SKIP = 3;
    private static final int TARGET_WATCHDOG_TICKS = 600;    // 30 giay/1 block toi da, qua thi bo qua bat ke ly do gi
    private static final int SEARCH_HARD_CAP = 400_000;      // an toan hieu nang khi quet vung lon
    private static final double PLAYER_RADIUS = 0.35;        // ban kinh gan dung cua hitbox nguoi choi (rong 0.6)

    public static boolean isRunning() {
        return running;
    }

    public static void start() {
        if (!LitematicaBridge.isLitematicaLoaded()) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(Text.literal("Auto Flyer: khong tim thay mod Litematica, khong the dung che do nay."), false);
            }
            return;
        }

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && !player.getAbilities().allowFlying) {
            player.sendMessage(Text.literal("Auto Flyer: canh bao - ban khong duoc phep bay tren server nay, Auto Build co the bi ket."), false);
        }

        running = true;
        currentTarget = null;
        phase = null;
        stuckTicks = 0;
        hoverTicks = 0;
        retries = 0;
        targetTotalTicks = 0;
        lastPos = null;
    }

    public static void stop() {
        running = false;
        currentTarget = null;
        phase = null;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            player.setVelocity(0, 0, 0);
        }
    }

    public static void tick(MinecraftClient client) {
        if (!running) return;
        tickCounter++;

        ClientPlayerEntity player = client.player;
        ClientWorld world = client.world;
        if (player == null || world == null) {
            stop();
            return;
        }

        AutoFlyerConfig cfg = AutoFlyerConfig.INSTANCE;

        // Chon block dich moi neu chua co, hoac block cu da duoc dat xong roi
        if (currentTarget == null || LitematicaBridge.isPlacedCorrectly(world, currentTarget)) {
            if (currentTarget != null) {
                skipUntilTick.remove(currentTarget); // da xong, khong can skip nua
            }
            cleanupExpiredSkips();

            currentTarget = LitematicaBridge.findNearestMissingBlock(world, player.getPos(), skipUntilTick.keySet(), SEARCH_HARD_CAP);
            if (currentTarget == null) {
                player.sendMessage(Text.literal("Auto Flyer: khong con block nao con thieu trong lop dang hien - da xong!"), false);
                playDoneSound(client);
                stop();
                return;
            }

            phase = null; // se tinh lai duong bay ben duoi
            hoverTicks = 0;
            retries = 0;
            targetTotalTicks = 0;
        }

        targetTotalTicks++;
        if (targetTotalTicks > TARGET_WATCHDOG_TICKS) {
            // Da qua lau ma van khong xong 1 block nay du da thu moi cach -> bo qua han, tranh treo mai
            skipUntilTick.put(currentTarget, tickCounter + 200);
            currentTarget = null;
            phase = null;
            return;
        }

        Vec3d hoverTarget = new Vec3d(
                currentTarget.getX() + 0.5,
                currentTarget.getY() + cfg.litematicaHoverHeight,
                currentTarget.getZ() + 0.5
        );

        if (phase == null) {
            phase = choosePath(world, player.getPos(), hoverTarget);
        }

        Vec3d current = player.getPos();
        double moveSpeed = Math.max(cfg.litematicaSpeed, 0.1);

        switch (phase) {
            case DIRECT -> {
                if (flyToward(player, hoverTarget, moveSpeed)) {
                    phase = Phase.HOVER;
                }
            }
            case ASCEND -> {
                Vec3d ascendTarget = new Vec3d(current.x, cruiseY, current.z);
                if (flyToward(player, ascendTarget, moveSpeed)) {
                    phase = Phase.TRAVEL;
                }
            }
            case TRAVEL -> {
                Vec3d travelTarget = new Vec3d(hoverTarget.x, cruiseY, hoverTarget.z);
                if (flyToward(player, travelTarget, moveSpeed)) {
                    phase = Phase.DESCEND;
                }
            }
            case DESCEND -> {
                if (flyToward(player, hoverTarget, moveSpeed)) {
                    phase = Phase.HOVER;
                }
            }
            case HOVER -> {
                player.setVelocity(0, 0, 0);
                hoverTicks++;
                if (hoverTicks > HOVER_TIMEOUT_TICKS) {
                    // Cho qua lau ma van chua thay dat -> bo qua tam thoi, sang block khac
                    skipUntilTick.put(currentTarget, tickCounter + 200); // ~10 giay
                    currentTarget = null;
                }
            }
        }

        // Phat hien bi ket (vuong tuong) trong luc dang bay (khong tinh luc HOVER)
        if (phase != Phase.HOVER) {
            if (lastPos != null && current.squaredDistanceTo(lastPos) < 0.02 * 0.02) {
                stuckTicks++;
            } else {
                stuckTicks = 0;
            }

            if (stuckTicks > STUCK_TIMEOUT_TICKS) {
                stuckTicks = 0;
                retries++;

                if (retries > MAX_RETRIES_BEFORE_SKIP) {
                    // Thu vai lan van khong toi duoc -> bo qua block nay, tim block khac
                    skipUntilTick.put(currentTarget, tickCounter + 200);
                    currentTarget = null;
                } else {
                    // Nhuc tuc thi len tren de thoat ra khoi cho bi ket (vd ket vao 1 goc tuong),
                    // roi tinh lai duong bay voi do cao cao hon lan truoc.
                    player.setVelocity(0, 0.4, 0);
                    player.velocityModified = true;
                    cruiseY = Math.max(cruiseY, current.y) + 6;
                    phase = Phase.ASCEND;
                }
            }
        }

        lastPos = current;
    }

    /**
     * Chon duong bay: neu duong thang toi dich khong bi vuong thi bay thang (nhanh nhat).
     * Neu bi vuong, DO TIM do cao thap nhat vua du de bay vong qua (tang dan tung buoc nho),
     * thay vi luon bay len tan dinh cao nhat cua ca cong trinh - tranh ton thoi gian khi vat can
     * chi la 1 khoi nho gan do.
     */
    private static Phase choosePath(ClientWorld world, Vec3d from, Vec3d to) {
        if (isPathClear(world, from, to)) {
            return Phase.DIRECT;
        }

        int buildTop = LitematicaBridge.getHighestEnabledPlacementTop();
        double baseY = Math.max(from.y, to.y);
        double capY = Math.max(buildTop + 5, baseY + 3); // gioi han tren cung, phong khi khong tim duoc cho nao thap hon

        // Dò tang dan tung 2 block 1, tim do cao THAP NHAT ma ca 3 doan (len - ngang - xuong) deu thong thoang
        for (double candidateY = baseY + 2; candidateY <= capY; candidateY += 2) {
            Vec3d ascendPoint = new Vec3d(from.x, candidateY, from.z);
            Vec3d travelPoint = new Vec3d(to.x, candidateY, to.z);

            if (isPathClear(world, from, ascendPoint)
                    && isPathClear(world, ascendPoint, travelPoint)
                    && isPathClear(world, travelPoint, to)) {
                cruiseY = candidateY;
                return Phase.ASCEND;
            }
        }

        // Khong tim duoc cho nao thap hon thong thoang -> danh phai bay len tan dinh cong trinh
        cruiseY = capY;
        return Phase.ASCEND;
    }

    /**
     * Kiem tra doan thang tu 'from' toi 'to' co bi block dac chan khong. Kiem tra ca "be rong" cua
     * nguoi choi (4 diem lech sang 2 ben theo PLAYER_RADIUS) va ca chan/dau (offset +1.6 theo Y),
     * khong chi 1 tia o chinh giua - tranh truong hop tuong nhu thoang nhung thuc ra va vao than
     * nguoi choi khi bay ngang qua (day la nguyen nhan chinh gay ket ma tia don khong phat hien duoc).
     */
    private static boolean isPathClear(ClientWorld world, Vec3d from, Vec3d to) {
        double distance = from.distanceTo(to);
        if (distance < 0.1) return true;

        int steps = (int) Math.ceil(distance / 0.5);
        for (int i = 1; i <= steps; i++) {
            double t = (double) i / steps;
            double x = from.x + (to.x - from.x) * t;
            double y = from.y + (to.y - from.y) * t;
            double z = from.z + (to.z - from.z) * t;

            if (isSolidNear(world, x, y, z) || isSolidNear(world, x, y + 1.5, z)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSolidNear(ClientWorld world, double x, double y, double z) {
        double[][] offsets = {
                {0, 0}, {PLAYER_RADIUS, 0}, {-PLAYER_RADIUS, 0}, {0, PLAYER_RADIUS}, {0, -PLAYER_RADIUS}
        };
        for (double[] off : offsets) {
            BlockPos pos = BlockPos.ofFloored(x + off[0], y, z + off[1]);
            BlockState state = world.getBlockState(pos);
            if (!state.isAir() && !state.getCollisionShape(world, pos).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /** Bay ve phia target voi toc do cho truoc. Tra ve true neu da toi noi. */
    private static boolean flyToward(ClientPlayerEntity player, Vec3d target, double speed) {
        Vec3d current = player.getPos();
        Vec3d diff = target.subtract(current);
        double distance = diff.length();

        if (distance < 0.4) {
            player.setVelocity(0, 0, 0);
            return true;
        }

        if (!player.getAbilities().flying && player.getAbilities().allowFlying) {
            player.getAbilities().flying = true;
            player.sendAbilitiesUpdate();
        }

        double moveAmount = Math.min(speed, distance);
        Vec3d direction = diff.normalize();
        player.setVelocity(direction.multiply(moveAmount));

        float yaw = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
        float pitch = (float) Math.toDegrees(-Math.asin(direction.y));
        player.setYaw(yaw);
        player.setPitch(pitch);
        player.velocityModified = true;

        return false;
    }

    private static void cleanupExpiredSkips() {
        Set<BlockPos> expired = new HashSet<>();
        for (Map.Entry<BlockPos, Long> e : skipUntilTick.entrySet()) {
            if (e.getValue() <= tickCounter) expired.add(e.getKey());
        }
        for (BlockPos pos : expired) skipUntilTick.remove(pos);
    }

    /** Phat am thanh bao hieu khi xay xong toan bo (het block can dat trong lop dang hien). */
    private static void playDoneSound(MinecraftClient client) {
        if (client.player == null || client.world == null) return;
        client.world.playSound(
                client.player,
                client.player.getBlockPos(),
                SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                SoundCategory.MASTER,
                1.0f,
                1.0f
        );
    }
}
