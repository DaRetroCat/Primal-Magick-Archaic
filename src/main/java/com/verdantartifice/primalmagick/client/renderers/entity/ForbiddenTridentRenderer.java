package com.verdantartifice.primalmagick.client.renderers.entity;

import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.common.entities.projectiles.AbstractTridentEntity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Renderer definition for a thrown forbidden trident.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class ForbiddenTridentRenderer extends AbstractTridentRenderer {
    public static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/entity/trident/forbidden_trident.png");

    public ForbiddenTridentRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public ResourceLocation getEntityTexture(AbstractTridentEntity entity) {
        return TEXTURE;
    }
}
