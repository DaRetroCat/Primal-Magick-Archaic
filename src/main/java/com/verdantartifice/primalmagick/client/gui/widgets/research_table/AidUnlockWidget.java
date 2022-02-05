package com.verdantartifice.primalmagick.client.gui.widgets.research_table;

import java.util.Collections;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.client.util.GuiUtils;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Display widget for showing that a project was unlocked by a research aid in the research table GUI.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class AidUnlockWidget extends Widget {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/gui/research_table_overlay.png");
    
    protected Block aidBlock;

    public AidUnlockWidget(int x, int y, @Nonnull Block aidBlock) {
        super(x, y, 8, 8, StringTextComponent.EMPTY);
        this.aidBlock = aidBlock;
    }
    
    @Override
    public void renderWidget(MatrixStack matrixStack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft mc = Minecraft.getInstance();
        
        // Draw padlock icon
        matrixStack.push();
        mc.getTextureManager().bindTexture(TEXTURE);
        matrixStack.translate(this.x, this.y, 0.0F);
        this.blit(matrixStack, 0, 0, 198, 0, 8, 8);
        matrixStack.pop();

        if (this.isHovered() && this.aidBlock != null) {
            // Render tooltip
            ITextComponent unlockText = new TranslationTextComponent("primalmagick.research_table.unlock", this.aidBlock.getTranslatedName());
            GuiUtils.renderCustomTooltip(matrixStack, Collections.singletonList(unlockText), this.x, this.y);
        }
    }
    
    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        // Disable click behavior
        return false;
    }
}
