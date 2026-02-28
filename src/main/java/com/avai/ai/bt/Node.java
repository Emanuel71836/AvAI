package com.avai.ai.bt;

import com.avai.ai.MobSnapshot;

public interface Node {
    Status tick(MobSnapshot snapshot);
}