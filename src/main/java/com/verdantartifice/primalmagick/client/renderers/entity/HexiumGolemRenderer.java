package com.verdantartifice.primalmagick.client.renderers.entity;

import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.client.renderers.entity.layers.HexiumGolemCracksLayer;
import com.verdantartifice.primalmagick.common.entities.companions.golems.HexiumGolemEntity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Entity renderer for a hexium golem.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class HexiumGolemRenderer extends AbstractEnchantedGolemRenderer<HexiumGolemEntity> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/entity/hexium_golem/hexium_golem.png");
    
    public HexiumGolemRenderer(EntityRendererManager rendererManager) {
        super(rendererManager);
        this.addLayer(new HexiumGolemCracksLayer(this));
    }

    @Override
    public ResourceLocation getEntityTexture(HexiumGolemEntity entity) {
        return TEXTURE;
    }
}
