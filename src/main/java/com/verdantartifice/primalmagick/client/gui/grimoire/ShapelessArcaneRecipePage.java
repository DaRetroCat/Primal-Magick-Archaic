package com.verdantartifice.primalmagick.client.gui.grimoire;

import com.verdantartifice.primalmagick.client.gui.GrimoireScreen;
import com.verdantartifice.primalmagick.client.gui.widgets.grimoire.ManaCostSummaryWidget;
import com.verdantartifice.primalmagick.common.crafting.ShapelessArcaneRecipe;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Grimoire page showing a shapeless arcane recipe.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class ShapelessArcaneRecipePage extends AbstractShapelessRecipePage<ShapelessArcaneRecipe> {
    public ShapelessArcaneRecipePage(ShapelessArcaneRecipe recipe) {
        super(recipe);
    }
    
    @Override
    protected String getTitleTranslationKey() {
        return "primalmagick.grimoire.shapeless_arcane_recipe_header";
    }
    
    @Override
    public void initWidgets(GrimoireScreen screen, int side, int x, int y) {
        // Add base page widgets
        super.initWidgets(screen, side, x, y);
        
        // Add mana cost summary widget
        int indent = 124;
        int overlayWidth = 52;
        screen.addWidgetToScreen(new ManaCostSummaryWidget(this.recipe.getManaCosts(), x + 75 + (side * 140) + (indent / 2) - (overlayWidth / 2), y + 30));
    }
}
