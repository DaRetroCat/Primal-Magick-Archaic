package com.verdantartifice.primalmagic.client.gui.grimoire.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.verdantartifice.primalmagic.PrimalMagic;
import com.verdantartifice.primalmagic.client.gui.grimoire.GrimoireScreen;
import com.verdantartifice.primalmagic.common.sounds.SoundsPM;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class PageButton extends Button {
    private static final ResourceLocation TEXTURE = new ResourceLocation(PrimalMagic.MODID, "textures/gui/grimoire.png");

    protected GrimoireScreen screen;
    protected boolean isNext;

    public PageButton(int widthIn, int heightIn, GrimoireScreen screen, boolean isNext) {
        super(widthIn, heightIn, 12, 5, "", new Handler());
        this.screen = screen;
        this.isNext = isNext;
    }
    
    public GrimoireScreen getScreen() {
        return this.screen;
    }
    
    public boolean isNext() {
        return this.isNext;
    }
    
    @Override
    public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft mc = Minecraft.getInstance();
        mc.getTextureManager().bindTexture(TEXTURE);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.isHovered()) {
            float scaleMod = MathHelper.sin(mc.player.ticksExisted / 3.0F) * 0.2F + 0.1F;
            GlStateManager.pushMatrix();
            int dx = this.width / 2;
            int dy = this.height / 2;
            GlStateManager.translatef(this.x + dx, this.y + dy, 0.0F);
            GlStateManager.scalef(1.0F + scaleMod, 1.0F + scaleMod, 1.0F);
            this.blit(-dx, -dy, this.isNext ? 12 : 0, 185, this.width, this.height);
            GlStateManager.popMatrix();
        } else {
            this.blit(this.x, this.y, this.isNext ? 12 : 0, 185, this.width, this.height);
        }
    }
    
    @Override
    public void playDownSound(SoundHandler handler) {
        handler.play(SimpleSound.master(SoundsPM.PAGE, 1.0F, 1.0F));
    }

    private static class Handler implements IPressable {
        @Override
        public void onPress(Button button) {
            if (button instanceof PageButton) {
                PageButton gpb = (PageButton)button;
                if (gpb.isNext()) {
                    gpb.getScreen().nextPage();
                } else {
                    gpb.getScreen().prevPage();
                }
            }
        }
    }
}