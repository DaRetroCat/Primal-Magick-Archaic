package com.verdantartifice.primalmagick.common.blocks.rituals;

import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.client.fx.FxDispatcher;
import com.verdantartifice.primalmagick.common.items.essence.EssenceItem;
import com.verdantartifice.primalmagick.common.rituals.IRitualPropBlock;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.tiles.rituals.EntropySinkTileEntity;
import com.verdantartifice.primalmagick.common.misc.HarvestLevel;
import com.verdantartifice.primalmagick.common.util.VoxelShapeUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.Constants;

import java.awt.*;
import java.util.Random;

/**
 * Block definition for an entropy sink.  Entropy sinks are optional props used in rituals to
 * reduce instability at the cost of essence.
 *
 * @author Daedalus4096
 */
public class EntropySinkBlock extends Block implements IRitualPropBlock {
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    protected static final VoxelShape SHAPE = VoxelShapeUtils.fromModel(new ResourceLocation(PrimalMagick.MODID, "block/entropy_sink"));

    public EntropySinkBlock() {
        super(Properties.create(Material.ROCK, MaterialColor.GOLD).hardnessAndResistance(3.5F).sound(SoundType.STONE).harvestTool(ToolType.PICKAXE).harvestLevel(HarvestLevel.NONE.getLevel()));
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH));
        this.setDefaultState(this.getDefaultState().with(LIT, Boolean.FALSE));
    }

    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(FACING, LIT);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation direction) {
        return state.with(FACING, direction.rotate(state.get(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new EntropySinkTileEntity();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        // Show glittering particles
        super.animateTick(stateIn, worldIn, pos, rand);
        if (this.isBlockSaltPowered(worldIn, pos)) {
            FxDispatcher.INSTANCE.spellTrail(pos.getX() + rand.nextDouble(), pos.getY() + rand.nextDouble(), pos.getZ() + rand.nextDouble(), Color.WHITE.getRGB());
        }

        // Show redstone particles if glowing
        if (stateIn.get(LIT)) {
            double x = (double)pos.getX() + (0.625D * rand.nextDouble()) + 0.1875D;
            double y = (double)pos.getY() + 1.0D;
            double z = (double)pos.getZ() + (0.625D * rand.nextDouble()) + 0.1875D;
            worldIn.addParticle(RedstoneParticleData.REDSTONE_DUST, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }


    @Override
    public float getStabilityBonus(World world, BlockPos pos) {
        return 0.02F;
    }

    @Override
    public float getSymmetryPenalty(World world, BlockPos pos) {
        return 0.02F;
    }

    @Override
    public boolean isPropActivated(BlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return (tile instanceof EntropySinkTileEntity && ((EntropySinkTileEntity) tile).isGlowing());
    }

    @Override
    public String getPropTranslationKey() {
        return "primalmagick.ritual.prop.entropy_sink";
    }

    public float getUsageStabilityBonus(EssenceItem item) {
        // Determine amount based on type of essence used
        return (float)item.getEssenceType().getAffinity();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack stack = player.getHeldItem(handIn);
        if (player != null && stack.getItem() instanceof EssenceItem && !this.isPropActivated(state, worldIn, pos)) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (!worldIn.isRemote && tile instanceof EntropySinkTileEntity) {
                // Start the sink glowing
                worldIn.setBlockState(pos, state.with(EntropySinkBlock.LIT, Boolean.TRUE), Constants.BlockFlags.DEFAULT_AND_RERENDER);
                ((EntropySinkTileEntity) tile).startGlowing();

                // If this block is awaiting activation for an altar, notify it
                if (this.isPropOpen(state, worldIn, pos)) {
                    this.onPropActivated(state, worldIn, pos, this.getUsageStabilityBonus((EssenceItem)stack.getItem()));
                }

                // Consume the used essence
                if (!player.abilities.isCreativeMode) {
                    stack.shrink(1);
                    if (stack.getCount() <= 0) {
                        player.setHeldItem(handIn, ItemStack.EMPTY);
                    }
                }
            }
            return ActionResultType.SUCCESS;
        } else {
            return ActionResultType.PASS;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        // Close out any pending ritual activity if replaced
        if (!worldIn.isRemote && state.getBlock() != newState.getBlock()) {
            this.closeProp(state, worldIn, pos);
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
        // Pass any received events on to the tile entity and let it decide what to do with it
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tile = worldIn.getTileEntity(pos);
        return (tile == null) ? false : tile.receiveClientEvent(id, param);
    }
    @Override
    public boolean isUniversal() {
        return true;
    }
}
