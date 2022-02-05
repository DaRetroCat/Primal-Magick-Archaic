package com.verdantartifice.primalmagick.client.gui.widgets.grimoire;

import java.util.Collections;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.client.util.GuiUtils;
import com.verdantartifice.primalmagick.common.research.SimpleResearchKey;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Display widget for showing a specific required research entry on the requirements page.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class ResearchWidget extends Widget {
    protected static final ResourceLocation BAG_TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/research/research_bag.png");
    protected static final ResourceLocation TUBE_TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/research/research_tube.png");
    protected static final ResourceLocation MAP_TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/research/research_map.png");
    protected static final ResourceLocation UNKNOWN_TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/research/research_unknown.png");
    protected static final ResourceLocation GRIMOIRE_TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/gui/grimoire.png");
    
    protected SimpleResearchKey key;
    protected boolean isComplete;
    
    public ResearchWidget(SimpleResearchKey key, int x, int y, boolean isComplete) {
        super(x, y, 16, 16, StringTextComponent.EMPTY);
        this.key = key;
        this.isComplete = isComplete;
    }
    
    @Override
    public void renderWidget(MatrixStack matrixStack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        // Pick the icon to show based on the prefix of the research key
        ResourceLocation loc;
        if (this.key.getRootKey().startsWith("m_")) {
            loc = MAP_TEXTURE;
        } else if (this.key.getRootKey().startsWith("b_")) {
            loc = BAG_TEXTURE;
        } else if (this.key.getRootKey().startsWith("t_")) {
            loc = TUBE_TEXTURE;
        } else {
            loc = UNKNOWN_TEXTURE;
        }
        
        // Render the icon
        matrixStack.push();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Minecraft.getInstance().getTextureManager().bindTexture(loc);
        matrixStack.translate(this.x, this.y, 0.0F);
        matrixStack.scale(0.0625F, 0.0625F, 0.0625F);
        this.blit(matrixStack, 0, 0, 0, 0, 255, 255);
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
            ITextComponent text = new TranslationTextComponent("primalmagick.research." + this.key.getRootKey() + ".text");
            GuiUtils.renderCustomTooltip(matrixStack, Collections.singletonList(text), this.x, this.y);
        }
    }
    
    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        // Disable click behavior
        return false;
    }
}
