package com.verdantartifice.primalmagick.client.gui.widgets.grimoire;

import java.awt.Color;
import java.util.Collections;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.client.util.GuiUtils;
import com.verdantartifice.primalmagick.common.research.Knowledge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Display widget for showing required knowledge (e.g. observations).
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class KnowledgeWidget extends Widget {
    protected static final ResourceLocation GRIMOIRE_TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/gui/grimoire.png");

    protected Knowledge knowledge;
    protected boolean isComplete;
    
    public KnowledgeWidget(Knowledge knowledge, int x, int y, boolean isComplete) {
        super(x, y, 16, 16, StringTextComponent.EMPTY);
        this.knowledge = knowledge;
        this.isComplete = isComplete;
    }
    
    @Override
    public void renderWidget(MatrixStack matrixStack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft mc = Minecraft.getInstance();
        
        matrixStack.push();
        
        // Draw knowledge type icon
        mc.getTextureManager().bindTexture(this.knowledge.getType().getIconLocation());
        matrixStack.translate(this.x, this.y, 0.0F);
        matrixStack.scale(0.0625F, 0.0625F, 0.0625F);
        this.blit(matrixStack, 0, 0, 0, 0, 255, 255);
        
        matrixStack.pop();
        
        // Draw amount str
        ITextComponent amountText = new StringTextComponent(Integer.toString(this.knowledge.getAmount()));
        int width = mc.fontRenderer.getStringWidth(amountText.getString());
        matrixStack.push();
        matrixStack.translate(this.x + 16 - width / 2, this.y + 12, 5.0F);
        matrixStack.scale(0.5F, 0.5F, 0.5F);
        mc.fontRenderer.drawTextWithShadow(matrixStack, amountText, 0.0F, 0.0F, this.isComplete ? Color.WHITE.getRGB() : Color.RED.getRGB());
        matrixStack.pop();
        
        if (this.isComplete) {
            // Render completion checkmark if appropriate
            matrixStack.push();
            matrixStack.translate(this.x + 8, this.y, 100.0F);
            Minecraft.getInstance().getTextureManager().bindTexture(GRIMOIRE_TEXTURE);
            this.blit(matrixStack, 0, 0, 159, 207, 10, 10);
            matrixStack.pop();
        }
        
        if (this.isHovered()) {
            // Render tooltip
            ITextComponent knowledgeText = new TranslationTextComponent(this.knowledge.getType().getNameTranslationKey());
            GuiUtils.renderCustomTooltip(matrixStack, Collections.singletonList(knowledgeText), this.x, this.y);
        }
    }
    
    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        // Disable click behavior
        return false;
    }
}
