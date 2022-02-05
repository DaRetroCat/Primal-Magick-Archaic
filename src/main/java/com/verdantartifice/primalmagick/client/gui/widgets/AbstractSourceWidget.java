package com.verdantartifice.primalmagick.client.gui.widgets;

import java.awt.Color;
import java.util.Collections;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.verdantartifice.primalmagick.client.util.GuiUtils;
import com.verdantartifice.primalmagick.common.sources.Source;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Base class for display widgets which show a source icon with amount.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public abstract class AbstractSourceWidget extends Widget {
    protected Source source;
    protected int amount;

    public AbstractSourceWidget(Source source, int amount, int xIn, int yIn) {
        super(xIn, yIn, 16, 16, StringTextComponent.EMPTY);
        this.source = source;
        this.amount = amount;
    }
    
    @Override
    public void renderWidget(MatrixStack matrixStack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft mc = Minecraft.getInstance();
        boolean discovered = this.source.isDiscovered(mc.player);
        
        // Draw the colored source icon
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        matrixStack.push();
        if (discovered) {
            mc.getTextureManager().bindTexture(this.source.getImage());
        } else {
            mc.getTextureManager().bindTexture(Source.getUnknownImage());
        }
        matrixStack.translate(this.x, this.y, 0.0F);
        matrixStack.scale(0.0625F, 0.0625F, 0.0625F);
        this.blit(matrixStack, 0, 0, 0, 0, 255, 255);
        matrixStack.pop();
        
        // Draw the amount string
        matrixStack.push();
        ITextComponent amountText = new StringTextComponent(Integer.toString(this.amount));
        int width = mc.fontRenderer.getStringWidth(amountText.getString());
        matrixStack.translate(this.x + 16 - width / 2, this.y + 12, 5.0F);
        matrixStack.scale(0.5F, 0.5F, 0.5F);
        mc.fontRenderer.drawTextWithShadow(matrixStack, amountText, 0.0F, 0.0F, Color.WHITE.getRGB());
        matrixStack.pop();
        
        // Draw the tooltip if applicable
        if (this.isHovered()) {
            ITextComponent sourceText = discovered ? 
                    new TranslationTextComponent(this.source.getNameTranslationKey()).mergeStyle(this.source.getChatColor()) :
                    new TranslationTextComponent(Source.getUnknownTranslationKey());
            ITextComponent labelText = new TranslationTextComponent(this.getTooltipTranslationKey(), this.amount, sourceText);
            GuiUtils.renderCustomTooltip(matrixStack, Collections.singletonList(labelText), this.x, this.y);
        }
    }
    
    /**
     * Get the translation key for this widget's tooltip
     * @return the translation key for this widget's tooltip
     */
    protected abstract String getTooltipTranslationKey();
    
    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        // Disable click behavior
        return false;
    }
}
