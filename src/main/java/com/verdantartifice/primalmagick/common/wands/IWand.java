package com.verdantartifice.primalmagick.common.wands;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import com.verdantartifice.primalmagick.common.spells.SpellPackage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

/**
 * Base interface for a wand.  Wands store mana for use in crafting and, optionally, casting spells.
 * They are replenished by drawing from mana fonts or being charged in a wand charger.  The wand's mana
 * is stored internally as centimana (hundredths of mana points), though most mana manipulation methods
 * deal in "real" mana, not centimana.
 * 
 * @author Daedalus4096
 */
public interface IWand {
    /**
     * Get the amount of centimana for the given source which is contained in the given wand stack.
     * 
     * @param stack the wand stack to be queried
     * @param source the type of mana to be queried
     * @return the amount of centimana contained
     */
    public int getMana(@Nullable ItemStack stack, @Nullable Source source);
    
    /**
     * Get the centimana amounts of all types of mana contained in the given wand stack.
     * 
     * @param stack the wand stack to be queried
     * @return the amount of each type of mana contained
     */
    @Nonnull
    public SourceList getAllMana(@Nullable ItemStack stack);
    
    /**
     * Get the maximum amount of centimana that can be held by the given wand stack.
     * 
     * @param stack the wand stack whose maximum mana to return
     * @return the maximum amount of centimana that can be held by the given wand stack
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
    public int addRealMana(@Nullable ItemStack stack, @Nullable Source source, int amount);
    
    /**
     * Consume the given amount of the given type of centimana from the given wand stack for the given player.  Takes
     * into account any cost modifiers.
     * 
     * @param stack the wand stack to be modified
     * @param player the player doing the consuming, if applicable
     * @param source the type of mana to be consumed
     * @param amount the amount of centimana to be consumed
     * @return true if sufficient centimana was present in the wand and successfully removed, false otherwise
     */
    public boolean consumeMana(@Nullable ItemStack stack, @Nullable PlayerEntity player, @Nullable Source source, int amount);
    
    /**
     * Consume the given amounts of centimana from the given wand stack for the given player.  Takes into account any
     * cost modifiers.
     * 
     * @param stack the wand stack to be modified
     * @param player the player doing the consuming, if applicable
     * @param sources the amount of each type of centimana to be consumed
     * @return true if sufficient centimana was present in the wand and successfully removed, false otherwise
     */
    public boolean consumeMana(@Nullable ItemStack stack, @Nullable PlayerEntity player, @Nullable SourceList sources);
    
    /**
     * Consume the given amount of the given type of mana from the given wand stack for the given player.  Takes
     * into account any cost modifiers.
     * 
     * @param stack the wand stack to be modified
     * @param player the player doing the consuming, if applicable
     * @param source the type of mana to be consumed
     * @param amount the amount of mana to be consumed
     * @return true if sufficient mana was present in the wand and successfully removed, false otherwise
     */
    public boolean consumeRealMana(@Nullable ItemStack stack, @Nullable PlayerEntity player, @Nullable Source source, int amount);
    
    /**
     * Consume the given amounts of mana from the given wand stack for the given player.  Takes into account any
     * cost modifiers.
     * 
     * @param stack the wand stack to be modified
     * @param player the player doing the consuming, if applicable
     * @param sources the amount of each type of mana to be consumed
     * @return true if sufficient mana was present in the wand and successfully removed, false otherwise
     */
    public boolean consumeRealMana(@Nullable ItemStack stack, @Nullable PlayerEntity player, @Nullable SourceList sources);
    
    /**
     * Determine if the given wand stack contains the given amount of the given type of centimana for the given player.  Takes
     * into account any cost modifiers.
     * 
     * @param stack the wand stack to be queried
     * @param player the player doing the check, if applicable
     * @param source the type of mana being queried
     * @param amount the amount of centimana required
     * @return true if sufficient centimana is present, false otherwise
     */
    public boolean containsMana(@Nullable ItemStack stack, @Nullable PlayerEntity player, @Nullable Source source, int amount);
    
    /**
     * Determine if the given wand stack contains the given amounts of centimana for the given player.  Takes into account
     * any cost modifiers.
     * 
     * @param stack the wand stack to be queried
     * @param player the player doing the check, if applicable
     * @param sources the amount of each type of centimana required
     * @return true if sufficient centimana is present, false otherwise
     */
    public boolean containsMana(@Nullable ItemStack stack, @Nullable PlayerEntity player, @Nullable SourceList sources);
    
    /**
     * Determine if the given wand stack contains the given amount of the given type of mana for the given player.  Takes
     * into account any cost modifiers.
     * 
     * @param stack the wand stack to be queried
     * @param player the player doing the check, if applicable
     * @param source the type of mana being queried
     * @param amount the amount of mana required
     * @return true if sufficient mana is present, false otherwise
     */
    public boolean containsRealMana(@Nullable ItemStack stack, @Nullable PlayerEntity player, @Nullable Source source, int amount);
    
    /**
     * Determine if the given wand stack contains the given amounts of mana for the given player.  Takes into account
     * any cost modifiers.
     * 
     * @param stack the wand stack to be queried
     * @param player the player doing the check, if applicable
     * @param sources the amount of each type of mana required
     * @return true if sufficient mana is present, false otherwise
     */
    public boolean containsRealMana(@Nullable ItemStack stack, @Nullable PlayerEntity player, @Nullable SourceList sources);
    
    /**
     * Get the base mana cost modifier to be applied to mana consumption, as determined by the cap of the wand, if any.
     * 
     * @param stack the wand stack to be queried
     * @return the base mana cost modifier to be applied to mana consumption
     */
    public double getBaseCostModifier(@Nullable ItemStack stack);
    
    /**
     * Get the total mana cost modifier to be applied to mana consumption, from all factors (e.g. wand cap, player gear, attunement).
     * 
     * @param stack the wand stack to be queried
     * @param player the player consuming the mana
     * @param source the type of mana being consumed
     * @return the total mana cost modifier to be applied to mana consumption
     */
    public double getTotalCostModifier(@Nullable ItemStack stack, @Nullable PlayerEntity player, @Nullable Source source);

    /**
     * Get the amount of real mana to siphon from a mana font when channeling it.
     *
     * @param stack the wand stack to be queried
     * @return the amount of real mana to siphon from mana fonts
     */
    public int getSiphonAmount(@Nullable ItemStack stack);

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
     * Get the text for the spell capacity of the given wand stack.
     * 
     * @param stack the wand stack to be queried
     * @return the text for the spell capacity
     */
    public ITextComponent getSpellCapacityText(@Nullable ItemStack stack);
    
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
