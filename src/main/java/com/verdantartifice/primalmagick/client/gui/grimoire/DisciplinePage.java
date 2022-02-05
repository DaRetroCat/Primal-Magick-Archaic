package com.verdantartifice.primalmagick.client.gui.grimoire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.verdantartifice.primalmagick.client.gui.GrimoireScreen;
import com.verdantartifice.primalmagick.client.gui.widgets.grimoire.EntryButton;
import com.verdantartifice.primalmagick.client.gui.widgets.grimoire.SectionHeaderWidget;
import com.verdantartifice.primalmagick.common.research.ResearchDiscipline;
import com.verdantartifice.primalmagick.common.research.ResearchEntry;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Grimoire page showing the list of available research entries in a discipline.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class DisciplinePage extends AbstractPage {
    protected ResearchDiscipline discipline;
    protected List<Object> contents = new ArrayList<>();
    protected boolean firstPage;
    
    public DisciplinePage(@Nonnull ResearchDiscipline discipline) {
        this(discipline, false);
    }
    
    public DisciplinePage(@Nonnull ResearchDiscipline discipline, boolean first) {
        this.discipline = discipline;
        this.firstPage = first;
    }
    
    @Nonnull
    public List<Object> getContents() {
        return Collections.unmodifiableList(this.contents);
    }
    
    public boolean addContent(Object entry) {
        return this.contents.add(entry);
    }
    
    public boolean isFirstPage() {
        return this.firstPage;
    }
    
    @Override
    protected String getTitleTranslationKey() {
        return this.discipline.getNameTranslationKey();
    }

    @Override
    public void render(MatrixStack matrixStack, int side, int x, int y, int mouseX, int mouseY) {
        // Just render the title; buttons have already been added
        if (this.isFirstPage() && side == 0) {
            this.renderTitle(matrixStack, side, x, y, mouseX, mouseY, this.discipline.getIconLocation());
        }
    }
    
    @Override
    public void initWidgets(GrimoireScreen screen, int side, int x, int y) {
        for (Object obj : this.getContents()) {
            if (obj instanceof ResearchEntry) {
                // If the current content object is a research entry, add a button for it to the screen
                ResearchEntry entry = (ResearchEntry)obj;
                ITextComponent text = new TranslationTextComponent(entry.getNameTranslationKey());
                screen.addWidgetToScreen(new EntryButton(x + 12 + (side * 140), y, text, screen, entry));
            } else if (obj instanceof ITextComponent) {
                // If the current content object is a text component, add a section header with that text to the screen
                screen.addWidgetToScreen(new SectionHeaderWidget(x + 12 + (side * 140), y, (ITextComponent)obj));
            }
            y += 12;
        }
    }
}
