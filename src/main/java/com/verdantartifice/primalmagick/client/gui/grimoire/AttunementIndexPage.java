package com.verdantartifice.primalmagick.client.gui.grimoire;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.verdantartifice.primalmagick.client.gui.GrimoireScreen;
import com.verdantartifice.primalmagick.client.gui.widgets.grimoire.AttunementButton;
import com.verdantartifice.primalmagick.common.sources.Source;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Grimoire page showing the list of discovered attunements.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class AttunementIndexPage extends AbstractPage {
    public static final String TOPIC = "attunements";

    protected boolean firstPage;

    public AttunementIndexPage() {
        this(false);
    }
    
    public AttunementIndexPage(boolean first) {
        this.firstPage = first;
    }
    
    @Override
    public void render(MatrixStack matrixStack, int side, int x, int y, int mouseX, int mouseY) {
        // Just render the title; buttons have already been added
        if (this.isFirstPage() && side == 0) {
            this.renderTitle(matrixStack, side, x, y, mouseX, mouseY, null);
        }
    }

    public boolean isFirstPage() {
        return this.firstPage;
    }
    
    @Override
    protected String getTitleTranslationKey() {
        return "primalmagick.grimoire.attunement_header";
    }

    @Override
    public void initWidgets(GrimoireScreen screen, int side, int x, int y) {
        // Add a button to the screen for each discovered source
    	Minecraft mc = Minecraft.getInstance();
        for (Source source : Source.SORTED_SOURCES) {
            if (source.isDiscovered(mc.player)) {
                ITextComponent text = new TranslationTextComponent(source.getNameTranslationKey());
                screen.addWidgetToScreen(new AttunementButton(x + 12 + (side * 140), y, text, screen, source));
                y += 12;
            }
        }
    }

}
