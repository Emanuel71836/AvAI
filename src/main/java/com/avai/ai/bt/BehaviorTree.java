package com.avai.ai.bt;

import com.avai.ai.MobSnapshot;

public class BehaviorTree {
    private final Node root;

    public BehaviorTree(Node root) {
        this.root = root;
    }

    public Status tick(MobSnapshot snapshot) {
        return root.tick(snapshot);
    }
}