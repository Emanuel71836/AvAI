package com.avai.pathfinding;

import com.avai.AdvancedAIMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class AvoidTrapdoorNodeEvaluator extends WalkNodeEvaluator {
    @Override
    public BlockPathTypes getBlockPathType(BlockGetter blockGetter, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = blockGetter.getBlockState(pos);
        if (state.getBlock() instanceof TrapDoorBlock && state.getValue(TrapDoorBlock.OPEN)) {
            AdvancedAIMod.LOGGER.debug("AvoidTrapdoorNodeEvaluator: BLOCKED open trapdoor at {}", pos);
            return BlockPathTypes.BLOCKED;
        }
        return super.getBlockPathType(blockGetter, x, y, z);
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter blockGetter, int x, int y, int z, Mob mob) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = blockGetter.getBlockState(pos);
        if (state.getBlock() instanceof TrapDoorBlock && state.getValue(TrapDoorBlock.OPEN)) {
            AdvancedAIMod.LOGGER.debug("AvoidTrapdoorNodeEvaluator (mob-aware): BLOCKED open trapdoor at {}", pos);
            return BlockPathTypes.BLOCKED;
        }
        return super.getBlockPathType(blockGetter, x, y, z, mob);
    }
}