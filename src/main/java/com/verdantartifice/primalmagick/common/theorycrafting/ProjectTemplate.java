package com.verdantartifice.primalmagick.common.theorycrafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.verdantartifice.primalmagick.common.capabilities.IPlayerKnowledge.KnowledgeType;
import com.verdantartifice.primalmagick.common.research.SimpleResearchKey;
import com.verdantartifice.primalmagick.common.stats.StatsManager;
import com.verdantartifice.primalmagick.common.stats.StatsPM;
import com.verdantartifice.primalmagick.common.util.WeightedRandomBag;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Class encapsulating a data-defined template for a theorycrafting project.  These templates determine
 * the parameters of concrete projects which are consumed by players for theory rewards.
 * 
 * @author Daedalus4096
 */
public class ProjectTemplate {
    protected ResourceLocation key;
    protected List<AbstractProjectMaterial> materialOptions = new ArrayList<>();
    protected SimpleResearchKey requiredResearch;
    protected Optional<Integer> requiredMaterialCountOverride = Optional.empty();
    protected Optional<Double> baseSuccessChanceOverride = Optional.empty();
    protected double rewardMultiplier = 0.25D;
    protected ResourceLocation aidBlock;
    
    protected ProjectTemplate(@Nonnull ResourceLocation key, @Nonnull List<AbstractProjectMaterial> materialOptions, @Nullable SimpleResearchKey requiredResearch,
            @Nonnull Optional<Integer> requiredMaterialCountOverride, @Nonnull Optional<Double> baseSuccessChanceOverride, double rewardMultiplier, @Nullable ResourceLocation aidBlock) {
        this.key = key;
        this.materialOptions = materialOptions;
        this.requiredResearch = requiredResearch;
        this.requiredMaterialCountOverride = requiredMaterialCountOverride;
        this.baseSuccessChanceOverride = baseSuccessChanceOverride;
        this.rewardMultiplier = rewardMultiplier;
        this.aidBlock = aidBlock;
    }

    public ResourceLocation getKey() {
        return this.key;
    }
    
    @Nullable
    public Project initialize(PlayerEntity player) {
        if (this.requiredResearch != null && !this.requiredResearch.isKnownByStrict(player)) {
            // Fail initialization to prevent use if the player doesn't have the right research unlocked
            return null;
        }
        
        // Randomly select materials to use from the bag of options, disallowing duplicates
        int attempts = 0;
        int maxMaterials = this.getRequiredMaterialCount(player);
        List<AbstractProjectMaterial> materials = new ArrayList<>();
        WeightedRandomBag<AbstractProjectMaterial> options = this.getMaterialOptions(player);
        while (materials.size() < maxMaterials && attempts < 1000) {
            attempts++;
            AbstractProjectMaterial material = options.getRandom(player.getRNG()).copy();
            if (!materials.contains(material)) {
                materials.add(material);
            }
        }
        
        // Create new initialized project
        return new Project(this.key, materials, this.getBaseSuccessChance(player), this.getTheoryPointReward(), this.aidBlock);
    }
    
    protected int getRequiredMaterialCount(PlayerEntity player) {
        return this.requiredMaterialCountOverride.orElseGet(() -> {
            // Get projects completed from stats and calculate based on that
            int completed = StatsManager.getValue(player, StatsPM.RESEARCH_PROJECTS_COMPLETED);
            return Math.min(4, 1 + (completed / 5));
        });
    }
    
    protected double getBaseSuccessChance(PlayerEntity player) {
        return this.baseSuccessChanceOverride.orElseGet(() -> {
            // Get projects completed from stats and calculate based on that
            int completed = StatsManager.getValue(player, StatsPM.RESEARCH_PROJECTS_COMPLETED);
            return Math.max(0.0D, 0.5D - (0.1D * (completed / 3)));
        });
    }
    
    @Nonnull
    protected WeightedRandomBag<AbstractProjectMaterial> getMaterialOptions(PlayerEntity player) {
        WeightedRandomBag<AbstractProjectMaterial> retVal = new WeightedRandomBag<>();
        for (AbstractProjectMaterial material : this.materialOptions) {
            if (material.hasRequiredResearch(player)) {
                retVal.add(material, material.getWeight());
            }
        }
        return retVal;
    }
    
    protected int getTheoryPointReward() {
        return (int)(KnowledgeType.THEORY.getProgression() * this.rewardMultiplier);
    }
    
    public static class Serializer implements IProjectTemplateSerializer {
        @Override
        public ProjectTemplate read(ResourceLocation templateId, JsonObject json) {
            String keyStr = json.getAsJsonPrimitive("key").getAsString();
            if (keyStr == null) {
                throw new JsonSyntaxException("Illegal key in project template JSON for " + templateId.toString());
            }
            ResourceLocation key = new ResourceLocation(keyStr);
            
            SimpleResearchKey requiredResearch = null;
            if (json.has("required_research")) {
                requiredResearch = SimpleResearchKey.parse(json.getAsJsonPrimitive("required_research").getAsString());
            }
            
            Optional<Integer> materialCountOverride = Optional.empty();
            if (json.has("required_material_count_override")) {
                materialCountOverride = Optional.of(Integer.valueOf(json.getAsJsonPrimitive("required_material_count_override").getAsInt()));
            }
            
            Optional<Double> baseSuccessChanceOverride = Optional.empty();
            if (json.has("base_success_chance_override")) {
                baseSuccessChanceOverride = Optional.of(Double.valueOf(json.getAsJsonPrimitive("base_success_chance_override").getAsDouble()));
            }
            
            double rewardMultiplier = json.getAsJsonPrimitive("reward_multiplier").getAsDouble();
            
            ResourceLocation aidBlock = null;
            if (json.has("aid_block")) {
                aidBlock = new ResourceLocation(json.getAsJsonPrimitive("aid_block").getAsString());
                if (!ForgeRegistries.BLOCKS.containsKey(aidBlock)) {
                    throw new JsonSyntaxException("Invalid aid block in project template JSON for " + templateId.toString());
                }
            }
            
            List<AbstractProjectMaterial> materials = new ArrayList<>();
            JsonArray materialsArray = json.getAsJsonArray("material_options");
            for (JsonElement materialElement : materialsArray) {
                try {
                    JsonObject materialObj = materialElement.getAsJsonObject();
                    IProjectMaterialSerializer<?> materialSerializer = TheorycraftManager.getMaterialSerializer(materialObj.getAsJsonPrimitive("type").getAsString());
                    materials.add(materialSerializer.read(templateId, materialObj));
                }
                catch (Exception e) {
                    throw new JsonSyntaxException("Invalid material in project template JSON for " + templateId.toString(), e);
                }
            }
            
            return new ProjectTemplate(key, materials, requiredResearch, materialCountOverride, baseSuccessChanceOverride, rewardMultiplier, aidBlock);
        }
    }
}
