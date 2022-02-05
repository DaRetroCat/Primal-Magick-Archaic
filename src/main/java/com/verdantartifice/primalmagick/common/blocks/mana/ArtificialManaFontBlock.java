package com.verdantartifice.primalmagick.common.blocks.mana;

import com.verdantartifice.primalmagick.common.misc.DeviceTier;
import com.verdantartifice.primalmagick.common.misc.ITieredDevice;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.tiles.mana.ArtificialManaFontTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

/**
 * Block definition for an ancient mana font.  Ancient mana fonts are found in shrines placed into the
 * world at generation time.
 *
 * @author Daedalus4096
 */
public class ArtificialManaFontBlock extends AbstractManaFontBlock implements ITieredDevice {
    public ArtificialManaFontBlock(Source source, DeviceTier tier, Block.Properties properties) {
        super(source, tier, properties);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ArtificialManaFontTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
        // Pass any received events on to the tile entity and let it decide what to do with it
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tile = worldIn.getTileEntity(pos);
        return tile != null && tile.receiveClientEvent(id, param);
    }
}