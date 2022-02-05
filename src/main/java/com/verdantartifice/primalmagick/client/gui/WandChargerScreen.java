package com.verdantartifice.primalmagick.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.common.containers.WandChargerContainer;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * GUI screen for wand charger block.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class WandChargerScreen extends ContainerScreen<WandChargerContainer> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/gui/wand_charger.png");

    public WandChargerScreen(WandChargerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        // Render background texture
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        // Animate charge progress indicator
        int charge = this.container.getChargeProgressionScaled();
        this.blit(matrixStack, this.guiLeft + 75, this.guiTop + 34, 176, 0, charge + 1, 16);
    }

}
