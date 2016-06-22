package com.gotofinal.diggler.core.utils.cmds;

import java.util.Iterator;
import java.util.Map;

import com.gotofinal.diggler.core.cfg.DeserializationWorker;
import com.gotofinal.diggler.core.cfg.R;
import com.gotofinal.diggler.core.cfg.SerializationBuilder;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.plugin.Plugin;

import org.apache.commons.lang.Validate;

@SerializableAs("PB_ItemCommand")
public class DelayedCommand implements ConfigurationSerializable
{
    public static final int TPS = 20;
    private final CommandType commandType;
    private final String      command;
    private final int         delay;

    public DelayedCommand(final CommandType commandType, final String command, final int delay)
    {
        this.commandType = commandType;
        this.command = command;
        this.delay = delay;
    }

    public DelayedCommand(final Map<String, Object> map)
    {
        final DeserializationWorker w = DeserializationWorker.start(map);
        this.delay = w.getInt("delay", 0);
        this.commandType = w.getEnum("as", CommandType.CONSOLE);
        this.command = w.getString("cmd");
        Validate.notEmpty(this.command, "Command can't be empty! " + this);
    }

    public void invoke(final Plugin plugin, final CommandSender target, final Iterator<DelayedCommand> next, final Runnable onEnd, final R... reps)
    {
        final Runnable action = () -> {
            this.commandType.invoke(target, this.command, reps);
            if ((next != null) && next.hasNext())
            {
                next.next().invoke(plugin, target, next, onEnd, reps);
            }
            else // that was last element.
            {
                if (onEnd != null)
                {
                    onEnd.run();
                }
            }
        };
        if (this.delay == 0)
        {
            action.run();
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, action, this.delay);
    }

    public static void invoke(final Plugin plugin, final CommandSender target, final Iterable<DelayedCommand> commands, final R... reps)
    {
        invoke(plugin, target, commands, null, reps);
    }

    public static void invoke(final Plugin plugin, final CommandSender target, final Iterable<DelayedCommand> commands, final Runnable onEnd, final R... reps)
    {
        final Iterator<DelayedCommand> it = commands.iterator();
        if (! it.hasNext())
        {
            return;
        }
        it.next().invoke(plugin, target, it, onEnd, reps);
    }

    @Override
    public Map<String, Object> serialize()
    {
        return SerializationBuilder.start(3).append("delay", this.delay).append("as", this.commandType).append("cmd", this.command).build();
    }

    @Override
    public String toString()
    {
        return new org.apache.commons.lang.builder.ToStringBuilder(this, org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("commandType", this.commandType).append("command", this.command).append("delay", this.delay).toString();
    }
}
