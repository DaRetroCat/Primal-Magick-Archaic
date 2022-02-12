package com.verdantartifice.primalmagick.client.renderers.tile;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.common.blocks.mana.AbstractManaFontBlock;
import com.verdantartifice.primalmagick.common.tiles.mana.AbstractManaFontTileEntity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Custom tile entity renderer for mana font blocks.
 * 
 * @author Daedalus4096
 * @see {@link com.verdantartifice.primalmagick.common.blocks.mana.AncientManaFontBlock}
 */
@OnlyIn(Dist.CLIENT)
public class ManaFontTER extends TileEntityRenderer<AbstractManaFontTileEntity> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagick.MODID, "entity/mana_font_core");

    public ManaFontTER(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }
    
    protected void addVertex(IVertexBuilder renderer, MatrixStack stack, float x, float y, float z, float r, float g, float b, float u, float v) {
        renderer.pos(stack.getLast().getMatrix(), x, y, z)
                .color(r, g, b, 1.0F)
                .tex(u, v)
                .lightmap(240, 80)
                .normal(1, 0, 0)
                .endVertex();
    }

    @Override
    public void render(AbstractManaFontTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
    	Minecraft mc = Minecraft.getInstance();
        Block block = tileEntityIn.getBlockState().getBlock();
        if (tileEntityIn != null && block instanceof AbstractManaFontBlock) {
            // Color the tile entity core according to the block's source
            Color sourceColor = new Color(((AbstractManaFontBlock)block).getSource().getColor());
            float r = sourceColor.getRed() / 255.0F;
            float g = sourceColor.getGreen() / 255.0F;
            float b = sourceColor.getBlue() / 255.0F;
            float ds = 0.1875F;
            int rot = (int)(this.renderDispatcher.world.getWorldInfo().getGameTime() % 360);
            float scale = (float)tileEntityIn.getMana() / Math.max(20.0F, (float)tileEntityIn.getManaCapacity());    // Shrink the core as it holds less mana
            
            @SuppressWarnings("deprecation")
            TextureAtlasSprite sprite = mc.getModelManager().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).getSprite(ManaFontTER.TEXTURE);
            IVertexBuilder builder = buffer.getBuffer(RenderType.getSolid());
            
            matrixStack.push();
            matrixStack.translate(0.5D, 0.5D, 0.5D);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(rot));   // Spin the core around its Y-axis
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(45.0F)); // Tilt the core onto its diagonal
            matrixStack.rotate(Vector3f.XP.rotationDegrees(45.0F)); // Tilt the core onto its diagonal
            matrixStack.scale(scale, scale, scale);
            
            // Draw the south face of the core
            this.addVertex(builder, matrixStack, -ds, ds, ds, r, g, b, sprite.getMinU(), sprite.getMaxV());
            this.addVertex(builder, matrixStack, -ds, -ds, ds, r, g, b, sprite.getMinU(), sprite.getMinV());
            this.addVertex(builder, matrixStack, ds, -ds, ds, r, g, b, sprite.getMaxU(), sprite.getMinV());
            this.addVertex(builder, matrixStack, ds, ds, ds, r, g, b, sprite.getMaxU(), sprite.getMaxV());
            
            // Draw the north face of the core
            this.addVertex(builder, matrixStack, -ds, ds, -ds, r, g, b, sprite.getMinU(), sprite.getMaxV());
            this.addVertex(builder, matrixStack, ds, ds, -ds, r, g, b, sprite.getMaxU(), sprite.getMaxV());
            this.addVertex(builder, matrixStack, ds, -ds, -ds, r, g, b, sprite.getMaxU(), sprite.getMinV());
            this.addVertex(builder, matrixStack, -ds, -ds, -ds, r, g, b, sprite.getMinU(), sprite.getMinV());
            
            // Draw the east face of the core
            this.addVertex(builder, matrixStack, ds, ds, -ds, r, g, b, sprite.getMinU(), sprite.getMaxV());
            this.addVertex(builder, matrixStack, ds, ds, ds, r, g, b, sprite.getMaxU(), sprite.getMaxV());
            this.addVertex(builder, matrixStack, ds, -ds, ds, r, g, b, sprite.getMaxU(), sprite.getMinV());
            this.addVertex(builder, matrixStack, ds, -ds, -ds, r, g, b, sprite.getMinU(), sprite.getMinV());
            
            // Draw the west face of the core
            this.addVertex(builder, matrixStack, -ds, -ds, ds, r, g, b, sprite.getMaxU(), sprite.getMinV());
            this.addVertex(builder, matrixStack, -ds, ds, ds, r, g, b, sprite.getMaxU(), sprite.getMaxV());
            this.addVertex(builder, matrixStack, -ds, ds, -ds, r, g, b, sprite.getMinU(), sprite.getMaxV());
            this.addVertex(builder, matrixStack, -ds, -ds, -ds, r, g, b, sprite.getMinU(), sprite.getMinV());
            
            // Draw the top face of the core
            this.addVertex(builder, matrixStack, ds, ds, -ds, r, g, b, sprite.getMaxU(), sprite.getMinV());
            this.addVertex(builder, matrixStack, -ds, ds, -ds, r, g, b, sprite.getMinU(), sprite.getMinV());
            this.addVertex(builder, matrixStack, -ds, ds, ds, r, g, b, sprite.getMinU(), sprite.getMaxV());
            this.addVertex(builder, matrixStack, ds, ds, ds, r, g, b, sprite.getMaxU(), sprite.getMaxV());
            
            // Draw the bottom face of the core
            this.addVertex(builder, matrixStack, ds, -ds, -ds, r, g, b, sprite.getMaxU(), sprite.getMinV());
            this.addVertex(builder, matrixStack, ds, -ds, ds, r, g, b, sprite.getMaxU(), sprite.getMaxV());
            this.addVertex(builder, matrixStack, -ds, -ds, ds, r, g, b, sprite.getMinU(), sprite.getMaxV());
            this.addVertex(builder, matrixStack, -ds, -ds, -ds, r, g, b, sprite.getMinU(), sprite.getMinV());
            
            matrixStack.pop();
        }
    }
}
