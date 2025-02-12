package com.verdantartifice.primalmagick.common.entities.companions.pixies;

import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.items.misc.PixieItem;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Definition of a grand sky pixie.  Middle of the sky pixies.
 * 
 * @author Daedalus4096
 */
public class GrandSkyPixieEntity extends AbstractSkyPixieEntity implements IGrandPixie {
    public GrandSkyPixieEntity(EntityType<? extends AbstractPixieEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected PixieItem getSpawnItem() {
        return ItemsPM.GRAND_SKY_PIXIE.get();
    }
}
