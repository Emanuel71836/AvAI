package com.avai.ai.bt.action;

import com.avai.AdvancedAIMod;
import com.avai.ai.MobSnapshot;
import com.avai.ai.bt.Node;
import com.avai.ai.bt.Status;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;

public class ChasePlayer implements Node {
    @Override
    public Status tick(MobSnapshot snapshot) {
        Mob mob = snapshot.getMob();
        var bb = snapshot.blackboard;

        // find nearest player in sight
        Player target = null;
        for (Player player : mob.level().players()) {
            if (player.distanceToSqr(mob) < 64 * 64 && mob.getSensing().hasLineOfSight(player)) {
                target = player;
                break;
            }
        }
        if (target == null) return Status.FAILURE;

        // check if already in attack range, let vanilla attack handle it
        double attackRange = getAttackRange(mob);
        if (mob.distanceToSqr(target) <= attackRange * attackRange) {
            // Don't move – allow vanilla attack to happen
            return Status.SUCCESS;
        }

        // otherwise, move towards target
        if (bb.computingChase.get()) return Status.RUNNING;
        if (mob.getNavigation().isInProgress()) return Status.RUNNING;

        bb.computingChase.set(true);
        AdvancedAIMod.PATHFINDING.requestPath(mob, target.blockPosition())
            .thenAccept(path -> {
                if (path != null) {
                    AdvancedAIMod.MAIN_THREAD_TASKS.add(() -> {
                        mob.getNavigation().moveTo(path, 1.0);
                    });
                }
                bb.computingChase.set(false);
            });
        return Status.RUNNING;
    }

    private double getAttackRange(Mob mob) {
        if (mob instanceof Monster) {
            if (mob.getType().toString().contains("skeleton")) return 15.0; // Skeletons shoot from distance
            if (mob.getType().toString().contains("creeper")) return 3.0;   // Creepers explode close
        }
        return 2.0; // Default melee range
    }
}