package com.verdantartifice.primalmagick.common.entities.companions.pixies;

import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.items.misc.PixieItem;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

/**
 * Definition of a majestic sea pixie.  Greatest of the sea pixies.
 * 
 * @author Daedalus4096
 */
public class MajesticSeaPixieEntity extends AbstractSeaPixieEntity implements IMajesticPixie {
    public MajesticSeaPixieEntity(EntityType<? extends AbstractPixieEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected PixieItem getSpawnItem() {
        return ItemsPM.MAJESTIC_SEA_PIXIE.get();
    }
}
