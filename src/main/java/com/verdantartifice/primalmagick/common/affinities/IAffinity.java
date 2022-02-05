package com.verdantartifice.primalmagick.common.affinities;

import javax.annotation.Nonnull;

import com.verdantartifice.primalmagick.common.sources.SourceList;

import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

/**
 * Primary interface for a data-defined affinity entry.
 * 
 * @author Daedalus4096
 */
public interface IAffinity {
    ResourceLocation getTarget();
    
    AffinityType getType();
    
    IAffinitySerializer<?> getSerializer();
    
    SourceList getTotal(@Nonnull RecipeManager recipeManager);
}
