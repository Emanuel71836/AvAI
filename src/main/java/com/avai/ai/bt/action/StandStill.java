package com.avai.ai.bt.action;

import com.avai.ai.MobSnapshot;
import com.avai.ai.bt.Node;
import com.avai.ai.bt.Status;

public class StandStill implements Node {
    @Override
    public Status tick(MobSnapshot snapshot) {
        return Status.RUNNING;
    }
}