package com.verdantartifice.primalmagick.common.entities.companions.pixies;

import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.items.misc.PixieItem;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Definition of a majestic blood pixie.  Greatest of the blood pixies.
 * 
 * @author Daedalus4096
 */
public class MajesticBloodPixieEntity extends AbstractBloodPixieEntity implements IMajesticPixie {
    public MajesticBloodPixieEntity(EntityType<? extends AbstractPixieEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected PixieItem getSpawnItem() {
        return ItemsPM.MAJESTIC_BLOOD_PIXIE.get();
    }
}
