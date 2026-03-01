package com.avai.ai.villager;

import com.avai.ai.MobSnapshot;
import com.avai.ai.bt.Node;
import com.avai.ai.bt.Status;
import com.avai.event.GlobalEventSystem;
import com.avai.event.HitEvent;
import net.minecraft.world.entity.npc.villager.Villager;

public class ReactToHit implements Node {
    private enum Phase { MOVE_TO_VICTIM, LOOK_AT_VICTIM, LOOK_AT_ATTACKER, DONE }
    private Phase phase = Phase.MOVE_TO_VICTIM;
    private HitEvent currentEvent = null;
    private int lookTicks = 0;

    @Override
    public Status tick(MobSnapshot snapshot) {
        Villager villager = (Villager) snapshot.getMob();

        if (phase == Phase.DONE) {
            phase = Phase.MOVE_TO_VICTIM;
            currentEvent = null;
            return Status.FAILURE;
        }

        if (currentEvent == null) {
            currentEvent = GlobalEventSystem.getRecentHit(villager.level());
            if (currentEvent == null) {
                return Status.FAILURE;
            }
        }

        double distance = villager.distanceToSqr(currentEvent.victim);
        if (distance > 32 * 32) {
            currentEvent = null;
            phase = Phase.MOVE_TO_VICTIM;
            return Status.FAILURE;
        }

        switch (phase) {
            case MOVE_TO_VICTIM:
                if (villager.getNavigation().isDone()) {
                    villager.getNavigation().moveTo(currentEvent.victim, 0.6);
                }
                if (distance <= 2 * 2) {
                    phase = Phase.LOOK_AT_VICTIM;
                    lookTicks = 0;
                }
                return Status.RUNNING;

            case LOOK_AT_VICTIM:
                villager.getLookControl().setLookAt(currentEvent.victim);
                lookTicks++;
                if (lookTicks >= 20) {
                    phase = Phase.LOOK_AT_ATTACKER;
                    lookTicks = 0;
                }
                return Status.RUNNING;

            case LOOK_AT_ATTACKER:
                if (currentEvent.attacker != null && currentEvent.attacker.isAlive()) {
                    villager.getLookControl().setLookAt(currentEvent.attacker);
                }
                lookTicks++;
                if (lookTicks >= 20) {
                    phase = Phase.DONE;
                }
                return Status.RUNNING;

            default:
                return Status.FAILURE;
        }
    }
}