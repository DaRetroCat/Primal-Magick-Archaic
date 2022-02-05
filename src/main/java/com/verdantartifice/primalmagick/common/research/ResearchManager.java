package com.verdantartifice.primalmagick.common.research;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.verdantartifice.primalmagick.common.affinities.AffinityManager;
import com.verdantartifice.primalmagick.common.attunements.AttunementManager;
import com.verdantartifice.primalmagick.common.attunements.AttunementType;
import com.verdantartifice.primalmagick.common.capabilities.IPlayerKnowledge;
import com.verdantartifice.primalmagick.common.capabilities.PrimalMagicCapabilities;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import com.verdantartifice.primalmagick.common.stats.StatsManager;
import com.verdantartifice.primalmagick.common.stats.StatsPM;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Primary access point for research-related methods.
 * 
 * @author Daedalus4096
 */
public class ResearchManager {
    // Hash codes of items that must be crafted to complete one or more research stages
    private static final Set<Integer> CRAFTING_REFERENCES = new HashSet<>();
    
    // Set of unique IDs of players that need their research synced to their client
    private static final Set<UUID> SYNC_SET = ConcurrentHashMap.newKeySet();
    
    // Registry of all defined scan triggers
    private static final List<IScanTrigger> SCAN_TRIGGERS = new ArrayList<>();
    
    public static Set<Integer> getAllCraftingReferences() {
        return Collections.unmodifiableSet(CRAFTING_REFERENCES);
    }
    
    public static boolean addCraftingReference(int reference) {
        return CRAFTING_REFERENCES.add(Integer.valueOf(reference));
    }
    
    static void clearCraftingReferences() {
        CRAFTING_REFERENCES.clear();
    }
    
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
    
    public static boolean hasPrerequisites(@Nullable PlayerEntity player, @Nullable SimpleResearchKey key) {
        if (player == null) {
            return false;
        }
        if (key == null) {
            return true;
        }
        ResearchEntry entry = ResearchEntries.getEntry(key);
        if (entry == null || entry.getParentResearch() == null) {
            return true;
        } else {
            // Perform a strict completion check on the given entry's parent research
            return entry.getParentResearch().isKnownByStrict(player);
        }
    }
    
    public static boolean isResearchComplete(@Nullable PlayerEntity player, @Nullable SimpleResearchKey key) {
        if (player == null || key == null) {
            return false;
        }
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(player);
        if (knowledge == null) {
            return false;
        } else {
            return knowledge.isResearchComplete(key);
        }
    }
    
    public static boolean completeResearch(@Nullable PlayerEntity player, @Nullable SimpleResearchKey key) {
        // Complete the given research and sync it to the player's client
        return completeResearch(player, key, true);
    }
    
    public static boolean completeResearch(@Nullable PlayerEntity player, @Nullable SimpleResearchKey key, boolean sync) {
        // Repeatedly progress the given research until it is completed, optionally syncing it to the player's client
        boolean retVal = false;
        while (progressResearch(player, key, sync)) {
            retVal = true;
        }
        return retVal;
    }
    
    public static void forceGrantWithAllParents(@Nullable PlayerEntity player, @Nullable SimpleResearchKey key) {
        if (player != null && key != null) {
            key = key.stripStage(); // When we force-grant, we fully complete the entry, not partially
            IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(player);
            if (knowledge != null && !knowledge.isResearchComplete(key)) {
                ResearchEntry entry = ResearchEntries.getEntry(key);
                if (entry != null) {
                    if (entry.getParentResearch() != null) {
                        // Recursively force-grant all of this entry's parent entries, even if not all of them are required
                        for (SimpleResearchKey parentKey : entry.getParentResearch().getKeys()) {
                            forceGrantWithAllParents(player, parentKey);
                        }
                    }
                    for (ResearchStage stage : entry.getStages()) {
                        // Complete any research required as a prerequisite for any of the entry's stages
                        if (stage.getRequiredResearch() != null) {
                            for (SimpleResearchKey requiredKey : stage.getRequiredResearch().getKeys()) {
                                completeResearch(player, requiredKey);
                            }
                        }
                    }
                }
                
                // Once all prerequisites are out of the way, complete this entry itself
                completeResearch(player, key);
                
                // Mark as updated any research entry that has a stage which requires completion of this entry
                for (ResearchEntry searchEntry : ResearchEntries.getAllEntries()) {
                    for (ResearchStage searchStage : searchEntry.getStages()) {
                        if (searchStage.getRequiredResearch() != null && searchStage.getRequiredResearch().contains(key)) {
                            knowledge.addResearchFlag(searchEntry.getKey(), IPlayerKnowledge.ResearchFlag.UPDATED);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public static void forceRevokeWithAllChildren(@Nullable PlayerEntity player, @Nullable SimpleResearchKey key) {
        if (player != null && key != null) {
            IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(player);
            if (knowledge != null && knowledge.isResearchComplete(key)) {
                // Revoke all child research of this entry
                for (ResearchEntry entry : ResearchEntries.getAllEntries()) {
                    CompoundResearchKey parentResearch = entry.getParentResearch();
                    if (parentResearch != null && parentResearch.containsStripped(key)) {
                        forceRevokeWithAllChildren(player, entry.getKey());
                    }
                }
                
                // Once all children are revoked, revoke this entry itself
                revokeResearch(player, key.stripStage());
            }
        }
    }
    
    public static boolean revokeResearch(@Nullable PlayerEntity player, @Nullable SimpleResearchKey key) {
        // Revoke the given research and sync it to the player's client
        return revokeResearch(player, key, true);
    }
    
    public static boolean revokeResearch(@Nullable PlayerEntity player, @Nullable SimpleResearchKey key, boolean sync) {
        // Remove the given research from the player's known list and optionally sync to the player's client
        if (player == null || key == null) {
            return false;
        }
        
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(player);
        if (knowledge == null) {
            return false;
        }

        knowledge.removeResearch(key);
        if (sync) {
            scheduleSync(player);
        }
        return true;
    }
    
    public static boolean progressResearch(@Nullable PlayerEntity player, @Nullable SimpleResearchKey key) {
        // Progress the given research to its next stage and sync to the player's client
        return progressResearch(player, key, true);
    }
    
    public static boolean progressResearch(@Nullable PlayerEntity player, @Nullable SimpleResearchKey key, boolean sync) {
        // Progress the given research to its next stage and sync to the player's client
        return progressResearch(player, key, sync, true);
    }
    
    public static boolean progressResearch(@Nullable PlayerEntity player, @Nullable SimpleResearchKey key, boolean sync, boolean flags) {
        // Progress the given research to its next stage and optionally sync to the player's client
        if (player == null || key == null) {
            return false;
        }
        
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(player);
        if (knowledge == null) {
            return false;
        }
        if (knowledge.isResearchComplete(key) || !hasPrerequisites(player, key)) {
            // If the research is already complete or the player doesn't have the prerequisites, abort
            return false;
        }
        
        // If the research is not started yet, start it
        boolean added = false;
        if (!knowledge.isResearchKnown(key)) {
            knowledge.addResearch(key);
            added = true;
        }
        
        ResearchEntry entry = ResearchEntries.getEntry(key);
        boolean entryComplete = true;   // Default to true for non-entry research (e.g. stat triggers)
        if (entry != null && !entry.getStages().isEmpty()) {
            // Get the current stage number of the research entry
            ResearchStage currentStage = null;
            int currentStageNum = knowledge.getResearchStage(key);
            
            // Increment the current stage, unless the research was just added then skip this step (because that already 
            // incremented the stage from -1 to 0)
            if (!added) {
                currentStageNum++;
            }
            if (currentStageNum == (entry.getStages().size() - 1) && !entry.getStages().get(currentStageNum).hasPrerequisites()) {
                // If we've advanced to the final stage of the entry and it has no further prereqs (which it shouldn't), then
                // advance one more to be considered complete
                currentStageNum++;
            }
            currentStageNum = Math.min(currentStageNum, entry.getStages().size());
            if (currentStageNum >= 0) {
                currentStage = entry.getStages().get(Math.min(currentStageNum, entry.getStages().size() - 1));
            }
            knowledge.setResearchStage(key, currentStageNum);
            
            // Determine whether the entry has been completed
            entryComplete = (currentStageNum >= entry.getStages().size());
            
            if (currentStage != null) {
                // Process any attunement grants in the newly-reached stage
                SourceList attunements = currentStage.getAttunements();
                for (Source source : attunements.getSources()) {
                    int amount = attunements.getAmount(source);
                    if (amount > 0) {
                        AttunementManager.incrementAttunement(player, source, AttunementType.PERMANENT, amount);
                    }
                }
            }

            // Give the player experience for advancing their research
            if (!added) {
                player.giveExperiencePoints(5);
            }
        }
        
        if (entryComplete) {
            if (sync) {
                // If the entry has been completed and we're syncing, add the appropriate flags
                knowledge.addResearchFlag(key, IPlayerKnowledge.ResearchFlag.POPUP);
                if (flags) {
                    knowledge.addResearchFlag(key, IPlayerKnowledge.ResearchFlag.NEW);
                }
            }
            
            // Reveal any addenda that depended on this research
            for (ResearchEntry searchEntry : ResearchEntries.getAllEntries()) {
                if (!searchEntry.getAddenda().isEmpty() && knowledge.isResearchComplete(searchEntry.getKey())) {
                    for (ResearchAddendum addendum : searchEntry.getAddenda()) {
                        if (addendum.getRequiredResearch() != null && addendum.getRequiredResearch().contains(key) && addendum.getRequiredResearch().isKnownByStrict(player)) {
                            // Announce completion of the addendum
                            ITextComponent nameComp = new TranslationTextComponent(searchEntry.getNameTranslationKey());
                            player.sendMessage(new TranslationTextComponent("event.primalmagick.add_addendum", nameComp), Util.DUMMY_UUID);
                            knowledge.addResearchFlag(searchEntry.getKey(), IPlayerKnowledge.ResearchFlag.UPDATED);
                            
                            // Process attunement grants
                            SourceList attunements = addendum.getAttunements();
                            for (Source source : attunements.getSources()) {
                                int amount = attunements.getAmount(source);
                                if (amount > 0) {
                                    AttunementManager.incrementAttunement(player, source, AttunementType.PERMANENT, amount);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // If syncing, queue it up for next tick
        if (sync) {
            scheduleSync(player);
        }

        return true;
    }
    
    public static boolean addKnowledge(PlayerEntity player, IPlayerKnowledge.KnowledgeType type, int points) {
        return addKnowledge(player, type, points, true);
    }
    
    public static boolean addKnowledge(PlayerEntity player, IPlayerKnowledge.KnowledgeType type, int points, boolean scheduleSync) {
        // Add the given number of knowledge points to the player and sync to their client
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(player);
        if (knowledge == null) {
            return false;
        }
        int levelsBefore = knowledge.getKnowledge(type);
        boolean success = knowledge.addKnowledge(type, points);
        if (!success) {
            return false;
        }
        if (points > 0) {
            int levelsAfter = knowledge.getKnowledge(type);
            int delta = levelsAfter - levelsBefore;
            if (type == IPlayerKnowledge.KnowledgeType.OBSERVATION) {
                StatsManager.incrementValue(player, StatsPM.OBSERVATIONS_MADE, delta);
            } else if (type == IPlayerKnowledge.KnowledgeType.THEORY) {
                StatsManager.incrementValue(player, StatsPM.THEORIES_FORMED, delta);
            }
            for (int index = 0; index < delta; index++) {
                // TODO send knowledge gain packet to player to show client effects for each level gained
            }
        }
        if (scheduleSync) {
            scheduleSync(player);
        }
        return true;
    }
    
    public static boolean registerScanTrigger(@Nullable IScanTrigger trigger) {
        if (trigger == null) {
            return false;
        }
        return SCAN_TRIGGERS.add(trigger);
    }
    
    public static void checkScanTriggers(ServerPlayerEntity player, IItemProvider itemProvider) {
        checkScanTriggersInner(player, itemProvider);
    }
    
    public static void checkScanTriggers(ServerPlayerEntity player, EntityType<?> entityType) {
        checkScanTriggersInner(player, entityType);
    }
    
    private static void checkScanTriggersInner(ServerPlayerEntity player, Object obj) {
        for (IScanTrigger trigger : SCAN_TRIGGERS) {
            if (trigger.matches(player, obj)) {
                trigger.onMatch(player, obj);
            }
        }
    }

    public static boolean isScanned(@Nullable ItemStack stack, @Nullable PlayerEntity player) {
        if (stack == null || stack.isEmpty() || player == null) {
            return false;
        }
        SourceList affinities = AffinityManager.getInstance().getAffinityValues(stack, player.world);
        if (affinities == null || affinities.isEmpty()) {
            // If the given itemstack has no affinities, consider it already scanned
            return true;
        }
        SimpleResearchKey key = SimpleResearchKey.parseItemScan(stack);
        return (key != null && key.isKnownByStrict(player));
    }
    
    public static boolean isScanned(@Nullable EntityType<?> type, @Nullable PlayerEntity player) {
        if (type == null || player == null) {
            return false;
        }
        // TODO Check entity type affinities
        SimpleResearchKey key = SimpleResearchKey.parseEntityScan(type);
        return (key != null && key.isKnownByStrict(player));
    }

    public static boolean setScanned(@Nullable ItemStack stack, @Nullable ServerPlayerEntity player) {
        // Scan the given itemstack and sync the data to the player's client
        return setScanned(stack, player, true);
    }

    public static boolean setScanned(@Nullable ItemStack stack, @Nullable ServerPlayerEntity player, boolean sync) {
        if (stack == null || stack.isEmpty() || player == null) {
            return false;
        }
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(player);
        if (knowledge == null) {
            return false;
        }
        
        // Generate a research key for the itemstack and add that research to the player
        SimpleResearchKey key = SimpleResearchKey.parseItemScan(stack);
        if (key != null && knowledge.addResearch(key)) {
            // Determine how many observation points the itemstack is worth and add those to the player's knowledge
            int obsPoints = getObservationPoints(stack, player.getEntityWorld());
            if (obsPoints > 0) {
                addKnowledge(player, IPlayerKnowledge.KnowledgeType.OBSERVATION, obsPoints, false);
            }
            
            // Increment the items analyzed stat
            StatsManager.incrementValue(player, StatsPM.ITEMS_ANALYZED);
            
            // Check to see if any scan triggers need to be run for the item
            checkScanTriggers(player, stack.getItem());
            
            // Sync the research/knowledge changes to the player's client if requested
            if (sync) {
                knowledge.sync(player); // Sync immediately, rather than scheduling, for snappy arcanometer response
            }
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean setScanned(@Nullable EntityType<?> type, @Nullable ServerPlayerEntity player) {
        return setScanned(type, player, true);
    }
    
    public static boolean setScanned(@Nullable EntityType<?> type, @Nullable ServerPlayerEntity player, boolean sync) {
        if (type == null || player == null) {
            return false;
        }
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(player);
        if (knowledge == null) {
            return false;
        }
        
        // Generate a research key for the entity type and add that research to the player
        SimpleResearchKey key = SimpleResearchKey.parseEntityScan(type);
        if (key != null && knowledge.addResearch(key)) {
            // Determine how many observation points the entity is worth and add those to the player's knowledge
            int obsPoints = getObservationPoints(type);
            if (obsPoints > 0) {
                addKnowledge(player, IPlayerKnowledge.KnowledgeType.OBSERVATION, obsPoints, false);
            }
            
            // Increment the entities analyzed stat
            StatsManager.incrementValue(player, StatsPM.ENTITIES_ANALYZED);
            
            // Check to see if any scan triggers need to be run for the entity
            checkScanTriggers(player, type);
            
            // Sync the research/knowledge changes to the player's client if requested
            if (sync) {
                knowledge.sync(player); // Sync immediately, rather than scheduling, for snappy arcanometer response
            }
            return true;
        } else {
            return false;
        }
    }

    public static int setAllScanned(@Nullable ServerPlayerEntity player) {
        if (player == null) {
            return 0;
        }
        IPlayerKnowledge knowledge = PrimalMagicCapabilities.getKnowledge(player);
        if (knowledge == null) {
            return 0;
        }
        int count = 0;
        SimpleResearchKey key;
        ItemStack stack;
        
        // Iterate over all registered items in the game
        for (Item item : ForgeRegistries.ITEMS) {
            // Generate a research key for the itemstack and add that research to the player
            stack = new ItemStack(item);
            key = SimpleResearchKey.parseItemScan(stack);
            if (key != null && knowledge.addResearch(key)) {
                count++;
    
                // Determine how many observation points the itemstack is worth and add those to the player's knowledge
                int obsPoints = getObservationPoints(stack, player.getEntityWorld());
                if (obsPoints > 0) {
                    addKnowledge(player, IPlayerKnowledge.KnowledgeType.OBSERVATION, obsPoints, false);
                }
                
                // Check to see if any scan triggers need to be run for the item
                checkScanTriggers(player, item);
            }
        }
        
        // If any items were successfully scanned, sync the research/knowledge changes to the player's client
        if (count > 0) {
            knowledge.sync(player); // Sync immediately, rather than scheduling, for snappy arcanometer response
        }
        
        // Return the number of items successfully scanned
        return count;
    }

    private static int getObservationPoints(@Nonnull ItemStack stack, @Nonnull World world) {
        // Calculate observation points for the itemstack based on its affinities
        return getObservationPoints(AffinityManager.getInstance().getAffinityValues(stack, world));
    }
    
    private static int getObservationPoints(@Nonnull EntityType<?> type) {
        // TODO Get affinities from affinity manager for entity type
        SourceList affinities = new SourceList();
        return getObservationPoints(affinities);
    }
    
    private static int getObservationPoints(@Nonnull SourceList affinities) {
        if (affinities == null || affinities.isEmpty()) {
            return 0;
        }
        double total = 0.0D;
        for (Source source : affinities.getSources()) {
            // Not all sources are worth the same amount of observation points
            total += (affinities.getAmount(source) * source.getObservationMultiplier());
        }
        if (total > 0.0D) {
            total = Math.sqrt(total);
        }
        
        // Round up to ensure that any item with affinities generates at least one observation point
        return MathHelper.ceil(total);
    }
}
