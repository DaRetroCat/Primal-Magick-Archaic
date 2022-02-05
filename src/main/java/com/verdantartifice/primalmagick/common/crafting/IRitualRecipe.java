package com.verdantartifice.primalmagick.common.crafting;

import com.verdantartifice.primalmagick.common.blocks.BlocksPM;
import com.verdantartifice.primalmagick.common.research.SimpleResearchKey;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.NonNullList;

/**
 * Crafting recipe interface for a ritual recipe.  Ritual recipes are performed across multiple
 * blocks, with a central altar surrounded by multiple offering pedestals and props.  They also
 * have research requirements and optional mana costs.
 * 
 * @author Daedalus4096
 */
public interface IRitualRecipe extends ICraftingRecipe, IHasManaCost {
    /**
     * Get the required research for the recipe.
     * 
     * @return the required research for the recipe
     */
    public SimpleResearchKey getRequiredResearch();
    
    /**
     * Get the list of props for the recipe.
     * 
     * @return the prop list fort he recipe
     */
    public NonNullList<BlockIngredient> getProps();
    
    /**
     * Get the instability rating of the recipe.
     * 
     * @return the instability rating of the recipe
     */
    public int getInstability();
    
    default IRecipeType<?> getType() {
        return RecipeTypesPM.RITUAL;
    }
    
    @Override
    default boolean isDynamic() {
        // Return true to keep ritual recipes from showing up in the vanilla recipe book
        return true;
    }
    
    default ItemStack getIcon() {
        return new ItemStack(BlocksPM.RITUAL_ALTAR.get());
    }
}
