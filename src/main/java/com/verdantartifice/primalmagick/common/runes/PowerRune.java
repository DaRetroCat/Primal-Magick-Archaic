package com.verdantartifice.primalmagick.common.runes;

import javax.annotation.Nonnull;

import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;

/**
 * Definition of a power rune data structure.  An optional rune to boost imbued enchantments.
 * 
 * @author Daedalus4096
 */
public class PowerRune extends Rune {
    public PowerRune(@Nonnull String tag) {
        super(tag, Rarity.UNCOMMON, true);
    }
    
    public PowerRune(@Nonnull ResourceLocation id) {
        super(id, Rarity.UNCOMMON, true);
    }
    
    @Override
    public RuneType getType() {
        return RuneType.POWER;
    }
}
