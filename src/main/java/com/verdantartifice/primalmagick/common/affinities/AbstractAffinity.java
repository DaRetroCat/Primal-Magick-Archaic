package com.verdantartifice.primalmagick.common.affinities;

import javax.annotation.Nonnull;

import com.verdantartifice.primalmagick.common.sources.SourceList;

import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractAffinity implements IAffinity {
    protected ResourceLocation targetId;
    protected SourceList totalCache;

    protected AbstractAffinity(ResourceLocation target) {
        this.targetId = target;
    }
    
    @Override
    public ResourceLocation getTarget() {
        return this.targetId;
    }

    @Override
    public SourceList getTotal(@Nonnull RecipeManager recipeManager) {
        if (this.totalCache == null) {
            this.totalCache = this.calculateTotal(recipeManager);
        }
        return this.totalCache.copy();
    }
    
    protected abstract SourceList calculateTotal(@Nonnull RecipeManager recipeManager);
}
