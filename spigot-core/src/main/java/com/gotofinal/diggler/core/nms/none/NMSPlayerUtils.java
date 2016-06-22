package com.gotofinal.diggler.core.nms.none;

import java.util.concurrent.TimeUnit;

import com.gotofinal.diggler.core.nms.ChatPosition;
import com.gotofinal.diggler.core.nms.INMSPlayerUtils;
import com.gotofinal.diggler.core.utils.SpammyError;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;

public class NMSPlayerUtils implements INMSPlayerUtils
{
    @Override
    public void sendMessage(final BaseComponent[] msg, final ChatPosition chatPosition, final Player player)
    {
        SpammyError.err("No implementation for: NMSPlayerUtils#sendMessage(BaseComponent, ChatPosition, Player), plugin may need update!", (int) TimeUnit.MINUTES.toSeconds(30), "NMSPlayerUtils#sendMessage(BaseComponent, ChatPosition, Player)");
    }
}
