package com.verdantartifice.primalmagick.common.rituals;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

/**
 * Interface indicating whether a block can send or receive salt power for rituals.
 * 
 * @author Daedalus4096
 */
public interface ISaltPowered {
    /**
     * Determine whether the block at the given position is receiving salt power from a neighbor.
     * 
     * @param world the world in which the queried block exists
     * @param pos the position of the queried block
     * @return whether the queried block is receiving salt power
     */
    public default boolean isBlockSaltPowered(@Nonnull IBlockReader world, @Nonnull BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (this.getSaltPower(world, pos.offset(dir), dir) > 0) {
                return true;
            }
        }
        return false;
    }
    
    public default int getSaltPower(@Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull Direction facing) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof ISaltPowered) {
            return ((ISaltPowered)state.getBlock()).getStrongSaltPower(state, world, pos, facing);
        } else {
            return 0;
        }
    }

    public default int getStrongSaltPower(@Nonnull BlockState blockState, @Nonnull IBlockReader blockAccess, @Nonnull BlockPos pos, @Nonnull Direction side) {
        return 0;
    }
}
