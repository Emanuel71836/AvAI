package com.avai.mixin;

import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Mob.class)
public class MobTargetMixin {
    // additional redirects if needed, the ones in MobAIMixin already cover both
}