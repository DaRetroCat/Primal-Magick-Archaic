package com.verdantartifice.primalmagic.common.wands;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.verdantartifice.primalmagic.common.sources.Source;
import com.verdantartifice.primalmagic.common.sources.SourceList;
import com.verdantartifice.primalmagic.common.spells.SpellPackage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Base interface for a wand.  Wands store mana for use in crafting and, optionally, casting spells.
 * They are replenished by drawing from mana fonts or being charged in a wand charger.
 * 
 * @author Daedalus4096
 */
public interface IWand {
    /**
     * Get the amount of mana for the given source which is contained in the given wand stack.
     * 
     * @param stack the wand stack to be queried
     * @param source the type of mana to be queried
     * @return the amount of mana contained
     */
    public int getMana(@Nullable ItemStack stack, @Nullable Source source);
    
    /**
     * Get the amounts of all types of mana contained in the given wand stack.
     * 
     * @param stack the wand stack to be queried
     * @return the amount of each type of mana contained
     */
    @Nonnull
    public SourceList getAllMana(@Nullable ItemStack stack);
    
    /**
     * Get the maximum amount of mana that can be held by the given wand stack.
     * 
     * @param stack the wand stack whose maximum mana to return
     * @return the maximum amount of mana that can be held by the given wand stack
     */
    public int getMaxMana(@Nullable ItemStack stack);
    
    /**
     * Add the given amount of the given type of mana to the given wand stack, up to its maximum.
     * 
     * @param stack the wand stack to be modified
     * @param source the type of mana to be added
     * @param amount the amount of mana to be added
     * @return the amount of leftover mana that could not fit in the wand
     */
    public int addMana(@Nullable ItemStack stack, @Nullable Source source, int amount);
    
    /**
     * Consume the given amount of the given type of mana from the given wand stack for the given player.
     * 
     * @param stack the wand stack to be modified
     * @param player the player doing the consuming
     * @param source the type of mana to be consumed
     * @param amount the amount of mana to be consumed
     * @return true if sufficient mana was present in the wand and successfully removed, false otherwise
     */
    public boolean consumeMana(@Nullable ItemStack stack, @Nullable PlayerEntity player, @Nullable Source source, int amount);
    
    /**
     * Consume the given amounts of mana from the given wand stack for the given player.
     * 
     * @param stack the wand stack to be modified
     * @param player the player doing the consuming
     * @param sources the amount of each type of mana to be consumed
     * @return true if sufficient mana was present in the wand and successfully removed, false otherwise
     */
    public boolean consumeMana(@Nullable ItemStack stack, @Nullable PlayerEntity player, @Nullable SourceList sources);
    
    /**
     * Determine if the given wand stack contains the given amount of the given type of mana for the given player.
     * 
     * @param stack the wand stack to be queried
     * @param player the player doing the check
     * @param source the type of mana being queried
     * @param amount the amount of mana required
     * @return true if sufficient mana is present, false otherwise
     */
    public boolean containsMana(@Nullable ItemStack stack, @Nullable PlayerEntity player, @Nullable Source source, int amount);
    
    /**
     * Determine if the given wand stack contains the given amounts of mana for the given player.
     * 
     * @param stack the wand stack to be queried
     * @param player the player doing the check
     * @param sources the amount of each type of mana required
     * @return true if sufficient mana is present, false otherwise
     */
    public boolean containsMana(@Nullable ItemStack stack, @Nullable PlayerEntity player, @Nullable SourceList sources);

    /**
     * Clear any stored position data for the last interacted-with tile.
     * 
     * @param wandStack the wand stack to be modified
     */
    public void clearTileInUse(@Nonnull ItemStack wandStack);

    /**
     * Store the position data for the given interactable tile into the given wand stack.
     * 
     * @param wandStack the wand stack to be modified
     * @param tile the tile whose position data is to be stored
     */
    public <T extends TileEntity & IInteractWithWand> void setTileInUse(@Nonnull ItemStack wandStack, @Nonnull T tile);

    /**
     * Get the tile currently being interacted with by the given wand stack.
     * 
     * @param wandStack the wand stack to be queried
     * @param world the world in which the tile resides
     * @return the tile currently being interacted with, or null if none was found
     */
    @Nullable
    public IInteractWithWand getTileInUse(@Nonnull ItemStack wandStack, @Nonnull World world);
    
    /**
     * Get the list of spell packages currently inscribed on the given wand stack.
     * 
     * @param stack the wand stack to be queried
     * @return the list of spell packages currently inscribed
     */
    @Nonnull
    public List<SpellPackage> getSpells(@Nullable ItemStack stack);
    
    /**
     * Get the number of spell packages currently inscribed on the given wand stack.
     * 
     * @param stack the wand stack to be queried
     * @return the number of spell packages currently inscribed
     */
    public int getSpellCount(@Nullable ItemStack stack);
    
    /**
     * Get the index of the currently selected inscribed spell package on the given wand stack.
     * 
     * @param stack the wand stack to be queried
     * @return the zero-based index of the currently selected spell, or -1 if no spell is selected
     */
    public int getActiveSpellIndex(@Nullable ItemStack stack);
    
    /**
     * Get the currently selected inscribed spell package on the given wand stack.
     * 
     * @param stack the wand stack to be queried
     * @return the currently selected spell, or null if no spell is selected
     */
    @Nullable
    public SpellPackage getActiveSpell(@Nullable ItemStack stack);
    
    /**
     * Get the index of the currently selected inscribed spell package on the given wand stack.
     * 
     * @param stack the wand stack to be modified
     * @param index the zero-based index of the newly selected spell, or -1 if no spell is to be selected
     * @return true if the given index was valid for the given wand, false otherwise
     */
    public boolean setActiveSpellIndex(@Nullable ItemStack stack, int index);
    
    /**
     * Determine if the given spell package can be inscribed onto the given wand stack.
     * 
     * @param stack the wand stack to be queried
     * @param spell the spell package to be inscribed
     * @return true if the spell will fit on the wand, false otherwise
     */
    public boolean canAddSpell(@Nullable ItemStack stack, @Nullable SpellPackage spell);
    
    /**
     * Add the given spell package to the given wand stack's list of inscribed spells.
     * 
     * @param stack the wand stack to be modified
     * @param spell the spell package to be inscribed
     * @return true if the spell was successfully added, false otherwise
     */
    public boolean addSpell(@Nullable ItemStack stack, @Nullable SpellPackage spell);
}
