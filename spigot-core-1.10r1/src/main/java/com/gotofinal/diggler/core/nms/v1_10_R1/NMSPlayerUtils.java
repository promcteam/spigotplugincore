package com.gotofinal.diggler.core.nms.v1_10_R1;

import com.gotofinal.diggler.core.nms.ChatPosition;
import com.gotofinal.diggler.core.nms.INMSPlayerUtils;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;

public class NMSPlayerUtils implements INMSPlayerUtils
{

    @Override
    public void sendMessage(final BaseComponent[] msg, final ChatPosition chatPosition, final Player player)
    {
        final CraftPlayer p = (CraftPlayer) player;
        final PacketPlayOutChat packet = new PacketPlayOutChat(null, (byte) chatPosition.ordinal());
        packet.components = msg;
        p.getHandle().playerConnection.sendPacket(packet);
    }
}
