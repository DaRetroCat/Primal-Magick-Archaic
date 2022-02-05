package com.verdantartifice.primalmagick.common.theorycrafting;

import com.google.gson.JsonObject;

import net.minecraft.util.ResourceLocation;

/**
 * Primary interface for the serializer of a data-defined theorycrafting project material entry. 
 * @author Daedalus4096
 */
public interface IProjectMaterialSerializer<T extends AbstractProjectMaterial> {
    /**
     * Read a project material object from JSON
     */
    T read(ResourceLocation projectId, JsonObject json);
}
