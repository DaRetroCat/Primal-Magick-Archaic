package com.verdantartifice.primalmagick.client.renderers.entity;

import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.client.renderers.entity.layers.PrimaliteGolemCracksLayer;
import com.verdantartifice.primalmagick.common.entities.companions.golems.PrimaliteGolemEntity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Entity renderer for a primalite golem.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class PrimaliteGolemRenderer extends AbstractEnchantedGolemRenderer<PrimaliteGolemEntity> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/entity/primalite_golem/primalite_golem.png");
    
    public PrimaliteGolemRenderer(EntityRendererManager rendererManager) {
        super(rendererManager);
        this.addLayer(new PrimaliteGolemCracksLayer(this));
    }

    @Override
    public ResourceLocation getEntityTexture(PrimaliteGolemEntity entity) {
        return TEXTURE;
    }
}
