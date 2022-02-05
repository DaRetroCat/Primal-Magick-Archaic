package com.verdantartifice.primalmagick.client.renderers.itemstack;

import com.verdantartifice.primalmagick.PrimalMagick;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Custom item stack renderer for a forbidden trident.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class ForbiddenTridentISTER extends AbstractTieredTridentISTER {
    protected static final ModelResourceLocation MRL = new ModelResourceLocation(new ResourceLocation(PrimalMagick.MODID, "forbidden_trident"), "inventory");
    protected static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/entity/trident/forbidden_trident.png");

    @Override
    public ModelResourceLocation getModelResourceLocation() {
        return MRL;
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return TEXTURE;
    }
}
