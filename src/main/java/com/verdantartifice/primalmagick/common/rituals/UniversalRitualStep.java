package com.verdantartifice.primalmagick.common.rituals;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

/**
 * Class identifying a single step in a ritual's process, one targeting a nearby universal prop.
 *
 * @author Daedalus4096
 */
public class UniversalRitualStep extends AbstractRitualStep {
    protected BlockPos pos;

    public UniversalRitualStep() {
        super();
        this.pos = BlockPos.ZERO;
    }

    public UniversalRitualStep(BlockPos pos) {
        super(RitualStepType.UNIVERSAL_PROP);
        this.pos = pos;
    }

    @Override
    public boolean isValid() {
        return super.isValid() && this.pos != null;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT retVal = super.serializeNBT();
        retVal.putInt("PosX", this.pos.getX());
        retVal.putInt("PosY", this.pos.getY());
        retVal.putInt("PosZ", this.pos.getZ());
        return retVal;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        this.pos = new BlockPos(nbt.getInt("PosX"), nbt.getInt("PosY"), nbt.getInt("PosZ"));
    }
}