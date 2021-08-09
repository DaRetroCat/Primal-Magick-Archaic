package com.verdantartifice.primalmagic.common.items.tools;

import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;
import com.verdantartifice.primalmagic.client.renderers.itemstack.ForbiddenTridentISTER;
import com.verdantartifice.primalmagic.common.enchantments.EnchantmentsPM;
import com.verdantartifice.primalmagic.common.entities.projectiles.AbstractTridentEntity;
import com.verdantartifice.primalmagic.common.entities.projectiles.ForbiddenTridentEntity;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;

/**
 * Definition for a trident made of the magical metal hexium which comes pre-enchanted with Rending.
 * 
 * @author Daedalus4096
 */
public class ForbiddenTridentItem extends AbstractTieredTridentItem {
    protected final RenderProperties renderProperties;
    
    public ForbiddenTridentItem(Item.Properties properties) {
        super(ItemTierPM.HEXIUM, properties);
        this.renderProperties = new RenderProperties();
    }

    @Override
    protected AbstractTridentEntity getThrownEntity(Level world, LivingEntity thrower, ItemStack stack) {
        return new ForbiddenTridentEntity(world, thrower, stack);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = new ItemStack(this);
        EnchantmentHelper.setEnchantments(ImmutableMap.of(EnchantmentsPM.RENDING.get(), 2), stack);
        return stack;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        // Populate the creative pane with a pre-enchanted trident
        if (this.allowdedIn(group)) {
            items.add(this.getDefaultInstance());
        }
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(this.getRenderProperties());
    }
    
    public RenderProperties getRenderProperties() {
        return this.renderProperties;
    }
    
    public static class RenderProperties implements IItemRenderProperties {
        protected final BlockEntityWithoutLevelRenderer renderer;
        
        public RenderProperties() {
            this.renderer = new ForbiddenTridentISTER();
        }

        @Override
        public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
            return renderer;
        }
    }
}