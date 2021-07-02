package com.verdantartifice.primalmagic.client.renderers.entity;

import com.verdantartifice.primalmagic.PrimalMagic;
import com.verdantartifice.primalmagic.common.entities.projectiles.AbstractTridentEntity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Renderer definition for a thrown primalite trident.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class PrimaliteTridentRenderer extends AbstractTridentRenderer {
    public static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagic.MODID, "textures/entity/trident/primalite_trident.png");

    public PrimaliteTridentRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public ResourceLocation getEntityTexture(AbstractTridentEntity entity) {
        return TEXTURE;
    }
}
