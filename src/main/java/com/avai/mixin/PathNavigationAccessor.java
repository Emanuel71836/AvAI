package com.avai.mixin;

import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PathNavigation.class)
public interface PathNavigationAccessor {

    /**
     * Accessor for the protected field 'nodeEvaluator' (Mojang mapping).
     * Allows us to inject our custom NodeEvaluator.
     */
    @Accessor("nodeEvaluator")
    void setNodeEvaluator(NodeEvaluator nodeEvaluator);
}