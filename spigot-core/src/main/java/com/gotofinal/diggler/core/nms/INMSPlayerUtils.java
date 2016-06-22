package com.gotofinal.diggler.core.nms;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;

public interface INMSPlayerUtils
{
    void sendMessage(BaseComponent[] msg, ChatPosition chatPosition, Player player);

}
