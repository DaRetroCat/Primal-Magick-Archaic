package com.verdantartifice.primalmagick.common.items.misc;

import java.util.List;

import com.verdantartifice.primalmagick.PrimalMagick;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Item definition for a hallowed orb.  A hallowed orb unlocks the Hallowed source when scanned on an
 * analysis table or with an arcanometer.
 * 
 * @author Daedalus4096
 */
public class HallowedOrbItem extends Item {
    public HallowedOrbItem() {
        super(new Item.Properties().group(PrimalMagick.ITEM_GROUP).rarity(Rarity.RARE));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("tooltip.primalmagick.hallowed_orb.1").mergeStyle(TextFormatting.WHITE, TextFormatting.ITALIC));
        tooltip.add(new TranslationTextComponent("tooltip.primalmagick.hallowed_orb.2").mergeStyle(TextFormatting.WHITE, TextFormatting.ITALIC));
    }
}
