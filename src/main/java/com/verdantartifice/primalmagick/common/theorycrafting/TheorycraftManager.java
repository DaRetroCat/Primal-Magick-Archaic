package com.verdantartifice.primalmagick.common.theorycrafting;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.verdantartifice.primalmagick.common.util.WeightedRandomBag;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

/**
 * Primary access point for theorycraft-related methods.  Also stores defined research projects in a
 * static registry.
 * 
 * @author Daedalus4096
 */
public class TheorycraftManager {
    public static final IProjectTemplateSerializer TEMPLATE_SERIALIZER = new ProjectTemplate.Serializer();
    protected static final Map<String, IProjectMaterialSerializer<?>> MATERIAL_SERIALIZERS = new ImmutableMap.Builder<String, IProjectMaterialSerializer<?>>()
            .put(ItemProjectMaterial.TYPE, ItemProjectMaterial.SERIALIZER)
            .put(ItemTagProjectMaterial.TYPE, ItemTagProjectMaterial.SERIALIZER)
            .put(ExperienceProjectMaterial.TYPE, ExperienceProjectMaterial.SERIALIZER)
            .put(ObservationProjectMaterial.TYPE, ObservationProjectMaterial.SERIALIZER)
            .build();
    protected static final Map<ResourceLocation, ProjectTemplate> TEMPLATES = new HashMap<>();
    
    @Nullable
    public static IProjectMaterialSerializer<?> getMaterialSerializer(@Nullable String type) {
        return MATERIAL_SERIALIZERS.get(type);
    }
    
    public static void clearAllTemplates() {
        TEMPLATES.clear();
    }
    
    public static Map<ResourceLocation, ProjectTemplate> getAllTemplates() {
        return Collections.unmodifiableMap(TEMPLATES);
    }
    
    public static boolean registerTemplate(ResourceLocation templateKey, ProjectTemplate template) {
        if (TEMPLATES.containsKey(templateKey)) {
            return false;
        } else {
            TEMPLATES.put(templateKey, template);
            return true;
        }
    }
    
    @Nonnull
    public static Project createRandomProject(@Nonnull PlayerEntity player, @Nonnull BlockPos tablePos) {
        WeightedRandomBag<ProjectTemplate> templateBag = new WeightedRandomBag<>();
        for (ProjectTemplate template : TEMPLATES.values()) {
            templateBag.add(template, 1);
        }
        
        // Determine what blocks are nearby so that aid blocks can be checked
        Set<Block> nearby = new HashSet<>();
        if (player.world.isAreaLoaded(tablePos, 5)) {
            Iterable<BlockPos> positions = BlockPos.getAllInBoxMutable(tablePos.add(-5, -2, -5), tablePos.add(5, 2, 5));
            for (BlockPos pos : positions) {
                nearby.add(player.world.getBlockState(pos).getBlock());
            }
        }
        
        Project retVal = null;
        int attempts = 0;   // Don't allow an infinite loop
        while (retVal == null && attempts < 1000) {
            attempts++;
            ProjectTemplate selectedTemplate = templateBag.getRandom(player.getRNG());
            Project initializedProject = selectedTemplate.initialize(player);
            // Only select the project if it initializes successfully and any required aid blocks are nearby
            if (initializedProject != null && (initializedProject.getAidBlock() == null || nearby.contains(initializedProject.getAidBlock()))) {
                retVal = initializedProject;
            }
        }
        return retVal;
    }
}
