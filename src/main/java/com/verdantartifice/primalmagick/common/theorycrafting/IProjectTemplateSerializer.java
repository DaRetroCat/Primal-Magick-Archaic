package com.verdantartifice.primalmagick.common.theorycrafting;

import com.google.gson.JsonObject;

import net.minecraft.util.ResourceLocation;

/**
 * Primary interface for the serializer of a data-defined theorycrafting project template. 
 * @author Daedalus4096
 */
public interface IProjectTemplateSerializer {
    /**
     * Read a project template from JSON
     */
    ProjectTemplate read(ResourceLocation templateId, JsonObject json);
}
