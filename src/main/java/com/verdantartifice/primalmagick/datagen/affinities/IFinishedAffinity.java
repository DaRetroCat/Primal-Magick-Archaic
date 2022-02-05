package com.verdantartifice.primalmagick.datagen.affinities;

import com.google.gson.JsonObject;
import com.verdantartifice.primalmagick.common.affinities.AffinityType;

import net.minecraft.util.ResourceLocation;

public interface IFinishedAffinity {
    AffinityType getType();
    
    ResourceLocation getId();
    
    void serialize(JsonObject json);
    
    default JsonObject getAffinityJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", this.getType().getString());
        this.serialize(json);
        return json;
    }
}
