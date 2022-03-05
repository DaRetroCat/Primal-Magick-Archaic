package com.verdantartifice.primalmagick.common.rituals;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Class identifying a single step in a ritual's process.
 * 
 * @author Daedalus4096
 */
public abstract class AbstractRitualStep implements INBTSerializable<CompoundNBT> {
    protected RitualStepType type;
    
    public AbstractRitualStep() {
        this.type = null;
    }
    
    public AbstractRitualStep(RitualStepType type) {
        this.type = type;
    }
    
    public boolean isValid() {
        return this.type != null;
    }
    
    public RitualStepType getType() {
        return this.type;
    }
    
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT retVal = new CompoundNBT();
        retVal.putString("Type", this.type.getString());
        return retVal;
    }
    
    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.type = RitualStepType.fromName(nbt.getString("Type"));
    }
}
