package com.verdantartifice.primalmagick.common.research;

import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * Definition of a trigger that grants a specified research entry upon finding a scan match.
 * 
 * @author Daedalus4096
 */
public abstract class AbstractScanResearchTrigger implements IScanTrigger {
    protected static final SimpleResearchKey SCANS_KEY = SimpleResearchKey.parse("UNLOCK_SCANS");

    protected final SimpleResearchKey toUnlock;
    protected final boolean unlockScansPage;
    
    protected AbstractScanResearchTrigger(SimpleResearchKey toUnlock, boolean unlockScansPage) {
        this.toUnlock = toUnlock.copy();
        this.unlockScansPage = unlockScansPage;
    }

    @Override
    public void onMatch(ServerPlayerEntity player, Object obj) {
        if (this.unlockScansPage) {
            ResearchManager.completeResearch(player, SCANS_KEY);
        }
        ResearchManager.completeResearch(player, this.toUnlock);
    }
}
