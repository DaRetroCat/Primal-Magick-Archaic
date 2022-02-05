package com.verdantartifice.primalmagick.common.blocks.trees;

import java.util.function.Supplier;

import com.verdantartifice.primalmagick.common.blockstates.properties.TimePhase;

import net.minecraft.block.BlockState;
import net.minecraft.world.IWorld;

/**
 * Block definition for sunwood stairs.  They are decorative blocks that fade out of existence and become indestructable at night.
 * 
 * @author Daedalus4096
 */
public class SunwoodStairsBlock extends AbstractPhasingStairsBlock {
    public SunwoodStairsBlock(Supplier<BlockState> state, Properties properties) {
        super(state, properties);
    }

    @Override
    protected TimePhase getCurrentPhase(IWorld world) {
        return TimePhase.getSunPhase(world);
    }
}
