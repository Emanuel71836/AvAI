package com.avai.ai.bt.action;

import com.avai.AdvancedAIMod;
import com.avai.ai.MobSnapshot;
import com.avai.ai.bt.Node;
import com.avai.ai.bt.Status;
import net.minecraft.world.entity.Mob;

public class MoveToTarget implements Node {
    @Override
    public Status tick(MobSnapshot snapshot) {
        if (snapshot.target == null) return Status.FAILURE;

        Mob mob = snapshot.getMob();
        var bb = snapshot.blackboard;

        if (bb.pathfindingInProgress.get()) {
            return Status.RUNNING;
        }

        if (mob.getNavigation().isInProgress()) {
            return Status.RUNNING;
        }

        bb.pathfindingInProgress.set(true);
        AdvancedAIMod.PATHFINDING.requestPath(mob, snapshot.target.blockPosition())
            .thenAccept(path -> {
                if (path != null) {
                    AdvancedAIMod.MAIN_THREAD_TASKS.add(() -> {
                        mob.getNavigation().moveTo(path, 1.0);
                    });
                }
                bb.pathfindingInProgress.set(false);
            });

        return Status.RUNNING;
    }
}