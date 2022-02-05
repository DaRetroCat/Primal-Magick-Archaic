package com.verdantartifice.primalmagick.common.items.misc;

import javax.annotation.Nullable;

import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.client.renderers.itemstack.ArcanometerISTER;
import com.verdantartifice.primalmagick.common.network.PacketHandler;
import com.verdantartifice.primalmagick.common.network.packets.misc.ScanEntityPacket;
import com.verdantartifice.primalmagick.common.network.packets.misc.ScanItemPacket;
import com.verdantartifice.primalmagick.common.network.packets.misc.ScanPositionPacket;
import com.verdantartifice.primalmagick.common.research.ResearchManager;
import com.verdantartifice.primalmagick.common.sounds.SoundsPM;
import com.verdantartifice.primalmagick.common.util.EntityUtils;
import com.verdantartifice.primalmagick.common.util.RayTraceUtils;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Item definition for an arcanometer.  An arcanometer is a PKE meter-like device that scans the 
 * primal affinities of blocks and items.  It is intended to be an upgrade to the analysis table.
 *  
 * @author Daedalus4096
 */
public class ArcanometerItem extends Item {
    public static final ResourceLocation SCAN_STATE_PROPERTY = new ResourceLocation(PrimalMagick.MODID, "scan_state");

    public ArcanometerItem() {
        super(new Item.Properties().group(PrimalMagick.ITEM_GROUP).maxStackSize(1).rarity(Rarity.UNCOMMON).setISTER(() -> ArcanometerISTER::new));
    }
    
    public static IItemPropertyGetter getScanStateProperty() {
    	return new IItemPropertyGetter() {
            @OnlyIn(Dist.CLIENT)
            protected float scanState = 0;

            @OnlyIn(Dist.CLIENT)
            @Override
            public float call(ItemStack stack, ClientWorld world, LivingEntity entity) {
                if (entity == null || !(entity instanceof PlayerEntity)) {
                    return 0.0F;
                } else {
                    // If the currently moused-over block/item has not yet been scanned, raise the antennae
                    if (isMouseOverScannable(RayTraceUtils.getMouseOver(), world, (PlayerEntity)entity)) {
                        this.incrementScanState();
                    } else {
                        this.decrementScanState();
                    }
                    return scanState;
                }
            }
            
            @OnlyIn(Dist.CLIENT)
            protected void incrementScanState() {
                this.scanState = Math.min(4.0F, this.scanState + 0.25F);
            }
            
            @OnlyIn(Dist.CLIENT)
            protected void decrementScanState() {
                this.scanState = Math.max(0.0F, this.scanState - 0.25F);
            }
        };
    }
    
    public static boolean isMouseOverScannable(@Nullable RayTraceResult result, @Nullable World world, @Nullable PlayerEntity player) {
        if (result == null || world == null) {
            return false;
        } else if (result.getType() == RayTraceResult.Type.ENTITY) {
            // If the current mouseover is an entity, try to get its corresponding item and scan that if it has one, otherwise scan the entity itself
            Entity entity = ((EntityRayTraceResult)result).getEntity();
            ItemStack stack = EntityUtils.getEntityItemStack(entity);
            if (!stack.isEmpty()) {
                return !ResearchManager.isScanned(stack, player);
            } else {
                return !ResearchManager.isScanned(entity.getType(), player);
            }
        } else if (result.getType() == RayTraceResult.Type.BLOCK) {
            // If the current mouseover is a block, try to get its corresponding block item and scan that
            BlockPos pos = ((BlockRayTraceResult)result).getPos();
            ItemStack stack = new ItemStack(world.getBlockState(pos).getBlock());
            return !stack.isEmpty() && !ResearchManager.isScanned(stack, player);
        } else {
            return false;
        }
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (worldIn.isRemote) {
            RayTraceResult result = RayTraceUtils.getMouseOver();
            if (result != null && result.getType() != RayTraceResult.Type.MISS) {
                // If something is being moused over, play the sound effect for the player and send a scan packet to the server
                worldIn.playSound(playerIn, playerIn.getPosition(), SoundsPM.SCAN.get(), SoundCategory.MASTER, 1.0F, 1.0F);
                if (result.getType() == RayTraceResult.Type.ENTITY) {
                    Entity entity = ((EntityRayTraceResult)result).getEntity();
                    ItemStack entityStack = EntityUtils.getEntityItemStack(entity);
                    if (!entityStack.isEmpty()) {
                        PacketHandler.sendToServer(new ScanItemPacket(entityStack));
                    } else {
                        PacketHandler.sendToServer(new ScanEntityPacket(entity.getType()));
                    }
                } else if (result.getType() == RayTraceResult.Type.BLOCK) {
                    BlockPos pos = ((BlockRayTraceResult)result).getPos();
                    PacketHandler.sendToServer(new ScanPositionPacket(pos));
                }
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
