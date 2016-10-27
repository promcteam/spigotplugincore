package com.gotofinal.diggler.core.nms.v1_10_R1;

import com.gotofinal.diggler.core.nms.ChatPosition;
import com.gotofinal.diggler.core.nms.INMSPlayerUtils;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;

public class NMSPlayerUtils implements INMSPlayerUtils
{
    @Override
    public void sendMessage(BaseComponent[] msg, ChatPosition chatPosition, Player player)
    {
        CraftPlayer p = (CraftPlayer) player;
        PacketPlayOutChat packet = new PacketPlayOutChat(null, (byte) chatPosition.ordinal());
        packet.components = msg;
        p.getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public HoverEvent convert(ItemStack itemStack)
    {
        return new HoverEvent(Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(CraftItemStack.asNMSCopy(itemStack).save(new NBTTagCompound()).toString())});
    }
}
