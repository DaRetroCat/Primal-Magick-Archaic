package com.verdantartifice.primalmagick.client.gui;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.common.research.ResearchEntry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * GUI element for the toast that shows when you complete a research entry.
 * 
 * @author Daedalus4096
 */
public class ResearchToast implements IToast {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/gui/hud.png");
    
    protected ResearchEntry entry;
    
    public ResearchToast(ResearchEntry entry) {
        this.entry = entry;
    }
    
    @Override
    public Visibility func_230444_a_(MatrixStack matrixStack, ToastGui toastGui, long delta) {
    	Minecraft mc = toastGui.getMinecraft();
    	
        // Render the toast background
    	mc.getTextureManager().bindTexture(TEXTURE);
        toastGui.blit(matrixStack, 0, 0, 0, 224, 160, 32);
        
        // Render the toast title text
        ITextComponent titleText = new TranslationTextComponent("primalmagick.toast.title");
        mc.fontRenderer.drawText(matrixStack, titleText, 6, 7, 0x551A8B);
        
        // Render the description of the completed research
        ITextComponent descText = new TranslationTextComponent(this.entry.getNameTranslationKey());
        float width = mc.fontRenderer.getStringWidth(descText.getString());
        if (width > 148.0F) {
            // Scale down the research description to make it fit, if needed
            float scale = (148.0F / width);
            matrixStack.push();
            matrixStack.translate(6.0F, 18.0F, 0.0F);
            matrixStack.scale(scale, scale, scale);
            mc.fontRenderer.drawText(matrixStack, descText, 0, 0, Color.BLACK.getRGB());
            matrixStack.pop();
        } else {
        	mc.fontRenderer.drawText(matrixStack, descText, 6, 18, Color.BLACK.getRGB());
        }
        
        // If the toast has been open long enough, hide it
        return (delta >= 5000L) ? Visibility.HIDE : Visibility.SHOW;
    }

}
