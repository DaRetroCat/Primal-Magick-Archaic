package com.verdantartifice.primalmagick.common.misc;

import com.verdantartifice.primalmagick.common.items.ItemsPM;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

/**
 * Definition of the mod's item group.  Creates a custom creative tab for this mod's items.
 * 
 * @author Daedalus4096
 */
public class ItemGroupPM extends ItemGroup {
    public ItemGroupPM() {
        super("primalmagick");
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ItemsPM.GRIMOIRE.get());
    }
}
