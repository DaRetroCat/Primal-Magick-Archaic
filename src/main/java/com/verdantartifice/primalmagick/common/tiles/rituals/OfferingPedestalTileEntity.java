package com.verdantartifice.primalmagick.common.tiles.rituals;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.verdantartifice.primalmagick.common.tiles.TileEntityTypesPM;
import com.verdantartifice.primalmagick.common.tiles.base.TileInventoryPM;

/**
 * Definition of an offering pedestal tile entity.  Holds the pedestal's inventory.
 * 
 * @author Daedalus4096
 * @see {@link com.verdantartifice.primalmagick.common.blocks.rituals.OfferingPedestalBlock}
 */
public class OfferingPedestalTileEntity extends TileInventoryPM {
    public OfferingPedestalTileEntity() {
        super(TileEntityTypesPM.OFFERING_PEDESTAL.get(), 1);
    }
    
    @Override
    protected Set<Integer> getSyncedSlotIndices() {
        // Sync the pedestal's item stack for client rendering use
        return ImmutableSet.of(Integer.valueOf(0));
    }
}
