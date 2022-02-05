package com.verdantartifice.primalmagick.common.spells.payloads;

import com.verdantartifice.primalmagick.common.research.CompoundResearchKey;
import com.verdantartifice.primalmagick.common.research.SimpleResearchKey;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.spells.SpellPackage;
import com.verdantartifice.primalmagick.common.util.EntityUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

/**
 * Definition of a teleport spell.  Teleports the caster to the target location.  Works similarly to
 * throwing ender pearls.  Not compatible with Burst mods.
 * 
 * @author Daedalus4096
 */
public class TeleportSpellPayload extends AbstractSpellPayload {
    public static final String TYPE = "teleport";
    protected static final CompoundResearchKey RESEARCH = CompoundResearchKey.from(SimpleResearchKey.parse("SPELL_PAYLOAD_TELEPORT"));

    public TeleportSpellPayload() {
        super();
    }
    
    public static CompoundResearchKey getResearch() {
        return RESEARCH;
    }

    @Override
    public void execute(RayTraceResult target, Vector3d burstPoint, SpellPackage spell, World world, LivingEntity caster, ItemStack spellSource) {
        if (burstPoint != null) {
            // Do nothing if this was from a burst spell
            return;
        }
        if (target.getType() == RayTraceResult.Type.ENTITY) {
            Entity entity = ((EntityRayTraceResult)target).getEntity();
            if (entity.equals(caster)) {
                // Do nothing if the caster targeted themselves
                return;
            }
        }
        EntityUtils.teleportEntity(caster, world, target.getHitVec());
    }

    @Override
    public Source getSource() {
        return Source.VOID;
    }

    @Override
    public int getBaseManaCost() {
        return 25;
    }

    @Override
    public void playSounds(World world, BlockPos origin) {
        world.playSound(null, origin, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F + (float)(world.rand.nextGaussian() * 0.05D));
    }

    @Override
    protected String getPayloadType() {
        return TYPE;
    }
}
