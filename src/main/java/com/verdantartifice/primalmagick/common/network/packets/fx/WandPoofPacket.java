package com.verdantartifice.primalmagick.common.network.packets.fx;

import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.verdantartifice.primalmagick.client.fx.FxDispatcher;
import com.verdantartifice.primalmagick.common.network.packets.IMessageToClient;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Packet sent from the server to trigger a wand poof particle effect on the client.
 * 
 * @author Daedalus4096
 */
public class WandPoofPacket implements IMessageToClient {
    protected double x;
    protected double y;
    protected double z;
    protected int color;
    protected boolean sound;
    protected byte face;
    
    public WandPoofPacket() {}
    
    public WandPoofPacket(double x, double y, double z, int color, boolean sound, @Nullable Direction facing) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.sound = sound;
        this.face = facing == null ? (byte)-1 : (byte)facing.getIndex();
    }
    
    public WandPoofPacket(@Nonnull BlockPos pos, int color, boolean sound, @Nullable Direction facing) {
        this(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, color, sound, facing);
    }
    
    public static void encode(WandPoofPacket message, PacketBuffer buf) {
        buf.writeDouble(message.x);
        buf.writeDouble(message.y);
        buf.writeDouble(message.z);
        buf.writeVarInt(message.color);
        buf.writeBoolean(message.sound);
        buf.writeByte(message.face);
    }
    
    public static WandPoofPacket decode(PacketBuffer buf) {
        WandPoofPacket message = new WandPoofPacket();
        message.x = buf.readDouble();
        message.y = buf.readDouble();
        message.z = buf.readDouble();
        message.color = buf.readVarInt();
        message.sound = buf.readBoolean();
        message.face = buf.readByte();
        return message;
    }
    
    public static class Handler {
        public static void onMessage(WandPoofPacket message, Supplier<NetworkEvent.Context> ctx) {
            // Enqueue the handler work on the main game thread
            ctx.get().enqueueWork(() -> {
                Direction side = null;
                if (message.face >= 0) {
                    side = Direction.byIndex(message.face);
                }
                FxDispatcher.INSTANCE.wandPoof(message.x, message.y, message.z, message.color, message.sound, side);
            });
            
            // Mark the packet as handled so we don't get warning log spam
            ctx.get().setPacketHandled(true);
        }
    }
}
