package com.verdantartifice.primalmagick.common.entities.companions.pixies;

import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.items.misc.PixieItem;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Definition of a grand void pixie.  Middle of the void pixies.
 * 
 * @author Daedalus4096
 */
public class GrandVoidPixieEntity extends AbstractVoidPixieEntity implements IGrandPixie {
    public GrandVoidPixieEntity(EntityType<? extends AbstractPixieEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected PixieItem getSpawnItem() {
        return ItemsPM.GRAND_VOID_PIXIE.get();
    }
}
