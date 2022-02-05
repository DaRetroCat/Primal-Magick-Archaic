package com.verdantartifice.primalmagick.common.spells.payloads;

import java.util.Map;

import com.verdantartifice.primalmagick.common.research.CompoundResearchKey;
import com.verdantartifice.primalmagick.common.research.SimpleResearchKey;
import com.verdantartifice.primalmagick.common.sounds.SoundsPM;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.spells.SpellPackage;
import com.verdantartifice.primalmagick.common.spells.SpellProperty;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

/**
 * Definition of a solar damage spell.  Does standard damage to the target.  As a secondary effect, sets
 * undead targets on fire.  The length of the fire effect scales with the duration property of the payload.
 * 
 * @author Daedalus4096
 */
public class SolarDamageSpellPayload extends AbstractDamageSpellPayload {
    public static final String TYPE = "solar_damage";
    protected static final CompoundResearchKey RESEARCH = CompoundResearchKey.from(SimpleResearchKey.parse("SPELL_PAYLOAD_SOLAR"));

    public SolarDamageSpellPayload() {
        super();
    }
    
    public SolarDamageSpellPayload(int power, int duration) {
        super(power);
        this.getProperty("duration").setValue(duration);
    }
    
    public static CompoundResearchKey getResearch() {
        return RESEARCH;
    }
    
    @Override
    protected Map<String, SpellProperty> initProperties() {
        Map<String, SpellProperty> propMap = super.initProperties();
        propMap.put("duration", new SpellProperty("duration", "primalmagick.spell.property.duration", 0, 5));
        return propMap;
    }

    @Override
    public Source getSource() {
        return Source.SUN;
    }

    @Override
    public void playSounds(World world, BlockPos origin) {
        world.playSound(null, origin, SoundsPM.SUNBEAM.get(), SoundCategory.PLAYERS, 1.0F, 1.0F + (float)(world.rand.nextGaussian() * 0.05D));
    }

    @Override
    protected float getTotalDamage(Entity target, SpellPackage spell, ItemStack spellSource) {
        return 3.0F + this.getModdedPropertyValue("power", spell, spellSource);
    }

    @Override
    protected String getPayloadType() {
        return TYPE;
    }

    @Override
    protected void applySecondaryEffects(RayTraceResult target, Vector3d burstPoint, SpellPackage spell, World world, LivingEntity caster, ItemStack spellSource) {
        int duration = this.getModdedPropertyValue("duration", spell, spellSource);
        if (target != null && target.getType() == RayTraceResult.Type.ENTITY && duration > 0) {
            EntityRayTraceResult entityTarget = (EntityRayTraceResult)target;
            if (entityTarget.getEntity() != null && entityTarget.getEntity() instanceof LivingEntity) {
                // Make the entity glow and set it on fire if undead
                LivingEntity entity = (LivingEntity)entityTarget.getEntity();
                entity.addPotionEffect(new EffectInstance(Effects.GLOWING, 20 * duration, 0));
                if (entity.isEntityUndead()) {
                    entity.setFire(duration);
                }
            }
        }
    }
    
    @Override
    public int getBaseManaCost() {
        return this.getPropertyValue("power") + this.getPropertyValue("duration");
    }
}
