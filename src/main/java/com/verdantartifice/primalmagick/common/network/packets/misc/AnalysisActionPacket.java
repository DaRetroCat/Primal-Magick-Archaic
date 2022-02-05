package com.verdantartifice.primalmagick.common.network.packets.misc;

import java.util.function.Supplier;

import com.verdantartifice.primalmagick.common.containers.AnalysisTableContainer;
import com.verdantartifice.primalmagick.common.network.packets.IMessageToServer;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Packet sent to trigger a server-side scan of the slotted item on an analysis table.  Necessary to
 * keep the inventories in sync and properly credit the resulting research.
 * 
 * @author Daedalus4096
 */
public class AnalysisActionPacket implements IMessageToServer {
    protected int windowId;
    
    public AnalysisActionPacket() {
        this.windowId = -1;
    }
    
    public AnalysisActionPacket(int windowId) {
        this.windowId = windowId;
    }
    
    public static void encode(AnalysisActionPacket message, PacketBuffer buf) {
        buf.writeVarInt(message.windowId);
    }
    
    public static AnalysisActionPacket decode(PacketBuffer buf) {
        AnalysisActionPacket message = new AnalysisActionPacket();
        message.windowId = buf.readVarInt();
        return message;
    }
    
    public static class Handler {
        public static void onMessage(AnalysisActionPacket message, Supplier<NetworkEvent.Context> ctx) {
            // Enqueue the handler work on the main game thread
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player.openContainer != null && player.openContainer.windowId == message.windowId && player.openContainer instanceof AnalysisTableContainer) {
                    // Trigger the scan if the open container window matches the given one
                    ((AnalysisTableContainer)player.openContainer).doScan();
                }
            });
            
            // Mark the packet as handled so we don't get warning log spam
            ctx.get().setPacketHandled(true);
        }
    }
}
