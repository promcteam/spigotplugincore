package com.gotofinal.darkrise.spigot.core.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.gotofinal.darkrise.spigot.core.DarkRiseCore;
import com.gotofinal.darkrise.spigot.core.utils.cmds.CommandType;
import com.gotofinal.darkrise.spigot.core.utils.cmds.DelayedCommand;
import com.gotofinal.darkrise.spigot.core.utils.cmds.R;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerEvent;

import org.diorite.cfg.annotations.defaults.CfgIntDefault;

public class CommandBlock
{
    private Material material;
    @CfgIntDefault(- 1)
    private int type = - 1;
    private String permission;
    private boolean              cancelAction    = true;
    private List<DelayedCommand> delayedCommands = Collections.singletonList(new DelayedCommand(CommandType.CONSOLE, "say {player}", 0));

    public CommandBlock()
    {
    }

    public CommandBlock(final Material material, final int type, final String permission, final boolean cancelAction, final List<DelayedCommand> delayedCommands)
    {
        this.material = material;
        this.type = type;
        this.permission = permission;
        this.cancelAction = cancelAction;
        this.delayedCommands = delayedCommands;
    }

    public CommandBlock(final Material material, final int type, final String permission, final boolean cancelAction, DelayedCommand... delayedCommands)
    {
        this.material = material;
        this.type = type;
        this.permission = permission;
        this.cancelAction = cancelAction;
        this.delayedCommands = new ArrayList<>(Arrays.asList(delayedCommands));
    }

    public void invoke(PlayerEvent event, R... reps)
    {
        Player player = event.getPlayer();
        if ((this.delayedCommands == null) || this.delayedCommands.isEmpty())
        {
            return;
        }
        if (player.hasPermission("core.oninteract.bypass"))
        {
            return;
        }
        if ((this.permission != null) && ! player.hasPermission(this.permission))
        {
            return;
        }
        if (this.cancelAction && (event instanceof Cancellable))
        {
            ((Cancellable) event).setCancelled(true);
        }
        DelayedCommand.invoke(DarkRiseCore.getInstance(), player, this.delayedCommands, reps);
    }

    public Material getMaterial()
    {
        return this.material;
    }

    public void setMaterial(final Material material)
    {
        this.material = material;
    }

    public boolean isCancelAction()
    {
        return this.cancelAction;
    }

    public void setCancelAction(final boolean cancelAction)
    {
        this.cancelAction = cancelAction;
    }

    public String getPermission()
    {
        return this.permission;
    }

    public void setPermission(final String permission)
    {
        this.permission = permission;
    }

    public int getType()
    {
        return this.type;
    }

    public void setType(final int type)
    {
        this.type = type;
    }

    public List<DelayedCommand> getDelayedCommands()
    {
        return this.delayedCommands;
    }

    public void setDelayedCommands(final List<DelayedCommand> delayedCommands)
    {
        this.delayedCommands = delayedCommands;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (! (o instanceof CommandBlock))
        {
            return false;
        }

        final CommandBlock that = (CommandBlock) o;

        return new EqualsBuilder().append(this.type, that.type).append(this.material, that.material).append(this.delayedCommands, that.delayedCommands).isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37).append(this.material).append(this.type).append(this.delayedCommands).toHashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("material", this.material).append("type", this.type).append("delayedCommands", this.delayedCommands).toString();
    }
}
