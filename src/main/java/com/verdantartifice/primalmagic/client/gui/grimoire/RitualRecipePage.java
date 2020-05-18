package com.verdantartifice.primalmagic.client.gui.grimoire;

import java.awt.Color;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.verdantartifice.primalmagic.client.gui.GrimoireScreen;
import com.verdantartifice.primalmagic.client.gui.widgets.grimoire.BlockIngredientWidget;
import com.verdantartifice.primalmagic.client.gui.widgets.grimoire.IngredientWidget;
import com.verdantartifice.primalmagic.client.gui.widgets.grimoire.ItemStackWidget;
import com.verdantartifice.primalmagic.client.gui.widgets.grimoire.ManaCostSummaryWidget;
import com.verdantartifice.primalmagic.common.crafting.BlockIngredient;
import com.verdantartifice.primalmagic.common.crafting.RitualRecipe;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Grimoire page showing a ritual recipe.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class RitualRecipePage extends AbstractRecipePage {
    protected static final int ITEMS_PER_ROW = 7;
    
    protected RitualRecipe recipe;
    
    public RitualRecipePage(RitualRecipe recipe) {
        this.recipe = recipe;
    }
    
    @Override
    protected String getTitleTranslationKey() {
        return "primalmagic.grimoire.ritual_recipe_header";
    }

    @Override
    public void initWidgets(GrimoireScreen screen, int side, int x, int y) {
        int indent = 124;
        int overlayWidth = 52;
        int deltaX = 0;
        Minecraft mc = Minecraft.getInstance();

        y += 27;    // Make room for page title
        
        // Init ingredient widgets
        if (!this.recipe.getIngredients().isEmpty()) {
            y += mc.fontRenderer.FONT_HEIGHT;   // Make room for section header
            for (Ingredient ingredient : this.recipe.getIngredients()) {
                screen.addWidgetToScreen(new IngredientWidget(ingredient, x + 8 + deltaX + (side * 144), y));
                deltaX += 18;
                if (deltaX >= (ITEMS_PER_ROW * 18)) {
                    deltaX = 0;
                    y += 18;
                }
            }
        }
        
        // Init prop widgets
        if (!this.recipe.getProps().isEmpty()) {
            y += mc.fontRenderer.FONT_HEIGHT;   // Make room for section header
            for (BlockIngredient prop : this.recipe.getProps()) {
                screen.addWidgetToScreen(new BlockIngredientWidget(prop, x + 8 + deltaX + (side * 144), y));
                deltaX += 18;
                if (deltaX >= (ITEMS_PER_ROW * 18)) {
                    deltaX = 0;
                    y += 18;
                }
            }
        }
        
        // Render output stack
        ItemStack output = this.recipe.getRecipeOutput();
        screen.addWidgetToScreen(new ItemStackWidget(output, x + 27 + (side * 140) + (indent / 2) - (overlayWidth / 2), y + 30, false));
        
        // Add mana cost summary widget
        screen.addWidgetToScreen(new ManaCostSummaryWidget(this.recipe.getManaCosts(), x + 75 + (side * 140) + (indent / 2) - (overlayWidth / 2), y + 30));
    }

    @Override
    public void render(int side, int x, int y, int mouseX, int mouseY) {
        super.render(side, x, y, mouseX, mouseY);
        y += 53;
        
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.translatef(0.0F, 0.0F, 1.0F);  // Bump up slightly in the Z-order to prevent the underline from being swallowed
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft mc = Minecraft.getInstance();

        // Render ingredients section header
        if (!this.recipe.getIngredients().isEmpty()) {
            ITextComponent leadComponent = new TranslationTextComponent("primalmagic.grimoire.ritual_offerings_header").applyTextStyle(TextFormatting.UNDERLINE);
            mc.fontRenderer.drawString(leadComponent.getFormattedText(), x - 3 + (side * 140), y - 6, Color.BLACK.getRGB());
            y += mc.fontRenderer.FONT_HEIGHT;
            y += (1 + (this.recipe.getIngredients().size() / ITEMS_PER_ROW)) * 18;  // Make room for ingredient widgets
        }
        
        // Render props section header
        if (!this.recipe.getProps().isEmpty()) {
            ITextComponent leadComponent = new TranslationTextComponent("primalmagic.grimoire.ritual_props_header").applyTextStyle(TextFormatting.UNDERLINE);
            mc.fontRenderer.drawString(leadComponent.getFormattedText(), x - 3 + (side * 140), y - 6, Color.BLACK.getRGB());
            y += mc.fontRenderer.FONT_HEIGHT;
            y += (1 + (this.recipe.getProps().size() / ITEMS_PER_ROW)) * 18;    // Make room for prop widgets
        }
        
        RenderSystem.popMatrix();
    }
}