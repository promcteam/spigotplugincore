package com.gotofinal.darkrise.spigot.core.command.listener;

import com.gotofinal.darkrise.spigot.core.DarkRiseCore;
import com.gotofinal.darkrise.spigot.core.utils.CommandMapUtils;
import com.gotofinal.darkrise.core.annotation.EventListener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

@EventListener(DarkRiseCore.class)
public class CommandListener implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(ServerCommandEvent event)
    {
        onCommand(event, event.getCommand(), event.getSender());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event)
    {
        onCommand(event, event.getMessage(), event.getPlayer());
    }

    public static void onCommand(Event event, String str, CommandSender sender)
    {
        if (str.startsWith("/"))
        {
            str = str.substring(1);
        }
        int spaceIndex = str.indexOf(' ');
        final String commandName;
        if (spaceIndex == - 1)
        {
            commandName = str;
        }
        else
        {
            commandName = str.substring(0, spaceIndex);
        }
        Command bukkitCommand = CommandMapUtils.getBukkitCommand(commandName);
        if (bukkitCommand != null)
        {
            return;
        }

        if (DarkRiseCore.getInstance().getCommandMap().dispatch(sender, str) && (event instanceof Cancellable))
        {
            ((Cancellable) event).setCancelled(true);
        }
    }
}
