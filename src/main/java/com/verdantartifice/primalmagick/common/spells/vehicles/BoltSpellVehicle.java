package com.verdantartifice.primalmagick.common.spells.vehicles;

import java.util.Map;

import com.verdantartifice.primalmagick.common.network.PacketHandler;
import com.verdantartifice.primalmagick.common.network.packets.fx.SpellBoltPacket;
import com.verdantartifice.primalmagick.common.research.CompoundResearchKey;
import com.verdantartifice.primalmagick.common.research.SimpleResearchKey;
import com.verdantartifice.primalmagick.common.spells.SpellPackage;
import com.verdantartifice.primalmagick.common.spells.SpellProperty;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

/**
 * Definition of a bolt spell vehicle.  Bolts are mid-range, instant spell vehicles that are not
 * affected by gravity.  Essentially, they're a longer-range touch vehicle with a particle effect.
 * 
 * @author Daedalus4096
 */
public class BoltSpellVehicle extends AbstractRaycastSpellVehicle {
    public static final String TYPE = "bolt";
    protected static final CompoundResearchKey RESEARCH = CompoundResearchKey.from(SimpleResearchKey.parse("SPELL_VEHICLE_BOLT"));
    
    public static CompoundResearchKey getResearch() {
        return RESEARCH;
    }

    @Override
    protected double getReachDistance(LivingEntity caster) {
        return 6.0D + (2.0D * this.getPropertyValue("range"));
    }

    @Override
    protected String getVehicleType() {
        return TYPE;
    }
    
    @Override
    protected Map<String, SpellProperty> initProperties() {
        Map<String, SpellProperty> propMap = super.initProperties();
        propMap.put("range", new SpellProperty("range", "primalmagick.spell.property.range", 1, 5));
        return propMap;
    }
    
    @Override
    public int getBaseManaCostModifier() {
        return this.getPropertyValue("range");
    }
    
    @Override
    protected void drawFx(World world, SpellPackage spell, Vector3d source, Vector3d target) {
        if (spell.getPayload() != null) {
            // Show a bolt particle effect to every player in range
            PacketHandler.sendToAllAround(
                    new SpellBoltPacket(source, target, spell.getPayload().getSource().getColor()), 
                    world.getDimensionKey(), 
                    new BlockPos(source), 
                    64.0D);
        }
    }
}
