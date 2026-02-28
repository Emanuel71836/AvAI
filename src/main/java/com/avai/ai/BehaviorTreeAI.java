package com.avai.ai;

import com.avai.AdvancedAIMod;
import com.avai.ai.bt.BehaviorTree;
import com.avai.ai.bt.Node;
import com.avai.ai.bt.composite.Selector;
import com.avai.ai.bt.composite.Sequence;
import com.avai.ai.bt.condition.CheckPlayerInSight;
import com.avai.ai.bt.condition.IsSunDangerous;
import com.avai.ai.bt.condition.IsTargetInDarkness;
import com.avai.ai.bt.action.ChasePlayer;
import com.avai.ai.bt.action.FlankPlayer;
import com.avai.ai.bt.action.StayInShade;
import com.avai.ai.bt.action.StandStill;
import com.avai.ai.bt.action.Wander;
import com.avai.ai.villager.VillagerRoutine;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.Villager;

public class BehaviorTreeAI {
    private final Mob mob;
    private final BehaviorTree tree;
    private long lastTick = 0;
    private static final long UPDATE_INTERVAL = 2;

    public BehaviorTreeAI(Mob mob) {
        this.mob = mob;
        this.tree = buildTree(mob);
    }

    private BehaviorTree buildTree(Mob mob) {
        if (!(mob instanceof PathfinderMob)) {
            return new BehaviorTree(new StandStill());
        }
        if (mob instanceof EnderMan) {
            return new BehaviorTree(new StandStill());
        }
        if (mob instanceof Villager villager) {
            return new BehaviorTree(new VillagerRoutine(villager));
        }
        if (mob instanceof Monster) {
            // monster behavior tree:
            // 1. sun safety (highest priority)
            // 2. combat, if player in sight, try flanking in darkness, otherwise chase
            // 3. wander (fallback) 
            Node root = new Selector(
                new Sequence(new IsSunDangerous(), new StayInShade()),
                new Sequence(
                    new CheckPlayerInSight(),
                    new Selector(
                        new Sequence(new IsTargetInDarkness(), new FlankPlayer()),
                        new ChasePlayer()
                    )
                ),
                new Wander()
            );
            return new BehaviorTree(root);
        }
        return new BehaviorTree(new Wander());
    }

    public void tick() {
        if (mob.tickCount - lastTick < UPDATE_INTERVAL) return;
        lastTick = mob.tickCount;
        try {
            tree.tick(new MobSnapshot(mob));
        } catch (Exception e) {
            AdvancedAIMod.LOGGER.error("Error ticking tree for " + mob.getName().getString(), e);
        }
    }
}