package com.gotofinal.darkrise.spigot.core.commands;

import java.util.Collections;
import java.util.regex.Matcher;

import com.gotofinal.darkrise.core.annotation.InvokeOn;
import com.gotofinal.darkrise.core.annotation.SubCommandAnnotation;
import com.gotofinal.darkrise.core.commands.Command;
import com.gotofinal.darkrise.spigot.core.DarkRiseCore;
import com.gotofinal.darkrise.spigot.core.command.Arguments;
import com.gotofinal.darkrise.spigot.core.command.CommandExecutor;
import com.gotofinal.darkrise.spigot.core.command.PluginCommandImpl;

import org.bukkit.command.CommandSender;

public class DarkRiseCommand extends PluginCommandImpl implements CommandExecutor
{
    private DarkRiseCore plugin;

    public DarkRiseCommand(DarkRiseCore plugin)
    {
        super("darkrise", Collections.singletonList("darkrise"), plugin);
        this.setUsage("core.commands.help");
        this.setCommandExecutor(this);
        SubCommandAnnotation.register(this);
    }

    @InvokeOn
    private static void init(DarkRiseCore plugin)
    {
        plugin.getCommandMap().registerCommand(new DarkRiseCommand(plugin));
    }

    @Override
    public void runCommand(final CommandSender sender, final Command<CommandSender> command, final String label, final Matcher matchedPattern, final Arguments args)
    {
        this.sendUsage(this.getUsage(), sender, command, args);
    }
}