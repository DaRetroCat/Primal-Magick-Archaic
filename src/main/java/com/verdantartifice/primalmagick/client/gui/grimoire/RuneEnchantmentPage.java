package com.verdantartifice.primalmagick.client.gui.grimoire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.client.gui.GrimoireScreen;
import com.verdantartifice.primalmagick.client.gui.widgets.grimoire.ItemStackWidget;
import com.verdantartifice.primalmagick.common.items.misc.RuneItem;
import com.verdantartifice.primalmagick.common.runes.Rune;
import com.verdantartifice.primalmagick.common.runes.RuneManager;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Grimoire page showing the page elements for a rune enchantment.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class RuneEnchantmentPage extends AbstractPage {
    protected static final ResourceLocation OVERLAY = new ResourceLocation(PrimalMagick.MODID, "textures/gui/grimoire_overlay.png");
    
    protected Enchantment enchant;
    protected List<IPageElement> contents = new ArrayList<>();
    protected boolean firstPage;
    
    public RuneEnchantmentPage(Enchantment enchant) {
        this(enchant, false);
    }
    
    public RuneEnchantmentPage(Enchantment enchant, boolean first) {
        this.enchant = enchant;
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
    protected String getTitleTranslationKey() {
        return this.enchant.getName();
    }
    
    @Override
    public void render(MatrixStack matrixStack, int side, int x, int y, int mouseX, int mouseY) {
        int startY = y;
        int indent = 84;
        int overlayWidth = 13;
        int overlayHeight = 13;
        
        // Draw title page and overlay background if applicable
        if (this.isFirstPage() && side == 0) {
            this.renderTitle(matrixStack, side, x, y, mouseX, mouseY, null);
            y += 77;
            
            Minecraft.getInstance().getTextureManager().bindTexture(OVERLAY);
            matrixStack.push();
            matrixStack.translate(x + (side * 140) + (indent / 2) - (overlayWidth / 2), startY + 49, 0.0F);
            this.blit(matrixStack, 0, 0, 0, 51, overlayWidth, overlayHeight);
            this.blit(matrixStack, 32, 0, 0, 51, overlayWidth, overlayHeight);
            matrixStack.pop();
        } else {
            y += 25;
        }
        
        // Render page contents
        for (IPageElement content : this.contents) {
            content.render(matrixStack, side, x, y);
            y = content.getNextY(y);
        }
    }
    
    @Override
    public void initWidgets(GrimoireScreen screen, int side, int x, int y) {
        int indent = 124;
        int overlayWidth = 52;

        // Render rune item stacks if applicable
        if (this.isFirstPage() && side == 0) {
            List<Rune> runes = RuneManager.getRunesForEnchantment(this.enchant);
            if (runes != null) {
                for (int index = 0; index < Math.min(runes.size(), 3); index++) {
                    ItemStack runeStack = RuneItem.getRune(runes.get(index));
                    if (runeStack != null) {
                        screen.addWidgetToScreen(new ItemStackWidget(runeStack, x - 5 + (side * 140) + (indent / 2) - (overlayWidth / 2) + (index * 32), y, false));
                    }
                }
            }
        }
    }
}
