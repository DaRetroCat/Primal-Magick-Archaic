package com.verdantartifice.primalmagick.common.tiles.rituals;

import com.verdantartifice.primalmagick.common.blocks.devices.SanguineCrucibleBlock;
import com.verdantartifice.primalmagick.common.blocks.rituals.EntropySinkBlock;
import com.verdantartifice.primalmagick.common.tiles.TileEntityTypesPM;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

/**
 * Definition of a bloodletter tile entity.
 *
 * @author Daedalus4096
 */
public class EntropySinkTileEntity extends AbstractRitualPropTileEntity {
    public static final int TICKS_PER_GLOW = 1200;  // 60s

    protected int glowTicks;
    protected boolean isGlowing;

    public EntropySinkTileEntity() {
        super(TileEntityTypesPM.ENTROPY_SINK.get());
        }


    public void tick() {
        if (this.isGlowing) {
            this.glowTicks++;
        }
        if (this.glowTicks >= TICKS_PER_GLOW) {
            this.isGlowing = false;
            this.glowTicks = 0;
            this.world.setBlockState(this.pos, this.world.getBlockState(pos).with(EntropySinkBlock.LIT, false), Constants.BlockFlags.DEFAULT_AND_RERENDER);
        }
    }
    public int getGlowTicks() {
        return this.glowTicks;
    }

    public boolean isGlowing() {
        return this.isGlowing;
    }

    public void startGlowing() {
        if (this.isGlowing) {
            this.glowTicks = 0;
        } else {
            this.isGlowing = true;
        }
        this.world.addBlockEvent(this.getPos(), this.getBlockState().getBlock(), 1, 0);
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            this.isGlowing = true;
            this.glowTicks = 0;
            return true;
        } else {
            return super.receiveClientEvent(id, type);
        }
    }
}
