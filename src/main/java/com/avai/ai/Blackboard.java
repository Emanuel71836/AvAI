package com.avai.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Blackboard {
    private static final ConcurrentHashMap<Mob, Blackboard> MEMORIES = new ConcurrentHashMap<>();

    // wander
    public final AtomicBoolean computingWander = new AtomicBoolean(false);
    public BlockPos wanderTarget = null;

    // combat
    public final AtomicBoolean computingChase = new AtomicBoolean(false);
    public final AtomicBoolean computingSight = new AtomicBoolean(false);
    public final AtomicBoolean playerInSight = new AtomicBoolean(false);
    public final AtomicBoolean pathfindingInProgress = new AtomicBoolean(false);

    // shade seeking
    public final AtomicBoolean computingShade = new AtomicBoolean(false);
    public BlockPos shadeTarget = null;

    // flanking
    public final AtomicBoolean computingFlank = new AtomicBoolean(false);
    public BlockPos flankTarget = null;

    private Blackboard() {}

    public static Blackboard get(Mob mob) {
        return MEMORIES.computeIfAbsent(mob, k -> new Blackboard());
    }
}