package com.verdantartifice.primalmagick.common.tags;

import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

/**
 * Collection of custom-defined Forge extension item tags.  Used to determine tag contents and for
 * data file generation.
 * 
 * @author Daedalus4096
 */
public class ItemTagsForgeExt {
    public static final IOptionalNamedTag<Item> DUSTS_IRON = tag("dusts/iron");
    public static final IOptionalNamedTag<Item> DUSTS_GOLD = tag("dusts/gold");
    public static final IOptionalNamedTag<Item> NUGGETS_QUARTZ = tag("nuggets/quartz");

    private static IOptionalNamedTag<Item> tag(String name) {
    	return ItemTags.createOptional(new ResourceLocation("forge", name));
    }
}
