package com.verdantartifice.primalmagick.client.gui;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.client.gui.widgets.ManaCostWidget;
import com.verdantartifice.primalmagick.common.containers.ArcaneWorkbenchContainer;
import com.verdantartifice.primalmagick.common.crafting.IArcaneRecipe;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.sources.SourceList;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * GUI screen for arcane workbench block.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class ArcaneWorkbenchScreen extends ContainerScreen<ArcaneWorkbenchContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/gui/arcane_workbench.png");

    public ArcaneWorkbenchScreen(ArcaneWorkbenchContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.ySize = 183;
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.initWidgets();
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        this.blit(matrixStack, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        // Generate text in the case that the current recipe, or lack there of, does not have a mana cost
        IArcaneRecipe activeArcaneRecipe = this.container.getActiveArcaneRecipe();
        if (activeArcaneRecipe == null || activeArcaneRecipe.getManaCosts() == null || activeArcaneRecipe.getManaCosts().isEmpty()) {
            ITextComponent text = new TranslationTextComponent("primalmagick.crafting.no_mana");
            int width = this.font.getStringWidth(text.getString());
            int x = 1 + (this.getXSize() - width) / 2;
            int y = 10 + (16 - this.font.FONT_HEIGHT) / 2;
            this.font.drawText(matrixStack, text, x, y, Color.BLACK.getRGB());
        }
    }

    protected void initWidgets() {
        this.buttons.clear();
        this.children.clear();
        
        // Show mana cost widgets, if the active recipe has a mana cost
        IArcaneRecipe activeArcaneRecipe = this.container.getActiveArcaneRecipe();
        if (activeArcaneRecipe != null) {
            SourceList manaCosts = activeArcaneRecipe.getManaCosts();
            if (!manaCosts.isEmpty()) {
                int widgetSetWidth = manaCosts.getSourcesSorted().size() * 18;
                int x = this.guiLeft + 1 + (this.getXSize() - widgetSetWidth) / 2;
                int y = this.guiTop + 10;
                for (Source source : manaCosts.getSourcesSorted()) {
                    this.addButton(new ManaCostWidget(source, manaCosts.getAmount(source), x, y));
                    x += 18;
                }
            }
        }
    }
}
