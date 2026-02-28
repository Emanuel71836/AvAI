package com.avai.ai.bt.action;

import com.avai.AdvancedAIMod;
import com.avai.ai.MobSnapshot;
import com.avai.ai.bt.Node;
import com.avai.ai.bt.Status;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class SeekShade implements Node {
    private static final int SEARCH_RADIUS = 16;
    private static final int MAX_TRIES = 50;

    @Override
    public Status tick(MobSnapshot snapshot) {
        Mob mob = snapshot.getMob();
        if (!(mob instanceof PathfinderMob)) return Status.FAILURE;

        var bb = snapshot.blackboard;
        BlockPos currentTarget = bb.shadeTarget;

        // if already have a target, navigate to it
        if (currentTarget != null) {
            // if reached the target or it no longer shady, clear it
            if (mob.blockPosition().closerThan(currentTarget, 2.0) || !isShady(mob.level(), currentTarget)) {
                bb.shadeTarget = null;
                return Status.RUNNING;
            }

            // if navigation is already in progress, keep running
            if (mob.getNavigation().isInProgress()) {
                return Status.RUNNING;
            }

            // otherwise, start moving to target
            bb.pathfindingInProgress.set(true);
            AdvancedAIMod.PATHFINDING.requestPath(mob, currentTarget)
                .thenAccept(path -> {
                    if (path != null) {
                        AdvancedAIMod.MAIN_THREAD_TASKS.add(() -> {
                            mob.getNavigation().moveTo(path, 1.0);
                        });
                    } else {
                        bb.shadeTarget = null; // failed to path, find new target next tick
                    }
                    bb.pathfindingInProgress.set(false);
                });
            return Status.RUNNING;
        }

        // no target yet, find a shady spot asynchronously
        if (bb.computingShade.get()) {
            return Status.RUNNING;
        }

        bb.computingShade.set(true);
        AdvancedAIMod.MAIN_THREAD_TASKS.add(() -> {
            BlockPos shadySpot = findShadySpot(mob);
            bb.shadeTarget = shadySpot;
            bb.computingShade.set(false);
        });
        return Status.RUNNING;
    }

    private BlockPos findShadySpot(Mob mob) {
        Level world = mob.level();
        BlockPos mobPos = mob.blockPosition();
        List<BlockPos> candidates = new ArrayList<>();

        // scan in a spiral pattern
        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx++) {
            for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz++) {
                for (int dy = -5; dy <= 5; dy++) { // also check vertical variation
                    BlockPos pos = mobPos.offset(dx, dy, dz);
                    if (isShady(world, pos) && isSafeStandingSpot(world, pos)) {
                        candidates.add(pos);
                    }
                }
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        // sort by distance and return closest
        candidates.sort((a, b) -> Double.compare(mobPos.distSqr(a), mobPos.distSqr(b)));
        return candidates.get(0);
    }

    private boolean isShady(Level world, BlockPos pos) {
        // a spot is shady if it not directly under the sky
        return !world.canSeeSky(pos) && world.getBlockState(pos.above()).isSolid();
    }

    private boolean isSafeStandingSpot(Level world, BlockPos pos) {
        // must be air or replaceable at the mob feet
        if (!world.getBlockState(pos).isAir() && !world.getBlockState(pos).canBeReplaced()) {
            return false;
        }
        // block below must be solid
        if (!world.getBlockState(pos.below()).isSolid()) {
            return false;
        }
        // no dangerous blocks like fire, lava, and more things that Mojang adds to fuck up my life
        if (world.getBlockState(pos).getBlock() == Blocks.LAVA || world.getBlockState(pos.below()).getBlock() == Blocks.LAVA) {
            return false;
        }
        return true;
    }
}