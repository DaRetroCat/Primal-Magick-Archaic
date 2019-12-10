package com.verdantartifice.primalmagic.common.network.packets.spellcrafting;

import java.util.function.Supplier;

import com.verdantartifice.primalmagic.PrimalMagic;
import com.verdantartifice.primalmagic.common.containers.SpellcraftingAltarContainer;
import com.verdantartifice.primalmagic.common.network.packets.IMessageToServer;
import com.verdantartifice.primalmagic.common.spells.SpellComponent;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetSpellComponentTypeIndexPacket implements IMessageToServer {
    protected int windowId;
    protected SpellComponent attr;
    protected int index;

    public SetSpellComponentTypeIndexPacket() {
        this.windowId = -1;
        this.attr = null;
        this.index = -1;
    }
    
    public SetSpellComponentTypeIndexPacket(int windowId, SpellComponent attr, int index) {
        this.windowId = windowId;
        this.attr = attr;
        this.index = index;
    }
    
    public static void encode(SetSpellComponentTypeIndexPacket message, PacketBuffer buf) {
        buf.writeInt(message.windowId);
        buf.writeString(message.attr.name());
        buf.writeInt(message.index);
    }
    
    public static SetSpellComponentTypeIndexPacket decode(PacketBuffer buf) {
        SetSpellComponentTypeIndexPacket message = new SetSpellComponentTypeIndexPacket();
        message.windowId = buf.readInt();
        String attrStr = buf.readString();
        try {
            message.attr = SpellComponent.valueOf(attrStr);
        } catch (Exception e) {
            PrimalMagic.LOGGER.warn("Received SetSpellAttributeTypeIndexPacket with unexpected attr value {}", attrStr);
            message.attr = null;
        }
        message.index = buf.readInt();
        return message;
    }
    
    public static class Handler {
        public static void onMessage(SetSpellComponentTypeIndexPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player.openContainer != null && player.openContainer.windowId == message.windowId && player.openContainer instanceof SpellcraftingAltarContainer) {
                    SpellcraftingAltarContainer container = (SpellcraftingAltarContainer)player.openContainer;
                    switch (message.attr) {
                    case PACKAGE:
                        container.setSpellPackageTypeIndex(message.index);
                        break;
                    case PAYLOAD:
                        container.setSpellPayloadTypeIndex(message.index);
                        break;
                    case PRIMARY_MOD:
                        container.setSpellPrimaryModTypeIndex(message.index);
                        break;
                    case SECONDARY_MOD:
                        container.setSpellSecondaryModTypeIndex(message.index);
                        break;
                    default:
                        // Do nothing
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}