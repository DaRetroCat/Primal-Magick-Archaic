package com.verdantartifice.primalmagick.common.network.packets.misc;

import java.util.function.Supplier;

import com.verdantartifice.primalmagick.common.network.packets.IMessageToServer;
import com.verdantartifice.primalmagick.common.spells.SpellManager;
import com.verdantartifice.primalmagick.common.wands.IWand;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Packet sent to trigger an update of an equipped wand's NBT for spell selection on the server.
 * 
 * @author Daedalus4096
 */
public class CycleActiveSpellPacket implements IMessageToServer {
    protected boolean reverse = false;
    
    public CycleActiveSpellPacket() {}
    
    public CycleActiveSpellPacket(boolean reverse) {
        this.reverse = reverse;
    }
    
    public static void encode(CycleActiveSpellPacket message, PacketBuffer buf) {
        buf.writeBoolean(message.reverse);
    }
    
    public static CycleActiveSpellPacket decode(PacketBuffer buf) {
        CycleActiveSpellPacket message = new CycleActiveSpellPacket();
        message.reverse = buf.readBoolean();
        return message;
    }
    
    public static class Handler {
        public static void onMessage(CycleActiveSpellPacket message, Supplier<NetworkEvent.Context> ctx) {
            // Enqueue the handler work on the main game thread
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player != null) {
                    // Mainhand takes priority over the offhand in case two wands are equipped
                    if (player.getHeldItemMainhand().getItem() instanceof IWand) {
                        SpellManager.cycleActiveSpell(player, player.getHeldItemMainhand(), message.reverse);
                    } else if (player.getHeldItemOffhand().getItem() instanceof IWand) {
                        SpellManager.cycleActiveSpell(player, player.getHeldItemOffhand(), message.reverse);
                    }
                }
            });
            
            // Mark the packet as handled so we don't get warning log spam
            ctx.get().setPacketHandled(true);
        }
    }
}
