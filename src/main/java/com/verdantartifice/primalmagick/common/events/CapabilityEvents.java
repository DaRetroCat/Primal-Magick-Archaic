package com.verdantartifice.primalmagick.common.events;

import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.common.capabilities.PlayerAttunements;
import com.verdantartifice.primalmagick.common.capabilities.PlayerCompanions;
import com.verdantartifice.primalmagick.common.capabilities.PlayerCooldowns;
import com.verdantartifice.primalmagick.common.capabilities.PlayerKnowledge;
import com.verdantartifice.primalmagick.common.capabilities.PlayerStats;
import com.verdantartifice.primalmagick.common.capabilities.WorldEntitySwappers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handlers for capability-related events.
 * 
 * @author Daedalus4096
 */
@Mod.EventBusSubscriber(modid= PrimalMagick.MODID)
public class CapabilityEvents {
    @SubscribeEvent
    public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            // Only attach these capabilities to players, not other types of entities
            event.addCapability(PlayerKnowledge.Provider.NAME, new PlayerKnowledge.Provider());
            event.addCapability(PlayerCooldowns.Provider.NAME, new PlayerCooldowns.Provider());
            event.addCapability(PlayerStats.Provider.NAME, new PlayerStats.Provider());
            event.addCapability(PlayerAttunements.Provider.NAME, new PlayerAttunements.Provider());
            event.addCapability(PlayerCompanions.Provider.NAME, new PlayerCompanions.Provider());
        }
    }
    
    @SubscribeEvent
    public static void attachWorldCapability(AttachCapabilitiesEvent<World> event) {
        event.addCapability(WorldEntitySwappers.Provider.NAME, new WorldEntitySwappers.Provider());
    }
}
