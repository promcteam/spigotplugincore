package com.gotofinal.darkrise.spigot.core.commands;

import java.util.regex.Matcher;

import com.gotofinal.darkrise.core.annotation.DarkRiseSubCommand;
import com.gotofinal.darkrise.core.commands.Command;
import com.gotofinal.darkrise.spigot.core.DarkRiseCore;
import com.gotofinal.darkrise.spigot.core.command.Arguments;
import com.gotofinal.darkrise.spigot.core.command.CommandExecutor;

import org.bukkit.command.CommandSender;

@DarkRiseSubCommand(value = DarkRiseCommand.class, name = "reload")
public class DarkRiseReloadCommand implements CommandExecutor
{
    private final DarkRiseCore core;

    public DarkRiseReloadCommand(final DarkRiseCore core)
    {
        this.core = core;
    }

    @Override
    public void runCommand(final CommandSender sender, final Command<CommandSender> command, final String label, final Matcher matchedPattern, final Arguments args)
    {
        if (! this.core.checkPermission(sender, "darkrise.reload"))
        {
            return;
        }
        try
        {
            this.core.reloadConfigs();
            this.core.reloadMessages();
            this.sendMessage("reload", sender);
        }
        catch (Exception e)
        {
            this.sendMessage("reload-error", sender);
            throw new RuntimeException("Invalid configuration!", e);
        }
    }
}
