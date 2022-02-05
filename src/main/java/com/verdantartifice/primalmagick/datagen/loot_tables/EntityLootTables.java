package com.verdantartifice.primalmagick.datagen.loot_tables;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.verdantartifice.primalmagick.PrimalMagick;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.verdantartifice.primalmagick.common.entities.EntityTypesPM;
import com.verdantartifice.primalmagick.common.items.ItemsPM;

import net.minecraft.advancements.criterion.EntityFlagsPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.conditions.EntityHasProperty;
import net.minecraft.loot.functions.LootingEnchantBonus;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.loot.functions.Smelt;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Data provider for all of the mod's entity loot tables.
 * 
 * @author Daedalus4096
 */
public class EntityLootTables implements IDataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final EntityPredicate.Builder ON_FIRE = EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().onFire(true).build());

    protected final Map<EntityType<?>, LootTable.Builder> lootTables = new HashMap<>();
    protected final Set<ResourceLocation> registeredEntities = new HashSet<>();
    
    private final DataGenerator generator;

    public EntityLootTables(DataGenerator dataGeneratorIn) {
        this.generator = dataGeneratorIn;
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        // Register all the loot tables with this provider
        this.addTables();

        Map<ResourceLocation, LootTable> tables = new HashMap<>();
        for (Map.Entry<EntityType<?>, LootTable.Builder> entry : this.lootTables.entrySet()) {
            // For each entry in the map, build the loot table and associate it with the entity's loot table location
            tables.put(entry.getKey().getLootTable(), entry.getValue().setParameterSet(LootParameterSets.ENTITY).build());
        }
        
        // Write out the loot table files to disk
        this.writeTables(cache, tables);
        
        // Check the registered loot tables against the registered block set for the mod
        this.checkExpectations();
    }

    private void writeTables(DirectoryCache cache, Map<ResourceLocation, LootTable> tables) {
        Path outputFolder = this.generator.getOutputFolder();
        tables.forEach((key, lootTable) -> {
            Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
            try {
                IDataProvider.save(GSON, cache, LootTableManager.toJson(lootTable), path);
            } catch (IOException e) {
                LOGGER.error("Couldn't write loot table {}", path, e);
            }
        });
    }
    
    private void checkExpectations() {
        // Collect all the resource locations for the blocks defined in this mod
        Set<ResourceLocation> entityTypes = ForgeRegistries.ENTITIES.getKeys().stream().filter(loc -> loc.getNamespace().equals(PrimalMagick.MODID)).collect(Collectors.toSet());
        
        // Warn for each mod entity that didn't have a loot table registered
        entityTypes.removeAll(this.registeredEntities);
        entityTypes.forEach(key -> LOGGER.warn("Missing entity loot table for {}", key.toString()));
    }

    @Override
    public String getName() {
        return "Primal Magic Entity Loot Tables";
    }
    
    private void registerEmptyLootTable(EntityType<?> type) {
        // Just mark that it's been registered without creating a table builder, to track expectations
        this.registeredEntities.add(type.getRegistryName());
    }
    
    private void registerLootTable(EntityType<?> type, LootTable.Builder builder) {
        this.lootTables.put(type, builder);
        this.registeredEntities.add(type.getRegistryName());
    }
    
    protected void addTables() {
        this.registerEmptyLootTable(EntityTypesPM.SPELL_MINE.get());
        this.registerEmptyLootTable(EntityTypesPM.SPELL_PROJECTILE.get());
        this.registerEmptyLootTable(EntityTypesPM.APPLE.get());
        this.registerEmptyLootTable(EntityTypesPM.ALCHEMICAL_BOMB.get());
        this.registerEmptyLootTable(EntityTypesPM.PRIMALITE_TRIDENT.get());
        this.registerEmptyLootTable(EntityTypesPM.HEXIUM_TRIDENT.get());
        this.registerEmptyLootTable(EntityTypesPM.HALLOWSTEEL_TRIDENT.get());
        this.registerEmptyLootTable(EntityTypesPM.FORBIDDEN_TRIDENT.get());
        this.registerEmptyLootTable(EntityTypesPM.SIN_CRASH.get());
        this.registerEmptyLootTable(EntityTypesPM.SIN_CRYSTAL.get());
        this.registerEmptyLootTable(EntityTypesPM.FLYING_CARPET.get());
        this.registerLootTable(EntityTypesPM.TREEFOLK.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.HEARTWOOD.get()).acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 1.0F))).acceptFunction(Smelt.func_215953_b().acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS, ON_FIRE))).acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F))))));
        this.registerEmptyLootTable(EntityTypesPM.INNER_DEMON.get());   // Loot dropped by Inner Demons is special
        this.registerLootTable(EntityTypesPM.PRIMALITE_GOLEM.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.PRIMALITE_INGOT.get()).acceptFunction(SetCount.builder(RandomValueRange.of(3.0F, 5.0F))))));
        this.registerLootTable(EntityTypesPM.HEXIUM_GOLEM.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.HEXIUM_INGOT.get()).acceptFunction(SetCount.builder(RandomValueRange.of(3.0F, 5.0F))))));
        this.registerLootTable(EntityTypesPM.HALLOWSTEEL_GOLEM.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.HALLOWSTEEL_INGOT.get()).acceptFunction(SetCount.builder(RandomValueRange.of(3.0F, 5.0F))))));
        this.registerLootTable(EntityTypesPM.BASIC_EARTH_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_DUST_EARTH.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.GRAND_EARTH_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_SHARD_EARTH.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.MAJESTIC_EARTH_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_CRYSTAL_EARTH.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.BASIC_SEA_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_DUST_SEA.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.GRAND_SEA_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_SHARD_SEA.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.MAJESTIC_SEA_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_CRYSTAL_SEA.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.BASIC_SKY_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_DUST_SKY.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.GRAND_SKY_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_SHARD_SKY.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.MAJESTIC_SKY_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_CRYSTAL_SKY.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.BASIC_SUN_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_DUST_SUN.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.GRAND_SUN_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_SHARD_SUN.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.MAJESTIC_SUN_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_CRYSTAL_SUN.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.BASIC_MOON_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_DUST_MOON.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.GRAND_MOON_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_SHARD_MOON.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.MAJESTIC_MOON_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_CRYSTAL_MOON.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.BASIC_BLOOD_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_DUST_BLOOD.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.GRAND_BLOOD_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_SHARD_BLOOD.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.MAJESTIC_BLOOD_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_CRYSTAL_BLOOD.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.BASIC_INFERNAL_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_DUST_INFERNAL.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.GRAND_INFERNAL_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_SHARD_INFERNAL.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.MAJESTIC_INFERNAL_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_CRYSTAL_INFERNAL.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.BASIC_VOID_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_DUST_VOID.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.GRAND_VOID_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_SHARD_VOID.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.MAJESTIC_VOID_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_CRYSTAL_VOID.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.BASIC_HALLOWED_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_DUST_HALLOWED.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.GRAND_HALLOWED_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_SHARD_HALLOWED.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
        this.registerLootTable(EntityTypesPM.MAJESTIC_HALLOWED_PIXIE.get(), LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(ItemsPM.ESSENCE_CRYSTAL_HALLOWED.get()).acceptFunction(SetCount.builder(ConstantRange.of(1))))));
    }
}
