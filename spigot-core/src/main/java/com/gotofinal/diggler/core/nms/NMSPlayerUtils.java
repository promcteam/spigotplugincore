package com.gotofinal.diggler.core.nms;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class NMSPlayerUtils implements INMSWrapper
{
    private static INMSPlayerUtils inst;

    public static INMSPlayerUtils getInst()
    {
        return inst;
    }

    public static void setInst(final INMSPlayerUtils inst)
    {
        NMSPlayerUtils.inst = inst;
    }

    public static void sendMessage(final BaseComponent[] msg, final ChatPosition chatPosition, final Player player)
    {
        inst.sendMessage(msg, chatPosition, player);
    }

    public static void sendMessage(final BaseComponent msg, final ChatPosition chatPosition, final Player player)
    {
        inst.sendMessage(new BaseComponent[]{msg}, chatPosition, player);
    }

    public static void sendMessage(final String msg, final ChatPosition chatPosition, final Player player)
    {
        inst.sendMessage(new BaseComponent[]{new TextComponent(msg)}, chatPosition, player);
    }
}
