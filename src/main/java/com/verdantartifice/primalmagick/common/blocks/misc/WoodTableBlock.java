package com.verdantartifice.primalmagick.common.blocks.misc;

import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.common.util.VoxelShapeUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

/**
 * Block definition for a plain wooden table.  Wood tables are decorative blocks used as components
 * for other devices, such as the analysis table.
 * 
 * @author Daedalus4096
 */
public class WoodTableBlock extends Block {
    protected static final VoxelShape SHAPE = VoxelShapeUtils.fromModel(new ResourceLocation(PrimalMagick.MODID, "block/wood_table"));

    public WoodTableBlock() {
        super(Block.Properties.create(Material.WOOD).hardnessAndResistance(1.5F, 6.0F).sound(SoundType.WOOD));
    }
    
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }
}
