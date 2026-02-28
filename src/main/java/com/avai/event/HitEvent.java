package com.avai.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class HitEvent {
    public final LivingEntity victim;
    public final Player attacker;
    public final BlockPos position;
    public final long timestamp;

    public HitEvent(LivingEntity victim, Player attacker) {
        this.victim = victim;
        this.attacker = attacker;
        this.position = victim.blockPosition();
        this.timestamp = System.currentTimeMillis();
    }
}