package com.verdantartifice.primalmagick.common.entities.projectiles;

import javax.annotation.Nullable;

import com.verdantartifice.primalmagick.common.entities.EntityTypesPM;
import com.verdantartifice.primalmagick.common.network.PacketHandler;
import com.verdantartifice.primalmagick.common.network.packets.fx.SpellTrailPacket;
import com.verdantartifice.primalmagick.common.spells.SpellManager;
import com.verdantartifice.primalmagick.common.spells.SpellPackage;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * Definition for a spell projectile entity.  Shot from a wand or scroll, they fly through the world until colliding with a
 * block or another entity, at which point it executes a given spell package upon the collider.
 * 
 * @author Daedalus4096
 */
public class SpellProjectileEntity extends ThrowableEntity {
    protected static final DataParameter<Integer> COLOR = EntityDataManager.createKey(SpellProjectileEntity.class, DataSerializers.VARINT);
    
    protected final SpellPackage spell;
    protected final ItemStack spellSource;
    
    public SpellProjectileEntity(EntityType<? extends ThrowableEntity> type, World worldIn) {
        super(type, worldIn);
        this.spell = null;
        this.spellSource = null;
    }
    
    public SpellProjectileEntity(World world, LivingEntity thrower, SpellPackage spell, @Nullable ItemStack spellSource) {
        super(EntityTypesPM.SPELL_PROJECTILE.get(), thrower, world);
        this.spell = spell;
        this.spellSource = spellSource == null ? null : spellSource.copy();
        if (spell != null && spell.getPayload() != null) {
            // Store the spell payload's color for use in rendering
            this.setColor(spell.getPayload().getSource().getColor());
        }
    }
    
    public SpellProjectileEntity(World world, double x, double y, double z, SpellPackage spell, @Nullable ItemStack spellSource) {
        super(EntityTypesPM.SPELL_PROJECTILE.get(), x, y, z, world);
        this.spell = spell;
        this.spellSource = spellSource == null ? null : spellSource.copy();
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
    
    @Override
    public void tick() {
        super.tick();
        if (!this.world.isRemote && this.isAlive() && this.ticksExisted % 2 == 0 && this.spell != null && this.spell.getPayload() != null) {
            // Leave a trail of particles in this entity's wake
            PacketHandler.sendToAllAround(
                    new SpellTrailPacket(this.getPositionVec(), this.spell.getPayload().getSource().getColor()), 
                    this.world.getDimensionKey(), 
                    this.getPosition(), 
                    64.0D);
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {
            if (result.getType() == RayTraceResult.Type.ENTITY && ((EntityRayTraceResult)result).getEntity() instanceof SpellProjectileEntity) {
                // Don't collide with other spell projectiles
                return;
            }
            if (this.spell != null && this.spell.getPayload() != null) {
                LivingEntity shooter = (this.getShooter() instanceof LivingEntity) ? (LivingEntity)this.getShooter() : null;
                SpellManager.executeSpellPayload(this.spell, result, this.world, shooter, this.spellSource, true);
            }
            this.remove();
        }
    }

    @Override
    protected void registerData() {
        this.getDataManager().register(COLOR, 0xFFFFFF);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
