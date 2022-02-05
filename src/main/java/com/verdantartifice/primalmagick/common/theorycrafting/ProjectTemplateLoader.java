package com.verdantartifice.primalmagick.common.theorycrafting;

import java.util.Map;

import com.verdantartifice.primalmagick.PrimalMagick;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid= PrimalMagick.MODID)
public class ProjectTemplateLoader extends JsonReloadListener {
    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogManager.getLogger();
    
    private static ProjectTemplateLoader INSTANCE;
    
    protected ProjectTemplateLoader() {
        super(GSON, "theorycrafting");
    }

    @SubscribeEvent
    public static void onResourceReload(AddReloadListenerEvent event) {
        INSTANCE = new ProjectTemplateLoader();
        event.addListener(INSTANCE);
    }
    
    public static ProjectTemplateLoader getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Cannot retrieve ProjectTemplateLoader until resources are loaded at least once");
        } else {
            return INSTANCE;
        }
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        TheorycraftManager.clearAllTemplates();
        for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet()) {
            ResourceLocation location = entry.getKey();
            if (location.getPath().startsWith("_")) {
                // Filter anything beginning with "_" as it's used for metadata.
                continue;
            }

            try {
                ProjectTemplate template = TheorycraftManager.TEMPLATE_SERIALIZER.read(location, JSONUtils.getJsonObject(entry.getValue(), "top member"));
                if (template == null || !TheorycraftManager.registerTemplate(location, template)) {
                    LOGGER.error("Failed to register theorycrafting project template {}", location);
                }
            } catch (Exception e) {
                LOGGER.error("Parsing error loading theorycrafting project template {}", location, e);
            }
        }
        LOGGER.info("Loaded {} theorycrafting project templates", TheorycraftManager.getAllTemplates().size());
    }
}
