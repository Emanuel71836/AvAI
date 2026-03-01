package com.avai.pathfinding;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.ChunkPos;

public class RegionUpdater {
    public static Region buildRegion(LevelChunk chunk, Level world) {
        ChunkPos pos = chunk.getPos();
        int minY = world.dimensionType().minY();
        int maxY = minY + world.dimensionType().height();
        Region region = new Region(pos, minY, maxY);

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            ChunkPos neighborPos = new ChunkPos(pos.x + dir.getStepX(), pos.z + dir.getStepZ());
            LevelChunk neighborChunk = world.getChunk(neighborPos.x, neighborPos.z);
            if (neighborChunk != null && !neighborChunk.isEmpty()) {
                BlockPos portal = findPortal(chunk, neighborChunk, dir, world);
                if (portal != null) {
                    Region neighborRegion = new Region(neighborPos, minY, maxY);
                    RegionEdge edge = new RegionEdge(region, neighborRegion, portal, 1.0f);
                    region.addEdge(neighborRegion, edge);
                }
            }
        }
        return region;
    }

    private static BlockPos findPortal(LevelChunk chunk, LevelChunk neighbor, Direction dir, Level world) {
        int x = dir.getStepX() > 0 ? chunk.getPos().getMaxBlockX() : chunk.getPos().getMinBlockX();
        int z = dir.getStepZ() > 0 ? chunk.getPos().getMaxBlockZ() : chunk.getPos().getMinBlockZ();
        int minY = world.dimensionType().minY();
        int maxY = minY + world.dimensionType().height();
        for (int y = minY; y < maxY; y++) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = chunk.getBlockState(pos);
            if (state.isAir() || !state.isSolid()) {
                return pos;
            }
        }
        return null;
    }
}