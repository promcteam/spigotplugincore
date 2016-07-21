package com.gotofinal.darkrise.spigot.core.utils;

import java.util.List;

import com.gotofinal.darkrise.spigot.core.DarkRiseCore;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

import org.diorite.utils.reflections.DioriteReflectionUtils;
import org.diorite.utils.reflections.FieldAccessor;

public final class CommandMapUtils
{
    private static SimpleCommandMap map;

    private CommandMapUtils()
    {
    }

    public static Command registerAsBukkitCommand(String prefix, com.gotofinal.darkrise.core.commands.Command<CommandSender> riseCommand)
    {
        String description = riseCommand.getDescription();
        if (description == null)
        {
            description = "";
        }
        String usage = riseCommand.getUsage();
        if (usage == null)
        {
            usage = "";
        }
        Command command = new Command(riseCommand.getName(), description, usage, riseCommand.getKnownAliases())
        {
            @Override
            public boolean execute(final CommandSender commandSender, final String s, final String[] strings)
            {
                DarkRiseCore.getInstance().getCommandMap().dispatch(commandSender, s + " " + StringUtils.join(strings, ' '));
                return true;
            }

            @Override
            public List<String> tabComplete(final CommandSender sender, final String alias, final String[] args) throws IllegalArgumentException
            {
                return DarkRiseCore.getInstance().getCommandMap().tabComplete(sender, alias + " " + StringUtils.join(args, ' '));
            }
        };
        getBukkitCommandMap().register(prefix, command);
        return command;
    }

    public static Command getBukkitCommand(String alias)
    {
        Command command = getBukkitCommandMap().getCommand(alias);
        if (command != null)
        {
            return command;
        }
//        for (final Command cmd : getBukkitCommandMap().getCommands())
//        {
//            if (cmd.getName().equalsIgnoreCase(alias) || cmd.getAliases().stream().anyMatch(s -> s.equalsIgnoreCase(alias)))
//            {
//                return cmd;
//            }
//        }
        return null;
    }

    public static SimpleCommandMap getBukkitCommandMap()
    {
        if (map != null)
        {
            return map;
        }
        FieldAccessor<SimpleCommandMap> field = DioriteReflectionUtils.getField(Bukkit.getServer().getClass(), SimpleCommandMap.class, 0);
        map = field.get(Bukkit.getServer());
        return map;
    }
}
