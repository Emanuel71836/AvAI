package com.avai.ai.bt.action;

import com.avai.AdvancedAIMod;
import com.avai.ai.MobSnapshot;
import com.avai.ai.bt.Node;
import com.avai.ai.bt.Status;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

// this code has already caused me so many headaches, whoever messes with it and breaks it will have to fix it, even if it's through sheer spite 😡😡😡

public class Wander implements Node {
    @Override
    public Status tick(MobSnapshot snapshot) {
        Mob mob = snapshot.getMob();
        if (!(mob instanceof PathfinderMob)) return Status.FAILURE;

        var bb = snapshot.blackboard;

        if (bb.computingWander.get()) {
            return Status.RUNNING;
        }

        BlockPos target = bb.wanderTarget;
        if (target == null) {
            bb.computingWander.set(true);
            AdvancedAIMod.MAIN_THREAD_TASKS.add(() -> {
                Vec3 vec = DefaultRandomPos.getPos((PathfinderMob) mob, 10, 7);
                bb.wanderTarget = vec != null ? BlockPos.containing(vec) : mob.blockPosition();
                bb.computingWander.set(false);
            });
            return Status.RUNNING;
        }

        if (target.equals(mob.blockPosition()) || target.closerThan(mob.blockPosition(), 2.0)) {
            bb.wanderTarget = null;
            return Status.RUNNING;
        }

        if (mob.getNavigation().isInProgress()) {
            return Status.RUNNING;
        }

        bb.computingWander.set(true);
        AdvancedAIMod.PATHFINDING.requestPath(mob, target)
            .thenAccept(path -> {
                if (path != null) {
                    AdvancedAIMod.MAIN_THREAD_TASKS.add(() -> {
                        mob.getNavigation().moveTo(path, 1.0);
                    });
                } else {
                    bb.wanderTarget = null;
                }
                bb.computingWander.set(false);
            });
        return Status.RUNNING;
    }
}