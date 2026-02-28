package com.avai.ai.bt.composite;

import com.avai.ai.MobSnapshot;
import com.avai.ai.bt.Node;
import com.avai.ai.bt.Status;

public class Sequence implements Node {
    private final Node[] children;

    public Sequence(Node... children) {
        this.children = children;
    }

    @Override
    public Status tick(MobSnapshot snapshot) {
        for (Node child : children) {
            Status status = child.tick(snapshot);
            if (status != Status.SUCCESS) {
                return status;
            }
        }
        return Status.SUCCESS;
    }
}