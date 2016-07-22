package com.gotofinal.diggler.core.nms;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;

public interface INMSPlayerUtils
{
    void sendMessage(BaseComponent[] msg, ChatPosition chatPosition, Player player);

    HoverEvent convert(ItemStack itemStack);
}
