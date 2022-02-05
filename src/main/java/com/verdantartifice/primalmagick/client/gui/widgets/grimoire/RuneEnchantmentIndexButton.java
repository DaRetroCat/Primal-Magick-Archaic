package com.verdantartifice.primalmagick.client.gui.widgets.grimoire;

import com.verdantartifice.primalmagick.client.gui.GrimoireScreen;
import com.verdantartifice.primalmagick.client.gui.grimoire.RuneEnchantmentIndexPage;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * GUI button to view the grimoire rune enchantment index page.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class RuneEnchantmentIndexButton extends AbstractTopicButton {
    public RuneEnchantmentIndexButton(int widthIn, int heightIn, ITextComponent text, GrimoireScreen screen) {
        super(widthIn, heightIn, 123, 12, text, screen, new Handler());
    }
    
    private static class Handler implements IPressable {
        @Override
        public void onPress(Button button) {
            if (button instanceof RuneEnchantmentIndexButton) {
                RuneEnchantmentIndexButton greb = (RuneEnchantmentIndexButton)button;
                
                // Push the current grimoire topic onto the history stack
                GrimoireScreen.HISTORY.add(greb.getScreen().getContainer().getTopic());
                
                // Set the new grimoire topic and open a new screen for it
                greb.getScreen().getContainer().setTopic(RuneEnchantmentIndexPage.TOPIC);
                greb.getScreen().getMinecraft().displayGuiScreen(new GrimoireScreen(
                    greb.getScreen().getContainer(),
                    greb.getScreen().getPlayerInventory(),
                    greb.getScreen().getTitle()
                ));
            }
        }
    }
}
