package com.verdantartifice.primalmagick.common.blocks.trees;

import java.util.function.Supplier;

import com.verdantartifice.primalmagick.common.blockstates.properties.TimePhase;

import net.minecraft.block.BlockState;
import net.minecraft.world.IWorld;

/**
 * Block definition for moonwood stairs.  They are decorative blocks that fade out of existence and become indestructable during the day.
 * 
 * @author Daedalus4096
 */
public class MoonwoodStairsBlock extends AbstractPhasingStairsBlock {
    public MoonwoodStairsBlock(Supplier<BlockState> state, Properties properties) {
        super(state, properties);
    }

    @Override
    protected TimePhase getCurrentPhase(IWorld world) {
        return TimePhase.getMoonPhase(world);
    }
}
