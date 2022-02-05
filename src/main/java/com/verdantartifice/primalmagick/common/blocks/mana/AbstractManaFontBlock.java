package com.verdantartifice.primalmagick.common.blocks.mana;

import com.verdantartifice.primalmagick.common.misc.DeviceTier;
import com.verdantartifice.primalmagick.common.misc.ITieredDevice;
import com.verdantartifice.primalmagick.common.sources.Source;

import com.verdantartifice.primalmagick.common.tiles.mana.AbstractManaFontTileEntity;
import com.verdantartifice.primalmagick.common.tiles.mana.AncientManaFontTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import static net.minecraft.block.Block.*;

/**
 * Base block definition for a mana font.  Mana fonts contain a slowly replenishing supply of mana,
 * which can be drained by a wand to power it.
 *
 * @author Daedalus4096
 */
public abstract class AbstractManaFontBlock extends Block implements ITieredDevice {
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    protected Source source;

    protected DeviceTier tier;

    public AbstractManaFontBlock(Source source, DeviceTier tier, Block.Properties properties) {
        super(properties);
        this.source = source;
        this.tier = tier;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    public Source getSource() {
        return this.source;
    }

    @Override
    public DeviceTier getDeviceTier() {
        return this.tier;
    }

    public int getManaCapacity() {
        switch (this.tier) {
            case BASIC:
                // The "basic" tier refers to ancient mana fonts, which cannot be constructed
                return 100;
            case ENCHANTED:
                return 10;
            case FORBIDDEN:
                return 100;
            case HEAVENLY:
                return 1000;
            default:
                return 0;
        }
    }
}