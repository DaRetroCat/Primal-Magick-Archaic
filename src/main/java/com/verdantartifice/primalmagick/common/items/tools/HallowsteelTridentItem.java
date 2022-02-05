package com.verdantartifice.primalmagick.common.items.tools;

import com.verdantartifice.primalmagick.common.entities.projectiles.AbstractTridentEntity;
import com.verdantartifice.primalmagick.common.entities.projectiles.HallowsteelTridentEntity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Definition for a trident made of the magical metal hallowsteel.
 * 
 * @author Daedalus4096
 */
public class HallowsteelTridentItem extends AbstractTieredTridentItem {
    public HallowsteelTridentItem(Item.Properties properties) {
        super(ItemTierPM.HALLOWSTEEL, properties);
    }

    @Override
    protected AbstractTridentEntity getThrownEntity(World world, LivingEntity thrower, ItemStack stack) {
        return new HallowsteelTridentEntity(world, thrower, stack);
    }
}
