package com.avai.ai.bt.action;

import com.avai.AdvancedAIMod;
import com.avai.ai.MobSnapshot;
import com.avai.ai.bt.Node;
import com.avai.ai.bt.Status;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class StayInShade implements Node {
    @Override
    public Status tick(MobSnapshot snapshot) {
        Mob mob = snapshot.getMob();
        if (!(mob instanceof PathfinderMob)) return Status.FAILURE;

        var bb = snapshot.blackboard;
        BlockPos currentTarget = bb.shadeTarget;

        // if already in shade, just stand still ( I racked my brain just to figure out that was the problem )
        if (isInShade(mob)) {
            // cancel any navigation to prevent moving out
            mob.getNavigation().stop();
            bb.shadeTarget = null; // clear target if any
            return Status.RUNNING; // keep running so higher priority stays active
        }

        // not in shade, search for a target
        if (currentTarget != null) {
            // if reached the target or it no longer shady, clear it
            if (mob.blockPosition().closerThan(currentTarget, 2.0) || !isShady(mob.level(), currentTarget)) {
                bb.shadeTarget = null;
                return Status.RUNNING;
            }

            // if navigation is already in progress, keep running ( fuck man i posted the same comment repeatedly in several files )
            if (mob.getNavigation().isInProgress()) {
                return Status.RUNNING;
            }

            // otherwise, start moving to target ( again repeated comments😭😭 )
            bb.pathfindingInProgress.set(true);
            AdvancedAIMod.PATHFINDING.requestPath(mob, currentTarget)
                .thenAccept(path -> {
                    if (path != null) {
                        AdvancedAIMod.MAIN_THREAD_TASKS.add(() -> {
                            mob.getNavigation().moveTo(path, 1.0);
                        });
                    } else {
                        bb.shadeTarget = null; // failed to path find new target next tick
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

    private boolean isInShade(Mob mob) {
        // consider the mob safe if its current position is not exposed to sky
        return !mob.level().canSeeSky(mob.blockPosition());
    }

    private BlockPos findShadySpot(Mob mob) {
        Level world = mob.level();
        BlockPos mobPos = mob.blockPosition();
        int searchRadius = 16;

        // simple scan for any shady spot
        for (int dx = -searchRadius; dx <= searchRadius; dx++) {
            for (int dz = -searchRadius; dz <= searchRadius; dz++) {
                for (int dy = -5; dy <= 5; dy++) {
                    BlockPos pos = mobPos.offset(dx, dy, dz);
                    if (isShady(world, pos) && isSafeStandingSpot(world, pos)) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    private boolean isShady(Level world, BlockPos pos) {
        return !world.canSeeSky(pos) && world.getBlockState(pos.above()).isSolid();
    }

    private boolean isSafeStandingSpot(Level world, BlockPos pos) {
        return world.getBlockState(pos).isAir() && world.getBlockState(pos.below()).isSolid();
    }
}