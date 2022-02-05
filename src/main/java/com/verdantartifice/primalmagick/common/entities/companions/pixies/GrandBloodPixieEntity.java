package com.verdantartifice.primalmagick.common.entities.companions.pixies;

import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.items.misc.PixieItem;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Definition of a grand blood pixie.  Middle of the blood pixies.
 * 
 * @author Daedalus4096
 */
public class GrandBloodPixieEntity extends AbstractBloodPixieEntity implements IGrandPixie {
    public GrandBloodPixieEntity(EntityType<? extends AbstractPixieEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected PixieItem getSpawnItem() {
        return ItemsPM.GRAND_BLOOD_PIXIE.get();
    }
}
