package com.avai.pathfinding;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class Region {
    private final int minX, minY, minZ;
    private final int maxX, maxY, maxZ;
    private final ChunkPos chunkPos;
    private final float traversalCost;
    private final ConcurrentMap<Region, RegionEdge> edges = new ConcurrentHashMap<>();

    public Region(ChunkPos chunkPos, int minY, int maxY) {
        this.chunkPos = chunkPos;
        this.minX = chunkPos.getMinBlockX();
        this.maxX = chunkPos.getMaxBlockX();
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = chunkPos.getMinBlockZ();
        this.maxZ = chunkPos.getMaxBlockZ();
        this.traversalCost = 1.0f;
    }

    public boolean contains(BlockPos pos) {
        return pos.getX() >= minX && pos.getX() <= maxX &&
               pos.getY() >= minY && pos.getY() <= maxY &&
               pos.getZ() >= minZ && pos.getZ() <= maxZ;
    }

    public BlockPos getCenter() {
        return new BlockPos((minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2);
    }

    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    public float getTraversalCost() {
        return traversalCost;
    }

    public void addEdge(Region neighbor, RegionEdge edge) {
        edges.put(neighbor, edge);
    }

    @Nullable
    public RegionEdge getEdge(Region neighbor) {
        return edges.get(neighbor);
    }

    public Iterable<RegionEdge> getEdges() {
        return edges.values();
    }
}