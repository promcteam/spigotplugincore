/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016. Diorite (by Bartłomiej Mazur (aka GotoFinal))
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.gotofinal.darkrise.spigot.core.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import com.gotofinal.darkrise.spigot.core.utils.CommandMapUtils;
import com.gotofinal.darkrise.spigot.core.utils.PlayerUtils;
import com.gotofinal.darkrise.core.DarkRisePlugin;
import com.gotofinal.darkrise.core.commands.Command;
import com.gotofinal.darkrise.core.commands.CommandMap;
import com.gotofinal.darkrise.core.commands.MainCommand;
import com.gotofinal.darkrise.core.commands.PluginCommand;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.diorite.utils.DioriteStringUtils;
import org.diorite.utils.collections.maps.CaseInsensitiveMap;

public class CommandMapImpl implements CommandMap<CommandSender>
{
    private final Map<String, MainCommand<CommandSender>> commandMap = new CaseInsensitiveMap<>(50, .20f);

    private Iterable<MainCommand<CommandSender>> getSortedCommandList()
    {
        return this.commandMap.values().stream().sorted((c1, c2) -> Integer.compare(c2.getPriority(), c1.getPriority())).collect(Collectors.toList());
    }

    private synchronized void addCommandToMap(String prefix, MainCommand<CommandSender> command)
    {
        this.commandMap.put(prefix + Command.COMMAND_PLUGIN_SEPARATOR + command.getName(), command);
        CommandMapUtils.registerAsBukkitCommand(prefix, command);
    }

    @Override
    public synchronized void registerCommand(final PluginCommand<CommandSender> pluginCommand)
    {
        this.addCommandToMap(pluginCommand.getPlugin().getName(), pluginCommand);
    }

    @Override
    public Set<MainCommand<CommandSender>> getCommandsFromPlugin(final DarkRisePlugin dioritePlugin)
    {
        return this.commandMap.values().parallelStream().filter(cmd -> (cmd instanceof PluginCommand) && ((PluginCommand<CommandSender>) cmd).getPlugin().equals(dioritePlugin)).collect(Collectors.toSet());
    }

    @Override
    public Set<MainCommand<CommandSender>> getCommandsFromPlugin(final String plugin)
    {
        return this.commandMap.values().parallelStream().filter(cmd -> (cmd instanceof PluginCommand) && ((PluginCommand<CommandSender>) cmd).getPlugin().getName().equalsIgnoreCase(plugin)).collect(Collectors.toSet());
    }

    @Override
    public Set<MainCommand<CommandSender>> getCommands(final String str)
    {
        return this.commandMap.values().parallelStream().filter(cmd -> cmd.getName().equalsIgnoreCase(str)).collect(Collectors.toSet());
    }

    @Override
    public Optional<MainCommand<CommandSender>> getCommand(final DarkRisePlugin dioritePlugin, final String str)
    {
        MainCommand<CommandSender> cmd = this.commandMap.get(dioritePlugin + Command.COMMAND_PLUGIN_SEPARATOR + str);
        if (cmd == null)
        {
            cmd = this.commandMap.get(str);
        }
        return Optional.ofNullable(cmd);
    }

    @Override
    public Optional<MainCommand<CommandSender>> getCommand(final String str)
    {
        final MainCommand<CommandSender> cmd = this.commandMap.get(str);
        if (cmd != null)
        {
            return Optional.of(cmd);
        }
        for (final Map.Entry<String, MainCommand<CommandSender>> entry : this.commandMap.entrySet())
        {
            if (entry.getKey().endsWith(Command.COMMAND_PLUGIN_SEPARATOR + str))
            {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public Map<String, MainCommand<CommandSender>> getCommandMap()
    {
        return new HashMap<>(this.commandMap);
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String cmdLine)
    {
        final String[] args;
        if ((cmdLine == null) || cmdLine.isEmpty() || ((args = DioriteStringUtils.splitArguments(cmdLine)).length == 0))
        {
            return this.commandMap.keySet().parallelStream().map(s -> Command.COMMAND_PREFIX + s).collect(Collectors.toList());
        }
        final String command = args[0];
        final String[] newArgs;
        if (args.length == 1)
        {
            newArgs = Command.EMPTY_ARGS;
        }
        else
        {
            newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        }
        for (final MainCommand<CommandSender> cmd : this.getSortedCommandList())
        {
            final Matcher matcher = cmd.matcher(command);
            if (matcher.matches())
            {
                final List<String> result = cmd.tabComplete(sender, command, matcher, newArgs);
                if (result.isEmpty())
                {
                    return PlayerUtils.getOnlinePlayersNames(args[args.length - 1]);
                }
                else
                {
                    return result;
                }
            }
        }
        if (args.length > 1)
        {
            return PlayerUtils.getOnlinePlayersNames(args[args.length - 1]);
        }
        if (cmdLine.endsWith(" "))
        {
            return PlayerUtils.getOnlinePlayersNames();
        }
        final String lcCmd = command.toLowerCase();
        final List<String> result = this.commandMap.entrySet().parallelStream().filter(this.tabCompeleterFilter.apply(lcCmd)).map(e -> Command.COMMAND_PREFIX + e.getValue().getFullName()).sorted().collect(Collectors.toList());
        return result.isEmpty() ? this.commandMap.keySet().parallelStream().map(s -> Command.COMMAND_PREFIX + s).collect(Collectors.toList()) : result;
    }

    private final Function<String, Predicate<Map.Entry<String, MainCommand<CommandSender>>>> tabCompeleterFilter = lcCmd -> e ->
    {
        final String key = e.getKey().toLowerCase();
        final String plugin = key.substring(0, key.indexOf(Command.COMMAND_PLUGIN_SEPARATOR) + 2);
        return (key.startsWith(lcCmd) || (! lcCmd.contains(Command.COMMAND_PLUGIN_SEPARATOR) && key.startsWith(plugin + lcCmd))) && ! key.equals(lcCmd);
    };

    @Override
    public Command<CommandSender> findCommand(final String cmdLine)
    {
        if ((cmdLine == null) || cmdLine.isEmpty())
        {
            return null;
        }
        final int index = cmdLine.indexOf(' ');
        final String command;
        if (index == - 1)
        {
            command = cmdLine.toLowerCase();
        }
        else
        {
            command = cmdLine.substring(0, index).toLowerCase();
        }
        for (final MainCommand<CommandSender> cmd : this.getSortedCommandList())
        {
            if (cmd.matches(command))
            {
                return cmd;
            }
        }
        return null;
    }

    @Override
    public boolean dispatch(final CommandSender sender, final String cmdLine)
    {
        if ((cmdLine == null) || cmdLine.isEmpty())
        {
            return false;
        }
        final String[] args = DioriteStringUtils.splitArguments(cmdLine);
        if (args.length == 0)
        {
            return false;
        }
        if (sender instanceof Player)
        {
            Bukkit.getConsoleSender().sendMessage(sender.getName() + ": " + Command.COMMAND_PREFIX + cmdLine);
        }
        final String command = args[0];
        final String[] newArgs;
        if (args.length == 1)
        {
            newArgs = Command.EMPTY_ARGS;
        }
        else
        {
            newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        }
        for (final MainCommand<CommandSender> cmd : this.getSortedCommandList())
        {
            if (cmd.tryDispatch(sender, command, newArgs))
            {
                return true;
            }
        }
        // TO|DO: changeable message
        //   sender.sendSimpleColoredMessage("&4No command: &c" + command);
        return false;
    }

    public synchronized void registerCommand(final MainCommand<CommandSender> command)
    {
        if (command instanceof PluginCommand)
        {
            this.registerCommand((PluginCommand<CommandSender>) command);
        }
        else if (command instanceof SystemCommandImpl)
        {
            this.addCommandToMap("darkrise", command);
        }
        else
        {
            throw new IllegalArgumentException("Command must be from plugin");
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("commandMap", this.commandMap.keySet()).toString();
    }
}
