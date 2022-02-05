package com.verdantartifice.primalmagick.client.fx.particles;

import java.util.Locale;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Particle data, specifically target point, for spell bolts
 * 
 * @author Daedalus4096
 */
public class SpellBoltParticleData implements IParticleData {
    public static final Codec<SpellBoltParticleData> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.DOUBLE.fieldOf("x").forGetter((data) -> {
            return data.target.x;
        }), Codec.DOUBLE.fieldOf("y").forGetter((data) -> {
            return data.target.y;
        }), Codec.DOUBLE.fieldOf("z").forGetter((data) -> {
            return data.target.z;
        })).apply(instance, SpellBoltParticleData::new);
    });
    
    @SuppressWarnings("deprecation")
    public static final IParticleData.IDeserializer<SpellBoltParticleData> DESERIALIZER = new IParticleData.IDeserializer<SpellBoltParticleData>() {
        @Override
        public SpellBoltParticleData deserialize(ParticleType<SpellBoltParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            double x = reader.readDouble();
            reader.expect(' ');
            double y = reader.readDouble();
            reader.expect(' ');
            double z = reader.readDouble();
            return new SpellBoltParticleData(x, y, z);
        }

        @Override
        public SpellBoltParticleData read(ParticleType<SpellBoltParticleData> particleTypeIn, PacketBuffer buffer) {
            return new SpellBoltParticleData(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }
    };
    
    protected final Vector3d target;
    
    public SpellBoltParticleData(Vector3d target) {
        this(target.x, target.y, target.z);
    }
    
    public SpellBoltParticleData(double targetX, double targetY, double targetZ) {
        this.target = new Vector3d(targetX, targetY, targetZ);
    }

    @Override
    public ParticleType<?> getType() {
        return ParticleTypesPM.SPELL_BOLT.get();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeDouble(this.target.x);
        buffer.writeDouble(this.target.y);
        buffer.writeDouble(this.target.z);
    }

    @Override
    public String getParameters() {
        return String.format(Locale.ROOT, "%s %d %d %d", ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()), this.target.x, this.target.y, this.target.z);
    }

    public Vector3d getTargetVec() {
        return this.target;
    }
}
