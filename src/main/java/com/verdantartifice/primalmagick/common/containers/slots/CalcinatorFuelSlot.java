package com.verdantartifice.primalmagick.common.containers.slots;

import com.verdantartifice.primalmagick.common.containers.CalcinatorContainer;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Custom GUI slot for calcinator fuel.
 * 
 * @author Daedalus4096
 */
public class CalcinatorFuelSlot extends Slot {
    protected final CalcinatorContainer container;

    public CalcinatorFuelSlot(CalcinatorContainer container, IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        this.container = container;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        // Only allow valid calcinator fuel or empty buckets
        return this.container.isFuel(stack) || this.isBucket(stack);
    }
    
    @Override
    public int getItemStackLimit(ItemStack stack) {
        // Only one empty bucket at a time
        return this.isBucket(stack) ? 1 : super.getItemStackLimit(stack);
    }
    
    protected boolean isBucket(ItemStack stack) {
        return stack.getItem() == Items.BUCKET;
    }
}
