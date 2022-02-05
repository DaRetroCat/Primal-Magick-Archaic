package com.verdantartifice.primalmagick.client.gui.widgets.research_table;

import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.verdantartifice.primalmagick.common.capabilities.IPlayerKnowledge;
import com.verdantartifice.primalmagick.common.theorycrafting.ObservationProjectMaterial;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Display widget for an observation research project material.  Used on the research table screen.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class ObservationProjectMaterialWidget extends AbstractProjectMaterialWidget {
    protected ObservationProjectMaterial material;
    
    public ObservationProjectMaterialWidget(ObservationProjectMaterial material, int x, int y) {
        super(material, x, y);
        this.material = material;
    }
    
    @Override
    public void renderWidget(MatrixStack matrixStack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        // Draw observation icon
        Minecraft.getInstance().getTextureManager().bindTexture(IPlayerKnowledge.KnowledgeType.OBSERVATION.getIconLocation());
        matrixStack.push();
        matrixStack.translate(this.x, this.y, 0.0F);
        matrixStack.scale(0.0625F, 0.0625F, 0.0625F);
        this.blit(matrixStack, 0, 0, 0, 0, 255, 255);
        matrixStack.pop();
        
        // Draw base class stuff
        super.renderWidget(matrixStack, p_renderButton_1_, p_renderButton_2_, p_renderButton_3_);
    }
    
    @Override
    protected List<ITextComponent> getHoverText() {
        return Collections.singletonList(new TranslationTextComponent(IPlayerKnowledge.KnowledgeType.OBSERVATION.getNameTranslationKey()));
    }
}
