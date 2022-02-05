package com.verdantartifice.primalmagick.common.crafting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.verdantartifice.primalmagick.common.research.CompoundResearchKey;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Wand transformation that turns a block into something else.
 * 
 * @author Daedalus4096
 */
public class WandTransformBlock extends AbstractWandTransform {
    protected final Block target;
    
    public WandTransformBlock(@Nonnull Block target, @Nonnull ItemStack result, @Nullable CompoundResearchKey research) {
        super(result, research);
        this.target = target;
    }

    @Override
    public boolean isValid(World world, PlayerEntity player, BlockPos pos) {
        // The expected block type must be at the given world position and the given player must know the right research
        return super.isValid(world, player, pos) && world.getBlockState(pos).getBlock() == this.target;
    }
}
