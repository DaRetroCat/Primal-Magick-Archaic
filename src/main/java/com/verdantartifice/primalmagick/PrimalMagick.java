package com.verdantartifice.primalmagick;

import com.verdantartifice.primalmagick.common.config.Config;
import com.verdantartifice.primalmagick.common.init.InitRegistries;
import com.verdantartifice.primalmagick.common.misc.ItemGroupPM;
import com.verdantartifice.primalmagick.proxy.ClientProxy;
import com.verdantartifice.primalmagick.proxy.IProxyPM;
import com.verdantartifice.primalmagick.proxy.ServerProxy;

import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

/**
 * Main class of the Primal Magic mod.  Most initialization doesn't happen here,
 * but rather in response to Forge events.
 * 
 * @see {@link com.verdantartifice.primalmagick.common.events.ModLifecycleEvents}
 * @see {@link com.verdantartifice.primalmagick.common.events.ServerLifecycleEvents}
 * @see {@link com.verdantartifice.primalmagick.client.events.ClientModLifecycleEvents}
 * 
 * @author Daedalus4096
 */
@Mod(PrimalMagick.MODID)
public class PrimalMagick {
    public static final String MODID = "primalmagick";
    public static final ItemGroup ITEM_GROUP = new ItemGroupPM();
    
    public static IProxyPM proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    
    public PrimalMagick() {
        Config.register();
        InitRegistries.initDeferredRegistries();
    }
}
