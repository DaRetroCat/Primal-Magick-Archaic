package com.verdantartifice.primalmagick.client.gui.grimoire;

import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Grimoire page showing a shaped vanilla recipe.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class ShapedRecipePage extends AbstractShapedRecipePage<ShapedRecipe> {
    public ShapedRecipePage(ShapedRecipe recipe) {
        super(recipe);
    }

    @Override
    protected String getTitleTranslationKey() {
        return "primalmagick.grimoire.shaped_recipe_header";
    }
}
