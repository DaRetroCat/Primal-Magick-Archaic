package com.verdantartifice.primalmagick.common.misc;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

/**
 * Definition of mod-specific damage source types.
 * 
 * @author Daedalus4096
 */
public class DamageSourcesPM {
    public static final String HELLISH_CHAIN_TYPE = "primalmagick.hellish_chain";
    
    public static final DamageSource BLEEDING = (new DamageSource("primalmagick.bleeding")).setDamageBypassesArmor();
    
    public static DamageSource causeHellishChainDamage(LivingEntity mob) {
        return (new EntityDamageSource(HELLISH_CHAIN_TYPE, mob)).setFireDamage();
    }
}
