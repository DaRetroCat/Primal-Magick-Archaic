package com.verdantartifice.primalmagick.common.blocks.trees;

import com.verdantartifice.primalmagick.common.blockstates.properties.TimePhase;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.world.IWorld;

/**
 * Block definition for sunwood planks.  They are decorative blocks that fade out of existence and become indestructable at night.
 * 
 * @author Daedalus4096
 */
public class SunwoodPlanksBlock extends AbstractPhasingBlock {
    public SunwoodPlanksBlock() {
        super(Block.Properties.create(Material.WOOD, MaterialColor.GOLD).hardnessAndResistance(2.0F, 3.0F).tickRandomly().notSolid().sound(SoundType.WOOD));
    }

    @Override
    protected TimePhase getCurrentPhase(IWorld world) {
        return TimePhase.getSunPhase(world);
    }
}
