package com.gotofinal.darkrise.spigot.core.listeners;

import java.util.Collection;

import com.gotofinal.darkrise.core.annotation.EventListener;
import com.gotofinal.darkrise.spigot.core.DarkRiseCore;
import com.gotofinal.darkrise.spigot.core.config.CommandBlock;
import com.gotofinal.darkrise.spigot.core.utils.cmds.DelayedCommand;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

@EventListener(DarkRiseCore.class)
public class PlayerListener implements Listener
{
    private final DarkRiseCore core;

    public PlayerListener(final DarkRiseCore core)
    {
        this.core = core;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e)
    {
        Block block = e.getClickedBlock();
        if (block == null)
        {
            return;
        }
        Collection<CommandBlock> onInteract = this.core.getCfg().getOnInteract(block.getType(), block.getData());
        for (final CommandBlock commandBlock : onInteract)
        {
            commandBlock.invoke(e);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoinLowest(PlayerJoinEvent e)
    {
        this.setHealthScale(e);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoinHighest(PlayerJoinEvent e)
    {
        DelayedCommand.invoke(this.core, e.getPlayer(), this.core.getCfg().getOnJoin());

        if(!e.getPlayer().hasPlayedBefore()) {
            DelayedCommand.invoke(this.core, e.getPlayer(), this.core.getCfg().getOnFirstJoin());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e)
    {
        this.setHealthScale(e);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e)
    {
        this.setHealthScale(e);
    }

    private void setHealthScale(PlayerEvent e)
    {
        setHealthScale(e.getPlayer(), this.core.getCfg().getScaleHealth());
    }

    public static void setHealthScale(Player player, double scale)
    {
        if (scale <= 0)
        {
            player.setHealthScaled(false);
            return;
        }
        player.setHealthScaled(true);
        player.setHealthScale(scale);
    }
}
