package com.verdantartifice.primalmagick.common.blocks.devices;

import java.util.List;

import com.verdantartifice.primalmagick.common.sources.IManaContainer;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import com.verdantartifice.primalmagick.common.tiles.TileEntityTypesPM;
import com.verdantartifice.primalmagick.common.tiles.devices.HoneyExtractorTileEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

/**
 * Block definition for a honey extractor.  Uses sky mana to spin the honey out of honeycombs.
 * 
 * @author Daedalus4096
 */
public class HoneyExtractorBlock extends BaseEntityBlock {
    protected static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public HoneyExtractorBlock() {
        super(Block.Properties.of(Material.WOOD).strength(1.5F, 6.0F).sound(SoundType.WOOD).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Make the block face the player when placed
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
    
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }
    
    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HoneyExtractorTileEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, TileEntityTypesPM.HONEY_EXTRACTOR.get(), HoneyExtractorTileEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!worldIn.isClientSide && player instanceof ServerPlayer) {
            // Open the GUI for the honey extractor
            BlockEntity tile = worldIn.getBlockEntity(pos);
            if (tile instanceof HoneyExtractorTileEntity) {
                NetworkHooks.openGui((ServerPlayer)player, (HoneyExtractorTileEntity)tile);
            }
        }
        return InteractionResult.SUCCESS;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        // Drop the tile entity's inventory into the world when the block is replaced
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tile = worldIn.getBlockEntity(pos);
            if (tile instanceof HoneyExtractorTileEntity) {
                Containers.dropContents(worldIn, pos, (HoneyExtractorTileEntity)tile);
                worldIn.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        CompoundTag nbt = stack.getTagElement("ManaContainerTag");
        if (nbt != null) {
            SourceList mana = new SourceList();
            mana.deserializeNBT(nbt);
            for (Source source : Source.SORTED_SOURCES) {
                int amount = mana.getAmount(source);
                if (amount > 0) {
                    Component nameComp = source.getNameText();
                    Component line = new TranslatableComponent("primalmagick.source.mana_container_tooltip", nameComp, (amount / 100.0D));
                    tooltip.add(line);
                }
            }
        }
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        BlockEntity tile = worldIn.getBlockEntity(pos);
        if (tile instanceof IManaContainer) {
            CompoundTag nbt = stack.getTagElement("ManaContainerTag");
            if (nbt != null) {
                SourceList mana = new SourceList();
                mana.deserializeNBT(nbt);
                ((IManaContainer)tile).setMana(mana);
            }
        }
    }
}