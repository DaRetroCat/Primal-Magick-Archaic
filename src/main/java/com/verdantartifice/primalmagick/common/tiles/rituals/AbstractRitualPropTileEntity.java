package com.verdantartifice.primalmagick.common.tiles.rituals;

import javax.annotation.Nullable;

import com.verdantartifice.primalmagick.common.rituals.IRitualPropTileEntity;
import com.verdantartifice.primalmagick.common.tiles.base.TilePM;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

/**
 * Base class for a ritual prop tile entity.  Holds information about the altar it interacts with.
 * 
 * @author Daedalus4096
 */
public abstract class AbstractRitualPropTileEntity extends TilePM implements IRitualPropTileEntity {
    protected BlockPos altarPos = null;
    
    public AbstractRitualPropTileEntity(TileEntityType<?> type) {
        super(type);
    }
    
    @Override
    @Nullable
    public BlockPos getAltarPos() {
        return this.altarPos;
    }
    
    @Override
    public void setAltarPos(@Nullable BlockPos pos) {
        this.altarPos = pos;
        this.markDirty();
    }
    
    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        this.altarPos = compound.contains("AltarPos", Constants.NBT.TAG_LONG) ? BlockPos.fromLong(compound.getLong("AltarPos")) : null;
    }
    
    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if (this.altarPos != null) {
            compound.putLong("AltarPos", this.altarPos.toLong());
        }
        return super.write(compound);
    }
    
    @Override
    public void notifyAltarOfPropActivation() {
        if (this.altarPos != null) {
            TileEntity tile = this.world.getTileEntity(this.altarPos);
            if (tile instanceof RitualAltarTileEntity) {
                ((RitualAltarTileEntity)tile).onPropActivation(this.pos);
            }
        }
    }
}
