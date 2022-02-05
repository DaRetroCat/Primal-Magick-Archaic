package com.verdantartifice.primalmagick.common.items.wands;

import com.verdantartifice.primalmagick.common.wands.WandCore;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;

/**
 * Item definition for a staff core.  May be used to construct modular staves.
 * 
 * @author Daedalus4096
 */
public class StaffCoreItem extends Item {
    protected final WandCore core;

    public StaffCoreItem(WandCore core, Properties properties) {
        super(properties);
        this.core = core;
    }

    public WandCore getWandCore() {
        return this.core;
    }
    
    @Override
    public Rarity getRarity(ItemStack stack) {
        return this.core.getRarity();
    }
}
