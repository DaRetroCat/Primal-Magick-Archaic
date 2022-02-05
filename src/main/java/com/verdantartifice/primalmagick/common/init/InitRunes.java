package com.verdantartifice.primalmagick.common.init;

import com.verdantartifice.primalmagick.common.enchantments.EnchantmentsPM;
import com.verdantartifice.primalmagick.common.research.CompoundResearchKey;
import com.verdantartifice.primalmagick.common.runes.Rune;
import com.verdantartifice.primalmagick.common.runes.RuneManager;

import net.minecraft.enchantment.Enchantments;

/**
 * Point of registration for mod rune enchantment combinations.
 * 
 * @author Daedalus4096
 */
public class InitRunes {
    public static void initRuneEnchantments() {
        // Register rune combinations for vanilla enchantments
        RuneManager.registerRuneEnchantment(Enchantments.PROTECTION, Rune.PROTECT, Rune.SELF, Rune.EARTH);
        RuneManager.registerRuneEnchantment(Enchantments.FIRE_PROTECTION, Rune.PROTECT, Rune.SELF, Rune.INFERNAL);
        RuneManager.registerRuneEnchantment(Enchantments.FEATHER_FALLING, Rune.PROJECT, Rune.ITEM, Rune.SKY);
        RuneManager.registerRuneEnchantment(Enchantments.BLAST_PROTECTION, Rune.PROTECT, Rune.SELF, Rune.VOID);
        RuneManager.registerRuneEnchantment(Enchantments.PROJECTILE_PROTECTION, Rune.PROTECT, Rune.SELF, Rune.SKY);
        RuneManager.registerRuneEnchantment(Enchantments.RESPIRATION, Rune.PROJECT, Rune.ITEM, Rune.SKY);
        RuneManager.registerRuneEnchantment(Enchantments.AQUA_AFFINITY, Rune.PROJECT, Rune.ITEM, Rune.SEA);
        RuneManager.registerRuneEnchantment(Enchantments.THORNS, Rune.PROJECT, Rune.CREATURE, Rune.BLOOD);
        RuneManager.registerRuneEnchantment(Enchantments.DEPTH_STRIDER, Rune.PROJECT, Rune.ITEM, Rune.SEA);
        RuneManager.registerRuneEnchantment(Enchantments.FROST_WALKER, Rune.PROJECT, Rune.AREA, Rune.SEA);
        RuneManager.registerRuneEnchantment(Enchantments.SHARPNESS, Rune.PROJECT, Rune.ITEM, Rune.EARTH);
        RuneManager.registerRuneEnchantment(Enchantments.KNOCKBACK, Rune.PROJECT, Rune.CREATURE, Rune.EARTH);
        RuneManager.registerRuneEnchantment(Enchantments.FIRE_ASPECT, Rune.PROJECT, Rune.ITEM, Rune.INFERNAL);
        RuneManager.registerRuneEnchantment(Enchantments.LOOTING, Rune.PROJECT, Rune.ITEM, Rune.MOON);
        RuneManager.registerRuneEnchantment(Enchantments.SWEEPING, Rune.PROJECT, Rune.AREA, Rune.SKY);
        RuneManager.registerRuneEnchantment(Enchantments.EFFICIENCY, Rune.PROJECT, Rune.ITEM, Rune.SKY);
        RuneManager.registerRuneEnchantment(Enchantments.SILK_TOUCH, Rune.PROJECT, Rune.ITEM, Rune.SEA);
        RuneManager.registerRuneEnchantment(Enchantments.UNBREAKING, Rune.PROTECT, Rune.ITEM, Rune.EARTH);
        RuneManager.registerRuneEnchantment(Enchantments.FORTUNE, Rune.PROJECT, Rune.ITEM, Rune.MOON);
        RuneManager.registerRuneEnchantment(Enchantments.POWER, Rune.PROJECT, Rune.ITEM, Rune.SKY);
        RuneManager.registerRuneEnchantment(Enchantments.PUNCH, Rune.PROJECT, Rune.CREATURE, Rune.EARTH);
        RuneManager.registerRuneEnchantment(Enchantments.FLAME, Rune.PROJECT, Rune.ITEM, Rune.INFERNAL);
        RuneManager.registerRuneEnchantment(Enchantments.INFINITY, Rune.SUMMON, Rune.ITEM, Rune.SKY);
        RuneManager.registerRuneEnchantment(Enchantments.LUCK_OF_THE_SEA, Rune.PROJECT, Rune.ITEM, Rune.MOON);
        RuneManager.registerRuneEnchantment(Enchantments.LURE, Rune.SUMMON, Rune.CREATURE, Rune.SEA);
        RuneManager.registerRuneEnchantment(Enchantments.LOYALTY, Rune.SUMMON, Rune.ITEM, Rune.SEA);
        RuneManager.registerRuneEnchantment(Enchantments.IMPALING, Rune.PROJECT, Rune.ITEM, Rune.SEA);
        RuneManager.registerRuneEnchantment(Enchantments.RIPTIDE, Rune.SUMMON, Rune.SELF, Rune.SEA);
        RuneManager.registerRuneEnchantment(Enchantments.CHANNELING, Rune.SUMMON, Rune.AREA, Rune.SKY);
        RuneManager.registerRuneEnchantment(Enchantments.MULTISHOT, Rune.SUMMON, Rune.ITEM, Rune.SKY);
        RuneManager.registerRuneEnchantment(Enchantments.QUICK_CHARGE, Rune.PROJECT, Rune.ITEM, Rune.SKY);
        RuneManager.registerRuneEnchantment(Enchantments.PIERCING, Rune.PROJECT, Rune.ITEM, Rune.EARTH);
        RuneManager.registerRuneEnchantment(Enchantments.MENDING, Rune.ABSORB, Rune.ITEM, Rune.SUN);
        
        // Register rune combinations for mod enchantments
        RuneManager.registerRuneEnchantment(EnchantmentsPM.LIFESTEAL.get(), Rune.ABSORB, Rune.SELF, Rune.BLOOD);
        RuneManager.registerRuneEnchantment(EnchantmentsPM.ENDERLOCK.get(), Rune.DISPEL, Rune.CREATURE, Rune.VOID);
        RuneManager.registerRuneEnchantment(EnchantmentsPM.JUDGMENT.get(), Rune.PROJECT, Rune.ITEM, Rune.HALLOWED);
        RuneManager.registerRuneEnchantment(EnchantmentsPM.ENDERPORT.get(), Rune.SUMMON, Rune.SELF, Rune.VOID);
        RuneManager.registerRuneEnchantment(EnchantmentsPM.REGROWTH.get(), Rune.ABSORB, Rune.ITEM, Rune.HALLOWED);
        RuneManager.registerRuneEnchantment(EnchantmentsPM.AEGIS.get(), Rune.PROTECT, Rune.SELF, Rune.HALLOWED);
        RuneManager.registerRuneEnchantment(EnchantmentsPM.MANA_EFFICIENCY.get(), Rune.DISPEL, Rune.ITEM, Rune.VOID);
        RuneManager.registerRuneEnchantment(EnchantmentsPM.SPELL_POWER.get(), Rune.PROJECT, Rune.ITEM, Rune.VOID);
        RuneManager.registerRuneEnchantment(EnchantmentsPM.TREASURE.get(), Rune.PROJECT, Rune.ITEM, Rune.MOON);
        RuneManager.registerRuneEnchantment(EnchantmentsPM.BLUDGEONING.get(), Rune.PROJECT, Rune.ITEM, Rune.EARTH);
        RuneManager.registerRuneEnchantment(EnchantmentsPM.REVERBERATION.get(), Rune.PROJECT, Rune.AREA, Rune.EARTH, CompoundResearchKey.from(true, "MASTER_RUNEWORKING", "PRIMAL_SHOVEL"));
        RuneManager.registerRuneEnchantment(EnchantmentsPM.BOUNTY.get(), Rune.SUMMON, Rune.AREA, Rune.SEA, CompoundResearchKey.from(true, "MASTER_RUNEWORKING", "PRIMAL_FISHING_ROD"));
        RuneManager.registerRuneEnchantment(EnchantmentsPM.DISINTEGRATION.get(), Rune.PROJECT, Rune.AREA, Rune.SKY, CompoundResearchKey.from(true, "MASTER_RUNEWORKING", "PRIMAL_AXE"));
        RuneManager.registerRuneEnchantment(EnchantmentsPM.LUCKY_STRIKE.get(), Rune.SUMMON, Rune.ITEM, Rune.MOON, CompoundResearchKey.from(true, "MASTER_RUNEWORKING", "PRIMAL_PICKAXE"));
        RuneManager.registerRuneEnchantment(EnchantmentsPM.RENDING.get(), Rune.PROJECT, Rune.CREATURE, Rune.BLOOD, CompoundResearchKey.from(true, "MASTER_RUNEWORKING", "FORBIDDEN_TRIDENT"));
        RuneManager.registerRuneEnchantment(EnchantmentsPM.SOULPIERCING.get(), Rune.ABSORB, Rune.CREATURE, Rune.INFERNAL, CompoundResearchKey.from(true, "MASTER_RUNEWORKING", "FORBIDDEN_BOw"));
        RuneManager.registerRuneEnchantment(EnchantmentsPM.ESSENCE_THIEF.get(), Rune.SUMMON, Rune.ITEM, Rune.VOID, CompoundResearchKey.from(true, "MASTER_RUNEWORKING", "FORBIDDEN_SWORD"));
    }
}
