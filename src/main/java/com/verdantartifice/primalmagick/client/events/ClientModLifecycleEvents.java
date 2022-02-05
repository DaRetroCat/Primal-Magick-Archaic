package com.verdantartifice.primalmagick.client.events;

import com.verdantartifice.primalmagick.PrimalMagick;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Respond to client-only Forge mod lifecycle events for setup.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid= PrimalMagick.MODID, value=Dist.CLIENT, bus=Mod.EventBusSubscriber.Bus.MOD)
public class ClientModLifecycleEvents {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        PrimalMagick.proxy.clientSetup(event);
    }
}
