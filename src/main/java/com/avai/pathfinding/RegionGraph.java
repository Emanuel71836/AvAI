package com.avai.pathfinding;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RegionGraph {
    private final ConcurrentMap<ChunkPos, Region> regions = new ConcurrentHashMap<>();

    public void addRegion(Region region) {
        regions.put(region.getChunkPos(), region);
    }

    public void removeRegion(ChunkPos pos) {
        regions.remove(pos);
    }

    @Nullable
    public Region getRegion(BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        Region region = regions.get(chunkPos);
        if (region != null && region.contains(pos)) return region;
        for (Region r : regions.values()) {
            if (r.contains(pos)) return r;
        }
        return null;
    }

    @Nullable
    public Region getRegion(ChunkPos chunkPos) {
        return regions.get(chunkPos);
    }

    public Iterable<Region> getAllRegions() {
        return regions.values();
    }
}