package com.verdantartifice.primalmagick.datagen.theorycrafting;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import com.verdantartifice.primalmagick.PrimalMagick;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.verdantartifice.primalmagick.common.blocks.BlocksPM;
import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.research.CompoundResearchKey;
import com.verdantartifice.primalmagick.common.research.SimpleResearchKey;
import com.verdantartifice.primalmagick.common.sources.Source;

import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;

public class ProjectProvider implements IDataProvider {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private static final Logger LOGGER = LogManager.getLogger();
    protected final DataGenerator generator;
    
    public ProjectProvider(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        Path path = this.generator.getOutputFolder();
        Map<ResourceLocation, IFinishedProject> map = new HashMap<>();
        this.registerProjects((project) -> {
            if (map.put(project.getId(), project) != null) {
                LOGGER.debug("Duplicate theorycrafting project in data generation: {}", project.getId().toString());
            }
        });
        for (Map.Entry<ResourceLocation, IFinishedProject> entry : map.entrySet()) {
            this.saveProject(cache, entry.getValue().getProjectJson(), path.resolve("data/" + entry.getKey().getNamespace() + "/theorycrafting/" + entry.getKey().getPath() + ".json"));
        }
    }

    private void saveProject(DirectoryCache cache, JsonObject json, Path path) {
        try {
            String jsonStr = GSON.toJson((JsonElement)json);
            String hash = HASH_FUNCTION.hashUnencodedChars(jsonStr).toString();
            if (!Objects.equals(cache.getPreviousHash(path), hash) || !Files.exists(path)) {
                Files.createDirectories(path.getParent());
                try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                    writer.write(jsonStr);
                }
            }
            cache.recordHash(path, hash);
        } catch (IOException e) {
            LOGGER.error("Couldn't save theorycrafting project {}", path, e);
        }
    }
    
    protected void registerProjects(Consumer<IFinishedProject> consumer) {
        SimpleResearchKey shardSynthesis = SimpleResearchKey.parse("SHARD_SYNTHESIS");
        
        ProjectBuilder.project("advanced_essence_analysis").requiredResearch("CRYSTAL_SYNTHESIS").rewardMultiplier(0.5D)
            .material(ObservationMaterialBuilder.observation(1, true).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_SHARD_EARTH.get(), true).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_SHARD_SEA.get(), true).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_SHARD_SKY.get(), true).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_SHARD_SUN.get(), true).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_SHARD_MOON.get(), true).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_SHARD_BLOOD.get(), true).requiredResearch(Source.BLOOD.getDiscoverKey()).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_SHARD_INFERNAL.get(), true).requiredResearch(Source.INFERNAL.getDiscoverKey()).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_SHARD_VOID.get(), true).requiredResearch(Source.VOID.getDiscoverKey()).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_SHARD_HALLOWED.get(), true).requiredResearch(Source.HALLOWED.getDiscoverKey()).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CRYSTAL_EARTH.get(), true).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CRYSTAL_SEA.get(), true).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CRYSTAL_SKY.get(), true).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CRYSTAL_SUN.get(), true).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CRYSTAL_MOON.get(), true).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CRYSTAL_BLOOD.get(), true).requiredResearch(Source.BLOOD.getDiscoverKey()).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CRYSTAL_INFERNAL.get(), true).requiredResearch(Source.INFERNAL.getDiscoverKey()).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CRYSTAL_VOID.get(), true).requiredResearch(Source.VOID.getDiscoverKey()).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CRYSTAL_HALLOWED.get(), true).requiredResearch(Source.HALLOWED.getDiscoverKey()).weight(1).build())
            .build(consumer);
        ProjectBuilder.project("apiamancy").aid(Blocks.BEEHIVE).materialCountOverride(1).baseSuccessChanceOverride(0.5D).rewardMultiplier(0.5D)
            .material(ItemTagMaterialBuilder.tag("minecraft", "small_flowers", true).weight(1).build())
            .build(consumer);
        ProjectBuilder.project("beacon_emanations").aid(Blocks.BEACON).materialCountOverride(1).baseSuccessChanceOverride(0.5D).rewardMultiplier(0.5D)
            .material(ItemTagMaterialBuilder.tag("forge", "beacon_payment", true).weight(1).build())
            .build(consumer);
        ProjectBuilder.project("brewing_experiments").requiredResearch(Source.INFERNAL.getDiscoverKey())
            .material(ItemMaterialBuilder.item(Items.BREWING_STAND, false).weight(3).build())
            .material(ItemTagMaterialBuilder.tag("forge", "crops/nether_wart", true).weight(3).build())
            .material(ItemMaterialBuilder.item(Items.FERMENTED_SPIDER_EYE, true).weight(1).build())
            .material(ItemTagMaterialBuilder.tag("forge", "dusts/glowstone", true).weight(1).build())
            .material(ItemTagMaterialBuilder.tag("forge", "dusts/redstone", true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.SUGAR, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.RABBIT_FOOT, true).weight(0.5D).build())
            .material(ItemMaterialBuilder.item(Items.BLAZE_POWDER, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GLISTERING_MELON_SLICE, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.SPIDER_EYE, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GHAST_TEAR, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.MAGMA_CREAM, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.PUFFERFISH, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_CARROT, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.TURTLE_HELMET, true).weight(0.5D).build())
            .material(ItemMaterialBuilder.item(Items.PHANTOM_MEMBRANE, true).weight(0.5D).build())
            .material(ObservationMaterialBuilder.observation(1, true).weight(3).build())
            .build(consumer);
        ProjectBuilder.project("draconic_energies").aid(Blocks.DRAGON_EGG).materialCountOverride(1).baseSuccessChanceOverride(0.5D).rewardMultiplier(0.5D)
            .material(ItemTagMaterialBuilder.tag("forge", "ender_pearls", true).weight(1).build())
            .build(consumer);
        ProjectBuilder.project("enchanting_studies").requiredResearch("BASIC_MANAWEAVING")
            .material(ItemMaterialBuilder.item(Items.ENCHANTING_TABLE, false).weight(5).build())
            .material(ItemTagMaterialBuilder.tag("forge", "gems/lapis", true).weight(5).build())
            .material(ExperienceMaterialBuilder.experience(3, true).weight(5).build())
            .material(ItemMaterialBuilder.item(Items.BOOK, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_SWORD, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_PICKAXE, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_SHOVEL, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_AXE, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_HOE, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_HELMET, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_CHESTPLATE, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_LEGGINGS, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_BOOTS, false).weight(1).build())
            .material(ObservationMaterialBuilder.observation(1, true).weight(5).build())
            .build(consumer);
        ProjectBuilder.project("end_expedition").requiredResearch(Source.VOID.getDiscoverKey()).rewardMultiplier(1.0D)
            .material(ItemMaterialBuilder.item(Items.NETHERITE_SWORD, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.CROSSBOW, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.ARROW, 32, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.NETHERITE_CHESTPLATE, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.MAP, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.CARTOGRAPHY_TABLE, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.TORCH, 32, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.BREAD, 8, true).weight(1).build())
            .material(ItemMaterialBuilder.item(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.SLOW_FALLING), true).weight(1).build())
            .material(ItemTagMaterialBuilder.tag("forge", "ender_pearls", 4, true).weight(3).build())
            .material(ItemMaterialBuilder.item(Items.ENDER_EYE, true).weight(1).build())
            .build(consumer);
        ProjectBuilder.project("essence_analysis").requiredResearch("BASIC_ALCHEMY")
            .material(ObservationMaterialBuilder.observation(1, true).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_DUST_EARTH.get(), true).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_DUST_SEA.get(), true).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_DUST_SKY.get(), true).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_DUST_SUN.get(), true).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_DUST_MOON.get(), true).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_DUST_BLOOD.get(), true).requiredResearch(Source.BLOOD.getDiscoverKey()).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_DUST_INFERNAL.get(), true).requiredResearch(Source.INFERNAL.getDiscoverKey()).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_DUST_VOID.get(), true).requiredResearch(Source.VOID.getDiscoverKey()).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_DUST_HALLOWED.get(), true).requiredResearch(Source.HALLOWED.getDiscoverKey()).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_SHARD_EARTH.get(), true).requiredResearch(shardSynthesis).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_SHARD_SEA.get(), true).requiredResearch(shardSynthesis).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_SHARD_SKY.get(), true).requiredResearch(shardSynthesis).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_SHARD_SUN.get(), true).requiredResearch(shardSynthesis).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_SHARD_MOON.get(), true).requiredResearch(shardSynthesis).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_SHARD_BLOOD.get(), true).requiredResearch(CompoundResearchKey.from(true, shardSynthesis, Source.BLOOD.getDiscoverKey())).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_SHARD_INFERNAL.get(), true).requiredResearch(CompoundResearchKey.from(true, shardSynthesis, Source.INFERNAL.getDiscoverKey())).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_SHARD_VOID.get(), true).requiredResearch(CompoundResearchKey.from(true, shardSynthesis, Source.VOID.getDiscoverKey())).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_SHARD_HALLOWED.get(), true).requiredResearch(CompoundResearchKey.from(true, shardSynthesis, Source.HALLOWED.getDiscoverKey())).weight(1).build())
            .build(consumer);
        ProjectBuilder.project("expedition")
            .material(ItemMaterialBuilder.item(Items.IRON_SWORD, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.BOW, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.ARROW, 4, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.IRON_CHESTPLATE, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.MAP, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.COMPASS, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.CLOCK, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.CARTOGRAPHY_TABLE, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.CAMPFIRE, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.TORCH, 4, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.BREAD, true).weight(1).build())
            .build(consumer);
        ProjectBuilder.project("hit_the_books").aid(Blocks.BOOKSHELF).materialCountOverride(1).baseSuccessChanceOverride(0.5D).rewardMultiplier(0.5D)
            .material(ItemMaterialBuilder.item(Items.BOOK, false).weight(1).build())
            .build(consumer);
        ProjectBuilder.project("master_essence_analysis").requiredResearch("CLUSTER_SYNTHESIS").rewardMultiplier(1.0D)
            .material(ObservationMaterialBuilder.observation(1, true).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CRYSTAL_EARTH.get(), true).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CRYSTAL_SEA.get(), true).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CRYSTAL_SKY.get(), true).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CRYSTAL_SUN.get(), true).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CRYSTAL_MOON.get(), true).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CRYSTAL_BLOOD.get(), true).requiredResearch(Source.BLOOD.getDiscoverKey()).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CRYSTAL_INFERNAL.get(), true).requiredResearch(Source.INFERNAL.getDiscoverKey()).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CRYSTAL_VOID.get(), true).requiredResearch(Source.VOID.getDiscoverKey()).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CRYSTAL_HALLOWED.get(), true).requiredResearch(Source.HALLOWED.getDiscoverKey()).weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CLUSTER_EARTH.get(), true).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CLUSTER_SEA.get(), true).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CLUSTER_SKY.get(), true).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CLUSTER_SUN.get(), true).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CLUSTER_MOON.get(), true).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CLUSTER_BLOOD.get(), true).requiredResearch(Source.BLOOD.getDiscoverKey()).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CLUSTER_INFERNAL.get(), true).requiredResearch(Source.INFERNAL.getDiscoverKey()).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CLUSTER_VOID.get(), true).requiredResearch(Source.VOID.getDiscoverKey()).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_CLUSTER_HALLOWED.get(), true).requiredResearch(Source.HALLOWED.getDiscoverKey()).weight(1).build())
            .build(consumer);
        ProjectBuilder.project("mundane_tinkering")
            .material(ItemMaterialBuilder.item(Items.CRAFTING_TABLE, false).weight(1).build())
            .material(ItemTagMaterialBuilder.tag("minecraft", "anvil", false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.FURNACE, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.BLAST_FURNACE, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.LOOM, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.SMOKER, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.SMITHING_TABLE, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.STONECUTTER, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GRINDSTONE, false).weight(1).build())
            .material(ObservationMaterialBuilder.observation(1, true).weight(5).build())
            .build(consumer);
        ProjectBuilder.project("nether_expedition").requiredResearch(Source.INFERNAL.getDiscoverKey()).rewardMultiplier(0.5D)
            .material(ItemMaterialBuilder.item(Items.DIAMOND_SWORD, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.CROSSBOW, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.ARROW, 16, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.DIAMOND_CHESTPLATE, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.MAP, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.CARTOGRAPHY_TABLE, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.TORCH, 16, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.BREAD, 4, true).weight(1).build())
            .material(ItemMaterialBuilder.item(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.FIRE_RESISTANCE), true).weight(4).build())
            .material(ItemMaterialBuilder.item(Items.MILK_BUCKET, true).weight(1).build())
            .material(ItemTagMaterialBuilder.tag("forge", "obsidian", 10, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.FLINT_AND_STEEL, false).weight(1).build())
            .build(consumer);
        ProjectBuilder.project("observation_analysis").aid(BlocksPM.ANALYSIS_TABLE.get()).materialCountOverride(1).baseSuccessChanceOverride(0.5D).rewardMultiplier(0.5D)
            .material(ObservationMaterialBuilder.observation(1, true).weight(1).build())
            .build(consumer);
        ProjectBuilder.project("piglin_barter").requiredResearch(Source.INFERNAL.getDiscoverKey()).rewardMultiplier(0.5D)
            .material(ItemMaterialBuilder.item(Items.BELL, true).weight(0.5D).build())
            .material(ItemTagMaterialBuilder.tag("forge", "storage_blocks/gold", true).weight(0.5D).build())
            .material(ItemMaterialBuilder.item(Items.CLOCK, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GILDED_BLACKSTONE, true).weight(2).build())
            .material(ItemMaterialBuilder.item(Items.GLISTERING_MELON_SLICE, true).weight(2).build())
            .material(ItemTagMaterialBuilder.tag("forge", "ingots/gold", true).weight(5).build())
            .material(ItemTagMaterialBuilder.tag("forge", "ores/gold", true).weight(2).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_APPLE, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_AXE, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_BOOTS, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_CARROT, true).weight(2).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_CHESTPLATE, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_HELMET, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_HOE, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_HORSE_ARMOR, true).weight(0.5D).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_LEGGINGS, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_PICKAXE, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_SHOVEL, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.GOLDEN_SWORD, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.LIGHT_WEIGHTED_PRESSURE_PLATE, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.NETHER_GOLD_ORE, true).weight(2).build())
            .build(consumer);
        ProjectBuilder.project("portal_detritus").aid(Blocks.NETHER_PORTAL).materialCountOverride(1).baseSuccessChanceOverride(0.5D).rewardMultiplier(0.5D)
            .material(ItemMaterialBuilder.item(ItemsPM.MAGNIFYING_GLASS.get(), false).weight(1).build())
            .build(consumer);
        ProjectBuilder.project("recuperation")
            .material(ItemTagMaterialBuilder.tag("minecraft", "beds", false).weight(2).build())
            .material(ItemMaterialBuilder.item(Items.JUKEBOX, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.BOOK, false).weight(2).build())
            .material(ItemMaterialBuilder.item(Items.COOKED_BEEF, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.BAKED_POTATO, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.MILK_BUCKET, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.CAKE, true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.ROSE_BUSH, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.TNT, true).weight(0.5D).build())
            .build(consumer);
        ProjectBuilder.project("redstone_tinkering").requiredResearch("BASIC_MAGITECH")
            .material(ItemMaterialBuilder.item(Items.DETECTOR_RAIL, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.ACTIVATOR_RAIL, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.DISPENSER, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.DROPPER, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.DAYLIGHT_DETECTOR, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.PISTON, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.HOPPER, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.REDSTONE_LAMP, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.STICKY_PISTON, false).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.COMPARATOR, false).weight(1).build())
            .material(ItemTagMaterialBuilder.tag("forge", "dusts/redstone", 4, true).weight(3).build())
            .material(ObservationMaterialBuilder.observation(1, true).weight(5).build())
            .build(consumer);
        ProjectBuilder.project("spellwork").requiredResearch("BASIC_SORCERY")
            .material(ItemMaterialBuilder.item(ItemsPM.SPELLCRAFTING_ALTAR.get(), false).weight(5).build())
            .material(ItemMaterialBuilder.item(ItemsPM.WAND_INSCRIPTION_TABLE.get(), false).requiredResearch("WAND_INSCRIPTION").weight(2).build())
            .material(ItemMaterialBuilder.item(ItemsPM.WAND_CHARGER.get(), false).requiredResearch("WAND_CHARGER").weight(2).build())
            .material(ItemMaterialBuilder.item(ItemsPM.MUNDANE_WAND.get(), false).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.SPELL_SCROLL_BLANK.get(), true).weight(5).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_DUST_EARTH.get(), true).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_DUST_SEA.get(), true).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_DUST_SKY.get(), true).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_DUST_SUN.get(), true).weight(1).build())
            .material(ItemMaterialBuilder.item(ItemsPM.ESSENCE_DUST_MOON.get(), true).weight(1).build())
            .material(ObservationMaterialBuilder.observation(1, true).weight(5).build())
            .build(consumer);
        ProjectBuilder.project("trade")
            .material(ItemTagMaterialBuilder.tag("forge", "gems/emerald", true).weight(10).build())
            .material(ItemTagMaterialBuilder.tag("forge", "gems/diamond", true).weight(2).build())
            .material(ItemMaterialBuilder.item(Items.COAL, true).weight(2).build())
            .material(ItemMaterialBuilder.item(Items.COMPASS, true).weight(1).build())
            .material(ItemTagMaterialBuilder.tag("forge", "ingots/gold", true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.PUMPKIN, true).weight(1).build())
            .material(ItemTagMaterialBuilder.tag("forge", "string", true).weight(1).build())
            .material(ItemTagMaterialBuilder.tag("forge", "leather", true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.BOOK, true).weight(1).build())
            .material(ItemTagMaterialBuilder.tag("forge", "gems/quartz", true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.WHITE_WOOL, true).weight(1).build())
            .material(ItemTagMaterialBuilder.tag("forge", "ingots/iron", true).weight(1).build())
            .build(consumer);
        ProjectBuilder.project("wand_tinkering").requiredResearch("BASIC_SORCERY")
            .material(ItemMaterialBuilder.item(ItemsPM.WAND_ASSEMBLY_TABLE.get(), false).requiredResearch("ADVANCED_WANDMAKING").weight(3).build())
            .material(ItemMaterialBuilder.item(ItemsPM.HEARTWOOD.get(), true).weight(1).build())
            .material(ItemTagMaterialBuilder.tag("forge", "obsidian", true).weight(1).build())
            .material(ItemTagMaterialBuilder.tag(PrimalMagick.MODID, "coral_blocks", true).weight(1).build())
            .material(ItemMaterialBuilder.item(Items.BAMBOO, true).weight(1).build())
            .material(ItemTagMaterialBuilder.tag(PrimalMagick.MODID, "sunwood_logs", true).weight(1).build())
            .material(ItemTagMaterialBuilder.tag(PrimalMagick.MODID, "moonwood_logs", true).weight(1).build())
            .material(ItemTagMaterialBuilder.tag("forge", "ingots/iron", true).weight(1).build())
            .material(ItemTagMaterialBuilder.tag("forge", "ingots/gold", true).weight(1).build())
            .material(ItemTagMaterialBuilder.tag("forge", "gems/diamond", true).weight(1).build())
            .material(ItemTagMaterialBuilder.tag(PrimalMagick.MODID, "essences/terrestrial_dusts", true).weight(1).build())
            .material(ObservationMaterialBuilder.observation(1, true).weight(5).build())
            .build(consumer);
    }

    @Override
    public String getName() {
        return "Primal Magic Theorycrafting Projects";
    }
}
