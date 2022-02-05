package com.verdantartifice.primalmagick.common.capabilities;

import javax.annotation.Nullable;

import com.verdantartifice.primalmagick.common.attunements.AttunementType;
import com.verdantartifice.primalmagick.common.sources.Source;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Capability interface for storing attunement data.  Attached to player entities.
 * 
 * @author Daedalus4096
 */
public interface IPlayerAttunements extends INBTSerializable<CompoundNBT> {
    /**
     * Remove all attunement data from the player.
     */
    public void clear();
    
    /**
     * Get the stored value of the given attunement for the player.
     * 
     * @param source the source of the attunement to be retrieved
     * @param type the type of the attunement to be retrieved
     * @return the value of the attunement, or zero if not found
     */
    public int getValue(@Nullable Source source, @Nullable AttunementType type);
    
    /**
     * Store the given value of the given attunement for the player.
     * 
     * @param source the source of the attunement to be stored
     * @param type the type of the attunement to be stored
     * @param value the value of the attunement to be stored
     */
    public void setValue(@Nullable Source source, @Nullable AttunementType type, int value);
    
    /**
     * Sync the given player's attunement data to the their client.
     * 
     * @param player the player whose client should receive the data
     */
    public void sync(@Nullable ServerPlayerEntity player);
}
