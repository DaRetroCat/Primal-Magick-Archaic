package com.verdantartifice.primalmagick.client.config;

import com.verdantartifice.primalmagick.PrimalMagick;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * Define and register custom client key-bindings.
 * 
 * @author Daedalus4096
 */
public class KeyBindings {
    public static KeyBinding changeSpellKey;    // Key for changing the active spell of a wand
    public static KeyBinding carpetForwardKey;  // Key for commanding a flying carpet forward
    public static KeyBinding carpetBackwardKey; // Key for commanding a flying carpet backward
    
    private static final String KEY_CATEGORY = "key.categories." + PrimalMagick.MODID;
    
    public static void init() {
        changeSpellKey = new KeyBinding("key.primalmagick.change_spell", GLFW.GLFW_KEY_R, KEY_CATEGORY);
        ClientRegistry.registerKeyBinding(changeSpellKey);
        
        carpetForwardKey = new KeyBinding("key.primalmagick.carpet_forward", GLFW.GLFW_KEY_W, KEY_CATEGORY);
        ClientRegistry.registerKeyBinding(carpetForwardKey);
        
        carpetBackwardKey = new KeyBinding("key.primalmagick.carpet_backward", GLFW.GLFW_KEY_S, KEY_CATEGORY);
        ClientRegistry.registerKeyBinding(carpetBackwardKey);
    }
}
