package com.verdantartifice.primalmagick.client.renderers.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.verdantartifice.primalmagick.common.tiles.rituals.OfferingPedestalTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Custom tile entity renderer for offering pedestal blocks.
 * 
 * @author Daedalus4096
 * @see {@link com.verdantartifice.primalmagick.common.blocks.rituals.OfferingPedestalBlock}
 */
@OnlyIn(Dist.CLIENT)
public class OfferingPedestalTER extends TileEntityRenderer<OfferingPedestalTileEntity> {
    public OfferingPedestalTER(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(OfferingPedestalTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        ItemStack stack = tileEntityIn.getSyncedStackInSlot(0).copy();
        if (!stack.isEmpty()) {
            // Render the held item stack above the pedestal
            int rot = (int)(this.renderDispatcher.world.getWorldInfo().getGameTime() % 360);
            matrixStack.push();
            matrixStack.translate(0.5D, 1.5D, 0.5D);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(rot));   // Spin the stack around its Y-axis
            matrixStack.scale(0.75F, 0.75F, 0.75F);
            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GUI, combinedLight, combinedOverlay, matrixStack, buffer);
            matrixStack.pop();
        }
    }
}
