package com.verdantartifice.primalmagick.common.containers;

import javax.annotation.Nullable;

import com.verdantartifice.primalmagick.client.gui.grimoire.AttunementIndexPage;
import com.verdantartifice.primalmagick.client.gui.grimoire.RuneEnchantmentIndexPage;
import com.verdantartifice.primalmagick.client.gui.grimoire.StatisticsPage;
import com.verdantartifice.primalmagick.common.research.ResearchDiscipline;
import com.verdantartifice.primalmagick.common.research.ResearchEntry;
import com.verdantartifice.primalmagick.common.sources.Source;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

/**
 * Server data container for the grimoire GUI.
 * 
 * @author Daedalus4096
 */
public class GrimoireContainer extends Container {
    protected Object topic;
    
    public GrimoireContainer(int windowId) {
        super(ContainersPM.GRIMOIRE.get(), windowId);
        this.topic = null;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
    
    public Object getTopic() {
        return this.topic;
    }
    
    /**
     * New topic can either be null (for the discipline index), a ResearchDiscipline (for a listing
     * of that discipline's entries), a ResearchEntry (for details of that entry), a Source (for
     * attunement details for that source), an Enchantment (for details about a rune enchantment), 
     * or specific strings (for various other topics).
     * 
     * @param newTopic
     */
    @Nullable
    public void setTopic(Object newTopic) {
        if ( newTopic == null || 
             newTopic instanceof ResearchDiscipline || 
             newTopic instanceof ResearchEntry || 
             newTopic instanceof Source || 
             newTopic instanceof Enchantment ||
             AttunementIndexPage.TOPIC.equals(newTopic) || 
             RuneEnchantmentIndexPage.TOPIC.equals(newTopic) || 
             StatisticsPage.TOPIC.equals(newTopic) ) {
            this.topic = newTopic;
        } else {
            throw new IllegalArgumentException("Invalid grimoire topic");
        }
    }
}
