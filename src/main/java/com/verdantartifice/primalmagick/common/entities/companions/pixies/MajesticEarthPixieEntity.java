package com.verdantartifice.primalmagick.common.entities.companions.pixies;

import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.items.misc.PixieItem;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Definition of a majestic earth pixie.  Greatest of the earth pixies.
 * 
 * @author Daedalus4096
 */
public class MajesticEarthPixieEntity extends AbstractEarthPixieEntity implements IMajesticPixie {
    public MajesticEarthPixieEntity(EntityType<? extends AbstractPixieEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected PixieItem getSpawnItem() {
        return ItemsPM.MAJESTIC_EARTH_PIXIE.get();
    }
}
