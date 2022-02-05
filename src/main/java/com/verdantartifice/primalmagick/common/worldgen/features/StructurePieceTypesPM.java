package com.verdantartifice.primalmagick.common.worldgen.features;

import com.verdantartifice.primalmagick.PrimalMagick;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;

/**
 * Holder for mod structure piece types.
 * 
 * @author Daedalus4096
 */
public class StructurePieceTypesPM {
    public static final IStructurePieceType SHRINE = register(ShrinePiece::new, new ResourceLocation(PrimalMagick.MODID, "shrine"));
    
    private static IStructurePieceType register(IStructurePieceType spt, ResourceLocation key) {
        return Registry.register(Registry.STRUCTURE_PIECE, key, spt);
    }
}
