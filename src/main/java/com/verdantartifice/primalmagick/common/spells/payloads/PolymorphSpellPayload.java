package com.verdantartifice.primalmagick.common.spells.payloads;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.verdantartifice.primalmagick.common.misc.EntitySwapper;
import com.verdantartifice.primalmagick.common.research.CompoundResearchKey;
import com.verdantartifice.primalmagick.common.research.SimpleResearchKey;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.spells.SpellManager;
import com.verdantartifice.primalmagick.common.spells.SpellPackage;
import com.verdantartifice.primalmagick.common.spells.SpellProperty;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

/**
 * Definition of a polymorph spell.  Temporarily replaces the target living, non-player, non-boss
 * entity with a neutral wolf.  The length of the replacement scales with the payload's duration
 * property.  NBT data of the original entity is preserved for the swap back.  Has no effect on
 * blocks.
 * 
 * @author Daedalus4096
 * @see {@link com.verdantartifice.primalmagick.common.misc.EntitySwapper}
 */
public class PolymorphSpellPayload extends AbstractSpellPayload {
    public static final String TYPE = "polymorph";
    protected static final CompoundResearchKey RESEARCH = CompoundResearchKey.from(SimpleResearchKey.parse("SPELL_PAYLOAD_POLYMORPH"));
    protected static final int TICKS_PER_DURATION = 120;

    public PolymorphSpellPayload() {
        super();
    }
    
    public PolymorphSpellPayload(int duration) {
        super();
        this.getProperty("duration").setValue(duration);
    }
    
    public static CompoundResearchKey getResearch() {
        return RESEARCH;
    }
    
    @Override
    protected Map<String, SpellProperty> initProperties() {
        Map<String, SpellProperty> propMap = super.initProperties();
        propMap.put("duration", new SpellProperty("duration", "primalmagick.spell.property.duration", 1, 5));
        return propMap;
    }
    
    @Override
    public void execute(RayTraceResult target, Vector3d burstPoint, SpellPackage spell, World world, LivingEntity caster, ItemStack spellSource) {
        if (target != null && target.getType() == RayTraceResult.Type.ENTITY) {
            EntityRayTraceResult entityTarget = (EntityRayTraceResult)target;
            if (SpellManager.canPolymorph(entityTarget.getEntity().getType())) {
                // Create and enqueue an entity swapper for the target entity
                UUID entityId = entityTarget.getEntity().getUniqueID();
                CompoundNBT originalData = entityTarget.getEntity().writeWithoutTypeId(new CompoundNBT());
                int ticks = this.getModdedPropertyValue("duration", spell, spellSource) * TICKS_PER_DURATION;
                EntitySwapper.enqueue(world, new EntitySwapper(entityId, EntityType.WOLF, originalData, Optional.of(Integer.valueOf(ticks)), 0));
            }
        }
    }
    
    @Override
    public Source getSource() {
        return Source.MOON;
    }

    @Override
    public int getBaseManaCost() {
        return 10 * this.getPropertyValue("duration");
    }

    @Override
    public void playSounds(World world, BlockPos origin) {
        world.playSound(null, origin, SoundEvents.ENTITY_WOLF_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.0F + (float)(world.rand.nextGaussian() * 0.05D));
    }

    @Override
    protected String getPayloadType() {
        return TYPE;
    }
}
