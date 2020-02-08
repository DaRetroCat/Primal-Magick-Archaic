package com.verdantartifice.primalmagic.common.containers.slots;

import com.verdantartifice.primalmagic.PrimalMagic;
import com.verdantartifice.primalmagic.common.items.wands.WandCapItem;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Custom GUI slot for wand cap inputs.
 * 
 * @author Daedalus4096
 */
public class WandCapSlot extends Slot {
    protected static final ResourceLocation ATLAS_TEXTURE = new ResourceLocation("textures/atlas/blocks.png");
    protected static final ResourceLocation SLOT_TEXTURE = new ResourceLocation(PrimalMagic.MODID, "item/empty_wand_cap_slot");
    
    public WandCapSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        this.setBackground(ATLAS_TEXTURE, SLOT_TEXTURE);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        // Only allow wand caps to be dropped into the slot
        return stack.getItem() instanceof WandCapItem;
    }
}
