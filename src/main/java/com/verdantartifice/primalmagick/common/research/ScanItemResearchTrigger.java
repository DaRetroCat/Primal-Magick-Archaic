package com.verdantartifice.primalmagick.common.research;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.IItemProvider;

/**
 * Definition of a trigger that grants a specified research entry upon scanning a given block/item.
 * 
 * @author Daedalus4096
 */
public class ScanItemResearchTrigger extends AbstractScanResearchTrigger {
    protected final IItemProvider target;
    
    public ScanItemResearchTrigger(IItemProvider target, SimpleResearchKey toUnlock) {
        this(target, toUnlock, true);
    }
    
    public ScanItemResearchTrigger(IItemProvider target, SimpleResearchKey toUnlock, boolean unlockScansPage) {
        super(toUnlock, unlockScansPage);
        this.target = target;
    }

    @Override
    public boolean matches(ServerPlayerEntity player, Object obj) {
        if (obj instanceof IItemProvider) {
            return target.asItem().equals(((IItemProvider)obj).asItem());
        } else {
            return false;
        }
    }
}
