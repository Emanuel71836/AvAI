package com.avai.pathfinding;

import com.avai.AdvancedAIMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class AvoidTrapdoorNodeEvaluator extends WalkNodeEvaluator {
    @Override
    public PathType getPathType(PathfindingContext context, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = context.getBlockState(pos);
        if (state.getBlock() instanceof TrapDoorBlock && state.getValue(TrapDoorBlock.OPEN)) {
            AdvancedAIMod.LOGGER.debug("AvoidTrapdoorNodeEvaluator: BLOCKED open trapdoor at {}", pos);
            return PathType.BLOCKED;
        }
        return super.getPathType(context, x, y, z);
    }
}