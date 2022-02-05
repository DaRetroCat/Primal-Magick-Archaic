package com.verdantartifice.primalmagick.client.gui.grimoire;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.client.gui.GrimoireScreen;
import com.verdantartifice.primalmagick.client.gui.widgets.grimoire.AttunementThresholdWidget;
import com.verdantartifice.primalmagick.common.attunements.AttunementManager;
import com.verdantartifice.primalmagick.common.attunements.AttunementThreshold;
import com.verdantartifice.primalmagick.common.attunements.AttunementType;
import com.verdantartifice.primalmagick.common.sources.Source;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Grimoire page showing the details of a discovered attunement.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class AttunementPage extends AbstractPage {
    private static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/gui/attunement_meter.png");

    protected Source source;
    protected List<IPageElement> contents = new ArrayList<>();
    protected boolean firstPage;

    public AttunementPage(@Nonnull Source source) {
        this(source, false);
    }
    
    public AttunementPage(@Nonnull Source source, boolean first) {
        this.source = source;
        this.firstPage = first;
    }
    
    @Nonnull
    public List<IPageElement> getElements() {
        return Collections.unmodifiableList(this.contents);
    }
    
    public boolean addElement(IPageElement element) {
        return this.contents.add(element);
    }
    
    public boolean isFirstPage() {
        return this.firstPage;
    }

    @Override
    public void render(MatrixStack matrixStack, int side, int x, int y, int mouseX, int mouseY) {
        // Draw title page if applicable
        if (this.isFirstPage() && side == 0) {
            this.renderTitle(matrixStack, side, x, y, mouseX, mouseY, null);
            y += 53;
        } else {
            y += 25;
        }
        
        if (this.isFirstPage()) {
            Color baseColor = new Color(this.source.getColor());
            Color color;
            
            // Render attunement meter
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            Minecraft mc = Minecraft.getInstance();
            mc.getTextureManager().bindTexture(TEXTURE);
            
            // Render meter background
            this.blit(matrixStack, x + 51 + (side * 140), y, 12, 0, 14, 120);
            
            int p = AttunementManager.getAttunement(mc.player, this.source, AttunementType.PERMANENT);
            int i = AttunementManager.getAttunement(mc.player, this.source, AttunementType.INDUCED);
            int t = AttunementManager.getAttunement(mc.player, this.source, AttunementType.TEMPORARY);

            // Render permanent meter bar
            color = baseColor.darker();
            RenderSystem.color4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
            this.blit(matrixStack, x + 53 + (side * 140), y + 10 + (100 - MathHelper.clamp(p, 0, 100)), 0, 10, 10, MathHelper.clamp(p, 0, 100));
            
            // Render induced meter bar
            color = baseColor;
            RenderSystem.color4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
            this.blit(matrixStack, x + 53 + (side * 140), y + 10 + (100 - MathHelper.clamp(p + i, 0, 100)), 0, 10, 10, MathHelper.clamp(i, 0, 100 - p));
            
            // Render temporary meter bar
            color = baseColor.brighter();
            RenderSystem.color4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
            this.blit(matrixStack, x + 53 + (side * 140), y + 10 + (100 - MathHelper.clamp(p + i + t, 0, 100)), 0, 10, 10, MathHelper.clamp(t, 0, 100 - p - i));

            // Render meter foreground
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.blit(matrixStack, x + 52 + (side * 140), y + 9, 27, 9, 15, 102);
        }

        // Render page contents
        for (IPageElement content : this.contents) {
            content.render(matrixStack, side, x, y);
            y = content.getNextY(y);
        }
    }

    @Override
    protected String getTitleTranslationKey() {
        return this.source.getNameTranslationKey();
    }

    @Override
    public void initWidgets(GrimoireScreen screen, int side, int x, int y) {
        if (this.isFirstPage()) {
            screen.addWidgetToScreen(new AttunementThresholdWidget(this.source, AttunementThreshold.MINOR, x + 83 + (side * 140), y + 79));
            screen.addWidgetToScreen(new AttunementThresholdWidget(this.source, AttunementThreshold.LESSER, x + 83 + (side * 140), y + 49));
            screen.addWidgetToScreen(new AttunementThresholdWidget(this.source, AttunementThreshold.GREATER, x + 83 + (side * 140), y + 19));
        }
    }
}
