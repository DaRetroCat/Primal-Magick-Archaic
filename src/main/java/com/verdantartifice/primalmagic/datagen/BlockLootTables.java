package com.verdantartifice.primalmagic.datagen;

import com.verdantartifice.primalmagic.common.blocks.BlocksPM;
import com.verdantartifice.primalmagic.common.items.ItemsPM;

import net.minecraft.data.DataGenerator;

/**
 * Data provider for all of the mod's block loot tables.
 * 
 * @author Daedalus4096
 */
public class BlockLootTables extends BlockLootTableProvider {
    public BlockLootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        this.registerMarbleLootTables();
        this.registerEnchantedMarbleLootTables();
        this.registerSmokedMarbleLootTables();
        this.registerSunwoodLootTables();
        this.registerMoonwoodLootTables();
        this.registerInfusedStoneLootTables();
        
        this.registerBasicTable(BlocksPM.ANALYSIS_TABLE);
        this.registerBasicTable(BlocksPM.ARCANE_WORKBENCH);
        this.registerBasicTable(BlocksPM.WAND_ASSEMBLY_TABLE);
        this.registerBasicTable(BlocksPM.WOOD_TABLE);
        this.registerBasicTable(BlocksPM.CALCINATOR);
    }
    
    private void registerMarbleLootTables() {
        this.registerBasicTable(BlocksPM.MARBLE_RAW);
        this.registerSlabTable(BlocksPM.MARBLE_BRICK_SLAB);
        this.registerBasicTable(BlocksPM.MARBLE_BRICK_STAIRS);
        this.registerBasicTable(BlocksPM.MARBLE_BRICK_WALL);
        this.registerBasicTable(BlocksPM.MARBLE_BRICKS);
        this.registerBasicTable(BlocksPM.MARBLE_CHISELED);
        this.registerBasicTable(BlocksPM.MARBLE_PILLAR);
        this.registerBasicTable(BlocksPM.MARBLE_RUNED);
        this.registerSlabTable(BlocksPM.MARBLE_SLAB);
        this.registerBasicTable(BlocksPM.MARBLE_STAIRS);
        this.registerBasicTable(BlocksPM.MARBLE_WALL);
    }
    
    private void registerEnchantedMarbleLootTables() {
        this.registerBasicTable(BlocksPM.MARBLE_ENCHANTED);
        this.registerSlabTable(BlocksPM.MARBLE_ENCHANTED_BRICK_SLAB);
        this.registerBasicTable(BlocksPM.MARBLE_ENCHANTED_BRICK_STAIRS);
        this.registerBasicTable(BlocksPM.MARBLE_ENCHANTED_BRICK_WALL);
        this.registerBasicTable(BlocksPM.MARBLE_ENCHANTED_BRICKS);
        this.registerBasicTable(BlocksPM.MARBLE_ENCHANTED_CHISELED);
        this.registerBasicTable(BlocksPM.MARBLE_ENCHANTED_PILLAR);
        this.registerBasicTable(BlocksPM.MARBLE_ENCHANTED_RUNED);
        this.registerSlabTable(BlocksPM.MARBLE_ENCHANTED_SLAB);
        this.registerBasicTable(BlocksPM.MARBLE_ENCHANTED_STAIRS);
        this.registerBasicTable(BlocksPM.MARBLE_ENCHANTED_WALL);
    }
    
    private void registerSmokedMarbleLootTables() {
        this.registerBasicTable(BlocksPM.MARBLE_SMOKED);
        this.registerSlabTable(BlocksPM.MARBLE_SMOKED_BRICK_SLAB);
        this.registerBasicTable(BlocksPM.MARBLE_SMOKED_BRICK_STAIRS);
        this.registerBasicTable(BlocksPM.MARBLE_SMOKED_BRICK_WALL);
        this.registerBasicTable(BlocksPM.MARBLE_SMOKED_BRICKS);
        this.registerBasicTable(BlocksPM.MARBLE_SMOKED_CHISELED);
        this.registerBasicTable(BlocksPM.MARBLE_SMOKED_PILLAR);
        this.registerBasicTable(BlocksPM.MARBLE_SMOKED_RUNED);
        this.registerSlabTable(BlocksPM.MARBLE_SMOKED_SLAB);
        this.registerBasicTable(BlocksPM.MARBLE_SMOKED_STAIRS);
        this.registerBasicTable(BlocksPM.MARBLE_SMOKED_WALL);
    }
    
    private void registerSunwoodLootTables() {
        this.registerBasicTable(BlocksPM.SUNWOOD_LOG);
        this.registerBasicTable(BlocksPM.STRIPPED_SUNWOOD_LOG);
        this.registerBasicTable(BlocksPM.SUNWOOD_WOOD);
        this.registerBasicTable(BlocksPM.STRIPPED_SUNWOOD_WOOD);
        this.registerLeavesTable(BlocksPM.SUNWOOD_LEAVES, BlocksPM.SUNWOOD_SAPLING);
        this.registerBasicTable(BlocksPM.SUNWOOD_SAPLING);
        this.registerBasicTable(BlocksPM.SUNWOOD_PLANKS);
        this.registerSlabTable(BlocksPM.SUNWOOD_SLAB);
        this.registerBasicTable(BlocksPM.SUNWOOD_STAIRS);
        this.registerBasicTable(BlocksPM.SUNWOOD_PILLAR);
    }
    
    private void registerMoonwoodLootTables() {
        this.registerBasicTable(BlocksPM.MOONWOOD_LOG);
        this.registerBasicTable(BlocksPM.STRIPPED_MOONWOOD_LOG);
        this.registerBasicTable(BlocksPM.MOONWOOD_WOOD);
        this.registerBasicTable(BlocksPM.STRIPPED_MOONWOOD_WOOD);
        this.registerLeavesTable(BlocksPM.MOONWOOD_LEAVES, BlocksPM.MOONWOOD_SAPLING);
        this.registerBasicTable(BlocksPM.MOONWOOD_SAPLING);
        this.registerBasicTable(BlocksPM.MOONWOOD_PLANKS);
        this.registerSlabTable(BlocksPM.MOONWOOD_SLAB);
        this.registerBasicTable(BlocksPM.MOONWOOD_STAIRS);
        this.registerBasicTable(BlocksPM.MOONWOOD_PILLAR);
    }
    
    private void registerInfusedStoneLootTables() {
        this.registerInfusedStoneTable(BlocksPM.INFUSED_STONE_EARTH, ItemsPM.ESSENCE_DUST_EARTH);
        this.registerInfusedStoneTable(BlocksPM.INFUSED_STONE_SEA, ItemsPM.ESSENCE_DUST_SEA);
        this.registerInfusedStoneTable(BlocksPM.INFUSED_STONE_SKY, ItemsPM.ESSENCE_DUST_SKY);
        this.registerInfusedStoneTable(BlocksPM.INFUSED_STONE_SUN, ItemsPM.ESSENCE_DUST_SUN);
        this.registerInfusedStoneTable(BlocksPM.INFUSED_STONE_MOON, ItemsPM.ESSENCE_DUST_MOON);
    }

    @Override
    public String getName() {
        return "Primal Magic Block Loot Tables";
    }
}
