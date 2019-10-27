package com.verdantartifice.primalmagic.client.fx;

import java.awt.Color;
import java.util.Random;

import com.verdantartifice.primalmagic.client.fx.particles.ParticleTypesPM;
import com.verdantartifice.primalmagic.common.sounds.SoundsPM;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FxDispatcher {
    public static final FxDispatcher INSTANCE = new FxDispatcher();
    
    protected World getWorld() {
        return Minecraft.getInstance().world;
    }
    
    public void wandPoof(double x, double y, double z, int color, boolean sound, Direction side) {
        Color c = new Color(color);
        float r = c.getRed() / 255.0F;
        float g = c.getGreen() / 255.0F;
        float b = c.getBlue() / 255.0F;
        this.wandPoof(x, y, z, r, g, b, sound, side);
    }
    
    public void wandPoof(double x, double y, double z, float r, float g, float b, boolean sound, Direction side) {
        Random rng = getWorld().rand;
        if (sound) {
            getWorld().playSound(x, y, z, SoundsPM.POOF, SoundCategory.BLOCKS, 1.0F, 1.0F + (float)rng.nextGaussian() * 0.05F, false);
        }
        for (int index = 0; index < 8 + rng.nextInt(3); index++) {
            double dx = (rng.nextFloat() * 0.05D) * (rng.nextBoolean() ? 1 : -1);
            double dy = (rng.nextFloat() * 0.05D) * (rng.nextBoolean() ? 1 : -1);
            double dz = (rng.nextFloat() * 0.05D) * (rng.nextBoolean() ? 1 : -1);
            if (side != null) {
                dx += (side.getXOffset() * 0.1D);
                dy += (side.getYOffset() * 0.1D);
                dz += (side.getZOffset() * 0.1D);
            }
            Particle p = Minecraft.getInstance().particles.addParticle(ParticleTypesPM.WAND_POOF, x + dx * 2.0D, y + dy * 2.0D, z + dz * 2.0D, dx / 2.0D, dy / 2.0D, dz / 2.0D);
            if (p != null) {
                p.setColor(r, g, b);
            }
        }
    }
    
    public void manaSparkle(double x1, double y1, double z1, double x2, double y2, double z2, int maxAge, int color) {
        Color c = new Color(color);
        float r = c.getRed() / 255.0F;
        float g = c.getGreen() / 255.0F;
        float b = c.getBlue() / 255.0F;
        this.manaSparkle(x1, y1, z1, x2, y2, z2, maxAge, r, g, b);
    }
    
    public void manaSparkle(double x1, double y1, double z1, double x2, double y2, double z2, int maxAge, float r, float g, float b) {
        double vx = (x2 - x1) / (double)maxAge;
        double vy = (y2 - y1) / (double)maxAge;
        double vz = (z2 - z1) / (double)maxAge;
        Particle p = Minecraft.getInstance().particles.addParticle(ParticleTypesPM.MANA_SPARKLE, x1, y1, z1, vx, vy, vz);
        if (p != null) {
            p.setColor(r, g, b);
            p.setMaxAge(maxAge);
        }
    }
}