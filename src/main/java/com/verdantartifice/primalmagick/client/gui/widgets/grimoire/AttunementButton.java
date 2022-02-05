package com.verdantartifice.primalmagick.client.gui.widgets.grimoire;

import com.verdantartifice.primalmagick.client.gui.GrimoireScreen;
import com.verdantartifice.primalmagick.common.sources.Source;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * GUI button to view the grimoire page for a given source attunement.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class AttunementButton extends AbstractTopicButton {
    protected Source source;
    
    public AttunementButton(int widthIn, int heightIn, ITextComponent text, GrimoireScreen screen, Source source) {
        super(widthIn, heightIn, 123, 12, text, screen, new Handler());
        this.source = source;
    }
    
    public Source getSource() {
        return this.source;
    }
    
    private static class Handler implements IPressable {
        @Override
        public void onPress(Button button) {
            if (button instanceof AttunementButton) {
                AttunementButton gab = (AttunementButton)button;
                
                // Push the current grimoire topic onto the history stack
                GrimoireScreen.HISTORY.add(gab.getScreen().getContainer().getTopic());
                
                // Set the new grimoire topic and open a new screen for it
                gab.getScreen().getContainer().setTopic(gab.getSource());
                gab.getScreen().getMinecraft().displayGuiScreen(new GrimoireScreen(
                    gab.getScreen().getContainer(),
                    gab.getScreen().getPlayerInventory(),
                    gab.getScreen().getTitle()
                ));
            }
        }
    }
}
