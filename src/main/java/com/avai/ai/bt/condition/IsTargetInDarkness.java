package com.avai.ai.bt.condition;

import com.avai.ai.MobSnapshot;
import com.avai.ai.bt.Node;
import com.avai.ai.bt.Status;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;

public class IsTargetInDarkness implements Node {
    private static final int DARKNESS_THRESHOLD = 7; // block light level below this is considered dark

    @Override
    public Status tick(MobSnapshot snapshot) {
        LivingEntity target = snapshot.target;
        if (target == null) return Status.FAILURE;

        // Check block light level at target's feet
        int blockLight = snapshot.world.getBrightness(LightLayer.BLOCK, target.blockPosition());
        if (blockLight < DARKNESS_THRESHOLD) {
            return Status.SUCCESS;
        }
        return Status.FAILURE;
    }
}