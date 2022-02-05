package com.verdantartifice.primalmagick.common.tiles.crafting;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.verdantartifice.primalmagick.common.affinities.AffinityManager;
import com.verdantartifice.primalmagick.common.blocks.crafting.AbstractCalcinatorBlock;
import com.verdantartifice.primalmagick.common.containers.CalcinatorContainer;
import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.items.essence.EssenceItem;
import com.verdantartifice.primalmagick.common.items.essence.EssenceType;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import com.verdantartifice.primalmagick.common.tiles.base.IOwnedTileEntity;
import com.verdantartifice.primalmagick.common.tiles.base.TileInventoryPM;
import com.verdantartifice.primalmagick.common.util.ItemUtils;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Constants;

/**
 * Base definition of a calcinator tile entity.  Provides the melting functionality for the corresponding
 * block.
 * 
 * @author Daedalus4096
 * @see {@link com.verdantartifice.primalmagick.common.blocks.crafting.AbstractCalcinatorBlock}
 * @see {@link net.minecraft.tileentity.FurnaceTileEntity}
 */
public abstract class AbstractCalcinatorTileEntity extends TileInventoryPM implements ITickableTileEntity, INamedContainerProvider, IOwnedTileEntity {
    protected static final int OUTPUT_CAPACITY = 9;
    
    protected int burnTime;
    protected int burnTimeTotal;
    protected int cookTime;
    protected int cookTimeTotal;
    protected PlayerEntity owner;
    protected UUID ownerUUID;
    
    // Define a container-trackable representation of this tile's relevant data
    protected final IIntArray calcinatorData = new IIntArray() {
        @Override
        public int get(int index) {
            switch (index) {
            case 0:
                return AbstractCalcinatorTileEntity.this.burnTime;
            case 1:
                return AbstractCalcinatorTileEntity.this.burnTimeTotal;
            case 2:
                return AbstractCalcinatorTileEntity.this.cookTime;
            case 3:
                return AbstractCalcinatorTileEntity.this.cookTimeTotal;
            default:
                return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
            case 0:
                AbstractCalcinatorTileEntity.this.burnTime = value;
                break;
            case 1:
                AbstractCalcinatorTileEntity.this.burnTimeTotal = value;
                break;
            case 2:
                AbstractCalcinatorTileEntity.this.cookTime = value;
                break;
            case 3:
                AbstractCalcinatorTileEntity.this.cookTimeTotal = value;
                break;
            }
        }

        @Override
        public int size() {
            return 4;
        }
    };
    
    public AbstractCalcinatorTileEntity(TileEntityType<? extends AbstractCalcinatorTileEntity> tileEntityType) {
        super(tileEntityType, OUTPUT_CAPACITY + 2);
    }
    
    protected boolean isBurning() {
        return this.burnTime > 0;
    }
    
    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        
        this.burnTime = compound.getInt("BurnTime");
        this.burnTimeTotal = compound.getInt("BurnTimeTotal");
        this.cookTime = compound.getInt("CookTime");
        this.cookTimeTotal = compound.getInt("CookTimeTotal");
        
        this.owner = null;
        this.ownerUUID = null;
        if (compound.contains("OwnerUUID")) {
            String ownerUUIDStr = compound.getString("OwnerUUID");
            if (!ownerUUIDStr.isEmpty()) {
                this.ownerUUID = UUID.fromString(ownerUUIDStr);
            }
        }
    }
    
    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("BurnTime", this.burnTime);
        compound.putInt("BurnTimeTotal", this.burnTimeTotal);
        compound.putInt("CookTime", this.cookTime);
        compound.putInt("CookTimeTotal", this.cookTimeTotal);
        if (this.ownerUUID != null) {
            compound.putString("OwnerUUID", this.ownerUUID.toString());
        }
        return super.write(compound);
    }

    @Override
    public void tick() {
        boolean burningAtStart = this.isBurning();
        boolean shouldMarkDirty = false;
        
        if (burningAtStart) {
            this.burnTime--;
        }
        if (!this.world.isRemote) {
            ItemStack inputStack = this.items.get(0);
            ItemStack fuelStack = this.items.get(1);
            if (this.isBurning() || !fuelStack.isEmpty() && !inputStack.isEmpty()) {
                // If the calcinator isn't burning, but has meltable input in place, light it up
                if (!this.isBurning() && this.canCalcinate(inputStack)) {
                    this.burnTime = ForgeHooks.getBurnTime(fuelStack);
                    this.burnTimeTotal = this.burnTime;
                    if (this.isBurning()) {
                        shouldMarkDirty = true;
                        if (fuelStack.hasContainerItem()) {
                            // If the fuel has a container item (e.g. a lava bucket), place the empty container in the fuel slot
                            this.items.set(1, fuelStack.getContainerItem());
                        } else if (!fuelStack.isEmpty()) {
                            // Otherwise, shrink the fuel stack
                            fuelStack.shrink(1);
                            if (fuelStack.isEmpty()) {
                                this.items.set(1, fuelStack.getContainerItem());
                            }
                        }
                    }
                }
                
                // If the calcinator is burning and has meltable input in place, process it
                if (this.isBurning() && this.canCalcinate(inputStack)) {
                    this.cookTime++;
                    if (this.cookTime == this.cookTimeTotal) {
                        this.cookTime = 0;
                        this.cookTimeTotal = this.getCookTimeTotal();
                        this.doCalcination();
                        shouldMarkDirty = true;
                    }
                } else {
                    this.cookTime = 0;
                }
            } else if (!this.isBurning() && this.cookTime > 0) {
                // Decay any cooking progress if the calcinator isn't lit
                this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
            }
            
            if (burningAtStart != this.isBurning()) {
                // Update the tile's block state if the calcinator was lit up or went out this tick
                shouldMarkDirty = true;
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractCalcinatorBlock.LIT, Boolean.valueOf(this.isBurning())), Constants.BlockFlags.DEFAULT);
            }
        }
        if (shouldMarkDirty) {
            this.markDirty();
            this.syncTile(true);
        }
    }

    protected void doCalcination() {
        ItemStack inputStack = this.items.get(0);
        if (!inputStack.isEmpty() && this.canCalcinate(inputStack)) {
            // Merge the items already in the output inventory with the new output items from the melting
            List<ItemStack> currentOutputs = this.items.subList(2, this.items.size());
            List<ItemStack> newOutputs = this.getCalcinationOutput(inputStack, false);
            List<ItemStack> mergedOutputs = ItemUtils.mergeItemStackLists(currentOutputs, newOutputs);
            for (int index = 0; index < Math.min(mergedOutputs.size(), OUTPUT_CAPACITY); index++) {
                ItemStack out = mergedOutputs.get(index);
                this.items.set(index + 2, (out == null ? ItemStack.EMPTY : out));
            }
            
            // Shrink the input stack
            inputStack.shrink(1);
        }
    }

    protected abstract int getCookTimeTotal();

    public static boolean isFuel(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack) > 0;
    }

    protected boolean canCalcinate(ItemStack inputStack) {
        if (inputStack != null && !inputStack.isEmpty()) {
            SourceList sources = AffinityManager.getInstance().getAffinityValues(inputStack, this.world);
            if (sources == null || sources.isEmpty()) {
                // An item without affinities cannot be melted
                return false;
            } else {
                // Merge the items already in the output inventory with the new output items from the melting
                List<ItemStack> currentOutputs = this.items.subList(2, this.items.size());
                List<ItemStack> newOutputs = this.getCalcinationOutput(inputStack, true);   // Force dreg generation to prevent random overflow
                List<ItemStack> mergedOutputs = ItemUtils.mergeItemStackLists(currentOutputs, newOutputs);
                return (mergedOutputs.size() <= OUTPUT_CAPACITY);
            }
        } else {
            return false;
        }
    }
    
    @Nonnull
    protected abstract List<ItemStack> getCalcinationOutput(ItemStack inputStack, boolean alwaysGenerateDregs);
    
    @Nonnull
    protected ItemStack getOutputEssence(EssenceType type, Source source, int count) {
        if (source.isDiscovered(this.getTileOwner())) {
            return EssenceItem.getEssence(type, source, count);
        } else {
            // If the calcinator's owner hasn't discovered the given source, only produce alchemical waste
            return new ItemStack(ItemsPM.ALCHEMICAL_WASTE.get(), count);
        }
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
        return new CalcinatorContainer(windowId, playerInv, this, this.calcinatorData);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(this.getBlockState().getBlock().getTranslationKey());
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack slotStack = this.items.get(index);
        super.setInventorySlotContents(index, stack);
        boolean flag = !stack.isEmpty() && stack.isItemEqual(slotStack) && ItemStack.areItemStackTagsEqual(stack, slotStack);
        if (index == 0 && !flag) {
            this.cookTimeTotal = this.getCookTimeTotal();
            this.cookTime = 0;
            this.markDirty();
        }
    }

    @Override
    public void setTileOwner(PlayerEntity owner) {
        this.owner = owner;
        this.ownerUUID = owner.getUniqueID();
    }

    @Override
    public PlayerEntity getTileOwner() {
        if (this.owner == null && this.ownerUUID != null && this.hasWorld() && this.world instanceof ServerWorld) {
            // If the owner cache is empty, find the entity matching the owner's unique ID
            ServerPlayerEntity player = ((ServerWorld)this.world).getServer().getPlayerList().getPlayerByUUID(this.ownerUUID);
            if (player != null) {
                this.owner = player;
            } else {
                this.ownerUUID = null;
            }
        }
        return this.owner;
    }
}
