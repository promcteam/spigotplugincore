package com.gotofinal.darkrise.spigot.core.utils.cmds;

import java.util.Iterator;
import java.util.Map;

import com.gotofinal.darkrise.spigot.core.utils.DeserializationWorker;
import com.gotofinal.darkrise.spigot.core.utils.SerializationBuilder;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.plugin.Plugin;

@SerializableAs("PB_DelayedCommand")
public class DelayedCommand implements ConfigurationSerializable
{
    public static final int TPS = 20;
    private CommandType as;
    private String cmd;
    private int         delay;

    public DelayedCommand()
    {
        this.as = CommandType.CONSOLE;
        this.cmd = "";
        this.delay = 0;
        System.out.println("Created DelayedCommand with empty constructor.");
    }

    public DelayedCommand(final CommandType as, final String cmd, final int delay)
    {
        this.as = as;
        this.cmd = cmd;
        this.delay = delay;
    }

    public DelayedCommand(final Map<String, Object> map)
    {
        final DeserializationWorker w = DeserializationWorker.start(map);
        this.delay = w.getInt("delay", 0);
        this.as = w.getEnum("as", CommandType.CONSOLE);
        this.cmd = w.getString("cmd");
        Validate.notEmpty(this.cmd, "Command can't be empty! " + this);
    }

    public CommandType getAs() {
        return as;
    }

    public String getCmd() {
        return cmd;
    }

    public int getDelay() {
        return delay;
    }

    public void setAs(CommandType as) {
        this.as = as;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void invoke(final Plugin plugin, final CommandSender target, final Iterator<DelayedCommand> next, final Runnable onEnd, final R... reps)
    {
        final Runnable action = () ->
        {
            this.as.invoke(target, this.cmd, reps);
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
        return SerializationBuilder.start(3).append("delay", this.delay).append("as", this.as).append("cmd", this.cmd).build();
    }

    @Override
    public String toString()
    {
        return new org.apache.commons.lang.builder.ToStringBuilder(this, org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("as", this.as).append("cmd", this.cmd).append("delay", this.delay).toString();
    }
}
