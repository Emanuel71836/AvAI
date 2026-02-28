package com.avai.ai.bt.condition;

import com.avai.ai.MobSnapshot;
import com.avai.ai.bt.Node;
import com.avai.ai.bt.Status;

public class HasTarget implements Node {
    @Override
    public Status tick(MobSnapshot snapshot) {
        return snapshot.target != null ? Status.SUCCESS : Status.FAILURE;
    }
}