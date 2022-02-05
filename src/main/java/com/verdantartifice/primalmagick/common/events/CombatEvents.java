package com.verdantartifice.primalmagick.common.events;

import java.util.Arrays;
import java.util.List;

import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.common.affinities.AffinityManager;
import com.verdantartifice.primalmagick.common.attunements.AttunementManager;
import com.verdantartifice.primalmagick.common.attunements.AttunementThreshold;
import com.verdantartifice.primalmagick.common.capabilities.IPlayerCooldowns;
import com.verdantartifice.primalmagick.common.capabilities.IPlayerCooldowns.CooldownType;
import com.verdantartifice.primalmagick.common.capabilities.PrimalMagicCapabilities;
import com.verdantartifice.primalmagick.common.effects.EffectsPM;
import com.verdantartifice.primalmagick.common.enchantments.EnchantmentsPM;
import com.verdantartifice.primalmagick.common.items.ItemsPM;
import com.verdantartifice.primalmagick.common.items.essence.EssenceItem;
import com.verdantartifice.primalmagick.common.items.essence.EssenceType;
import com.verdantartifice.primalmagick.common.misc.DamageSourcesPM;
import com.verdantartifice.primalmagick.common.network.PacketHandler;
import com.verdantartifice.primalmagick.common.network.packets.fx.SpellBoltPacket;
import com.verdantartifice.primalmagick.common.research.ResearchManager;
import com.verdantartifice.primalmagick.common.research.SimpleResearchKey;
import com.verdantartifice.primalmagick.common.sounds.SoundsPM;
import com.verdantartifice.primalmagick.common.sources.Source;
import com.verdantartifice.primalmagick.common.sources.SourceList;
import com.verdantartifice.primalmagick.common.util.EntityUtils;
import com.verdantartifice.primalmagick.common.util.WeightedRandomBag;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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
@Mod.EventBusSubscriber(modid= PrimalMagick.MODID)
public class CombatEvents {
    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event) {
        // Handle effects caused by damage target
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity target = (PlayerEntity)event.getEntityLiving();
            
            // Players with greater infernal attunement are immune to all fire damage
            if (event.getSource().isFireDamage() && AttunementManager.meetsThreshold(target, Source.INFERNAL, AttunementThreshold.GREATER)) {
                event.setCanceled(true);
                return;
            }

            // Attuned players have a chance to turn invisible upon taking damage, if they aren't already
            if (target.world.rand.nextDouble() < 0.5D &&
                    !target.isPotionActive(Effects.INVISIBILITY) && 
                    AttunementManager.meetsThreshold(target, Source.MOON, AttunementThreshold.LESSER)) {
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
                            attacker.world.getDimensionKey(), event.getEntityLiving().getPosition(), 64.0D);
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
            
            // Gain appropriate research for damage sources, if applicable
            if (ResearchManager.isResearchComplete(target, SimpleResearchKey.FIRST_STEPS)) {
                if (event.getSource() == DamageSource.DROWN && !ResearchManager.isResearchComplete(target, SimpleResearchKey.parse("m_drown_a_little"))) {
                    ResearchManager.completeResearch(target, SimpleResearchKey.parse("m_drown_a_little"));
                }
                if (event.getSource() == DamageSource.LAVA && !ResearchManager.isResearchComplete(target, SimpleResearchKey.parse("m_feel_the_burn"))) {
                    ResearchManager.completeResearch(target, SimpleResearchKey.parse("m_feel_the_burn"));
                }
            }

            // Reduce fall damage if the recipient has lesser sky attunement
            if (event.getSource() == DamageSource.FALL && AttunementManager.meetsThreshold(target, Source.SKY, AttunementThreshold.LESSER)) {
                float newDamage = Math.max(0.0F, event.getAmount() / 3.0F - 2.0F);
                if (newDamage < event.getAmount()) {
                    event.setAmount(newDamage);
                }
                if (event.getAmount() < 1.0F) {
                    // If the fall damage was reduced to less than one, cancel it
                    event.setAmount(0.0F);
                    event.setCanceled(true);
                    return;
                }
            }
            
            // Reduce all non-absolute (e.g. starvation) damage taken players with lesser void attunement
            if (!event.getSource().isDamageAbsolute() && AttunementManager.meetsThreshold(target, Source.VOID, AttunementThreshold.LESSER)) {
                event.setAmount(0.9F * event.getAmount());
            }
        }
        
        // Handle effects triggered by the damage source
        if (event.getSource().getTrueSource() instanceof PlayerEntity) {
            PlayerEntity attacker = (PlayerEntity)event.getSource().getTrueSource();
            
            // Increase all non-absolute damage dealt by players with greater void attunement
            if (!event.getSource().isDamageAbsolute() && AttunementManager.meetsThreshold(attacker, Source.VOID, AttunementThreshold.GREATER)) {
                event.setAmount(1.25F * event.getAmount());
            }
            
            // Increase damage to undead targets by players with lesser hallowed attunement
            if (event.getEntityLiving().isEntityUndead() && AttunementManager.meetsThreshold(attacker, Source.HALLOWED, AttunementThreshold.LESSER)) {
                event.setAmount(2.0F * event.getAmount());
            }

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
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        
        // If the player has greater hallowed attunement and it's not on cooldown, cancel death as if using a totem of undying
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)event.getEntityLiving();
            IPlayerCooldowns cooldowns = PrimalMagicCapabilities.getCooldowns(player);
            if (AttunementManager.meetsThreshold(player, Source.HALLOWED, AttunementThreshold.GREATER) &&
                    cooldowns != null &&
                    !cooldowns.isOnCooldown(CooldownType.DEATH_SAVE)) {
                player.setHealth(1.0F);
                player.clearActivePotions();
                player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 900, 1));
                player.addPotionEffect(new EffectInstance(Effects.ABSORPTION, 100, 1));
                player.addPotionEffect(new EffectInstance(EffectsPM.WEAKENED_SOUL.get(), 6000, 0, true, false, true));
                cooldowns.setCooldown(CooldownType.DEATH_SAVE, 6000);
                player.world.playSound(null, player.getPosition(), SoundsPM.ANGELS.get(), 
                        SoundCategory.PLAYERS, 1.0F, 1.0F + (0.05F * (float)player.world.rand.nextGaussian()));
                event.setCanceled(true);
            }
        }
        
        // If the entity is afflicted with Drain Soul, drop some soul gems
        if (entity.isPotionActive(EffectsPM.DRAIN_SOUL.get()) && !event.isCanceled()) {
            float gems = entity.getType().getClassification().getPeacefulCreature() ? 
                    MathHelper.sqrt(entity.getMaxHealth()) / 20.0F : 
                    entity.getMaxHealth() / 20.0F;
            int wholeGems = MathHelper.floor(gems);
            int slivers = MathHelper.floor(MathHelper.frac(gems) * 10.0F);
            InventoryHelper.spawnItemStack(entity.getEntityWorld(), entity.getPosX(), entity.getPosY(), entity.getPosZ(), new ItemStack(ItemsPM.SOUL_GEM.get(), wholeGems));
            InventoryHelper.spawnItemStack(entity.getEntityWorld(), entity.getPosX(), entity.getPosY(), entity.getPosZ(), new ItemStack(ItemsPM.SOUL_GEM_SLIVER.get(), slivers));
        }
        
        // If the entity is afflicted with Stolen Essence, drop a sample of its essence
        if (entity.isPotionActive(EffectsPM.STOLEN_ESSENCE.get()) && !event.isCanceled()) {
            EffectInstance instance = entity.getActivePotionEffect(EffectsPM.STOLEN_ESSENCE.get());
            SourceList affinities = AffinityManager.getInstance().getAffinityValues(entity.getType());
            if (!affinities.isEmpty()) {
                WeightedRandomBag<Source> bag = new WeightedRandomBag<>();
                for (Source source : affinities.getSources()) {
                    int amount = affinities.getAmount(source);
                    if (amount > 0) {
                        bag.add(source, amount);
                    }
                }
                for (int index = 0; index < instance.getAmplifier() + 1; index++) {
                    InventoryHelper.spawnItemStack(entity.getEntityWorld(), entity.getPosX(), entity.getPosY(), entity.getPosZ(), EssenceItem.getEssence(EssenceType.DUST, bag.getRandom(entity.getRNG())));
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onArrowImpact(ProjectileImpactEvent.Arrow event) {
        RayTraceResult rayTraceResult = event.getRayTraceResult();
        if (rayTraceResult.getType() == RayTraceResult.Type.ENTITY) {
            Entity targetEntity = ((EntityRayTraceResult)rayTraceResult).getEntity();
            if (targetEntity instanceof LivingEntity) {
                LivingEntity target = (LivingEntity)targetEntity;
                Entity shooterEntity = event.getArrow().getShooter();
                if (shooterEntity instanceof LivingEntity) {
                    LivingEntity shooter = (LivingEntity)shooterEntity;
                    
                    // If the target can have its soul pierced, spawn some soul slivers
                    int soulpiercingLevel = EnchantmentHelper.getEnchantmentLevel(EnchantmentsPM.SOULPIERCING.get(), shooter.getHeldItemMainhand());
                    if (soulpiercingLevel > 0) {
                        EffectInstance soulpiercedInstance = new EffectInstance(EffectsPM.SOULPIERCED.get(), 12000, 0, false, false);
                        if (target.isPotionApplicable(soulpiercedInstance) && !target.isPotionActive(soulpiercedInstance.getPotion())) {
                            InventoryHelper.spawnItemStack(target.world, target.getPosX(), target.getPosY(), target.getPosZ(), new ItemStack(ItemsPM.SOUL_GEM_SLIVER.get(), soulpiercingLevel));
                            target.addPotionEffect(soulpiercedInstance);
                        }
                    }
                }
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
