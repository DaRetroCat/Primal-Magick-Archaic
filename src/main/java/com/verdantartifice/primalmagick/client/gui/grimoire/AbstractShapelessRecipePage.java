package com.verdantartifice.primalmagick.client.gui.grimoire;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.verdantartifice.primalmagick.client.gui.GrimoireScreen;
import com.verdantartifice.primalmagick.client.gui.widgets.grimoire.IngredientWidget;
import com.verdantartifice.primalmagick.client.gui.widgets.grimoire.ItemStackWidget;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Base class for grimoire shapeless recipe pages.
 * 
 * @author Daedalus4096
 * @param <T> type of recipe, e.g. ShapelessArcaneRecipe
 */
@OnlyIn(Dist.CLIENT)
public abstract class AbstractShapelessRecipePage<T extends IRecipe<?>> extends AbstractRecipePage {
    protected T recipe;
    
    public AbstractShapelessRecipePage(T recipe) {
        this.recipe = recipe;
    }

    @Override
    public void initWidgets(GrimoireScreen screen, int side, int x, int y) {
        int indent = 124;
        int overlayWidth = 51;

        // Render ingredient stacks
        List<Ingredient> ingredients = this.recipe.getIngredients();
        for (int index = 0; index < Math.min(ingredients.size(), 9); index++) {
            Ingredient ingredient = ingredients.get(index);
            if (ingredient != null) {
                screen.addWidgetToScreen(new IngredientWidget(ingredient, x - 5 + (side * 140) + (indent / 2) - (overlayWidth / 2) + ((index % 3) * 32), y + 67 + ((index / 3) * 32)));
            }
        }
        
        // Render output stack
        ItemStack output = this.recipe.getRecipeOutput();
        screen.addWidgetToScreen(new ItemStackWidget(output, x + 27 + (side * 140) + (indent / 2) - (overlayWidth / 2), y + 30, false));
    }
    
    @Override
    public void render(MatrixStack matrixStack, int side, int x, int y, int mouseX, int mouseY) {
        super.render(matrixStack, side, x, y, mouseX, mouseY);
        y += 53;
        
        int indent = 124;
        int overlayWidth = 51;
        int overlayHeight = 51;
        
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);        
        Minecraft.getInstance().getTextureManager().bindTexture(OVERLAY);
        
        // Render overlay background
        matrixStack.push();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        matrixStack.translate(x - 6 + (side * 140) + (indent / 2), y + 49 + (overlayHeight / 2), 0.0F);
        matrixStack.scale(2.0F, 2.0F, 1.0F);
        this.blit(matrixStack, -(overlayWidth / 2), -(overlayHeight / 2), 0, 0, overlayWidth, overlayHeight);
        matrixStack.pop();
    }
}
