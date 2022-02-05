package com.verdantartifice.primalmagick.common.blocks.golems;

import javax.annotation.Nullable;

import com.verdantartifice.primalmagick.common.entities.companions.CompanionManager;
import com.verdantartifice.primalmagick.common.entities.companions.golems.AbstractEnchantedGolemEntity;
import com.verdantartifice.primalmagick.common.research.SimpleResearchKey;
import com.verdantartifice.primalmagick.common.wands.IInteractWithWand;
import com.verdantartifice.primalmagick.common.wands.IWand;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockMaterialMatcher;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

/**
 * Definition for the "head" block of an enchanted golem.  Place it on top of a T shape of
 * enchanted metal blocks, then use a wand on it, and a golem entity will be created.
 * 
 * @author Daedalus4096
 */
public abstract class AbstractEnchantedGolemControllerBlock<T extends AbstractEnchantedGolemEntity> extends Block implements IInteractWithWand {
    protected static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    @Nullable
    private BlockPattern golemPattern;

    public AbstractEnchantedGolemControllerBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
    }
    
    protected abstract SimpleResearchKey getRequiredResearch();
    
    protected abstract EntityType<T> getEntityType();
    
    protected abstract Block getBaseBlock();
    
    protected abstract Block getControllerBlock();

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        // Make the block face the player when placed
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }
    
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }
    
    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    
    @Override
    public ActionResultType onWandRightClick(ItemStack wandStack, World world, PlayerEntity player, BlockPos pos, Direction direction) {
        if (!world.isRemote && wandStack.getItem() instanceof IWand && this.getRequiredResearch().isKnownByStrict(player)) {
            BlockPattern.PatternHelper helper = this.getGolemPattern().match(world, pos);
            if (helper != null) {
                for (int i = 0; i < this.getGolemPattern().getPalmLength(); i++) {
                    for (int j = 0; j < this.getGolemPattern().getThumbLength(); j++) {
                        CachedBlockInfo info = helper.translateOffset(i, j, 0);
                        world.setBlockState(info.getPos(), Blocks.AIR.getDefaultState(), Constants.BlockFlags.BLOCK_UPDATE);
                        world.playEvent(2001, info.getPos(), Block.getStateId(info.getBlockState()));
                    }
                }
                
                BlockPos blockpos = helper.translateOffset(1, 2, 0).getPos();
                AbstractEnchantedGolemEntity golem = this.getEntityType().create(world);
                CompanionManager.addCompanion(player, golem);
                golem.setLocationAndAngles((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.05D, (double)blockpos.getZ() + 0.5D, 0.0F, 0.0F);
                world.addEntity(golem);

                for (int i = 0; i < this.getGolemPattern().getPalmLength(); i++) {
                    for (int j = 0; j < this.getGolemPattern().getThumbLength(); j++) {
                        CachedBlockInfo info = helper.translateOffset(i, j, 0);
                        world.updateBlock(info.getPos(), Blocks.AIR);
                    }
                }

                return ActionResultType.SUCCESS;
            } else {
                return ActionResultType.FAIL;
            }
        } else {
            return ActionResultType.FAIL;
        }
    }

    @Override
    public void onWandUseTick(ItemStack wandStack, PlayerEntity player, int count) {
        // Do nothing; golem controllers don't support wand channeling
    }
    
    protected BlockPattern getGolemPattern() {
        if (this.golemPattern == null) {
            this.golemPattern = BlockPatternBuilder.start().aisle("~^~", "###", "~#~")
                    .where('^', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(this.getControllerBlock())))
                    .where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(this.getBaseBlock())))
                    .where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR)))
                    .build();
        }
        return this.golemPattern;
    }
}
