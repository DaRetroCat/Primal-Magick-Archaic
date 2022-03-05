package com.verdantartifice.primalmagick.common.rituals;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.verdantartifice.primalmagick.common.network.PacketHandler;
import com.verdantartifice.primalmagick.common.network.packets.fx.PropMarkerPacket;
import com.verdantartifice.primalmagick.common.network.packets.fx.RemovePropMarkerPacket;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

/**
 * Interface indicating whether a block can serve as a prop for magical rituals.
 * 
 * @author Daedalus4096
 */
public interface IRitualPropBlock extends ISaltPowered, IRitualStabilizer {
    public boolean isPropActivated(BlockState state, World world, BlockPos pos);

    public default void onPropActivated(BlockState state, World world, BlockPos pos, float stabilityBonus) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IRitualPropTileEntity) {
            ((IRitualPropTileEntity)tile).notifyAltarOfPropActivation(stabilityBonus);
        }
    }
    
    public default boolean isPropOpen(BlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return (tile instanceof IRitualPropTileEntity) && ((IRitualPropTileEntity)tile).getAltarPos() != null;
    }
    
    public default void openProp(BlockState state, World world, BlockPos pos, @Nullable PlayerEntity player, BlockPos altarPos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IRitualPropTileEntity) {
            ((IRitualPropTileEntity)tile).setAltarPos(altarPos);
            PacketHandler.sendToAllAround(new PropMarkerPacket(pos), world.getDimensionKey(), pos, 32.0D);
            if (player != null) {
                this.sendPropStatusMessage(player);
            }
        }
    }
    
    public default void closeProp(BlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IRitualPropTileEntity) {
            ((IRitualPropTileEntity)tile).setAltarPos(null);
            PacketHandler.sendToAllAround(new RemovePropMarkerPacket(pos), world.getDimensionKey(), pos, 32.0D);
        }
    }
    
    public default void sendPropStatusMessage(@Nonnull PlayerEntity player) {
        player.sendStatusMessage(new TranslationTextComponent(this.getPropTranslationKey()), false);
    }
    
    public String getPropTranslationKey();

    /**
     * Indicated whether the block is a universal ritual prop that can and will be used by any
     * ritual started in range.
     *
     * @return whether the block is a universal ritual prop
     */
    public default boolean isUniversal() {
        return false;
    }
}
