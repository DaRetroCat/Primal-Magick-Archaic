package com.verdantartifice.primalmagick.common.entities.companions.pixies;

import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.items.misc.PixieItem;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Definition of a basic sun pixie.  Weakest of the sun pixies.
 * 
 * @author Daedalus4096
 */
public class BasicSunPixieEntity extends AbstractSunPixieEntity implements IBasicPixie {
    public BasicSunPixieEntity(EntityType<? extends AbstractPixieEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected PixieItem getSpawnItem() {
        return ItemsPM.BASIC_SUN_PIXIE.get();
    }
}
