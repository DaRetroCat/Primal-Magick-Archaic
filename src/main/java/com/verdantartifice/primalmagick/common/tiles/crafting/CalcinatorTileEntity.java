package com.verdantartifice.primalmagick.common.tiles.crafting;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.verdantartifice.primalmagick.common.affinities.AffinityManager;
import com.verdantartifice.primalmagick.common.blocks.crafting.CalcinatorBlock;
import com.verdantartifice.primalmagick.common.items.essence.EssenceType;
import com.verdantartifice.primalmagick.common.misc.DeviceTier;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import com.verdantartifice.primalmagick.common.tiles.TileEntityTypesPM;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

/**
 * Definition of a proper calcinator tile entity.  Provides the melting functionality for the corresponding
 * device.  Works quickly and generates essence based on item affinity, with a chance for dregs on low-affinity
 * items.
 * 
 * @author Daedalus4096
 */
public class CalcinatorTileEntity extends AbstractCalcinatorTileEntity {
    public CalcinatorTileEntity() {
        super(TileEntityTypesPM.CALCINATOR.get());
    }
    
    @Override
    protected int getCookTimeTotal() {
        return 100;
    }

    @Override
    @Nonnull
    protected List<ItemStack> getCalcinationOutput(ItemStack inputStack, boolean alwaysGenerateDregs) {
        List<ItemStack> output = new ArrayList<>();
        SourceList sources = AffinityManager.getInstance().getAffinityValues(inputStack, this.world);
        EssenceType maxEssenceType = this.getMaxOutputEssenceType();
        if (sources != null && !sources.isEmpty()) {
            for (Source source : Source.SORTED_SOURCES) {
                int amount = sources.getAmount(source);
                if (amount > 0) {
                    EssenceType currentEssenceType = maxEssenceType;
                    while (currentEssenceType != null) {
                        if (amount >= currentEssenceType.getAffinity()) {
                            // Generate output for each affinity multiple in the input stack
                            int count = (amount / currentEssenceType.getAffinity());
                            ItemStack stack = this.getOutputEssence(currentEssenceType, source, count);
                            if (!stack.isEmpty()) {
                                output.add(stack);
                                break;
                            }
                        } else {
                            currentEssenceType = currentEssenceType.getDowngrade();
                        }
                    }
                    if (currentEssenceType == null && amount > 0 && (alwaysGenerateDregs || this.world.rand.nextInt(EssenceType.DUST.getAffinity()) < amount)) {
                        // If the item's affinity is too low for guaranteed essence, give a random chance of generating a dust anyway
                        ItemStack stack = this.getOutputEssence(EssenceType.DUST, source, 1);
                        if (!stack.isEmpty()) {
                            output.add(stack);
                        }
                    }
                }
            }
        }
        return output;
    }
    
    @Nonnull
    protected EssenceType getMaxOutputEssenceType() {
        Block block = this.getBlockState().getBlock();
        if (block instanceof CalcinatorBlock) {
            CalcinatorBlock calcinatorBlock = (CalcinatorBlock)block;
            DeviceTier tier = calcinatorBlock.getDeviceTier();
            switch (tier) {
            case BASIC:
                return EssenceType.DUST;
            case ENCHANTED:
                return EssenceType.SHARD;
            case FORBIDDEN:
                return EssenceType.CRYSTAL;
            case HEAVENLY:
                return EssenceType.CLUSTER;
            default:
                throw new IllegalStateException("Unknown device tier " + tier);
            }
        } else {
            throw new IllegalStateException("Unknown block type " + block);
        }
    }
}
