package com.verdantartifice.primalmagick.common.network;

import com.verdantartifice.primalmagick.PrimalMagick;
import com.verdantartifice.primalmagick.common.network.packets.IMessageToClient;
import com.verdantartifice.primalmagick.common.network.packets.IMessageToServer;
import com.verdantartifice.primalmagick.common.network.packets.data.SyncAttunementsPacket;
import com.verdantartifice.primalmagick.common.network.packets.data.SyncCompanionsPacket;
import com.verdantartifice.primalmagick.common.network.packets.data.SyncCooldownsPacket;
import com.verdantartifice.primalmagick.common.network.packets.data.SyncKnowledgePacket;
import com.verdantartifice.primalmagick.common.network.packets.data.SyncProgressPacket;
import com.verdantartifice.primalmagick.common.network.packets.data.SyncResearchFlagsPacket;
import com.verdantartifice.primalmagick.common.network.packets.data.SyncStatsPacket;
import com.verdantartifice.primalmagick.common.network.packets.data.TileToClientPacket;
import com.verdantartifice.primalmagick.common.network.packets.data.TileToServerPacket;
import com.verdantartifice.primalmagick.common.network.packets.fx.ManaSparklePacket;
import com.verdantartifice.primalmagick.common.network.packets.fx.OfferingChannelPacket;
import com.verdantartifice.primalmagick.common.network.packets.fx.PlayClientSoundPacket;
import com.verdantartifice.primalmagick.common.network.packets.fx.PotionExplosionPacket;
import com.verdantartifice.primalmagick.common.network.packets.fx.PropMarkerPacket;
import com.verdantartifice.primalmagick.common.network.packets.fx.RemovePropMarkerPacket;
import com.verdantartifice.primalmagick.common.network.packets.fx.SpellBoltPacket;
import com.verdantartifice.primalmagick.common.network.packets.fx.SpellImpactPacket;
import com.verdantartifice.primalmagick.common.network.packets.fx.SpellTrailPacket;
import com.verdantartifice.primalmagick.common.network.packets.fx.TeleportArrivalPacket;
import com.verdantartifice.primalmagick.common.network.packets.fx.WandPoofPacket;
import com.verdantartifice.primalmagick.common.network.packets.misc.AnalysisActionPacket;
import com.verdantartifice.primalmagick.common.network.packets.misc.CycleActiveSpellPacket;
import com.verdantartifice.primalmagick.common.network.packets.misc.ResetFallDistancePacket;
import com.verdantartifice.primalmagick.common.network.packets.misc.ScanEntityPacket;
import com.verdantartifice.primalmagick.common.network.packets.misc.ScanItemPacket;
import com.verdantartifice.primalmagick.common.network.packets.misc.ScanPositionPacket;
import com.verdantartifice.primalmagick.common.network.packets.spellcrafting.SetSpellComponentPropertyPacket;
import com.verdantartifice.primalmagick.common.network.packets.spellcrafting.SetSpellComponentTypeIndexPacket;
import com.verdantartifice.primalmagick.common.network.packets.spellcrafting.SetSpellNamePacket;
import com.verdantartifice.primalmagick.common.network.packets.theorycrafting.CompleteProjectPacket;
import com.verdantartifice.primalmagick.common.network.packets.theorycrafting.SetProjectMaterialSelectionPacket;
import com.verdantartifice.primalmagick.common.network.packets.theorycrafting.StartProjectPacket;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * Handler class for processing packets.  Responsible for all custom communication between the client and the server.
 * 
 * @author Daedalus4096
 */
public class PacketHandler {
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    
    private static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(PrimalMagick.MODID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();
    
    public static void registerMessages() {
        int disc = 0;
        
        INSTANCE.registerMessage(disc++, SyncKnowledgePacket.class, SyncKnowledgePacket::encode, SyncKnowledgePacket::decode, SyncKnowledgePacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, SyncProgressPacket.class, SyncProgressPacket::encode, SyncProgressPacket::decode, SyncProgressPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, SyncResearchFlagsPacket.class, SyncResearchFlagsPacket::encode, SyncResearchFlagsPacket::decode, SyncResearchFlagsPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, WandPoofPacket.class, WandPoofPacket::encode, WandPoofPacket::decode, WandPoofPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, ManaSparklePacket.class, ManaSparklePacket::encode, ManaSparklePacket::decode, ManaSparklePacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, ScanItemPacket.class, ScanItemPacket::encode, ScanItemPacket::decode, ScanItemPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, ScanPositionPacket.class, ScanPositionPacket::encode, ScanPositionPacket::decode, ScanPositionPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, AnalysisActionPacket.class, AnalysisActionPacket::encode, AnalysisActionPacket::decode, AnalysisActionPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, SyncCooldownsPacket.class, SyncCooldownsPacket::encode, SyncCooldownsPacket::decode, SyncCooldownsPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, CycleActiveSpellPacket.class, CycleActiveSpellPacket::encode, CycleActiveSpellPacket::decode, CycleActiveSpellPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, SetSpellNamePacket.class, SetSpellNamePacket::encode, SetSpellNamePacket::decode, SetSpellNamePacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, SetSpellComponentTypeIndexPacket.class, SetSpellComponentTypeIndexPacket::encode, SetSpellComponentTypeIndexPacket::decode, SetSpellComponentTypeIndexPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, SetSpellComponentPropertyPacket.class, SetSpellComponentPropertyPacket::encode, SetSpellComponentPropertyPacket::decode, SetSpellComponentPropertyPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, SpellTrailPacket.class, SpellTrailPacket::encode, SpellTrailPacket::decode, SpellTrailPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, SpellImpactPacket.class, SpellImpactPacket::encode, SpellImpactPacket::decode, SpellImpactPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, TileToClientPacket.class, TileToClientPacket::encode, TileToClientPacket::decode, TileToClientPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, TileToServerPacket.class, TileToServerPacket::encode, TileToServerPacket::decode, TileToServerPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, TeleportArrivalPacket.class, TeleportArrivalPacket::encode, TeleportArrivalPacket::decode, TeleportArrivalPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, SpellBoltPacket.class, SpellBoltPacket::encode, SpellBoltPacket::decode, SpellBoltPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, SyncStatsPacket.class, SyncStatsPacket::encode, SyncStatsPacket::decode, SyncStatsPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, SyncAttunementsPacket.class, SyncAttunementsPacket::encode, SyncAttunementsPacket::decode, SyncAttunementsPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, ResetFallDistancePacket.class, ResetFallDistancePacket::encode, ResetFallDistancePacket::decode, ResetFallDistancePacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, StartProjectPacket.class, StartProjectPacket::encode, StartProjectPacket::decode, StartProjectPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, CompleteProjectPacket.class, CompleteProjectPacket::encode, CompleteProjectPacket::decode, CompleteProjectPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, SetProjectMaterialSelectionPacket.class, SetProjectMaterialSelectionPacket::encode, SetProjectMaterialSelectionPacket::decode, SetProjectMaterialSelectionPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, PlayClientSoundPacket.class, PlayClientSoundPacket::encode, PlayClientSoundPacket::decode, PlayClientSoundPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, OfferingChannelPacket.class, OfferingChannelPacket::encode, OfferingChannelPacket::decode, OfferingChannelPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, PropMarkerPacket.class, PropMarkerPacket::encode, PropMarkerPacket::decode, PropMarkerPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, RemovePropMarkerPacket.class, RemovePropMarkerPacket::encode, RemovePropMarkerPacket::decode, RemovePropMarkerPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, SyncCompanionsPacket.class, SyncCompanionsPacket::encode, SyncCompanionsPacket::decode, SyncCompanionsPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, ScanEntityPacket.class, ScanEntityPacket::encode, ScanEntityPacket::decode, ScanEntityPacket.Handler::onMessage);
        INSTANCE.registerMessage(disc++, PotionExplosionPacket.class, PotionExplosionPacket::encode, PotionExplosionPacket::decode, PotionExplosionPacket.Handler::onMessage);
    }
    
    public static void sendToServer(IMessageToServer message) {
        // Send a packet from a client to the server
        INSTANCE.sendToServer(message);
    }
    
    public static void sendToPlayer(IMessageToClient message, ServerPlayerEntity player) {
        // Send a message from the server to a specific player's client
        INSTANCE.sendTo(message, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    }
    
    public static void sendToAllAround(IMessageToClient message, RegistryKey<World> dimension, BlockPos center, double radius) {
        // Send a message to the clients of all players within a given distance of the given world position
        INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(center.getX() + 0.5D, center.getY() + 0.5D, center.getZ() + 0.5D, radius, dimension)), message);
    }
}
