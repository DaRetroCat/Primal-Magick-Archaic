package com.verdantartifice.primalmagic.common.runes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.verdantartifice.primalmagic.PrimalMagic;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

/**
 * Primary access point for rune-related methods.  Also stores registered rune combinations in a
 * static registry.
 * 
 * @author Daedalus4096
 */
public class RuneManager {
    protected static final Map<Enchantment, List<Rune>> REGISTRY = new HashMap<>();
    protected static final Map<VerbRune, Set<Enchantment>> VERB_ENCHANTMENTS = new HashMap<>();
    protected static final Map<NounRune, Set<Enchantment>> NOUN_ENCHANTMENTS = new HashMap<>();
    protected static final Map<SourceRune, Set<Enchantment>> SOURCE_ENCHANTMENTS = new HashMap<>();
    
    public static void registerRuneEnchantment(@Nullable Enchantment enchantment, @Nullable VerbRune verb, @Nullable NounRune noun, @Nullable SourceRune source) {
        if (enchantment != null && verb != null && noun != null && source != null) {
            if (REGISTRY.containsKey(enchantment)) {
                throw new IllegalArgumentException("Rune enchantment already registered for " + enchantment.getRegistryName().toString());
            }
            REGISTRY.put(enchantment, Arrays.asList(verb, noun, source));
            VERB_ENCHANTMENTS.computeIfAbsent(verb, r -> new HashSet<>()).add(enchantment);
            NOUN_ENCHANTMENTS.computeIfAbsent(noun, r -> new HashSet<>()).add(enchantment);
            SOURCE_ENCHANTMENTS.computeIfAbsent(source, r -> new HashSet<>()).add(enchantment);
        }
    }
    
    /**
     * Calculate the map of enchantments and corresponding levels which are created by applying the given
     * combination of runes to the given item stack.
     * 
     * @param runes the runes for which to determine enchantments
     * @param stack the item stack to which the given runes are to be applied
     * @return the map of rune enchantments and the levels at which they should be applied
     */
    @Nonnull
    public static Map<Enchantment, Integer> getRuneEnchantments(@Nullable NonNullList<Rune> runes, @Nullable ItemStack stack) {
        if (runes == null || runes.isEmpty() || stack == null || stack.isEmpty()) {
            return Collections.emptyMap();
        }
        
        Map<Enchantment, Integer> retVal = new HashMap<>();
        
        // Separate out the given runes by type
        List<VerbRune> verbRunes = runes.stream().filter(r -> r.getType() == RuneType.VERB).map(r -> (VerbRune)r).collect(Collectors.toList());
        List<NounRune> nounRunes = runes.stream().filter(r -> r.getType() == RuneType.NOUN).map(r -> (NounRune)r).collect(Collectors.toList());
        List<SourceRune> sourceRunes = runes.stream().filter(r -> r.getType() == RuneType.SOURCE).map(r -> (SourceRune)r).collect(Collectors.toList());
        int powerLevel = 1 + (int)runes.stream().filter(r -> r.getType() == RuneType.POWER).count();
        
        // Iterate through each combination of verb, noun, and source to find enchantments
        for (VerbRune verb : verbRunes) {
            for (NounRune noun : nounRunes) {
                for (SourceRune source : sourceRunes) {
                    // Intersect the sets of enchantments for each verb, noun, and source combination
                    Set<Enchantment> possibleEnchantments = new HashSet<>(VERB_ENCHANTMENTS.getOrDefault(verb, new HashSet<>()));   // Needs to be mutable
                    possibleEnchantments.retainAll(NOUN_ENCHANTMENTS.getOrDefault(noun, Collections.emptySet()));
                    possibleEnchantments.retainAll(SOURCE_ENCHANTMENTS.getOrDefault(source, Collections.emptySet()));
                    
                    for (Enchantment possible : possibleEnchantments) {
                        // If the rune enchantment can be applied to the given item stack, is compatible with 
                        // those already found, and it meets the minimum power level, add it to the result set
                        if ( possible.canApply(stack) && 
                             EnchantmentHelper.areAllCompatibleWith(retVal.keySet(), possible) && 
                             powerLevel >= possible.getMinLevel() ) {
                            retVal.put(possible, Math.min(powerLevel, possible.getMaxLevel()));
                        }
                    }
                }
            }
        }
        
        return retVal;
    }
    
    /**
     * Merge the two enchantment maps, taking the stronger one in case of a collision.
     * 
     * @param map1 the first enchantment map
     * @param map2 the second enchantment map
     * @return the merged enchantment map
     */
    public static Map<Enchantment, Integer> mergeEnchantments(@Nonnull Map<Enchantment, Integer> map1, @Nonnull Map<Enchantment, Integer> map2) {
        Map<Enchantment, Integer> retVal = new HashMap<>();
        
        // Union the two enchantment sets
        Set<Enchantment> keySet = new HashSet<Enchantment>();
        keySet.addAll(map1.keySet());
        keySet.addAll(map2.keySet());
        
        // Add the more powerful one to the output map
        for (Enchantment ench : keySet) {
            retVal.put(ench, Math.max(map1.getOrDefault(ench, 0), map2.getOrDefault(ench, 0)));
        }
        return retVal;
    }
    
    /**
     * Get the list of runes that have been applied to this item stack.
     * 
     * @param stack the item stack to query
     * @return the list of runes applied to the item; empty if none
     */
    @Nonnull
    public static List<Rune> getRunes(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.hasTag()) {
            return Collections.emptyList();
        }
        
        List<Rune> retVal = new ArrayList<>();
        ListNBT tagList = stack.getTag().getList(PrimalMagic.MODID + ":runes", Constants.NBT.TAG_STRING);
        for (int index = 0; index < tagList.size(); index++) {
            String tagStr = tagList.getString(index);
            Rune rune = Rune.getRune(new ResourceLocation(tagStr));
            if (rune != null) {
                retVal.add(rune);
            }
        }
        
        return retVal;
    }
    
    /**
     * Sets the list of runes applied to this item stack.
     * 
     * @param stack the item stack to modify
     * @param runes the list of runes to apply to the item
     */
    public static void setRunes(@Nullable ItemStack stack, @Nullable List<Rune> runes) {
        if (stack != null && !stack.isEmpty() && runes != null && !runes.isEmpty()) {
            ListNBT tagList = new ListNBT();
            for (Rune rune : runes) {
                if (rune != null) {
                    tagList.add(StringNBT.valueOf(rune.getId().toString()));
                }
            }
            stack.setTagInfo(PrimalMagic.MODID + ":runes", tagList);
        }
    }
}