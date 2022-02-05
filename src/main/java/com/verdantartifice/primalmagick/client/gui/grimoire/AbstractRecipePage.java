package com.verdantartifice.primalmagick.client.gui.grimoire;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.verdantartifice.primalmagick.PrimalMagick;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Base class for all grimoire recipe pages.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public abstract class AbstractRecipePage extends AbstractPage {
    protected static final ResourceLocation OVERLAY = new ResourceLocation(PrimalMagick.MODID, "textures/gui/grimoire_overlay.png");
    
    @Override
    public void render(MatrixStack matrixStack, int side, int x, int y, int mouseX, int mouseY) {
        this.renderTitle(matrixStack, side, x, y, mouseX, mouseY, null);
    }

    @Override
    protected boolean renderTopTitleBar() {
        return false;
    }
}
