package com.verdantartifice.primalmagick.common.tiles.devices;

import com.verdantartifice.primalmagick.common.blocks.devices.SunlampBlock;
import com.verdantartifice.primalmagick.common.tiles.TileEntityTypesPM;
import com.verdantartifice.primalmagick.common.tiles.base.TilePM;
import com.verdantartifice.primalmagick.common.util.RayTraceUtils;

import net.minecraft.block.Block;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.util.Constants;

/**
 * Definition of a sunlamp tile entity.  Periodically attempts to spawn glow fields in dark air
 * near the sunlamp.
 * 
 * @author Daedalus4096
 */
public class SunlampTileEntity extends TilePM implements ITickableTileEntity {
    protected int ticksExisted = 0;
    
    public SunlampTileEntity() {
        super(TileEntityTypesPM.SUNLAMP.get());
    }

    @Override
    public void tick() {
        this.ticksExisted++;
        if (!this.world.isRemote && this.ticksExisted % 5 == 0) {
            // Pick a random location within 15 blocks
            int x = this.world.rand.nextInt(16) - this.world.rand.nextInt(16);
            int y = this.world.rand.nextInt(16) - this.world.rand.nextInt(16);
            int z = this.world.rand.nextInt(16) - this.world.rand.nextInt(16);
            BlockPos bp = this.pos.add(x, y, z);
            
            // Constrain the selected block pos
            BlockPos rainHeight = this.world.getHeight(Heightmap.Type.MOTION_BLOCKING, bp);
            if (bp.getY() > rainHeight.getY() + 4) {
                bp = rainHeight.up(4);
            }
            if (bp.getY() < 1) {
                bp = new BlockPos(bp.getX(), 1, bp.getZ());
            }

            // If location is ordinary air and dark enough and in line-of-sight, spawn a glow field there
            Block block = this.world.getBlockState(this.pos).getBlock();
            if (block instanceof SunlampBlock) {
                Block glowBlock = ((SunlampBlock)block).getGlowField();
                if ( this.world.isAirBlock(bp) &&
                     this.world.getBlockState(bp) != glowBlock.getDefaultState() &&
                     this.world.getLightFor(LightType.BLOCK, bp) < 11 &&
                     RayTraceUtils.hasLineOfSight(this.world, this.pos, bp) ) {
                    this.world.setBlockState(bp, glowBlock.getDefaultState(), Constants.BlockFlags.DEFAULT);
                }
            }
        }
    }
}
