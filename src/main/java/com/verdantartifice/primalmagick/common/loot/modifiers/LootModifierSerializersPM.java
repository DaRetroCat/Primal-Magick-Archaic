package com.verdantartifice.primalmagick.common.loot.modifiers;

import com.verdantartifice.primalmagick.PrimalMagick;

import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Deferred registry for the mod's global loot modifier serializers.
 * 
 * @author Daedalus4096
 */
public class LootModifierSerializersPM {
    private static final DeferredRegister<GlobalLootModifierSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, PrimalMagick.MODID);
    
    public static void init() {
        SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    
    public static final RegistryObject<GlobalLootModifierSerializer<BloodyFleshModifier>> BLOODY_FLESH = SERIALIZERS.register("bloody_flesh", BloodyFleshModifier.Serializer::new);
    public static final RegistryObject<GlobalLootModifierSerializer<BonusNuggetModifier>> BONUS_NUGGET = SERIALIZERS.register("bonus_nugget", BonusNuggetModifier.Serializer::new);
    public static final RegistryObject<GlobalLootModifierSerializer<BountyFarmingModifier>> BOUNTY_FARMING = SERIALIZERS.register("bounty_farming", BountyFarmingModifier.Serializer::new);
    public static final RegistryObject<GlobalLootModifierSerializer<BountyFishingModifier>> BOUNTY_FISHING = SERIALIZERS.register("bounty_fishing", BountyFishingModifier.Serializer::new);
}
