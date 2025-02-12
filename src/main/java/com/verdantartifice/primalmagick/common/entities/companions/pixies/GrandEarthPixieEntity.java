package com.verdantartifice.primalmagick.common.entities.companions.pixies;

import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.items.misc.PixieItem;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Definition of a grand earth pixie.  Middle of the earth pixies.
 * 
 * @author Daedalus4096
 */
public class GrandEarthPixieEntity extends AbstractEarthPixieEntity implements IGrandPixie {
    public GrandEarthPixieEntity(EntityType<? extends AbstractPixieEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected PixieItem getSpawnItem() {
        return ItemsPM.GRAND_EARTH_PIXIE.get();
    }
}
