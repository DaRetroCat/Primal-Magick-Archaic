package com.verdantartifice.primalmagick.client.gui.widgets.grimoire;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * GUI placeholder to show instead of the progress button while waiting for the server to process.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class ProgressingWidget extends Widget {
    public ProgressingWidget(int xIn, int yIn, ITextComponent msg) {
        super(xIn, yIn, 119, 20, msg);
        this.active = false;
    }
}
