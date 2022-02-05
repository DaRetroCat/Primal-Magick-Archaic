package com.verdantartifice.primalmagick.common.network.packets.spellcrafting;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.verdantartifice.primalmagick.common.containers.SpellcraftingAltarContainer;
import com.verdantartifice.primalmagick.common.network.packets.IMessageToServer;
import com.verdantartifice.primalmagick.common.spells.SpellComponent;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Packet sent to update the value of a spell component's property on the server in the spellcrafting altar GUI.
 * 
 * @author Daedalus4096
 */
public class SetSpellComponentPropertyPacket implements IMessageToServer {
    private static final Logger LOGGER = LogManager.getLogger();

    protected int windowId;
    protected SpellComponent attr;
    protected String name;
    protected int value;

    public SetSpellComponentPropertyPacket() {
        this.windowId = -1;
        this.attr = null;
        this.name = "";
        this.value = -1;
    }
    
    public SetSpellComponentPropertyPacket(int windowId, SpellComponent attr, String name, int value) {
        this.windowId = windowId;
        this.attr = attr;
        this.name = name;
        this.value = value;
    }

    public static void encode(SetSpellComponentPropertyPacket message, PacketBuffer buf) {
        buf.writeInt(message.windowId);
        buf.writeString(message.attr.name());
        buf.writeString(message.name);
        buf.writeInt(message.value);
    }
    
    public static SetSpellComponentPropertyPacket decode(PacketBuffer buf) {
        SetSpellComponentPropertyPacket message = new SetSpellComponentPropertyPacket();
        message.windowId = buf.readInt();
        String attrStr = buf.readString();
        try {
            message.attr = SpellComponent.valueOf(attrStr);
        } catch (Exception e) {
            LOGGER.warn("Received SetSpellComponentPropertyPacket with unexpected attr value {}", attrStr);
            message.attr = null;
        }
        message.name = buf.readString();
        message.value = buf.readInt();
        return message;
    }
    
    public static class Handler {
        public static void onMessage(SetSpellComponentPropertyPacket message, Supplier<NetworkEvent.Context> ctx) {
            // Enqueue the handler work on the main game thread
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player.openContainer != null && player.openContainer.windowId == message.windowId && player.openContainer instanceof SpellcraftingAltarContainer) {
                    // Update the property value if the open container window matches the given one
                    SpellcraftingAltarContainer container = (SpellcraftingAltarContainer)player.openContainer;
                    container.setSpellPropertyValue(message.attr, message.name, message.value);
                }
            });
            
            // Mark the packet as handled so we don't get warning log spam
            ctx.get().setPacketHandled(true);
        }
    }
}
