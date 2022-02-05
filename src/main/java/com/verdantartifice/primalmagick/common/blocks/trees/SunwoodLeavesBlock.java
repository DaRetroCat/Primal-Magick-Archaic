package com.verdantartifice.primalmagick.common.blocks.trees;

import com.verdantartifice.primalmagick.common.blockstates.properties.TimePhase;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.world.IWorld;

/**
 * Block definition for sunwood leaves.  They are decorative blocks that fade out of existence and become indestructable at night.
 * 
 * @author Daedalus4096
 */
public class SunwoodLeavesBlock extends AbstractPhasingLeavesBlock {
    public SunwoodLeavesBlock() {
        super(Block.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).tickRandomly().notSolid().sound(SoundType.PLANT).setLightLevel((state) -> {
            return state.get(PHASE).getLightLevel();
        }).setSuffocates((state, blockReader, pos) -> {
        	return false;
        }).setBlocksVision((state, blockReader, pos) -> {
        	return false;
        }).setAllowsSpawn(AbstractPhasingLeavesBlock::allowsSpawnOnLeaves));
    }

    @Override
    public TimePhase getCurrentPhase(IWorld world) {
        return TimePhase.getSunPhase(world);
    }
}
