package com.example.autoflyer.litematica;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.litematica.schematic.placement.SubRegionPlacement;
import fi.dy.masa.litematica.selection.Box;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.litematica.world.WorldSchematic;
import fi.dy.masa.malilib.util.LayerRange;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

/**
 * Lop cau noi doc du lieu tu mod Litematica (khong dung mixin, chi goi API cong khai cua no).
 * Chi duoc goi khi Litematica DA duoc cai (kiem tra bang isLitematicaLoaded() truoc),
 * de tranh loi thieu class cho nguoi khong cai Litematica.
 *
 * "Block chua dat" = vi tri ma Litematica dang hien hologram (khac air trong WorldSchematic)
 * NHUNG block that trong world thuc te lai khac (chua dat hoac dat sai).
 * Chi xet trong pham vi lop (layer) ma Litematica dang hien (DataManager.getRenderLayerRange()),
 * dung y "chi nhung block dang hien moi di den" nguoi dung yeu cau.
 *
 * LUU Y QUAN TRONG: KHONG dung SchematicPlacement.getEclosingBox() de lay vung can quet, vi gia tri
 * nay CHI duoc tinh khi nguoi dung bat tuy chon "Render enclosing box" trong Litematica (mac dinh TAT),
 * neu khong no luon tra ve null -> khien vong quet bo qua het moi thu va bao "da xong" ngay lap tuc.
 * Thay vao do, dung getSubRegionBoxes(...) - luon duoc tinh lai moi lan goi, khong phu thuoc tuy chon hien thi.
 */
public final class LitematicaBridge {
    private LitematicaBridge() {}

    public static boolean isLitematicaLoaded() {
        return FabricLoader.getInstance().isModLoaded("litematica");
    }

    /**
     * Tim vi tri block gan nguoi choi nhat (theo khoang cach thang) can dat, trong so cac
     * placement dang bat, gioi han trong layer range hien tai cua Litematica.
     * Bo qua nhung vi tri co trong {@code skip} (vi du vua cho o do qua lau ma van chua duoc dat).
     *
     * @param hardCapCells so o toi da duyet qua (an toan, tranh lag neu vung qua lon)
     */
    public static BlockPos findNearestMissingBlock(ClientWorld world, Vec3d fromPos, Set<BlockPos> skip, int hardCapCells) {
        WorldSchematic schematicWorld = SchematicWorldHandler.getSchematicWorld();
        if (schematicWorld == null || world == null) return null;

        LayerRange layerRange = DataManager.getRenderLayerRange();

        BlockPos best = null;
        double bestDistSq = Double.MAX_VALUE;
        int checked = 0;

        for (SchematicPlacement placement : DataManager.getSchematicPlacementManager().getAllSchematicsPlacements()) {
            if (!placement.isEnabled()) continue;

            for (Box box : placement.getSubRegionBoxes(SubRegionPlacement.RequiredEnabled.PLACEMENT_ENABLED).values()) {
                BlockPos p1 = box.getPos1();
                BlockPos p2 = box.getPos2();
                if (p1 == null || p2 == null) continue;

                int minX = Math.min(p1.getX(), p2.getX());
                int maxX = Math.max(p1.getX(), p2.getX());
                int minZ = Math.min(p1.getZ(), p2.getZ());
                int maxZ = Math.max(p1.getZ(), p2.getZ());
                int minY = Math.max(Math.min(p1.getY(), p2.getY()), layerRange.getLayerMin());
                int maxY = Math.min(Math.max(p1.getY(), p2.getY()), layerRange.getLayerMax());

                for (int y = minY; y <= maxY; y++) {
                    for (int x = minX; x <= maxX; x++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            if (++checked > hardCapCells) {
                                return best; // an toan: du lon roi thi tra ve gan nhat tim duoc
                            }

                            BlockPos pos = new BlockPos(x, y, z);
                            if (skip.contains(pos)) continue;
                            if (!layerRange.isPositionWithinRange(pos)) continue;

                            BlockState schemState = schematicWorld.getBlockState(pos);
                            if (schemState.isAir()) continue;

                            BlockState realState = world.getBlockState(pos);
                            if (schemState.equals(realState)) continue; // da dat dung roi

                            double dx = (x + 0.5) - fromPos.x;
                            double dy = (y + 0.5) - fromPos.y;
                            double dz = (z + 0.5) - fromPos.z;
                            double distSq = dx * dx + dy * dy + dz * dz;

                            if (distSq < bestDistSq) {
                                bestDistSq = distSq;
                                best = pos;
                            }
                        }
                    }
                }
            }
        }

        return best;
    }

    /** Block tai vi tri nay da duoc dat dung nhu trong schematic chua. */
    public static boolean isPlacedCorrectly(ClientWorld world, BlockPos pos) {
        WorldSchematic schematicWorld = SchematicWorldHandler.getSchematicWorld();
        if (schematicWorld == null || world == null) return true;

        BlockState schemState = schematicWorld.getBlockState(pos);
        if (schemState.isAir()) return true;

        return schemState.equals(world.getBlockState(pos));
    }

    /** Uoc tinh chieu cao dinh cao nhat cua toan bo cac placement dang bat, de bay vong len tren tranh vuong. */
    public static int getHighestEnabledPlacementTop() {
        int top = Integer.MIN_VALUE;
        for (SchematicPlacement placement : DataManager.getSchematicPlacementManager().getAllSchematicsPlacements()) {
            if (!placement.isEnabled()) continue;
            for (Box box : placement.getSubRegionBoxes(SubRegionPlacement.RequiredEnabled.PLACEMENT_ENABLED).values()) {
                if (box.getPos1() == null || box.getPos2() == null) continue;
                top = Math.max(top, Math.max(box.getPos1().getY(), box.getPos2().getY()));
            }
        }
        return top == Integer.MIN_VALUE ? 0 : top;
    }
}
