package com.verdantartifice.primalmagick.client.gui;

import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.common.containers.RunescribingAltarForbiddenContainer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * GUI screen for forbidden runescribing altar blocks.
 * 
 * @author Daedalus4096
 */
@OnlyIn(Dist.CLIENT)
public class RunescribingAltarForbiddenScreen extends AbstractRunescribingAltarScreen<RunescribingAltarForbiddenContainer> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagick.MODID, "textures/gui/runescribing_altar_7.png");

    public RunescribingAltarForbiddenScreen(RunescribingAltarForbiddenContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }
    
    @Override
    protected ResourceLocation getTextureLocation() {
        return TEXTURE;
    }
}
