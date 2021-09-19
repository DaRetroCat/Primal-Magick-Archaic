package com.verdantartifice.primalmagic.client.gui.widgets.research_table;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.verdantartifice.primalmagic.PrimalMagic;
import com.verdantartifice.primalmagic.client.util.GuiUtils;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

/**
 * Display widget for showing all nearby research aids on the research table.
 * 
 * @author Daedalus4096
 */
public class AidListWidget extends AbstractWidget {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagic.MODID, "textures/gui/research_table_overlay.png");
    
    protected final List<Component> aidNames;

    public AidListWidget(int x, int y, List<Component> aidNames) {
        super(x, y, 8, 8, TextComponent.EMPTY);
        this.aidNames = aidNames;
    }
    
    @Override
    public void renderButton(PoseStack matrixStack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        // Draw padlock icon
        matrixStack.pushPose();
        RenderSystem.setShaderTexture(0, TEXTURE);
        matrixStack.translate(this.x, this.y, 0.0F);
        this.blit(matrixStack, 0, 0, 206, 0, 8, 8);
        matrixStack.popPose();

        if (this.isHovered() && !this.aidNames.isEmpty()) {
            // Render tooltip
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(new TranslatableComponent("primalmagic.research_table.aid_header"));
            tooltip.addAll(this.aidNames);
            GuiUtils.renderCustomTooltip(matrixStack, tooltip, this.x, this.y);
        }
    }
    
    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        // Disable click behavior
        return false;
    }

    @Override
    public void updateNarration(NarrationElementOutput p_169152_) {
    }
}