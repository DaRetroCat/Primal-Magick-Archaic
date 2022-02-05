package com.verdantartifice.primalmagick.client.gui.widgets.research_table;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.client.util.GuiUtils;
import com.verdantartifice.primalmagick.common.theorycrafting.AbstractProjectMaterial;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Base class for a display widget for a research project material.  Used on the research table screen.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public abstract class AbstractProjectMaterialWidget extends Widget {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/gui/research_table_overlay.png");

    protected boolean complete;
    protected boolean consumed;
    
    public AbstractProjectMaterialWidget(AbstractProjectMaterial material, int x, int y) {
        super(x, y, 16, 16, StringTextComponent.EMPTY);
        Minecraft mc = Minecraft.getInstance();
        this.consumed = material.isConsumed();
        this.complete = material.isSatisfied(mc.player);
    }
    
    @Override
    public void renderWidget(MatrixStack matrixStack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
        if (this.complete) {
            // Render completion checkmark if appropriate
            matrixStack.push();
            matrixStack.translate(this.x + 8, this.y, 200.0F);
            this.blit(matrixStack, 0, 0, 162, 0, 10, 10);
            matrixStack.pop();
        }
        if (this.consumed) {
            // Render consumption exclamation point if appropriate
            matrixStack.push();
            matrixStack.translate(this.x - 3, this.y, 200.0F);
            this.blit(matrixStack, 0, 0, 172, 0, 10, 10);
            matrixStack.pop();
        }
        if (this.isHovered()) {
            // Render tooltip
            GuiUtils.renderCustomTooltip(matrixStack, this.getHoverText(), this.x, this.y);
        }
    }
    
    /**
     * Get the text component to show in a tooltip when this widget is hovered over.
     * 
     * @return the text component to show in a tooltip
     */
    protected abstract List<ITextComponent> getHoverText();
    
    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        // Disable click behavior
        return false;
    }
}
