package com.verdantartifice.primalmagick.common.spells.vehicles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.verdantartifice.primalmagick.common.spells.SpellProperty;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Base class for a spell vehicle.  Handles property management and serialization.
 * 
 * @author Daedalus4096
 */
public abstract class AbstractSpellVehicle implements ISpellVehicle {
    protected final Map<String, SpellProperty> properties;
    
    public AbstractSpellVehicle() {
        this.properties = this.initProperties();
    }

    /**
     * Get the type name for this spell vehicle.
     * 
     * @return the type name for this spell vehicle
     */
    protected abstract String getVehicleType();

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("VehicleType", this.getVehicleType());
        for (Map.Entry<String, SpellProperty> entry : this.properties.entrySet()) {
            nbt.putInt(entry.getKey(), entry.getValue().getValue());
        }
        return nbt;
    }
    
    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        for (Map.Entry<String, SpellProperty> entry : this.properties.entrySet()) {
            entry.getValue().setValue(nbt.getInt(entry.getKey()));
        }
    }
    
    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public ITextComponent getTypeName() {
        return new TranslationTextComponent("primalmagick.spell.vehicle.type." + this.getVehicleType());
    }
    
    @Override
    public ITextComponent getDefaultNamePiece() {
        return new TranslationTextComponent("primalmagick.spell.vehicle.default_name." + this.getVehicleType());
    }
    
    @Override
    public int getBaseManaCostModifier() {
        // No change by default
        return 0;
    }

    /**
     * Initialize the property map for this spell vehicle.  Should create a maximum of two properties.
     * 
     * @return a map of property names to spell properties
     */
    @Nonnull
    protected Map<String, SpellProperty> initProperties() {
        return new HashMap<>();
    }
    
    @Override
    public List<SpellProperty> getProperties() {
        // Sort properties by their display names
        return this.properties.values().stream().sorted((p1, p2) -> p1.getName().compareTo(p2.getName())).collect(Collectors.toList());
    }

    @Override
    public SpellProperty getProperty(String name) {
        return this.properties.get(name);
    }

    @Override
    public int getPropertyValue(String name) {
        return this.properties.containsKey(name) ? this.properties.get(name).getValue() : 0;
    }
}
