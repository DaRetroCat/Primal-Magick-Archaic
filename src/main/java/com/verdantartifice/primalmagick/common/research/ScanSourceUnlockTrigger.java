package com.verdantartifice.primalmagick.common.research;

import com.verdantartifice.primalmagick.common.sources.Source;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Subtype of research scan trigger that grants the required research for a primal source.  In addition
 * to granting the research, it will also display a localized message in the player's chat window.
 * 
 * @author Daedalus4096
 */
public class ScanSourceUnlockTrigger extends ScanItemResearchTrigger {
    protected final Source source;
    
    public ScanSourceUnlockTrigger(IItemProvider target, Source source) {
        super(target, source.getDiscoverKey(), false);
        this.source = source;
    }
    
    @Override
    public void onMatch(ServerPlayerEntity player, Object obj) {
        super.onMatch(player, obj);
        player.sendStatusMessage(new TranslationTextComponent("event.primalmagick.discover_source." + this.source.getTag()).mergeStyle(TextFormatting.GREEN), false);
    }
}
