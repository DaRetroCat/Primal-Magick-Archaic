package com.verdantartifice.primalmagick.common.init;

import com.verdantartifice.primalmagick.common.capabilities.IPlayerAttunements;
import com.verdantartifice.primalmagick.common.capabilities.IPlayerCompanions;
import com.verdantartifice.primalmagick.common.capabilities.IPlayerCooldowns;
import com.verdantartifice.primalmagick.common.capabilities.IPlayerKnowledge;
import com.verdantartifice.primalmagick.common.capabilities.IPlayerStats;
import com.verdantartifice.primalmagick.common.capabilities.IManaStorage;
import com.verdantartifice.primalmagick.common.capabilities.IWorldEntitySwappers;
import com.verdantartifice.primalmagick.common.capabilities.PlayerAttunements;
import com.verdantartifice.primalmagick.common.capabilities.PlayerCompanions;
import com.verdantartifice.primalmagick.common.capabilities.PlayerCooldowns;
import com.verdantartifice.primalmagick.common.capabilities.PlayerKnowledge;
import com.verdantartifice.primalmagick.common.capabilities.PlayerStats;
import com.verdantartifice.primalmagick.common.capabilities.ManaStorage;
import com.verdantartifice.primalmagick.common.capabilities.WorldEntitySwappers;

import net.minecraftforge.common.capabilities.CapabilityManager;

/**
 * Point of registration for mod capabilities.
 * 
 * @author Daedalus4096
 */
public class InitCapabilities {
    public static void initCapabilities() {
        CapabilityManager.INSTANCE.register(IPlayerKnowledge.class, new PlayerKnowledge.Storage(), new PlayerKnowledge.Factory());
        CapabilityManager.INSTANCE.register(IPlayerCooldowns.class, new PlayerCooldowns.Storage(), new PlayerCooldowns.Factory());
        CapabilityManager.INSTANCE.register(IPlayerStats.class, new PlayerStats.Storage(), new PlayerStats.Factory());
        CapabilityManager.INSTANCE.register(IPlayerAttunements.class, new PlayerAttunements.Storage(), new PlayerAttunements.Factory());
        CapabilityManager.INSTANCE.register(IPlayerCompanions.class, new PlayerCompanions.Storage(), new PlayerCompanions.Factory());
        CapabilityManager.INSTANCE.register(IWorldEntitySwappers.class, new WorldEntitySwappers.Storage(), new WorldEntitySwappers.Factory());
        CapabilityManager.INSTANCE.register(IManaStorage.class, new ManaStorage.Storage(), new ManaStorage.Factory());
    }
}
