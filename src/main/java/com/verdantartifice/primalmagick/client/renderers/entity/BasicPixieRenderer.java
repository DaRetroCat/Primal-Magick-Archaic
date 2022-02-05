package com.verdantartifice.primalmagick.client.renderers.entity;

import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.client.renderers.entity.model.PixieModel;
import com.verdantartifice.primalmagick.common.entities.companions.pixies.AbstractPixieEntity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

/**
 * Entity renderer for a basic pixie.
 * 
 * @author Daedalus4096
 */
public class BasicPixieRenderer extends AbstractPixieRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/entity/pixie/basic_pixie.png");
    
    public BasicPixieRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new PixieModel(false));
    }

    @Override
    public ResourceLocation getEntityTexture(AbstractPixieEntity entity) {
        return TEXTURE;
    }
}
