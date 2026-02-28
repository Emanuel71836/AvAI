package com.avai.pathfinding;

import net.minecraft.core.BlockPos;

public class RegionEdge {
    private final Region from;
    private final Region to;
    private final BlockPos portalPos;
    private final float cost;

    public RegionEdge(Region from, Region to, BlockPos portalPos, float cost) {
        this.from = from;
        this.to = to;
        this.portalPos = portalPos;
        this.cost = cost;
    }

    public Region getFrom() { return from; }
    public Region getTo() { return to; }
    public BlockPos getPortalPos() { return portalPos; }
    public float getCost() { return cost; }
}