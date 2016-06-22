package com.gotofinal.diggler.core.nms.v1_9_R2;

import net.minecraft.server.v1_9_R2.PacketPlayOutChat;

import com.gotofinal.diggler.core.nms.ChatPosition;
import com.gotofinal.diggler.core.nms.INMSPlayerUtils;

import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;

public class NMSPlayerUtils implements INMSPlayerUtils
{
    @Override
    public void sendMessage(final BaseComponent[] msg, final ChatPosition chatPosition, final Player player)
    {
        final CraftPlayer p = (CraftPlayer) player;
        final PacketPlayOutChat packet = new PacketPlayOutChat(null, (byte)chatPosition.ordinal());
        packet.components = msg;
        p.getHandle().playerConnection.sendPacket(packet);
    }
}
