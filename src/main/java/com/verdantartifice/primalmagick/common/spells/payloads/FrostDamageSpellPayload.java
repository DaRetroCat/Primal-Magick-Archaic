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
 * Definition of a frost damage spell.  Does standard damage to the target and applies a slowness
 * potion effect.  The strength of the slowness effect scales with the payload's power property and
 * its length scales with the duration property.
 * 
 * @author Daedalus4096
 */
public class FrostDamageSpellPayload extends AbstractDamageSpellPayload {
    public static final String TYPE = "frost_damage";
    protected static final CompoundResearchKey RESEARCH = CompoundResearchKey.from(SimpleResearchKey.parse("SPELL_PAYLOAD_FROST"));

    public FrostDamageSpellPayload() {
        super();
    }
    
    public FrostDamageSpellPayload(int power, int duration) {
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
        return Source.SEA;
    }

    @Override
    public void playSounds(World world, BlockPos origin) {
        world.playSound(null, origin, SoundsPM.ICE.get(), SoundCategory.PLAYERS, 1.0F, 1.0F + (float)(world.rand.nextGaussian() * 0.05D));
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
                int potency = (int)((1.0F + this.getModdedPropertyValue("power", spell, spellSource)) / 3.0F);   // 0, 1, 1, 1, 2
                ((LivingEntity)entityTarget.getEntity()).addPotionEffect(new EffectInstance(Effects.SLOWNESS, 20 * duration, potency));
            }
        }
    }
    
    @Override
    public int getBaseManaCost() {
        return this.getPropertyValue("power") + this.getPropertyValue("duration");
    }
}
