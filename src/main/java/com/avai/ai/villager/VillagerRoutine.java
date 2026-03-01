package com.avai.ai.villager;

import com.avai.ai.MobSnapshot;
import com.avai.ai.bt.Node;
import com.avai.ai.bt.Status;
import com.avai.ai.bt.composite.Selector;
import com.avai.ai.bt.action.Wander;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.core.Holder;   // <-- Added

import java.util.ArrayList;
import java.util.List;

public class VillagerRoutine implements Node {
    private final Villager villager;
    private final Node routineTree;

    public VillagerRoutine(Villager villager) {
        this.villager = villager;
        this.routineTree = buildRoutine();
    }

    private Node buildRoutine() {
        Holder<VillagerProfession> prof = villager.getVillagerData().profession();  // Returns Holder

        Node workNode;
        if (prof.is(VillagerProfession.FARMER)) {
            workNode = new FarmerWork();
        } else if (prof.is(VillagerProfession.FISHERMAN)) {
            workNode = new FishermanWork();
        } else if (prof.is(VillagerProfession.LIBRARIAN)) {
            workNode = new LibrarianWork();
        } else {
            workNode = null;
        }

        List<Node> children = new ArrayList<>();
        children.add(new ReactToHit());        // highest priority
        if (workNode != null) {
            children.add(workNode);
        }
        children.add(new Wander());            // fallback

        return new Selector(children.toArray(new Node[0]));
    }

    @Override
    public Status tick(MobSnapshot snapshot) {
        return routineTree.tick(snapshot);
    }
}