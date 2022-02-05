package com.verdantartifice.primalmagick.common.network.packets.fx;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.verdantartifice.primalmagick.client.fx.FxDispatcher;
import com.verdantartifice.primalmagick.common.network.packets.IMessageToClient;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Packet sent from the server to remove a prop marker particle effect on the client.
 * 
 * @author Daedalus4096
 */
public class RemovePropMarkerPacket implements IMessageToClient {
    protected BlockPos pos;
    
    public RemovePropMarkerPacket() {}
    
    public RemovePropMarkerPacket(@Nonnull BlockPos pos) {
        this.pos = pos;
    }
    
    public static void encode(RemovePropMarkerPacket message, PacketBuffer buf) {
        buf.writeBlockPos(message.pos);
    }
    
    public static RemovePropMarkerPacket decode(PacketBuffer buf) {
        RemovePropMarkerPacket message = new RemovePropMarkerPacket();
        message.pos = buf.readBlockPos();
        return message;
    }
    
    public static class Handler {
        @SuppressWarnings("deprecation")
        public static void onMessage(RemovePropMarkerPacket message, Supplier<NetworkEvent.Context> ctx) {
            // Enqueue the handler work on the main game thread
            ctx.get().enqueueWork(() -> {
            	Minecraft mc = Minecraft.getInstance();
                World world = mc.world;
                // Only process positions that are currently loaded into the world.  Safety check to prevent
                // resource thrashing from falsified packets.
                if (world != null && world.isBlockLoaded(message.pos)) {
                    FxDispatcher.INSTANCE.removePropMarker(message.pos);
                }
            });
            
            // Mark the packet as handled so we don't get warning log spam
            ctx.get().setPacketHandled(true);
        }
    }
}
