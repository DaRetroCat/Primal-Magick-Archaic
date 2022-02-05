package com.verdantartifice.primalmagick.common.entities.projectiles;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.verdantartifice.primalmagick.common.entities.EntityTypesPM;
import com.verdantartifice.primalmagick.common.spells.SpellManager;
import com.verdantartifice.primalmagick.common.spells.SpellPackage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * Definition for a spell mine entity.  Sits in the world until another entity collides with it, at which point it executes a
 * given spell package upon the colliding entity.
 * 
 * @author Daedalus4096
 */
public class SpellMineEntity extends Entity {
    protected static final DataParameter<Integer> COLOR = EntityDataManager.createKey(SpellMineEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Boolean> ARMED = EntityDataManager.createKey(SpellMineEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Integer> LIFESPAN = EntityDataManager.createKey(SpellMineEntity.class, DataSerializers.VARINT);
    
    protected static final int DURATION_FACTOR = 4800;  // Number of ticks to live per duration of the mine
    protected static final int ARMING_TIME = 60;        // Number of ticks before switching to an armed state
    
    protected SpellPackage spell;
    protected LivingEntity caster;
    protected UUID casterId;
    protected ItemStack spellSource;
    protected int currentLife = 0;
    
    public SpellMineEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        this.spell = null;
    }
    
    public SpellMineEntity(World world, Vector3d pos, LivingEntity caster, SpellPackage spell, @Nullable ItemStack spellSource, int duration) {
        super(EntityTypesPM.SPELL_MINE.get(), world);
        this.setPosition(pos.x, pos.y, pos.z);
        this.spell = spell;
        this.spellSource = spellSource.copy();
        this.caster = caster;
        this.casterId = caster.getUniqueID();
        this.setLifespan(duration * DURATION_FACTOR);
        if (spell != null && spell.getPayload() != null) {
            // Store the spell payload's color for use in rendering
            this.setColor(spell.getPayload().getSource().getColor());
        }
    }
    
    @Nullable
    public SpellPackage getSpell() {
        return this.spell;
    }

    public int getColor() {
        return this.getDataManager().get(COLOR).intValue();
    }
    
    protected void setColor(int color) {
        this.getDataManager().set(COLOR, Integer.valueOf(color));
    }
    
    public boolean isArmed() {
        return this.getDataManager().get(ARMED).booleanValue();
    }
    
    protected void setArmed(boolean armed) {
        this.getDataManager().set(ARMED, Boolean.valueOf(armed));
    }
    
    protected int getLifespan() {
        return this.getDataManager().get(LIFESPAN).intValue();
    }
    
    protected void setLifespan(int ticks) {
        this.getDataManager().set(LIFESPAN, Integer.valueOf(ticks));
    }
    
    @Nullable
    public LivingEntity getCaster() {
        if (this.caster == null && this.casterId != null && this.world instanceof ServerWorld) {
            // If the caster cache is empty, find the entity matching the caster's unique ID
            Entity entity = ((ServerWorld)this.world).getEntityByUuid(this.casterId);
            if (entity != null && entity instanceof LivingEntity) {
                this.caster = (LivingEntity)entity;
            } else {
                this.casterId = null;
            }
        }
        return this.caster;
    }

    @Override
    protected void registerData() {
        this.getDataManager().register(COLOR, 0xFFFFFF);
        this.getDataManager().register(ARMED, Boolean.FALSE);
        this.getDataManager().register(LIFESPAN, 0);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        this.caster = null;
        if (compound.contains("Caster", Constants.NBT.TAG_COMPOUND)) {
            this.casterId =  compound.getUniqueId("Caster");
        }
        
        this.spell = null;
        if (compound.contains("Spell", Constants.NBT.TAG_COMPOUND)) {
            this.spell = new SpellPackage(compound.getCompound("Spell"));
        }
        if (this.spell != null && !this.spell.isValid()) {
            this.spell = null;
        }
        if (this.spell != null && this.spell.getPayload() != null) {
            this.setColor(this.spell.getPayload().getSource().getColor());
        }
        
        this.spellSource = null;
        if (compound.contains("SpellSource", Constants.NBT.TAG_COMPOUND)) {
            this.spellSource = ItemStack.read(compound.getCompound("SpellSource"));
        }
        
        this.currentLife = compound.getInt("CurrentLife");
        this.setLifespan(compound.getInt("Lifespan"));
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        if (this.casterId != null) {
            compound.putUniqueId("Caster", this.casterId);
        }
        if (this.spell != null) {
            compound.put("Spell", this.spell.serializeNBT());
        }
        if (this.spellSource != null) {
            compound.put("SpellSource", this.spellSource.serializeNBT());
        }
        compound.putInt("CurrentLife", this.currentLife);
        compound.putInt("Lifespan", this.getLifespan());
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isRemote && (this.spell == null || !this.spell.isValid())) {
            this.remove();
        }
        if (++this.currentLife > this.getLifespan()) {
            this.remove();
        }
        if (!this.world.isRemote && this.isAlive()) {
            if (!this.isArmed() && this.currentLife >= ARMING_TIME) {
                this.setArmed(true);
            }
            if (this.isArmed() && this.currentLife % 5 == 0) {
                // Scan for living entities within a block
                AxisAlignedBB aabb = new AxisAlignedBB(this.getPositionVec(), this.getPositionVec()).grow(1.0D);
                List<Entity> entityList = this.world.getEntitiesInAABBexcluding(this, aabb, e -> (e instanceof LivingEntity));
                boolean found = false;
                for (Entity entity : entityList) {
                    if (entity.isAlive()) {
                        // If found, execute the spell payload on them then remove self
                        if (this.spell != null && this.spell.getPayload() != null) {
                            this.spell.getPayload().playSounds(this.world, this.getPosition());
                        }
                        if (this.getCaster() != null) {
                            SpellManager.executeSpellPayload(this.spell, new EntityRayTraceResult(entity, this.getPositionVec().add(0.0D, 0.5D, 0.0D)), this.world, this.getCaster(), this.spellSource, false);
                        }
                        found = true;
                    }
                }
                if (found) {
                    this.remove();
                }
            }
        }
    }
}
