package com.verdantartifice.primalmagick.client.gui.grimoire;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.verdantartifice.primalmagick.client.gui.GrimoireScreen;
import com.verdantartifice.primalmagick.client.gui.widgets.grimoire.AttunementIndexButton;
import com.verdantartifice.primalmagick.client.gui.widgets.grimoire.DisciplineButton;
import com.verdantartifice.primalmagick.client.gui.widgets.grimoire.RuneEnchantmentIndexButton;
import com.verdantartifice.primalmagick.client.gui.widgets.grimoire.StatisticsButton;
import com.verdantartifice.primalmagick.common.research.ResearchDiscipline;
import com.verdantartifice.primalmagick.common.research.ResearchDisciplines;
import com.verdantartifice.primalmagick.common.research.ResearchManager;
import com.verdantartifice.primalmagick.common.research.SimpleResearchKey;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Grimoire page showing a list of topics not directly tied to research entries.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class OtherIndexPage extends AbstractPage {
    @Override
    public void render(MatrixStack matrixStack, int side, int x, int y, int mouseX, int mouseY) {
        // Just render the title; buttons have already been added
        this.renderTitle(matrixStack, side, x, y, mouseX, mouseY, null);
    }

    @Override
    protected String getTitleTranslationKey() {
        return "primalmagick.grimoire.other_header";
    }

    @Override
    public void initWidgets(GrimoireScreen screen, int side, int x, int y) {
    	Minecraft mc = Minecraft.getInstance();
        ITextComponent text;
        
        // Make room for page title
        y += 24;
        
        // Add attunements button if attunements are unlocked
        if (ResearchManager.isResearchComplete(mc.player, SimpleResearchKey.parse("ATTUNEMENTS"))) {
            text = new TranslationTextComponent("primalmagick.grimoire.attunement_header");
            screen.addWidgetToScreen(new AttunementIndexButton(x + 12 + (side * 140), y, text, screen));
            y += 12;
        }
        
        // Add rune enchantments button if rune enchantments are unlocked
        if (ResearchManager.isResearchComplete(mc.player, SimpleResearchKey.parse("UNLOCK_RUNE_ENCHANTMENTS"))) {
            text = new TranslationTextComponent("primalmagick.grimoire.rune_enchantment_header");
            screen.addWidgetToScreen(new RuneEnchantmentIndexButton(x + 12 + (side * 140), y, text, screen));
            y += 12;
        }
        
        // Add scans button if at least one scan is unlocked
        ResearchDiscipline scans = ResearchDisciplines.getDiscipline("SCANS");
        if (scans != null && scans.getUnlockResearchKey().isKnownBy(Minecraft.getInstance().player)) {
            text = new TranslationTextComponent(scans.getNameTranslationKey());
            screen.addWidgetToScreen(new DisciplineButton(x + 12 + (side * 140), y, text, screen, scans));
            y += 12;
        }
        
        // Add statistics button
        text = new TranslationTextComponent("primalmagick.grimoire.stats_header");
        screen.addWidgetToScreen(new StatisticsButton(x + 12 + (side * 140), y, text, screen));
        y += 12;
    }
}
