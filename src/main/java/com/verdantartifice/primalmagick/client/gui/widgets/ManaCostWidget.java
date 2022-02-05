package com.verdantartifice.primalmagick.client.gui.widgets;

import com.verdantartifice.primalmagick.common.sources.Source;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Display widget for showing how much of a given source something has as a mana cost.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class ManaCostWidget extends AbstractSourceWidget {
    public ManaCostWidget(Source source, int amount, int xIn, int yIn) {
        super(source, amount, xIn, yIn);
    }

    @Override
    protected String getTooltipTranslationKey() {
        return "primalmagick.crafting.mana_tooltip";
    }
}
