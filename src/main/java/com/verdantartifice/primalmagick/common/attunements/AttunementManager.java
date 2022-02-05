package com.verdantartifice.primalmagick.common.attunements;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.verdantartifice.primalmagick.common.capabilities.IPlayerAttunements;
import com.verdantartifice.primalmagick.common.capabilities.PrimalMagicCapabilities;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.sources.SourceList;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Primary access point for attunement-related methods.  As players utilize magic, they gain or
 * (sometimes) lose attunement with that magic's source.  Reaching certain thresholds of attunement
 * value results in the acquisition of certain passive abilities for as long as the player maintains
 * the attunement.  Attunement values may be permanent, induced, or temporary, but the total value
 * is what determines any bonuses received.
 * 
 * @author Daedalus4096
 * @see {@link com.verdantartifice.primalmagick.common.attunements.AttunementType}
 */
public class AttunementManager {
    protected static final List<AttunementAttributeModifier> MODIFIERS = new ArrayList<>();
    
    // Set of unique IDs of players that need their research synced to their client
    private static final Set<UUID> SYNC_SET = ConcurrentHashMap.newKeySet();
    
    public static boolean isSyncScheduled(@Nullable PlayerEntity player) {
        if (player == null) {
            return false;
        } else {
            return SYNC_SET.remove(player.getUniqueID());
        }
    }
    
    public static void scheduleSync(@Nullable PlayerEntity player) {
        if (player != null) {
            SYNC_SET.add(player.getUniqueID());
        }
    }
    
    public static void registerAttributeModifier(@Nonnull Source source, AttunementThreshold threshold, @Nonnull Attribute attribute, @Nonnull String uuidStr, double modValue, @Nonnull AttributeModifier.Operation modOperation) {
        MODIFIERS.add(new AttunementAttributeModifier(source, threshold, attribute, uuidStr, modValue, modOperation));
    }
    
    /**
     * Gets a partial attunement value for the given player.
     * 
     * @param player the player to be queried
     * @param source the source of attunement to be retrieved
     * @param type the type of attunement to be retrieved
     * @return the partial attunement value
     */
    public static int getAttunement(@Nullable PlayerEntity player, @Nullable Source source, @Nullable AttunementType type) {
        if (player != null && source != null && type != null) {
            IPlayerAttunements attunements = PrimalMagicCapabilities.getAttunements(player);
            if (attunements != null) {
                return attunements.getValue(source, type);
            }
        }
        return 0;
    }
    
    /**
     * Gets the total attunement value for the given player.
     * 
     * @param player the player to be queried
     * @param source the source of attunement to be retrieved
     * @return the total attunement value
     */
    public static int getTotalAttunement(@Nullable PlayerEntity player, @Nullable Source source) {
        if (player != null && source != null) {
            IPlayerAttunements attunements = PrimalMagicCapabilities.getAttunements(player);
            if (attunements != null) {
                // Sum up the partial attunement values for each attunement type
                int total = 0;
                for (AttunementType type : AttunementType.values()) {
                    total += attunements.getValue(source, type);
                }
                return total;
            }
        }
        return 0;
    }
    
    /**
     * Determine whether the given player's total attunement for the given source meets or exceeds the
     * given threshold.
     * 
     * @param player the player to be queried
     * @param source the source of attunement being queried
     * @param threshold the threshold value to test against
     * @return true if the player's total attunement meets or exceeds the given threshold, false otherwise
     */
    public static boolean meetsThreshold(@Nullable PlayerEntity player, @Nullable Source source, @Nullable AttunementThreshold threshold) {
        if (player != null && source != null && threshold != null) {
            return getTotalAttunement(player, source) >= threshold.getValue();
        } else {
            return false;
        }
    }
    
    /**
     * Sets the partial attunement value for the given player.
     * 
     * @param player the player to be modified
     * @param source the source of attunement to be set
     * @param type the type of attunement to be set
     * @param value the new partial attunement value
     */
    public static void setAttunement(@Nullable PlayerEntity player, @Nullable Source source, @Nullable AttunementType type, int value) {
        if (player instanceof ServerPlayerEntity && source != null && type != null) {
            IPlayerAttunements attunements = PrimalMagicCapabilities.getAttunements(player);
            if (attunements != null) {
                int oldTotal = getTotalAttunement(player, source);
                
                // Set the new value into the player capability
                attunements.setValue(source, type, value);
                scheduleSync(player);

                int newTotal = getTotalAttunement(player, source);

                // Determine if any thresholds were passed, either up or down
                for (AttunementThreshold threshold : AttunementThreshold.values()) {
                    int thresholdValue = threshold.getValue();
                    ITextComponent sourceText = new TranslationTextComponent(source.getNameTranslationKey()).mergeStyle(source.getChatColor());
                    if (oldTotal < thresholdValue && newTotal >= thresholdValue) {
                        // If gaining a threshold, send a message to the player
                        if (source.isDiscovered(player)) {
                            player.sendStatusMessage(new TranslationTextComponent("primalmagick.attunement.threshold_gain", sourceText), false);
                        }
                        
                        // Apply any new attribute modifiers from the threshold gain
                        for (AttunementAttributeModifier modifier : MODIFIERS) {
                            if (source.equals(modifier.getSource()) && threshold == modifier.getThreshold()) {
                                modifier.applyToEntity(player);
                            }
                        }
                    }
                    if (oldTotal >= thresholdValue && newTotal < thresholdValue) {
                        // If losing a threshold, send a message to the player
                        if (source.isDiscovered(player)) {
                            player.sendStatusMessage(new TranslationTextComponent("primalmagick.attunement.threshold_loss", sourceText), false);
                        }
                        
                        // Remove any lost attribute modifiers from the threshold loss
                        for (AttunementAttributeModifier modifier : MODIFIERS) {
                            if (source.equals(modifier.getSource()) && threshold == modifier.getThreshold()) {
                                modifier.removeFromEntity(player);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Sets the partial attunement values for the given player.
     * 
     * @param player the player to be modified
     * @param type the type of attunement to be set
     * @param values the new partial attunement values
     */
    public static void setAttunement(@Nullable PlayerEntity player, @Nullable AttunementType type, @Nullable SourceList values) {
        if (values != null && !values.isEmpty()) {
            for (Source source : values.getSources()) {
                setAttunement(player, source, type, values.getAmount(source));
            }
        }
    }
    
    /**
     * Increments the partial attunement value for the given player by the given amount.
     * 
     * @param player the player to be modified
     * @param source the source of attunement to be changed
     * @param type the type of attunement to be changed
     * @param delta the amount of change to apply, may be negative
     */
    public static void incrementAttunement(@Nullable PlayerEntity player, @Nullable Source source, @Nullable AttunementType type, int delta) {
        int oldValue = getAttunement(player, source, type);
        setAttunement(player, source, type, oldValue + delta);
    }
    
    /**
     * Increments the partial attunement value for the given player by one.
     * 
     * @param player the player to be modified
     * @param source the source of attunement to be changed
     * @param type the type of attunement to be changed
     */
    public static void incrementAttunement(@Nullable PlayerEntity player, @Nullable Source source, @Nullable AttunementType type) {
        incrementAttunement(player, source, type, 1);
    }
    
    /**
     * Increments the partial attunement values for the given player by the given amounts.
     * 
     * @param player the player to be modified
     * @param type the type of attunement to be changed
     * @param deltas the amounts of change to apply, may be negative
     */
    public static void incrementAttunement(@Nullable PlayerEntity player, @Nullable AttunementType type, @Nullable SourceList deltas) {
        SourceList newValues = new SourceList();
        for (Source source : deltas.getSources()) {
            int oldValue = getAttunement(player, source, type);
            newValues.add(source, oldValue + deltas.getAmount(source));
        }
        setAttunement(player, type, newValues);
    }
    
    /**
     * Decrease all temporary attunements for the given player by one.
     * 
     * @param player the player to be modified
     */
    public static void decayTemporaryAttunements(@Nullable PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            for (Source source : Source.SORTED_SOURCES) {
                incrementAttunement(player, source, AttunementType.TEMPORARY, -1);
            }
        }
    }
}
