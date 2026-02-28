package com.avai.ai.bt.composite;

import com.avai.ai.MobSnapshot;
import com.avai.ai.bt.Node;
import com.avai.ai.bt.Status;

public class Selector implements Node {
    private final Node[] children;

    public Selector(Node... children) {
        this.children = children;
    }

    @Override
    public Status tick(MobSnapshot snapshot) {
        for (Node child : children) {
            Status status = child.tick(snapshot);
            if (status != Status.FAILURE) {
                return status;
            }
        }
        return Status.FAILURE;
    }
}