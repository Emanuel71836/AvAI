package com.avai.ai.bt.condition;

import com.avai.AdvancedAIMod;
import com.avai.ai.MobSnapshot;
import com.avai.ai.bt.Node;
import com.avai.ai.bt.Status;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class CheckPlayerInSight implements Node {
    @Override
    public Status tick(MobSnapshot snapshot) {
        Mob mob = snapshot.getMob();
        var bb = snapshot.blackboard;

        if (bb.computingSight.get()) {
            return bb.playerInSight.get() ? Status.SUCCESS : Status.FAILURE;
        }

        bb.computingSight.set(true);
        AdvancedAIMod.MAIN_THREAD_TASKS.add(() -> {
            boolean sight = false;
            for (Player player : mob.level().players()) {
                if (player.distanceToSqr(mob) < 64 * 64 && mob.getSensing().hasLineOfSight(player)) {
                    sight = true;
                    break;
                }
            }
            bb.playerInSight.set(sight);
            bb.computingSight.set(false);
        });

        return bb.playerInSight.get() ? Status.SUCCESS : Status.FAILURE;
    }
}