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
 * Packet sent from the server to trigger a prop marker particle effect on the client.
 * 
 * @author Daedalus4096
 */
public class PropMarkerPacket implements IMessageToClient {
    protected BlockPos pos;
    
    public PropMarkerPacket() {}
    
    public PropMarkerPacket(@Nonnull BlockPos pos) {
        this.pos = pos;
    }
    
    public static void encode(PropMarkerPacket message, PacketBuffer buf) {
        buf.writeBlockPos(message.pos);
    }
    
    public static PropMarkerPacket decode(PacketBuffer buf) {
        PropMarkerPacket message = new PropMarkerPacket();
        message.pos = buf.readBlockPos();
        return message;
    }
    
    public static class Handler {
        @SuppressWarnings("deprecation")
        public static void onMessage(PropMarkerPacket message, Supplier<NetworkEvent.Context> ctx) {
            // Enqueue the handler work on the main game thread
            ctx.get().enqueueWork(() -> {
            	Minecraft mc = Minecraft.getInstance();
                World world = mc.world;
                // Only process positions that are currently loaded into the world.  Safety check to prevent
                // resource thrashing from falsified packets.
                if (world != null && world.isBlockLoaded(message.pos)) {
                    FxDispatcher.INSTANCE.propMarker(message.pos);
                }
            });
            
            // Mark the packet as handled so we don't get warning log spam
            ctx.get().setPacketHandled(true);
        }
    }
}
