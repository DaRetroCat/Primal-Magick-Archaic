package com.verdantartifice.primalmagick.common.tiles.crafting;

import com.verdantartifice.primalmagick.common.blocks.crafting.RunescribingAltarBlock;
import com.verdantartifice.primalmagick.common.containers.RunescribingAltarBasicContainer;
import com.verdantartifice.primalmagick.common.containers.RunescribingAltarEnchantedContainer;
import com.verdantartifice.primalmagick.common.containers.RunescribingAltarForbiddenContainer;
import com.verdantartifice.primalmagick.common.containers.RunescribingAltarHeavenlyContainer;
import com.verdantartifice.primalmagick.common.misc.DeviceTier;
import com.verdantartifice.primalmagick.common.tiles.TileEntityTypesPM;
import com.verdantartifice.primalmagick.common.tiles.base.TilePM;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Definition of a runescribing altar tile entity.
 * 
 * @author Daedalus4096
 */
public class RunescribingAltarTileEntity extends TilePM implements INamedContainerProvider {
    public RunescribingAltarTileEntity() {
        super(TileEntityTypesPM.RUNESCRIBING_ALTAR.get());
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
        if (this.getBlockState().getBlock() instanceof RunescribingAltarBlock) {
            DeviceTier tier = ((RunescribingAltarBlock)this.getBlockState().getBlock()).getDeviceTier();
            switch (tier) {
            case BASIC:
                return new RunescribingAltarBasicContainer(windowId, playerInv);
            case ENCHANTED:
                return new RunescribingAltarEnchantedContainer(windowId, playerInv);
            case FORBIDDEN:
                return new RunescribingAltarForbiddenContainer(windowId, playerInv);
            case HEAVENLY:
                return new RunescribingAltarHeavenlyContainer(windowId, playerInv);
            default:
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(this.getBlockState().getBlock().getTranslationKey());
    }
}
