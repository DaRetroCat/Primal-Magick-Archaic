package com.verdantartifice.primalmagick.proxy;

import com.verdantartifice.primalmagick.client.config.KeyBindings;
import com.verdantartifice.primalmagick.client.fx.particles.ParticleTypesPM;
import com.verdantartifice.primalmagick.client.gui.AnalysisTableScreen;
import com.verdantartifice.primalmagick.client.gui.ArcaneWorkbenchScreen;
import com.verdantartifice.primalmagick.client.gui.CalcinatorScreen;
import com.verdantartifice.primalmagick.client.gui.ConcocterScreen;
import com.verdantartifice.primalmagick.client.gui.GrimoireScreen;
import com.verdantartifice.primalmagick.client.gui.HoneyExtractorScreen;
import com.verdantartifice.primalmagick.client.gui.ResearchTableScreen;
import com.verdantartifice.primalmagick.client.gui.RunecarvingTableScreen;
import com.verdantartifice.primalmagick.client.gui.RunescribingAltarBasicScreen;
import com.verdantartifice.primalmagick.client.gui.RunescribingAltarEnchantedScreen;
import com.verdantartifice.primalmagick.client.gui.RunescribingAltarForbiddenScreen;
import com.verdantartifice.primalmagick.client.gui.RunescribingAltarHeavenlyScreen;
import com.verdantartifice.primalmagick.client.gui.SpellcraftingAltarScreen;
import com.verdantartifice.primalmagick.client.gui.WandAssemblyTableScreen;
import com.verdantartifice.primalmagick.client.gui.WandChargerScreen;
import com.verdantartifice.primalmagick.client.gui.WandInscriptionTableScreen;
import com.verdantartifice.primalmagick.client.renderers.entity.BasicPixieRenderer;
import com.verdantartifice.primalmagick.client.renderers.entity.FlyingCarpetRenderer;
import com.verdantartifice.primalmagick.client.renderers.entity.ForbiddenTridentRenderer;
import com.verdantartifice.primalmagick.client.renderers.entity.GrandPixieRenderer;
import com.verdantartifice.primalmagick.client.renderers.entity.HallowsteelGolemRenderer;
import com.verdantartifice.primalmagick.client.renderers.entity.HallowsteelTridentRenderer;
import com.verdantartifice.primalmagick.client.renderers.entity.HexiumGolemRenderer;
import com.verdantartifice.primalmagick.client.renderers.entity.HexiumTridentRenderer;
import com.verdantartifice.primalmagick.client.renderers.entity.InnerDemonRenderer;
import com.verdantartifice.primalmagick.client.renderers.entity.MajesticPixieRenderer;
import com.verdantartifice.primalmagick.client.renderers.entity.PrimaliteGolemRenderer;
import com.verdantartifice.primalmagick.client.renderers.entity.PrimaliteTridentRenderer;
import com.verdantartifice.primalmagick.client.renderers.entity.SinCrashRenderer;
import com.verdantartifice.primalmagick.client.renderers.entity.SinCrystalRenderer;
import com.verdantartifice.primalmagick.client.renderers.entity.SpellMineRenderer;
import com.verdantartifice.primalmagick.client.renderers.entity.SpellProjectileRenderer;
import com.verdantartifice.primalmagick.client.renderers.entity.TreefolkRenderer;
import com.verdantartifice.primalmagick.client.renderers.tile.ManaFontTER;
import com.verdantartifice.primalmagick.client.renderers.tile.OfferingPedestalTER;
import com.verdantartifice.primalmagick.client.renderers.tile.RitualAltarTER;
import com.verdantartifice.primalmagick.client.renderers.tile.RitualBellTER;
import com.verdantartifice.primalmagick.client.renderers.tile.RitualLecternTER;
import com.verdantartifice.primalmagick.client.renderers.tile.RunescribingAltarTER;
import com.verdantartifice.primalmagick.client.renderers.tile.SanguineCrucibleTER;
import com.verdantartifice.primalmagick.client.renderers.tile.WandChargerTER;
import com.verdantartifice.primalmagick.common.blocks.BlocksPM;
import com.verdantartifice.primalmagick.common.containers.ContainersPM;
import com.verdantartifice.primalmagick.common.entities.EntityTypesPM;
import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.items.entities.FlyingCarpetItem;
import com.verdantartifice.primalmagick.common.items.misc.ArcanometerItem;
import com.verdantartifice.primalmagick.common.tiles.TileEntityTypesPM;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Client sided proxy.  Handles client setup issues and provides side-dependent utility methods.
 * 
 * @author Daedalus4096
 */
public class ClientProxy implements IProxyPM {
    @Override
    public void initDeferredRegistries() {
        ParticleTypesPM.init();
    }
    
    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        this.registerKeybinds();
        this.registerScreens();
        this.registerTERs();
        this.registerEntityRenderers(event);
        this.registerItemProperties(event);
        this.setRenderLayers();
    }
    
    private void registerKeybinds() {
        KeyBindings.init();
    }
    
    private void registerScreens() {
        // Register screen factories for each container
        ScreenManager.registerFactory(ContainersPM.GRIMOIRE.get(), GrimoireScreen::new);
        ScreenManager.registerFactory(ContainersPM.ARCANE_WORKBENCH.get(), ArcaneWorkbenchScreen::new);
        ScreenManager.registerFactory(ContainersPM.WAND_ASSEMBLY_TABLE.get(), WandAssemblyTableScreen::new);
        ScreenManager.registerFactory(ContainersPM.ANALYSIS_TABLE.get(), AnalysisTableScreen::new);
        ScreenManager.registerFactory(ContainersPM.CALCINATOR.get(), CalcinatorScreen::new);
        ScreenManager.registerFactory(ContainersPM.WAND_INSCRIPTION_TABLE.get(), WandInscriptionTableScreen::new);
        ScreenManager.registerFactory(ContainersPM.SPELLCRAFTING_ALTAR.get(), SpellcraftingAltarScreen::new);
        ScreenManager.registerFactory(ContainersPM.WAND_CHARGER.get(), WandChargerScreen::new);
        ScreenManager.registerFactory(ContainersPM.RESEARCH_TABLE.get(), ResearchTableScreen::new);
        ScreenManager.registerFactory(ContainersPM.RUNESCRIBING_ALTAR_BASIC.get(), RunescribingAltarBasicScreen::new);
        ScreenManager.registerFactory(ContainersPM.RUNESCRIBING_ALTAR_ENCHANTED.get(), RunescribingAltarEnchantedScreen::new);
        ScreenManager.registerFactory(ContainersPM.RUNESCRIBING_ALTAR_FORBIDDEN.get(), RunescribingAltarForbiddenScreen::new);
        ScreenManager.registerFactory(ContainersPM.RUNESCRIBING_ALTAR_HEAVENLY.get(), RunescribingAltarHeavenlyScreen::new);
        ScreenManager.registerFactory(ContainersPM.RUNECARVING_TABLE.get(), RunecarvingTableScreen::new);
        ScreenManager.registerFactory(ContainersPM.HONEY_EXTRACTOR.get(), HoneyExtractorScreen::new);
        ScreenManager.registerFactory(ContainersPM.CONCOCTER.get(), ConcocterScreen::new);
    }
    
    private void registerTERs() {
        // Register tile entity renderers for those tile entities that need them
        ClientRegistry.bindTileEntityRenderer(TileEntityTypesPM.ANCIENT_MANA_FONT.get(), ManaFontTER::new);
        ClientRegistry.bindTileEntityRenderer(TileEntityTypesPM.ARTIFICIAL_MANA_FONT.get(), ManaFontTER::new);
        ClientRegistry.bindTileEntityRenderer(TileEntityTypesPM.WAND_CHARGER.get(), dispatcher -> new WandChargerTER(dispatcher));
        ClientRegistry.bindTileEntityRenderer(TileEntityTypesPM.RITUAL_ALTAR.get(), dispatcher -> new RitualAltarTER(dispatcher));
        ClientRegistry.bindTileEntityRenderer(TileEntityTypesPM.OFFERING_PEDESTAL.get(), dispatcher -> new OfferingPedestalTER(dispatcher));
        ClientRegistry.bindTileEntityRenderer(TileEntityTypesPM.RITUAL_LECTERN.get(), dispatcher -> new RitualLecternTER(dispatcher));
        ClientRegistry.bindTileEntityRenderer(TileEntityTypesPM.RITUAL_BELL.get(), dispatcher -> new RitualBellTER(dispatcher));
        ClientRegistry.bindTileEntityRenderer(TileEntityTypesPM.RUNESCRIBING_ALTAR.get(), dispatcher -> new RunescribingAltarTER(dispatcher));
        ClientRegistry.bindTileEntityRenderer(TileEntityTypesPM.SANGUINE_CRUCIBLE.get(), dispatcher -> new SanguineCrucibleTER(dispatcher));
    }
    
    private void registerEntityRenderers(FMLClientSetupEvent event) {
        // Register renderers for each entity type
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.SPELL_PROJECTILE.get(), SpellProjectileRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.SPELL_MINE.get(), SpellMineRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.SIN_CRASH.get(), SinCrashRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.SIN_CRYSTAL.get(), SinCrystalRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.APPLE.get(), (renderManager) -> {
            return new SpriteRenderer<>(renderManager, event.getMinecraftSupplier().get().getItemRenderer());
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.ALCHEMICAL_BOMB.get(), (renderManager) -> {
            return new SpriteRenderer<>(renderManager, event.getMinecraftSupplier().get().getItemRenderer());
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.PRIMALITE_TRIDENT.get(), PrimaliteTridentRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.HEXIUM_TRIDENT.get(), HexiumTridentRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.HALLOWSTEEL_TRIDENT.get(), HallowsteelTridentRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.FORBIDDEN_TRIDENT.get(), ForbiddenTridentRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.FLYING_CARPET.get(), FlyingCarpetRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.TREEFOLK.get(), TreefolkRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.INNER_DEMON.get(), InnerDemonRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.PRIMALITE_GOLEM.get(), PrimaliteGolemRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.HEXIUM_GOLEM.get(), HexiumGolemRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.HALLOWSTEEL_GOLEM.get(), HallowsteelGolemRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.BASIC_EARTH_PIXIE.get(), BasicPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.GRAND_EARTH_PIXIE.get(), GrandPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.MAJESTIC_EARTH_PIXIE.get(), MajesticPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.BASIC_SEA_PIXIE.get(), BasicPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.GRAND_SEA_PIXIE.get(), GrandPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.MAJESTIC_SEA_PIXIE.get(), MajesticPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.BASIC_SKY_PIXIE.get(), BasicPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.GRAND_SKY_PIXIE.get(), GrandPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.MAJESTIC_SKY_PIXIE.get(), MajesticPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.BASIC_SUN_PIXIE.get(), BasicPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.GRAND_SUN_PIXIE.get(), GrandPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.MAJESTIC_SUN_PIXIE.get(), MajesticPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.BASIC_MOON_PIXIE.get(), BasicPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.GRAND_MOON_PIXIE.get(), GrandPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.MAJESTIC_MOON_PIXIE.get(), MajesticPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.BASIC_BLOOD_PIXIE.get(), BasicPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.GRAND_BLOOD_PIXIE.get(), GrandPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.MAJESTIC_BLOOD_PIXIE.get(), MajesticPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.BASIC_INFERNAL_PIXIE.get(), BasicPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.GRAND_INFERNAL_PIXIE.get(), GrandPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.MAJESTIC_INFERNAL_PIXIE.get(), MajesticPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.BASIC_VOID_PIXIE.get(), BasicPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.GRAND_VOID_PIXIE.get(), GrandPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.MAJESTIC_VOID_PIXIE.get(), MajesticPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.BASIC_HALLOWED_PIXIE.get(), BasicPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.GRAND_HALLOWED_PIXIE.get(), GrandPixieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesPM.MAJESTIC_HALLOWED_PIXIE.get(), MajesticPixieRenderer::new);
    }
    
    private void registerItemProperties(FMLClientSetupEvent event) {
        // Register properties for items on the main thread in a thread-safe fashion
        event.enqueueWork(() -> {
            ItemModelsProperties.registerProperty(ItemsPM.ARCANOMETER.get(), ArcanometerItem.SCAN_STATE_PROPERTY, ArcanometerItem.getScanStateProperty());
            ItemModelsProperties.registerProperty(ItemsPM.FLYING_CARPET.get(), FlyingCarpetItem.COLOR_PROPERTY, FlyingCarpetItem.getColorProperty());
            
            IItemPropertyGetter castProperty = (ItemStack stack, ClientWorld world, LivingEntity entity) -> {
                if (entity == null) {
                    return 0.0F;
                } else {
                    boolean inMain = entity.getHeldItemMainhand() == stack;
                    boolean inOff = entity.getHeldItemOffhand() == stack;
                    if (entity.getHeldItemMainhand().getItem() instanceof FishingRodItem) {
                        inOff = false;
                    }
                    return (inMain || inOff) && entity instanceof PlayerEntity && ((PlayerEntity)entity).fishingBobber != null ? 1.0F : 0.0F;
                }
            };
            ItemModelsProperties.registerProperty(ItemsPM.PRIMALITE_FISHING_ROD.get(), new ResourceLocation("cast"), castProperty);
            ItemModelsProperties.registerProperty(ItemsPM.HEXIUM_FISHING_ROD.get(), new ResourceLocation("cast"), castProperty);
            ItemModelsProperties.registerProperty(ItemsPM.HALLOWSTEEL_FISHING_ROD.get(), new ResourceLocation("cast"), castProperty);
            ItemModelsProperties.registerProperty(ItemsPM.PRIMAL_FISHING_ROD.get(), new ResourceLocation("cast"), castProperty);
            
            IItemPropertyGetter handActiveProperty = (ItemStack stack, ClientWorld world, LivingEntity entity) -> {
                return entity != null && entity.isHandActive() && entity.getActiveItemStack() == stack ? 1.0F : 0.0F;
            };
            ItemModelsProperties.registerProperty(ItemsPM.PRIMALITE_TRIDENT.get(), new ResourceLocation("throwing"), handActiveProperty);
            ItemModelsProperties.registerProperty(ItemsPM.HEXIUM_TRIDENT.get(), new ResourceLocation("throwing"), handActiveProperty);
            ItemModelsProperties.registerProperty(ItemsPM.HALLOWSTEEL_TRIDENT.get(), new ResourceLocation("throwing"), handActiveProperty);
            ItemModelsProperties.registerProperty(ItemsPM.FORBIDDEN_TRIDENT.get(), new ResourceLocation("throwing"), handActiveProperty);
            ItemModelsProperties.registerProperty(ItemsPM.PRIMALITE_SHIELD.get(), new ResourceLocation("blocking"), handActiveProperty);
            ItemModelsProperties.registerProperty(ItemsPM.HEXIUM_SHIELD.get(), new ResourceLocation("blocking"), handActiveProperty);
            ItemModelsProperties.registerProperty(ItemsPM.HALLOWSTEEL_SHIELD.get(), new ResourceLocation("blocking"), handActiveProperty);
            
            IItemPropertyGetter pullProperty = (ItemStack stack, ClientWorld world, LivingEntity entity) -> {
                if (entity == null) {
                    return 0.0F;
                } else {
                    return entity.getActiveItemStack() != stack ? 0.0F : (float)(stack.getUseDuration() - entity.getItemInUseCount()) / 20.0F;
                }
            };
            ItemModelsProperties.registerProperty(ItemsPM.PRIMALITE_BOW.get(), new ResourceLocation("pull"), pullProperty);
            ItemModelsProperties.registerProperty(ItemsPM.PRIMALITE_BOW.get(), new ResourceLocation("pulling"), handActiveProperty);
            ItemModelsProperties.registerProperty(ItemsPM.HEXIUM_BOW.get(), new ResourceLocation("pull"), pullProperty);
            ItemModelsProperties.registerProperty(ItemsPM.HEXIUM_BOW.get(), new ResourceLocation("pulling"), handActiveProperty);
            ItemModelsProperties.registerProperty(ItemsPM.HALLOWSTEEL_BOW.get(), new ResourceLocation("pull"), pullProperty);
            ItemModelsProperties.registerProperty(ItemsPM.HALLOWSTEEL_BOW.get(), new ResourceLocation("pulling"), handActiveProperty);
            ItemModelsProperties.registerProperty(ItemsPM.FORBIDDEN_BOW.get(), new ResourceLocation("pull"), pullProperty);
            ItemModelsProperties.registerProperty(ItemsPM.FORBIDDEN_BOW.get(), new ResourceLocation("pulling"), handActiveProperty);
    	});
    }
    
    private void setRenderLayers() {
        // Set the render layers for any blocks that don't use the default
        RenderTypeLookup.setRenderLayer(BlocksPM.MOONWOOD_SAPLING.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlocksPM.MOONWOOD_LEAVES.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.MOONWOOD_LOG.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.MOONWOOD_PILLAR.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.MOONWOOD_PLANKS.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.MOONWOOD_SLAB.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.MOONWOOD_STAIRS.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.MOONWOOD_WOOD.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STRIPPED_MOONWOOD_LOG.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STRIPPED_MOONWOOD_WOOD.get(), RenderType.getTranslucent());

        RenderTypeLookup.setRenderLayer(BlocksPM.SUNWOOD_SAPLING.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlocksPM.SUNWOOD_LEAVES.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.SUNWOOD_LOG.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.SUNWOOD_PILLAR.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.SUNWOOD_PLANKS.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.SUNWOOD_SLAB.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.SUNWOOD_STAIRS.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.SUNWOOD_WOOD.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STRIPPED_SUNWOOD_LOG.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STRIPPED_SUNWOOD_WOOD.get(), RenderType.getTranslucent());
        
        RenderTypeLookup.setRenderLayer(BlocksPM.HALLOWOOD_SAPLING.get(), RenderType.getCutout());
        
        RenderTypeLookup.setRenderLayer(BlocksPM.SALT_TRAIL.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlocksPM.SUNLAMP.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlocksPM.SPIRIT_LANTERN.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlocksPM.BLOODLETTER.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlocksPM.CONCOCTER.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlocksPM.CELESTIAL_HARP.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlocksPM.HONEY_EXTRACTOR.get(), RenderType.getTranslucent());

        RenderTypeLookup.setRenderLayer(BlocksPM.SKYGLASS.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_BLACK.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_BLUE.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_BROWN.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_CYAN.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_GRAY.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_GREEN.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_LIGHT_BLUE.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_LIGHT_GRAY.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_LIME.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_MAGENTA.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_ORANGE.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_PINK.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_PURPLE.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_RED.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_WHITE.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_YELLOW.get(), RenderType.getTranslucent());
        
        RenderTypeLookup.setRenderLayer(BlocksPM.SKYGLASS_PANE.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_PANE_BLACK.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_PANE_BLUE.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_PANE_BROWN.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_PANE_CYAN.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_PANE_GRAY.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_PANE_GREEN.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_PANE_LIGHT_BLUE.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_PANE_LIGHT_GRAY.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_PANE_LIME.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_PANE_MAGENTA.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_PANE_ORANGE.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_PANE_PINK.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_PANE_PURPLE.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_PANE_RED.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_PANE_WHITE.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(BlocksPM.STAINED_SKYGLASS_PANE_YELLOW.get(), RenderType.getTranslucent());
    }
    
    @Override
    public boolean isShiftDown() {
        return Screen.hasShiftDown();
    }
}
