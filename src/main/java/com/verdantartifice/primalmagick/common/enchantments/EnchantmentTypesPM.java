package com.verdantartifice.primalmagick.common.enchantments;

import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.common.wands.IStaff;
import com.verdantartifice.primalmagick.common.wands.IWand;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.ShieldItem;

/**
 * Definition of extended enchantment types for the mod.
 * 
 * @author Daedalus4096
 */
public class EnchantmentTypesPM {
    public static final EnchantmentType WAND = EnchantmentType.create(PrimalMagick.MODID + ":wand", i -> i instanceof IWand);
    public static final EnchantmentType STAFF = EnchantmentType.create(PrimalMagick.MODID + ":staff", i -> i instanceof IStaff);
    public static final EnchantmentType SHIELD = EnchantmentType.create(PrimalMagick.MODID + ":shield", i -> i instanceof ShieldItem);
}
