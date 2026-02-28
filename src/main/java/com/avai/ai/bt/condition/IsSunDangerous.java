package com.avai.ai.bt.condition;

import com.avai.ai.MobSnapshot;
import com.avai.ai.bt.Node;
import com.avai.ai.bt.Status;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.Level;

public class IsSunDangerous implements Node {
    @Override
    public Status tick(MobSnapshot snapshot) {
        Mob mob = snapshot.getMob();

        // only undead mobs are affected
        if (!(mob instanceof Zombie || mob instanceof Skeleton || mob instanceof Stray ||
              mob instanceof Husk || mob instanceof Phantom)) {
            return Status.FAILURE;
        }

        // helmet prevents sun damage
        if (!mob.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            return Status.FAILURE;
        }

        Level world = mob.level();
        // daytime and not raining = sun dangerous
        if (world.isDay() && !world.isRaining()) {
            return Status.SUCCESS;
        }

        return Status.FAILURE;
    }
}