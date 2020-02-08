package com.verdantartifice.primalmagic.common.blocks.trees;

import java.util.Random;

import com.verdantartifice.primalmagic.common.blockstates.properties.TimePhase;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

/**
 * Base definition for leaf blocks that phase in and out over time.
 * 
 * @author Daedalus4096
 */
public abstract class AbstractPhasingLeavesBlock extends LeavesBlock {
    public static final EnumProperty<TimePhase> PHASE = EnumProperty.create("phase", TimePhase.class);

    public AbstractPhasingLeavesBlock(Block.Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(PHASE, TimePhase.FULL));
    }
    
    /**
     * Get the current phase of the block based on the current game time.
     * 
     * @param world the game world
     * @return the block's current phase
     */
    protected abstract TimePhase getCurrentPhase(IWorld world);
    
    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(PHASE);
    }
    
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        // Set the block's phase upon placement
        TimePhase phase = this.getCurrentPhase(context.getWorld());
        return super.getStateForPlacement(context).with(PHASE, phase);
    }
    
    @Override
    public BlockRenderLayer getRenderLayer() {
        // Even though not all phases are translucent, this method isn't world-aware
        return BlockRenderLayer.TRANSLUCENT;
    }
    
    @Override
    public void func_225542_b_(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        // Formerly randomTick. Periodically check to see if the block's phase needs to be updated
        super.func_225542_b_(state, worldIn, pos, random);
        TimePhase newPhase = this.getCurrentPhase(worldIn);
        if (newPhase != state.get(PHASE)) {
            worldIn.setBlockState(pos, state.with(PHASE, newPhase), Constants.BlockFlags.DEFAULT);
        }
    }
    
    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        // Immediately check to see if the block's phase needs to be updated when one of its neighbors changes
        BlockState state = super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        TimePhase newPhase = this.getCurrentPhase(worldIn);
        if (newPhase != state.get(PHASE)) {
            state = state.with(PHASE, newPhase);
        }
        return state;
    }
    
    @Override
    public float getBlockHardness(BlockState blockState, IBlockReader worldIn, BlockPos pos) {
        if (blockState.get(PHASE) == TimePhase.FULL) {
            // If the block is fully phased in, use its default hardness as those aren't all the same
            return this.blockHardness;
        } else {
            return blockState.get(PHASE).getHardness();
        }
    }
    
    @Override
    public float getExplosionResistance(BlockState state, IWorldReader world, BlockPos pos, Entity exploder, Explosion explosion) {
        if (state.get(PHASE) == TimePhase.FULL) {
            // If the block is fully phased in, use its default resistance as those aren't all the same
            return this.blockResistance;
        } else {
            return state.get(PHASE).getResistance();
        }
    }

    @Override
    public int getLightValue(BlockState state) {
        return state.get(PHASE).getLightLevel();
    }
}
