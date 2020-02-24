package com.verdantartifice.primalmagic.common.events;

import java.util.Arrays;
import java.util.List;

import com.verdantartifice.primalmagic.PrimalMagic;
import com.verdantartifice.primalmagic.common.attunements.AttunementManager;
import com.verdantartifice.primalmagic.common.attunements.AttunementThreshold;
import com.verdantartifice.primalmagic.common.effects.EffectsPM;
import com.verdantartifice.primalmagic.common.misc.DamageSourcesPM;
import com.verdantartifice.primalmagic.common.network.PacketHandler;
import com.verdantartifice.primalmagic.common.network.packets.fx.SpellBoltPacket;
import com.verdantartifice.primalmagic.common.sounds.SoundsPM;
import com.verdantartifice.primalmagic.common.sources.Source;
import com.verdantartifice.primalmagic.common.util.EntityUtils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handlers for combat-related events.
 * 
 * @author Daedalus4096
 */
@Mod.EventBusSubscriber(modid=PrimalMagic.MODID)
public class CombatEvents {
    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event) {
        // Handle effects caused by damage target
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity target = (PlayerEntity)event.getEntityLiving();
            if (target.world.rand.nextDouble() < 0.5D &&
                    !target.isPotionActive(Effects.INVISIBILITY) && 
                    AttunementManager.meetsThreshold(target, Source.MOON, AttunementThreshold.LESSER)) {
                // Attuned players have a chance to turn invisible upon taking damage, if they aren't already
                target.world.playSound(target, target.getPosition(), SoundsPM.SHIMMER.get(), 
                        SoundCategory.PLAYERS, 1.0F, 1.0F + (0.05F * (float)target.world.rand.nextGaussian()));
                target.addPotionEffect(new EffectInstance(Effects.INVISIBILITY, 200));
            }
        }
        
        // Handle effects caused by damage source
        if (event.getSource().getTrueSource() instanceof PlayerEntity) {
            PlayerEntity attacker = (PlayerEntity)event.getSource().getTrueSource();
            
            // If the attacker has lesser infernal attunement, launch a hellish chain at the next closest nearby target
            if (!DamageSourcesPM.HELLISH_CHAIN_TYPE.equals(event.getSource().damageType) && 
                    !attacker.world.isRemote && 
                    AttunementManager.meetsThreshold(attacker, Source.INFERNAL, AttunementThreshold.LESSER)) {
                List<LivingEntity> targets = EntityUtils.getEntitiesInRangeSorted(attacker.world, event.getEntityLiving().getPositionVec(), 
                        Arrays.asList(event.getEntityLiving(), attacker), LivingEntity.class, 4.0D);
                if (!targets.isEmpty()) {
                    LivingEntity target = targets.get(0);
                    target.attackEntityFrom(DamageSourcesPM.causeHellishChainDamage(attacker), event.getAmount() / 2.0F);
                    PacketHandler.sendToAllAround(new SpellBoltPacket(event.getEntityLiving().getEyePosition(1.0F), target.getEyePosition(1.0F), Source.INFERNAL.getColor()), 
                            attacker.world.dimension.getType(), event.getEntityLiving().getPosition(), 64.0D);
                    attacker.world.playSound(null, event.getEntityLiving().getPosition(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1.0F, 1.0F + (float)(attacker.world.rand.nextGaussian() * 0.05D));
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        // Handle effects triggered by damage target
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity target = (PlayerEntity)event.getEntityLiving();
            if (event.getSource() == DamageSource.FALL && AttunementManager.meetsThreshold(target, Source.SKY, AttunementThreshold.LESSER)) {
                // Reduce fall damage if the recipient has lesser sky attunement
                float newDamage = Math.max(0.0F, event.getAmount() / 3.0F - 2.0F);
                if (newDamage < event.getAmount()) {
                    event.setAmount(newDamage);
                }
                
                // If the fall damage was reduced to less than one, cancel it
                if (event.getAmount() < 1.0F) {
                    event.setAmount(0.0F);
                    event.setCanceled(true);
                    return;
                }
            }
        }
        
        // Handle effects triggered by the damage source
        if (event.getSource().getTrueSource() instanceof PlayerEntity) {
            PlayerEntity attacker = (PlayerEntity)event.getSource().getTrueSource();

            // If at least one point of damage was done by a player with the lesser blood attunement, cause bleeding
            if (event.getAmount() >= 1.0F && AttunementManager.meetsThreshold(attacker, Source.BLOOD, AttunementThreshold.LESSER)) {
                event.getEntityLiving().addPotionEffect(new EffectInstance(EffectsPM.BLEEDING.get(), 200));
            }
            
            // Players with greater blood attunement can steal health, with a chance based on damage done
            if (attacker.world.rand.nextFloat() < (event.getAmount() / 12.0F) && AttunementManager.meetsThreshold(attacker, Source.BLOOD, AttunementThreshold.GREATER)) {
                attacker.heal(1.0F);
            }
        }
    }
    
    @SubscribeEvent
    public static void onPotionApplicable(PotionEvent.PotionApplicableEvent event) {
        if (event.getPotionEffect().getPotion() == EffectsPM.BLEEDING.get() && event.getEntityLiving().isEntityUndead()) {
            // The undead can't bleed
            event.setResult(Result.DENY);
        }
    }
}
