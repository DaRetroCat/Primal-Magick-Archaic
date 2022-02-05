package com.verdantartifice.primalmagick.common.blocks.trees;

import com.verdantartifice.primalmagick.common.blockstates.properties.TimePhase;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.world.IWorld;

/**
 * Block definition for moonwood pillars.  They are decorative blocks that fade out of existence and become indestructable during the day.
 * 
 * @author Daedalus4096
 */
public class MoonwoodPillarBlock extends AbstractPhasingPillarBlock {
    public MoonwoodPillarBlock() {
        super(Block.Properties.create(Material.WOOD, MaterialColor.IRON).hardnessAndResistance(2.0F).tickRandomly().notSolid().sound(SoundType.WOOD));
    }

    @Override
    protected TimePhase getCurrentPhase(IWorld world) {
        return TimePhase.getMoonPhase(world);
    }
}
