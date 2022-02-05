package com.verdantartifice.primalmagick.client.gui.grimoire;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Rendering interface for an element of a grimoire page
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public interface IPageElement {
    /**
     * Render this page element
     * @param matrixStack the current rendering matrix stack
     * @param side the side of the grimoire on which the page lies; 0 for left, 1 for right
     * @param x the page-relative X-coordinate at which to render this element
     * @param y the page-relative Y-coordinate at which to render this element
     */
    public void render(MatrixStack matrixStack, int side, int x, int y);
    
    /**
     * Get the Y-coordinate at which to render the next page element
     * @param y the page-relative Y-coordinate of this element
     * @return the page-relative Y-coordinate at which to render the next element
     */
    public int getNextY(int y);
}
