package com.verdantartifice.primalmagick.common.items.tools;

import com.verdantartifice.primalmagick.common.entities.projectiles.AbstractTridentEntity;
import com.verdantartifice.primalmagick.common.entities.projectiles.PrimaliteTridentEntity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Definition for a trident made of the magical metal primalite.
 * 
 * @author Daedalus4096
 */
public class PrimaliteTridentItem extends AbstractTieredTridentItem {
    public PrimaliteTridentItem(Item.Properties properties) {
        super(ItemTierPM.PRIMALITE, properties);
    }

    @Override
    protected AbstractTridentEntity getThrownEntity(World world, LivingEntity thrower, ItemStack stack) {
        return new PrimaliteTridentEntity(world, thrower, stack);
    }
}
