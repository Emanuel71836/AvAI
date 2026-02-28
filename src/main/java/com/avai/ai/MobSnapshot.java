package com.avai.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MobSnapshot {
    private final Mob mob;
    public final Blackboard blackboard;

    public final Vec3 position;
    public final BlockPos blockPos;
    public final LivingEntity target;
    public final Level world;

    public MobSnapshot(Mob mob) {
        this.mob = mob;
        this.blackboard = Blackboard.get(mob);
        this.position = mob.position();
        this.blockPos = mob.blockPosition();
        this.target = mob.getTarget();
        this.world = mob.level();
    }

    public Mob getMob() {
        return mob;
    }
}