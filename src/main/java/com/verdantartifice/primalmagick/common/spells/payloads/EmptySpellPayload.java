package com.verdantartifice.primalmagick.common.spells.payloads;

import com.verdantartifice.primalmagick.common.research.CompoundResearchKey;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.spells.SpellPackage;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

/**
 * Definition of an empty spell payload.  This payload has no effect and is not valid in spells.  Its 
 * only purpose is to provide a selection entry in the spellcrafting altar GUI for when the player has
 * not selected a payload for the spell.
 * 
 * @author Daedalus4096
 */
public class EmptySpellPayload extends AbstractSpellPayload {
    public static final String TYPE = "none";

    @Override
    public void execute(RayTraceResult target, Vector3d burstPoint, SpellPackage spell, World world, LivingEntity caster, ItemStack spellSource) {
        // Do nothing
    }

    @Override
    public boolean isActive() {
        return false;
    }
    
    @Override
    public Source getSource() {
        return Source.EARTH;
    }

    @Override
    public int getBaseManaCost() {
        return 0;
    }

    @Override
    public void playSounds(World world, BlockPos origin) {
        // Do nothing
    }

    @Override
    protected String getPayloadType() {
        return TYPE;
    }
    
    public static CompoundResearchKey getResearch() {
        return null;
    }
}
