package com.verdantartifice.primalmagick.client.fx.particles;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.verdantartifice.primalmagick.client.renderers.types.ThickLinesRenderType;
import com.verdantartifice.primalmagick.common.util.LineSegment;
import com.verdantartifice.primalmagick.common.util.VectorUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Particle type shown when casting a bolt-vehicle spell.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class SpellBoltParticle extends Particle {
    protected static final float WIDTH = 6F;
    protected static final double MAX_DISPLACEMENT = 0.5D;
    protected static final double PERTURB_DISTANCE = 0.002D;
    protected static final int GENERATIONS = 5;
    
    protected final Vector3d delta;
    protected final List<LineSegment> segmentList;
    protected final List<Vector3d> perturbList;
    
    protected SpellBoltParticle(ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Vector3d target) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.delta = target.subtract(new Vector3d(x, y, z));
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.maxAge = 10;
        this.segmentList = this.calcSegments();
        this.perturbList = this.calcPerturbs();
    }
    
    @Nonnull
    protected List<LineSegment> calcSegments() {
        List<LineSegment> retVal = new ArrayList<>();
        double curDisplacement = MAX_DISPLACEMENT;
        
        // Fractally generate a series of line segments, splitting each at the midpoint and adding an orthogonal displacement
        retVal.add(new LineSegment(Vector3d.ZERO, this.delta));
        for (int gen = 0; gen < GENERATIONS; gen++) {
            List<LineSegment> tempList = new ArrayList<>();
            for (LineSegment segment : retVal) {
            	Vector3d midpoint = segment.getMiddle();
                midpoint = midpoint.add(VectorUtils.getRandomOrthogonalUnitVector(segment.getDelta(), this.world.rand).scale(curDisplacement));
                tempList.add(new LineSegment(segment.getStart(), midpoint));
                tempList.add(new LineSegment(midpoint, segment.getEnd()));
            }
            retVal = tempList;
            curDisplacement /= 2.0D;
        }
        return retVal;
    }
    
    @Nonnull
    protected List<Vector3d> calcPerturbs() {
        // Generate a perturbation vector for each point in the segment list, except for the start and end points
        List<Vector3d> retVal = new ArrayList<>();
        retVal.add(Vector3d.ZERO);
        for (LineSegment segment : this.segmentList) {
            retVal.add(segment.getEnd().equals(this.delta) ? Vector3d.ZERO : VectorUtils.getRandomUnitVector(this.world.rand).scale(PERTURB_DISTANCE * this.world.rand.nextDouble()));
        }
        return retVal;
    }
    
    @Override
    public void renderParticle(IVertexBuilder builder, ActiveRenderInfo entityIn, float partialTicks) {
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.disableCull();

        RenderSystem.pushMatrix();
        RenderSystem.translated(this.posX - entityIn.getProjectedView().x, this.posY - entityIn.getProjectedView().y, this.posZ - entityIn.getProjectedView().z);

        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        IVertexBuilder lineBuilder = buffer.getBuffer(ThickLinesRenderType.THICK_LINES);
        
        // Draw each line segment
        for (int index = 0; index < this.segmentList.size(); index++) {
            LineSegment segment = this.segmentList.get(index);
            
            // Move the endpoints of each segment along their computed motion path before rendering to make the bolt move
            segment.perturb(this.perturbList.get(index), this.perturbList.get(index + 1));
            
            lineBuilder.pos(segment.getStart().x, segment.getStart().y, segment.getStart().z).color(this.particleRed, this.particleGreen, this.particleBlue, 0.5F).endVertex();
            lineBuilder.pos(segment.getEnd().x, segment.getEnd().y, segment.getEnd().z).color(this.particleRed, this.particleGreen, this.particleBlue, 0.5F).endVertex();
        }
        buffer.finish(ThickLinesRenderType.THICK_LINES);
        
        RenderSystem.enableCull();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);

        RenderSystem.popMatrix();
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.CUSTOM;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<SpellBoltParticleData> {
        public Factory(IAnimatedSprite spriteSet) {}
        
        @Override
        public Particle makeParticle(SpellBoltParticleData typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new SpellBoltParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, typeIn.getTargetVec());
        }
    }
}
