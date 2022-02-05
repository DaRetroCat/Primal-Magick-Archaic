package com.verdantartifice.primalmagick.common.entities.companions.pixies;

import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.items.misc.PixieItem;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Definition of a basic earth pixie.  Weakest of the earth pixies.
 * 
 * @author Daedalus4096
 */
public class BasicEarthPixieEntity extends AbstractEarthPixieEntity implements IBasicPixie {
    public BasicEarthPixieEntity(EntityType<? extends AbstractPixieEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected PixieItem getSpawnItem() {
        return ItemsPM.BASIC_EARTH_PIXIE.get();
    }
}
