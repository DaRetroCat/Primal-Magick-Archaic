package com.verdantartifice.primalmagick.datagen.affinities;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.verdantartifice.primalmagick.common.affinities.AffinityType;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.sources.SourceList;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionBonusAffinityBuilder {
    protected final ResourceLocation targetId;
    protected SourceList bonusValues;
    
    protected PotionBonusAffinityBuilder(@Nonnull Potion target) {
        this.targetId = target.getRegistryName();
    }
    
    public static PotionBonusAffinityBuilder potionBonusAffinity(@Nonnull Potion target) {
        return new PotionBonusAffinityBuilder(target);
    }
    
    public PotionBonusAffinityBuilder bonus(SourceList bonusValues) {
        this.bonusValues = bonusValues.copy();
        return this;
    }
    
    public PotionBonusAffinityBuilder bonus(Source source, int amount) {
        if (this.bonusValues == null) {
            this.bonusValues = new SourceList();
        }
        this.bonusValues.add(source, amount);
        return this;
    }
    
    private void validate(ResourceLocation id) {
        if (this.targetId == null) {
            throw new IllegalStateException("No target potion for affinity " + id.toString());
        }
        if (!ForgeRegistries.POTION_TYPES.containsKey(this.targetId)) {
            throw new IllegalStateException("Unknown target potion " + this.targetId.toString() + " for affinity " + id.toString());
        }
    }
    
    public void build(Consumer<IFinishedAffinity> consumer) {
        this.build(consumer, this.targetId);
    }
    
    public void build(Consumer<IFinishedAffinity> consumer, String name) {
        this.build(consumer, new ResourceLocation(name));
    }
    
    public void build(Consumer<IFinishedAffinity> consumer, ResourceLocation id) {
        this.validate(id);
        consumer.accept(new PotionBonusAffinityBuilder.Result(id, this.targetId, this.bonusValues));
    }
    
    public static class Result implements IFinishedAffinity {
        protected final ResourceLocation id;
        protected final ResourceLocation targetId;
        protected final SourceList bonusValues;
        
        public Result(@Nonnull ResourceLocation id, @Nonnull ResourceLocation targetId, @Nullable SourceList bonusValues) {
            this.id = id;
            this.targetId = targetId;
            this.bonusValues = bonusValues;
        }

        @Override
        public AffinityType getType() {
            return AffinityType.POTION_BONUS;
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public void serialize(JsonObject json) {
            json.addProperty("target", this.targetId.toString());
            if (this.bonusValues != null) {
                json.add("bonus", this.bonusValues.serializeJson());
            }
        }
    }
}
