package com.verdantartifice.primalmagic.common.init;

import com.verdantartifice.primalmagic.PrimalMagic;
import com.verdantartifice.primalmagic.common.blocks.base.StairsBlockPM;
import com.verdantartifice.primalmagic.common.blocks.crafting.ArcaneWorkbenchBlock;
import com.verdantartifice.primalmagic.common.blocks.crafting.WandAssemblyTableBlock;
import com.verdantartifice.primalmagic.common.blocks.mana.AncientManaFontBlock;
import com.verdantartifice.primalmagic.common.blocks.misc.AnalysisTableBlock;
import com.verdantartifice.primalmagic.common.blocks.misc.MarblePillarBlock;
import com.verdantartifice.primalmagic.common.blocks.misc.WoodTableBlock;
import com.verdantartifice.primalmagic.common.sources.Source;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.registries.IForgeRegistry;

public class InitBlocks {
    public static void initBlocks(IForgeRegistry<Block> registry) {
        Block marbleRaw = new Block(Block.Properties.create(Material.ROCK, MaterialColor.QUARTZ).hardnessAndResistance(1.5F, 6.0F).sound(SoundType.STONE)).setRegistryName(PrimalMagic.MODID, "marble_raw");
        registry.register(marbleRaw);
        registry.register(new SlabBlock(Block.Properties.from(marbleRaw)).setRegistryName(PrimalMagic.MODID, "marble_slab"));
        registry.register(new StairsBlockPM(marbleRaw.getDefaultState(), Block.Properties.from(marbleRaw)).setRegistryName(PrimalMagic.MODID, "marble_stairs"));
        Block marbleBricks = new Block(Block.Properties.from(marbleRaw)).setRegistryName(PrimalMagic.MODID, "marble_bricks");
        registry.register(marbleBricks);
        registry.register(new SlabBlock(Block.Properties.from(marbleRaw)).setRegistryName(PrimalMagic.MODID, "marble_brick_slab"));
        registry.register(new StairsBlockPM(marbleBricks.getDefaultState(), Block.Properties.from(marbleBricks)).setRegistryName(PrimalMagic.MODID, "marble_brick_stairs"));
        registry.register(new MarblePillarBlock());
        registry.register(new Block(Block.Properties.from(marbleRaw)).setRegistryName(PrimalMagic.MODID, "marble_chiseled"));
        registry.register(new Block(Block.Properties.from(marbleRaw)).setRegistryName(PrimalMagic.MODID, "marble_runed"));
        registry.register(new Block(Block.Properties.create(Material.ROCK, MaterialColor.QUARTZ).hardnessAndResistance(1.5F, 6.0F)).setRegistryName(PrimalMagic.MODID, "marble_enchanted"));
        registry.register(new ArcaneWorkbenchBlock());
        registry.register(new AncientManaFontBlock(Source.EARTH));
        registry.register(new AncientManaFontBlock(Source.SEA));
        registry.register(new AncientManaFontBlock(Source.SKY));
        registry.register(new AncientManaFontBlock(Source.SUN));
        registry.register(new AncientManaFontBlock(Source.MOON));
        registry.register(new WandAssemblyTableBlock());
        registry.register(new WoodTableBlock());
        registry.register(new AnalysisTableBlock());
    }
}
