package com.avai.pathfinding;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

public class LowLevelPathfinder {
    public static Path findPath(Level world, Mob mob, BlockPos start, BlockPos goal) {
        // use vanilla navigations pathfinder as it optimized and safe
        return mob.getNavigation().createPath(goal, 1);
    }
}