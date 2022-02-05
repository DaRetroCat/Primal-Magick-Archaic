package com.verdantartifice.primalmagick.common.entities.companions.pixies;

import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.items.misc.PixieItem;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Definition of a grand sea pixie.  Middle of the sea pixies.
 * 
 * @author Daedalus4096
 */
public class GrandSeaPixieEntity extends AbstractSeaPixieEntity implements IGrandPixie {
    public GrandSeaPixieEntity(EntityType<? extends AbstractPixieEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected PixieItem getSpawnItem() {
        return ItemsPM.GRAND_SEA_PIXIE.get();
    }
}
