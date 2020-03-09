package com.verdantartifice.primalmagic.common.theorycrafting.projects;

import com.verdantartifice.primalmagic.common.research.SimpleResearchKey;
import com.verdantartifice.primalmagic.common.sources.Source;
import com.verdantartifice.primalmagic.common.theorycrafting.AbstractProject;
import com.verdantartifice.primalmagic.common.theorycrafting.AbstractProjectMaterial;
import com.verdantartifice.primalmagic.common.theorycrafting.ItemProjectMaterial;
import com.verdantartifice.primalmagic.common.util.WeightedRandomBag;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;

/**
 * Definition of a research project option.
 * 
 * @author Daedalus4096
 */
public class NetherExpeditionProject extends AbstractProject {
    public static final String TYPE = "nether_expedition";
    
    protected static final WeightedRandomBag<AbstractProjectMaterial> OPTIONS = new WeightedRandomBag<>();
    protected static final SimpleResearchKey RESEARCH = Source.INFERNAL.getDiscoverKey();
    
    static {
        OPTIONS.add(new ItemProjectMaterial(Items.DIAMOND_SWORD, false), 1);
        OPTIONS.add(new ItemProjectMaterial(Items.CROSSBOW, false), 1);
        OPTIONS.add(new ItemProjectMaterial(new ItemStack(Items.ARROW, 16), true), 1);
        OPTIONS.add(new ItemProjectMaterial(Items.DIAMOND_CHESTPLATE, false), 1);
        OPTIONS.add(new ItemProjectMaterial(Items.MAP, true), 1);
        OPTIONS.add(new ItemProjectMaterial(Items.CARTOGRAPHY_TABLE, false), 1);
        OPTIONS.add(new ItemProjectMaterial(new ItemStack(Items.TORCH, 16), true), 1);
        OPTIONS.add(new ItemProjectMaterial(new ItemStack(Items.BREAD, 4), true), 1);
        OPTIONS.add(new ItemProjectMaterial(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.FIRE_RESISTANCE), true, true), 4);
        OPTIONS.add(new ItemProjectMaterial(Items.MILK_BUCKET, true), 1);
        OPTIONS.add(new ItemProjectMaterial(new ItemStack(Items.OBSIDIAN, 10), false), 1);
        OPTIONS.add(new ItemProjectMaterial(Items.FLINT_AND_STEEL, false), 1);
    }
    
    @Override
    protected String getProjectType() {
        return TYPE;
    }

    @Override
    protected WeightedRandomBag<AbstractProjectMaterial> getMaterialOptions() {
        return OPTIONS;
    }
    
    @Override
    public SimpleResearchKey getRequiredResearch() {
        return RESEARCH;
    }
}