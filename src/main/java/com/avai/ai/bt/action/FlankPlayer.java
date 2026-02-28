package com.avai.ai.bt.action;

import com.avai.AdvancedAIMod;
import com.avai.ai.MobSnapshot;
import com.avai.ai.bt.Node;
import com.avai.ai.bt.Status;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlankPlayer implements Node {
    private static final double FLANK_RADIUS = 5.0; // distance from player to try to flank
    private static final int SEARCH_TRIES = 20;
    private final Random random = new Random();

    @Override
    public Status tick(MobSnapshot snapshot) {
        Mob mob = snapshot.getMob();
        LivingEntity target = snapshot.target;
        if (!(mob instanceof PathfinderMob) || target == null) return Status.FAILURE;

        var bb = snapshot.blackboard;
        BlockPos currentTarget = bb.flankTarget;

        // if we already have a flank target, navigate to it
        if (currentTarget != null) {
            // if reached the target or it no longer valid (e.g player moved far), clear it
            if (mob.blockPosition().closerThan(currentTarget, 2.0) || !isValidFlankTarget(mob, target, currentTarget)) {
                bb.flankTarget = null;
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
                        bb.flankTarget = null; // failed to path, find new target next tick
                    }
                    bb.pathfindingInProgress.set(false);
                });
            return Status.RUNNING;
        }

        // no target yet, find a flanking spot asynchronously
        if (bb.computingFlank.get()) {
            return Status.RUNNING;
        }

        bb.computingFlank.set(true);
        AdvancedAIMod.MAIN_THREAD_TASKS.add(() -> {
            BlockPos flankSpot = findFlankSpot(mob, target);
            bb.flankTarget = flankSpot;
            bb.computingFlank.set(false);
        });
        return Status.RUNNING;
    }

    private BlockPos findFlankSpot(Mob mob, LivingEntity target) {
        Level world = mob.level();
        Vec3 targetPos = target.position();
        Vec3 mobPos = mob.position();

        // direction from target to mob
        Vec3 toMob = mobPos.subtract(targetPos).normalize();

        // want to flank to a side, so choose a perpendicular direction (left or right randomly)
        Vec3 perpendicular = new Vec3(-toMob.z, 0, toMob.x).normalize(); // rotate 90 degrees
        if (random.nextBoolean()) {
            perpendicular = perpendicular.scale(-1); // other side
        }

        // try several candidate points around the target at FLANK_RADIUS distance, in the perpendicular direction
        // plus some random variation to avoid getting stuck
        List<BlockPos> candidates = new ArrayList<>();
        for (int i = 0; i < SEARCH_TRIES; i++) {
            double angleVariation = (random.nextDouble() - 0.5) * 1.5; // -0.75 to 0.75 radians
            Vec3 direction = perpendicular.yRot((float) angleVariation); // rotate around Y axis
            Vec3 candidateVec = targetPos.add(direction.scale(FLANK_RADIUS));
            BlockPos candidatePos = BlockPos.containing(candidateVec);

            // ensure the candidate is not directly in front of the player 
            // also check that it's safe to stand
            if (isSafeStandingSpot(world, candidatePos) && !isDirectlyInFront(target, candidateVec)) {
                candidates.add(candidatePos);
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        // pick the candidate closest to the mob
        candidates.sort((a, b) -> Double.compare(mobPos.distanceToSqr(Vec3.atCenterOf(a)), mobPos.distanceToSqr(Vec3.atCenterOf(b))));
        return candidates.get(0);
    }

    private boolean isSafeStandingSpot(Level world, BlockPos pos) {
        return world.getBlockState(pos).isAir() && world.getBlockState(pos.below()).isSolid();
    }

    private boolean isDirectlyInFront(LivingEntity target, Vec3 point) {
        // check if the point is within a 60-degree cone in front of the player
        Vec3 lookVec = target.getLookAngle().normalize();
        Vec3 toPoint = point.subtract(target.position()).normalize();
        double dot = lookVec.dot(toPoint);
        return dot > 0.5; // roughly 60 degrees (cos 60 = 0.5)
    }

    private boolean isValidFlankTarget(Mob mob, LivingEntity target, BlockPos pos) {
        // check if the flank target is still reasonably close to the player
        // and not directly in front
        if (target.distanceToSqr(Vec3.atCenterOf(pos)) > FLANK_RADIUS * FLANK_RADIUS * 2) {
            return false; // too far from player
        }
        if (isDirectlyInFront(target, Vec3.atCenterOf(pos))) {
            return false; // now it in front, not flanking
        }
        return true;
    }
}