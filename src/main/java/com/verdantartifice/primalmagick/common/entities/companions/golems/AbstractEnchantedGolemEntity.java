package com.verdantartifice.primalmagick.common.entities.companions.golems;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.verdantartifice.primalmagick.common.capabilities.IPlayerCompanions.CompanionType;
import com.verdantartifice.primalmagick.common.effects.EffectsPM;
import com.verdantartifice.primalmagick.common.entities.ai.goals.CompanionOwnerHurtByTargetGoal;
import com.verdantartifice.primalmagick.common.entities.ai.goals.CompanionOwnerHurtTargetGoal;
import com.verdantartifice.primalmagick.common.entities.ai.goals.CompanionStayGoal;
import com.verdantartifice.primalmagick.common.entities.ai.goals.FollowCompanionOwnerGoal;
import com.verdantartifice.primalmagick.common.entities.companions.AbstractCompanionEntity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.ITag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Base definition for an enchanted golem entity.  Like an iron golem, but follows the player as a
 * companion.  Can be made from a variety of magical metals.
 * 
 * @author Daedalus4096
 */
public abstract class AbstractEnchantedGolemEntity extends AbstractCompanionEntity implements IAngerable {
    protected static final DataParameter<Integer> ANGER_TIME = EntityDataManager.createKey(AbstractEnchantedGolemEntity.class, DataSerializers.VARINT);
    protected static final RangedInteger ANGER_TIME_RANGE = TickRangeConverter.convertRange(20, 39);

    protected int attackTimer;
    protected UUID angerTarget;
    protected long lastStayChangeTime;

    public AbstractEnchantedGolemEntity(EntityType<? extends AbstractEnchantedGolemEntity> type, World worldIn) {
        super(type, worldIn);
        this.stepHeight = 1.0F;
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        this.writeAngerNBT(compound);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (!this.world.isRemote) {
            this.readAngerNBT((ServerWorld)this.world, compound);
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new CompanionStayGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
        this.goalSelector.addGoal(6, new FollowCompanionOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new CompanionOwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new CompanionOwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::func_233680_b_));
        this.targetSelector.addGoal(5, new ResetAngerGoal<>(this, false));
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(ANGER_TIME, 0);
    }

    @Override
    protected int decreaseAirSupply(int air) {
        // Golems don't have to breathe
        return air;
    }

    @Override
    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    @Override
    public int getTalkInterval() {
        return 120;
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected void collideWithEntity(Entity entityIn) {
        if (entityIn instanceof IMob && !(entityIn instanceof CreeperEntity) && this.getRNG().nextInt(20) == 0) {
            this.setAttackTarget((LivingEntity)entityIn);
        }
        super.collideWithEntity(entityIn);
    }

    @Override
    public void livingTick() {
        super.livingTick();
        
        if (this.attackTimer > 0) {
            this.attackTimer--;
        }
        
        if (horizontalMag(this.getMotion()) > (double)2.5000003E-7F && this.rand.nextInt(5) == 0) {
            int i = MathHelper.floor(this.getPosX());
            int j = MathHelper.floor(this.getPosY() - (double)0.2F);
            int k = MathHelper.floor(this.getPosZ());
            BlockPos pos = new BlockPos(i, j, k);
            BlockState blockstate = this.world.getBlockState(pos);
            
            @SuppressWarnings("deprecation")
            boolean isAir = blockstate.isAir(this.world, pos);
            if (!isAir) {
                this.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate).setPos(pos), this.getPosX() + ((double)this.rand.nextFloat() - 0.5D) * (double)this.getWidth(), this.getPosY() + 0.1D, this.getPosZ() + ((double)this.rand.nextFloat() - 0.5D) * (double)this.getWidth(), 4.0D * ((double)this.rand.nextFloat() - 0.5D), 0.5D, ((double)this.rand.nextFloat() - 0.5D) * 4.0D);
            }
        }
        
        if (!this.world.isRemote) {
            this.func_241359_a_((ServerWorld)this.world, true);
        }
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return this.isCompanionOwner(target) ? false : super.canAttack(target);
    }

    @Override
    public boolean shouldAttackEntity(LivingEntity target, PlayerEntity owner) {
        if (target instanceof CreeperEntity) {
            return false;
        } else {
            return super.shouldAttackEntity(target, owner);
        }
    }

    @Override
    public int getAngerTime() {
        return this.dataManager.get(ANGER_TIME);
    }

    @Override
    public void setAngerTime(int time) {
        this.dataManager.set(ANGER_TIME, time);
    }

    @Override
    public UUID getAngerTarget() {
        return this.angerTarget;
    }

    @Override
    public void setAngerTarget(UUID target) {
        this.angerTarget = target;
    }

    @Override
    public void func_230258_H__() {
        this.setAngerTime(ANGER_TIME_RANGE.getRandomWithinRange(this.rand));
    }

    @Override
    public CompanionType getCompanionType() {
        return CompanionType.GOLEM;
    }

    public AbstractEnchantedGolemEntity.Cracks getCrackLevel() {
        return AbstractEnchantedGolemEntity.Cracks.getForHealthPercentage(this.getHealth() / this.getMaxHealth());
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        AbstractEnchantedGolemEntity.Cracks cracksBefore = this.getCrackLevel();
        boolean success = super.attackEntityFrom(source, amount);
        if (success && cracksBefore != this.getCrackLevel()) {
            this.playSound(SoundEvents.ENTITY_IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
        }
        return success;
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        this.attackTimer = 10;
        this.world.setEntityState(this, (byte)4);
        float rawDamage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float damage = ((int)rawDamage > 0) ? (rawDamage / 2.0F) + (float)this.rand.nextInt((int)rawDamage) : rawDamage;
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
        if (flag) {
            entityIn.setMotion(entityIn.getMotion().add(0.0D, 0.4D, 0.0D));
            this.applyEnchantments(this, entityIn);
        }
        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return flag;
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 4) {
            this.attackTimer = 10;
            this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        } else {
            super.handleStatusUpdate(id);
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    public int getAttackTimer() {
        return this.attackTimer;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_IRON_GOLEM_DEATH;
    }
    
    protected abstract ITag<Item> getRepairMaterialTag();
    
    protected abstract float getRepairHealAmount();

    @Override
    protected ActionResultType getEntityInteractionResult(PlayerEntity playerIn, Hand hand) {
        ItemStack itemstack = playerIn.getHeldItem(hand);
        Item item = itemstack.getItem();
        if (!item.isIn(this.getRepairMaterialTag())) {
            ActionResultType actionResult = super.getEntityInteractionResult(playerIn, hand);
            if (!actionResult.isSuccessOrConsume() && this.isCompanionOwner(playerIn) && !this.world.isRemote) {
                long time = playerIn.world.getGameTime();
                if (this.lastStayChangeTime != time) {
                    this.setCompanionStaying(!this.isCompanionStaying());
                    if (this.isCompanionStaying()) {
                        playerIn.sendMessage(new TranslationTextComponent("event.primalmagick.golem.stay"), Util.DUMMY_UUID);
                    } else {
                        playerIn.sendMessage(new TranslationTextComponent("event.primalmagick.golem.follow"), Util.DUMMY_UUID);
                    }
                    this.lastStayChangeTime = time;
                }
                this.navigator.clearPath();
                this.setAttackTarget(null);
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.PASS;
        } else {
            float healthBefore = this.getHealth();
            this.heal(this.getRepairHealAmount());
            if (this.getHealth() == healthBefore) {
                return ActionResultType.PASS;
            } else {
                this.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
                if (!playerIn.abilities.isCreativeMode) {
                    itemstack.shrink(1);
                }
                return ActionResultType.func_233537_a_(this.world.isRemote);
            }
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 1.0F, 1.0F);
    }

    @Override
    public boolean isNotColliding(IWorldReader worldIn) {
        BlockPos downPos = this.getPosition().down();
        BlockState downState = worldIn.getBlockState(downPos);
        if (!downState.canSpawnMobs(worldIn, downPos, this)) {
            return false;
        } else {
            for (int i = 1; i < 3; i++) {
                BlockPos upPos = this.getPosition().up(i);
                BlockState upState = worldIn.getBlockState(upPos);
                if (!WorldEntitySpawner.func_234968_a_(worldIn, upPos, upState, upState.getFluidState(), this.getType())) {
                    return false;
                }
            }
            return WorldEntitySpawner.func_234968_a_(worldIn, this.getPosition(), worldIn.getBlockState(this.getPosition()), Fluids.EMPTY.getDefaultState(), this.getType()) && 
                    worldIn.checkNoEntityCollision(this);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Vector3d getLeashStartPosition() {
        return new Vector3d(0.0D, (double)(0.875F * this.getEyeHeight()), (double)(this.getWidth() * 0.4F));
    }

    @Override
    public boolean isPotionApplicable(EffectInstance potioneffectIn) {
        Effect effect = potioneffectIn.getPotion();
        return (effect == Effects.POISON || effect == EffectsPM.BLEEDING.get()) ? false : super.isPotionApplicable(potioneffectIn);
    }

    public static enum Cracks {
        NONE(1.0F),
        LOW(0.75F),
        MEDIUM(0.5F),
        HIGH(0.25F);
        
        private static final List<AbstractEnchantedGolemEntity.Cracks> SORTED_VALUES = Stream.of(values()).sorted(Comparator.comparingDouble((c) -> {
            return (double)c.healthPercentage;
        })).collect(ImmutableList.toImmutableList());
        
        private final float healthPercentage;
        
        private Cracks(float healthPercentage) {
            this.healthPercentage = healthPercentage;
        }
        
        public static AbstractEnchantedGolemEntity.Cracks getForHealthPercentage(float percentage) {
            for (AbstractEnchantedGolemEntity.Cracks cracks : SORTED_VALUES) {
                if (percentage < cracks.healthPercentage) {
                    return cracks;
                }
            }
            return NONE;
        }
    }
}
