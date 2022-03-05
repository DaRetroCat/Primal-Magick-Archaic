package com.verdantartifice.primalmagick.common.tiles;

import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.common.blocks.BlocksPM;
import com.verdantartifice.primalmagick.common.tiles.crafting.CalcinatorTileEntity;
import com.verdantartifice.primalmagick.common.tiles.crafting.ConcocterTileEntity;
import com.verdantartifice.primalmagick.common.tiles.crafting.EssenceFurnaceTileEntity;
import com.verdantartifice.primalmagick.common.tiles.crafting.RunescribingAltarTileEntity;
import com.verdantartifice.primalmagick.common.tiles.devices.HoneyExtractorTileEntity;
import com.verdantartifice.primalmagick.common.tiles.devices.SanguineCrucibleTileEntity;
import com.verdantartifice.primalmagick.common.tiles.devices.SunlampTileEntity;
import com.verdantartifice.primalmagick.common.tiles.mana.AncientManaFontTileEntity;
import com.verdantartifice.primalmagick.common.tiles.mana.ArtificialManaFontTileEntity;
import com.verdantartifice.primalmagick.common.tiles.mana.WandChargerTileEntity;
import com.verdantartifice.primalmagick.common.tiles.rituals.BloodletterTileEntity;
import com.verdantartifice.primalmagick.common.tiles.rituals.CelestialHarpTileEntity;
import com.verdantartifice.primalmagick.common.tiles.rituals.EntropySinkTileEntity;
import com.verdantartifice.primalmagick.common.tiles.rituals.IncenseBrazierTileEntity;
import com.verdantartifice.primalmagick.common.tiles.rituals.OfferingPedestalTileEntity;
import com.verdantartifice.primalmagick.common.tiles.rituals.RitualAltarTileEntity;
import com.verdantartifice.primalmagick.common.tiles.rituals.RitualBellTileEntity;
import com.verdantartifice.primalmagick.common.tiles.rituals.RitualCandleTileEntity;
import com.verdantartifice.primalmagick.common.tiles.rituals.RitualLecternTileEntity;
import com.verdantartifice.primalmagick.common.tiles.rituals.SoulAnvilTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Deferred registry for mod tile entity types.
 * 
 * @author Daedalus4096
 */
public class TileEntityTypesPM {
    private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, PrimalMagick.MODID);
    
    public static void init() {
        TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    
    public static final RegistryObject<TileEntityType<AncientManaFontTileEntity>> ANCIENT_MANA_FONT = TILE_ENTITIES.register("ancient_mana_font", () -> TileEntityType.Builder.create(AncientManaFontTileEntity::new, BlocksPM.ANCIENT_FONT_EARTH.get(), BlocksPM.ANCIENT_FONT_SEA.get(), BlocksPM.ANCIENT_FONT_SKY.get(), BlocksPM.ANCIENT_FONT_SUN.get(), BlocksPM.ANCIENT_FONT_MOON.get()).build(null));
    public static final RegistryObject<TileEntityType<ArtificialManaFontTileEntity>> ARTIFICIAL_MANA_FONT = TILE_ENTITIES.register("artificial_mana_font", () -> TileEntityType.Builder.create(ArtificialManaFontTileEntity::new, BlocksPM.ARTIFICIAL_FONT_EARTH.get(), BlocksPM.ARTIFICIAL_FONT_SEA.get(), BlocksPM.ARTIFICIAL_FONT_SKY.get(), BlocksPM.ARTIFICIAL_FONT_SUN.get(), BlocksPM.ARTIFICIAL_FONT_MOON.get(), BlocksPM.ARTIFICIAL_FONT_BLOOD.get(), BlocksPM.ARTIFICIAL_FONT_INFERNAL.get(), BlocksPM.ARTIFICIAL_FONT_VOID.get(), BlocksPM.ARTIFICIAL_FONT_HALLOWED.get(), BlocksPM.FORBIDDEN_FONT_EARTH.get(), BlocksPM.FORBIDDEN_FONT_SEA.get(), BlocksPM.FORBIDDEN_FONT_SKY.get(), BlocksPM.FORBIDDEN_FONT_SUN.get(), BlocksPM.FORBIDDEN_FONT_MOON.get(), BlocksPM.FORBIDDEN_FONT_BLOOD.get(), BlocksPM.FORBIDDEN_FONT_INFERNAL.get(), BlocksPM.FORBIDDEN_FONT_VOID.get(), BlocksPM.FORBIDDEN_FONT_HALLOWED.get(), BlocksPM.HEAVENLY_FONT_EARTH.get(), BlocksPM.HEAVENLY_FONT_SEA.get(), BlocksPM.HEAVENLY_FONT_SKY.get(), BlocksPM.HEAVENLY_FONT_SUN.get(), BlocksPM.HEAVENLY_FONT_MOON.get(), BlocksPM.HEAVENLY_FONT_BLOOD.get(), BlocksPM.HEAVENLY_FONT_INFERNAL.get(), BlocksPM.HEAVENLY_FONT_VOID.get(), BlocksPM.HEAVENLY_FONT_HALLOWED.get()).build(null));
    public static final RegistryObject<TileEntityType<EssenceFurnaceTileEntity>> ESSENCE_FURNACE = TILE_ENTITIES.register("essence_furnace", () -> TileEntityType.Builder.create(EssenceFurnaceTileEntity::new, BlocksPM.ESSENCE_FURNACE.get()).build(null));
    public static final RegistryObject<TileEntityType<CalcinatorTileEntity>> CALCINATOR = TILE_ENTITIES.register("calcinator", () -> TileEntityType.Builder.create(CalcinatorTileEntity::new, BlocksPM.CALCINATOR_BASIC.get(), BlocksPM.CALCINATOR_ENCHANTED.get(), BlocksPM.CALCINATOR_FORBIDDEN.get(), BlocksPM.CALCINATOR_HEAVENLY.get()).build(null));
    public static final RegistryObject<TileEntityType<WandChargerTileEntity>> WAND_CHARGER = TILE_ENTITIES.register("wand_charger", () -> TileEntityType.Builder.create(WandChargerTileEntity::new, BlocksPM.WAND_CHARGER.get()).build(null));
    public static final RegistryObject<TileEntityType<RitualAltarTileEntity>> RITUAL_ALTAR = TILE_ENTITIES.register("ritual_altar", () -> TileEntityType.Builder.create(RitualAltarTileEntity::new, BlocksPM.RITUAL_ALTAR.get()).build(null));
    public static final RegistryObject<TileEntityType<SunlampTileEntity>> SUNLAMP = TILE_ENTITIES.register("sunlamp", () -> TileEntityType.Builder.create(SunlampTileEntity::new, BlocksPM.SUNLAMP.get(), BlocksPM.SPIRIT_LANTERN.get()).build(null));
    public static final RegistryObject<TileEntityType<OfferingPedestalTileEntity>> OFFERING_PEDESTAL = TILE_ENTITIES.register("offering_pedestal", () -> TileEntityType.Builder.create(OfferingPedestalTileEntity::new, BlocksPM.OFFERING_PEDESTAL.get()).build(null));
    public static final RegistryObject<TileEntityType<RitualCandleTileEntity>> RITUAL_CANDLE = TILE_ENTITIES.register("ritual_candle", () -> TileEntityType.Builder.create(RitualCandleTileEntity::new, BlocksPM.RITUAL_CANDLE_BLACK.get(), BlocksPM.RITUAL_CANDLE_BLUE.get(), BlocksPM.RITUAL_CANDLE_BROWN.get(), BlocksPM.RITUAL_CANDLE_CYAN.get(), BlocksPM.RITUAL_CANDLE_GRAY.get(), BlocksPM.RITUAL_CANDLE_GREEN.get(), BlocksPM.RITUAL_CANDLE_LIGHT_BLUE.get(), BlocksPM.RITUAL_CANDLE_LIGHT_GRAY.get(), BlocksPM.RITUAL_CANDLE_LIME.get(), BlocksPM.RITUAL_CANDLE_MAGENTA.get(), BlocksPM.RITUAL_CANDLE_ORANGE.get(), BlocksPM.RITUAL_CANDLE_PINK.get(), BlocksPM.RITUAL_CANDLE_PURPLE.get(), BlocksPM.RITUAL_CANDLE_RED.get(), BlocksPM.RITUAL_CANDLE_WHITE.get(), BlocksPM.RITUAL_CANDLE_YELLOW.get()).build(null));
    public static final RegistryObject<TileEntityType<IncenseBrazierTileEntity>> INCENSE_BRAZIER = TILE_ENTITIES.register("incense_brazier", () -> TileEntityType.Builder.create(IncenseBrazierTileEntity::new, BlocksPM.INCENSE_BRAZIER.get()).build(null));
    public static final RegistryObject<TileEntityType<RitualLecternTileEntity>> RITUAL_LECTERN = TILE_ENTITIES.register("ritual_lectern", () -> TileEntityType.Builder.create(RitualLecternTileEntity::new, BlocksPM.RITUAL_LECTERN.get()).build(null));
    public static final RegistryObject<TileEntityType<RitualBellTileEntity>> RITUAL_BELL = TILE_ENTITIES.register("ritual_bell", () -> TileEntityType.Builder.create(RitualBellTileEntity::new, BlocksPM.RITUAL_BELL.get()).build(null));
    public static final RegistryObject<TileEntityType<BloodletterTileEntity>> BLOODLETTER = TILE_ENTITIES.register("bloodletter", () -> TileEntityType.Builder.create(BloodletterTileEntity::new, BlocksPM.BLOODLETTER.get()).build(null));
    public static final RegistryObject<TileEntityType<SoulAnvilTileEntity>> SOUL_ANVIL = TILE_ENTITIES.register("soul_anvil", () -> TileEntityType.Builder.create(SoulAnvilTileEntity::new, BlocksPM.SOUL_ANVIL.get()).build(null));
    public static final RegistryObject<TileEntityType<RunescribingAltarTileEntity>> RUNESCRIBING_ALTAR = TILE_ENTITIES.register("runescribing_altar", () -> TileEntityType.Builder.create(RunescribingAltarTileEntity::new, BlocksPM.RUNESCRIBING_ALTAR_BASIC.get(), BlocksPM.RUNESCRIBING_ALTAR_ENCHANTED.get(), BlocksPM.RUNESCRIBING_ALTAR_FORBIDDEN.get(), BlocksPM.RUNESCRIBING_ALTAR_HEAVENLY.get()).build(null));
    public static final RegistryObject<TileEntityType<HoneyExtractorTileEntity>> HONEY_EXTRACTOR = TILE_ENTITIES.register("honey_extractor", () -> TileEntityType.Builder.create(HoneyExtractorTileEntity::new, BlocksPM.HONEY_EXTRACTOR.get()).build(null));
    public static final RegistryObject<TileEntityType<SanguineCrucibleTileEntity>> SANGUINE_CRUCIBLE = TILE_ENTITIES.register("sanguine_crucible", () -> TileEntityType.Builder.create(SanguineCrucibleTileEntity::new, BlocksPM.SANGUINE_CRUCIBLE.get()).build(null));
    public static final RegistryObject<TileEntityType<ConcocterTileEntity>> CONCOCTER = TILE_ENTITIES.register("concocter", () -> TileEntityType.Builder.create(ConcocterTileEntity::new, BlocksPM.CONCOCTER.get()).build(null));
    public static final RegistryObject<TileEntityType<CelestialHarpTileEntity>> CELESTIAL_HARP = TILE_ENTITIES.register("celestial_harp", () -> TileEntityType.Builder.create(CelestialHarpTileEntity::new, BlocksPM.CELESTIAL_HARP.get()).build(null));
    public static final RegistryObject<TileEntityType<EntropySinkTileEntity>> ENTROPY_SINK = TILE_ENTITIES.register("entropy_sink", () -> TileEntityType.Builder.create(EntropySinkTileEntity::new, BlocksPM.ENTROPY_SINK.get()).build(null));
}
