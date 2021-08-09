package com.verdantartifice.primalmagic.client.renderers.entity;

import com.verdantartifice.primalmagic.PrimalMagic;
import com.verdantartifice.primalmagic.common.entities.projectiles.AbstractTridentEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Renderer definition for a thrown hallowsteel trident.
 * 
 * @author Daedalus4096
 */
public class HallowsteelTridentRenderer extends AbstractTridentRenderer {
    public static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagic.MODID, "textures/entity/trident/hallowsteel_trident.png");

    public HallowsteelTridentRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractTridentEntity entity) {
        return TEXTURE;
    }
}