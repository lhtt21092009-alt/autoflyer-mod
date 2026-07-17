package com.example.autoflyer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 * Che do "Auto Fly" (thu cong): bay theo kieu ziczac (giong may cat co) trong hinh chu nhat
 * tao boi Vi tri 1 va Vi tri 2:
 *  - Bay thang tu canh nay sang canh kia (theo truc dai hon).
 *  - Het 1 hang thi dich ngang sang 1 khoang (mac dinh 2 block, cfg.zigzagStep).
 *  - Bay nguoc lai theo huong doi dien (ziczac).
 *  - Lap lai cho toi khi het hinh chu nhat, roi quay ve diem bat dau va lap vo han.
 */
public class FlightTask {
    private static boolean running = false;
    private static List<Vec3d> route = new ArrayList<>();
    private static int targetIndex = 0;

    public static boolean isRunning() {
        return running;
    }

    public static void start() {
        route = buildZigzagRoute(AutoFlyerConfig.INSTANCE);
        targetIndex = 0;
        running = !route.isEmpty();
    }

    public static void stop() {
        running = false;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            player.setVelocity(0, 0, 0);
        }
    }

    public static void tick(MinecraftClient client) {
        if (!running || route.isEmpty()) return;
        ClientPlayerEntity player = client.player;
        if (player == null) {
            stop();
            return;
        }

        AutoFlyerConfig cfg = AutoFlyerConfig.INSTANCE;
        Vec3d target = route.get(targetIndex);
        Vec3d current = player.getPos();
        Vec3d diff = target.subtract(current);
        double distance = diff.length();

        if (distance < 0.5) {
            targetIndex = (targetIndex + 1) % route.size();
            player.setVelocity(0, 0, 0);
            return;
        }

        if (!player.getAbilities().flying && player.getAbilities().allowFlying) {
            player.getAbilities().flying = true;
            player.sendAbilitiesUpdate();
        }

        double speed = Math.min(cfg.speed, distance);
        Vec3d direction = diff.normalize();
        player.setVelocity(direction.multiply(speed));

        float yaw = (float) (Math.toDegrees(Math.atan2(-direction.x, direction.z)));
        float pitch = (float) (Math.toDegrees(-Math.asin(direction.y)));
        player.setYaw(yaw);
        player.setPitch(pitch);

        player.velocityModified = true;
    }

    private static List<Vec3d> buildZigzagRoute(AutoFlyerConfig cfg) {
        List<Vec3d> pts = new ArrayList<>();
        double x1 = cfg.x1, y = cfg.y1, z1 = cfg.z1;
        double x2 = cfg.x2, z2 = cfg.z2;
        double step = Math.max(0.5, cfg.zigzagStep);

        double dx = x2 - x1;
        double dz = z2 - z1;
        boolean sweepOnX = Math.abs(dx) >= Math.abs(dz);

        pts.add(new Vec3d(x1, y, z1));
        int guard = 0;

        if (sweepOnX) {
            double zDir = Math.signum(dz);
            double z = z1;
            boolean towardX2 = true;
            while (guard++ < 2000) {
                double xEnd = towardX2 ? x2 : x1;
                pts.add(new Vec3d(xEnd, y, z));
                if (zDir == 0 || Math.abs(z2 - z) < 1e-6) break;
                double nextZ = z + zDir * step;
                if ((zDir > 0 && nextZ >= z2) || (zDir < 0 && nextZ <= z2)) nextZ = z2;
                z = nextZ;
                pts.add(new Vec3d(xEnd, y, z));
                towardX2 = !towardX2;
            }
        } else {
            double xDir = Math.signum(dx);
            double x = x1;
            boolean towardZ2 = true;
            while (guard++ < 2000) {
                double zEnd = towardZ2 ? z2 : z1;
                pts.add(new Vec3d(x, y, zEnd));
                if (xDir == 0 || Math.abs(x2 - x) < 1e-6) break;
                double nextX = x + xDir * step;
                if ((xDir > 0 && nextX >= x2) || (xDir < 0 && nextX <= x2)) nextX = x2;
                x = nextX;
                pts.add(new Vec3d(x, y, zEnd));
                towardZ2 = !towardZ2;
            }
        }
        return pts;
    }
}
