package com.verdantartifice.primalmagick.client.renderers.entity;

import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.client.renderers.entity.layers.HallowsteelGolemCracksLayer;
import com.verdantartifice.primalmagick.common.entities.companions.golems.HallowsteelGolemEntity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Entity renderer for a hallowsteel golem.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class HallowsteelGolemRenderer extends AbstractEnchantedGolemRenderer<HallowsteelGolemEntity> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/entity/hallowsteel_golem/hallowsteel_golem.png");
    
    public HallowsteelGolemRenderer(EntityRendererManager rendererManager) {
        super(rendererManager);
        this.addLayer(new HallowsteelGolemCracksLayer(this));
    }

    @Override
    public ResourceLocation getEntityTexture(HallowsteelGolemEntity entity) {
        return TEXTURE;
    }
}
