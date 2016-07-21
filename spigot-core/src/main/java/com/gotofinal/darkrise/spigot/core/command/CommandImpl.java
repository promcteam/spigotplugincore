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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gotofinal.darkrise.core.commands.Command;
import com.gotofinal.darkrise.core.commands.CommandExecutor;
import com.gotofinal.darkrise.core.commands.ExceptionHandler;
import com.gotofinal.darkrise.core.commands.SubCommand;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.command.CommandSender;

public abstract class CommandImpl implements Command<CommandSender>
{
    private final String                                 name;
    protected     Pattern                                pattern;
    protected     List<String>                           knownAliases;
    private       String                                 description;
    private       String                                 usage;
    private       Map<String, SubCommand<CommandSender>> subCommandMap;

    private transient ExceptionHandler               exceptionHandler;
    private transient CommandExecutor<CommandSender> commandExecutor;


    public CommandImpl(final String name, final Pattern pattern)
    {
        this.name = name;
        this.pattern = (pattern == null) ? Pattern.compile(name, Pattern.CASE_INSENSITIVE) : pattern;
    }

    public CommandImpl(final String name)
    {
        this(name, (Pattern) null);
    }

    public CommandImpl(final String name, final Collection<String> aliases)
    {
        Objects.requireNonNull(name, "Command name can't be null");
        Objects.requireNonNull(aliases, "Command aliases can't be null");
        this.name = name;
        this.knownAliases = new ArrayList<>(aliases);
        this.pattern = Pattern.compile(aliases.stream().map(str -> "(" + Pattern.quote(str) + ")").reduce((s1, s2) -> s1 + "|" + s2).map(str -> name + "|(" + str + ")").orElse(name), Pattern.CASE_INSENSITIVE);
    }

    private void checkSubCommandsMap()
    {
        if (this.subCommandMap == null)
        {
            this.subCommandMap = new ConcurrentHashMap<>(5, .75f, 8);
        }
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public List<String> getKnownAliases()
    {
        return this.knownAliases;
    }

    @Override
    public Pattern getPattern()
    {
        return this.pattern;
    }

    @Override
    public void setPattern(final Pattern pattern)
    {
        this.pattern = pattern;
    }

    @Override
    public void setPattern(final String pattern)
    {
        this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public void setDescription(final String description)
    {
        this.description = description;
    }

    @Override
    public String getDescription()
    {
        return this.description;
    }

    @Override
    public void setUsage(final String usage)
    {
        this.usage = usage;
    }

    @Override
    public String getUsage()
    {
        if (this.usage == null)
        {
            return this.name;
        }
        return this.usage;
    }

    @Override
    public ExceptionHandler getExceptionHandler()
    {
        return this.exceptionHandler;
    }

    @Override
    public void setExceptionHandler(final ExceptionHandler exceptionHandler)
    {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public CommandExecutor<CommandSender> getCommandExecutor()
    {
        return this.commandExecutor;
    }

    @Override
    public void setCommandExecutor(final CommandExecutor<CommandSender> commandExecutor)
    {
        this.commandExecutor = commandExecutor;
    }

    @Override
    public SubCommand<CommandSender> registerSubCommand(final String name, final Pattern pattern)
    {
        final SubCommand<CommandSender> subCmd = new SubCommandImpl(name, pattern, this);
        this.registerSubCommand(subCmd);
        return subCmd;
    }

    @Override
    public SubCommand<CommandSender> registerSubCommand(final String name, final String pattern)
    {
        final SubCommand<CommandSender> subCmd = new SubCommandImpl(name, Pattern.compile(pattern, Pattern.CASE_INSENSITIVE), this);
        this.registerSubCommand(subCmd);
        return subCmd;
    }

    @Override
    public SubCommand<CommandSender> registerSubCommand(final String name, final Collection<String> aliases)
    {
        final SubCommand<CommandSender> subCmd = new SubCommandImpl(name, aliases, this);
        this.registerSubCommand(subCmd);
        return subCmd;
    }

    @Override
    public SubCommand<CommandSender> registerSubCommand(final String name, final Pattern pattern, final CommandExecutor<CommandSender> commandExecutor)
    {
        final SubCommand<CommandSender> subCmd = new SubCommandImpl(name, pattern, this);
        subCmd.setCommandExecutor(commandExecutor);
        this.registerSubCommand(subCmd);
        return subCmd;
    }

    @Override
    public SubCommand<CommandSender> registerSubCommand(final String name, final String pattern, final CommandExecutor<CommandSender> commandExecutor)
    {
        final SubCommand<CommandSender> subCmd = new SubCommandImpl(name, Pattern.compile(pattern, Pattern.CASE_INSENSITIVE), this);
        subCmd.setCommandExecutor(commandExecutor);
        this.registerSubCommand(subCmd);
        return subCmd;
    }

    @Override
    public SubCommand<CommandSender> registerSubCommand(final String name, final Collection<String> aliases, final CommandExecutor<CommandSender> commandExecutor)
    {
        final SubCommand<CommandSender> subCmd = new SubCommandImpl(name, aliases, this);
        subCmd.setCommandExecutor(commandExecutor);
        this.registerSubCommand(subCmd);
        return subCmd;
    }

    @Override
    public SubCommand<CommandSender> registerSubCommand(final String name, final Pattern pattern, final CommandExecutor<CommandSender> commandExecutor, final ExceptionHandler exceptionHandler)
    {
        final SubCommand<CommandSender> subCmd = new SubCommandImpl(name, pattern, this);
        subCmd.setCommandExecutor(commandExecutor);
        subCmd.setExceptionHandler(exceptionHandler);
        this.registerSubCommand(subCmd);
        return subCmd;
    }

    @Override
    public SubCommand<CommandSender> registerSubCommand(final String name, final String pattern, final CommandExecutor<CommandSender> commandExecutor, final ExceptionHandler exceptionHandler)
    {
        final SubCommand<CommandSender> subCmd = new SubCommandImpl(name, Pattern.compile(pattern, Pattern.CASE_INSENSITIVE), this);
        subCmd.setCommandExecutor(commandExecutor);
        subCmd.setExceptionHandler(exceptionHandler);
        this.registerSubCommand(subCmd);
        return subCmd;
    }

    @Override
    public SubCommand<CommandSender> registerSubCommand(final String name, final Collection<String> aliases, final CommandExecutor<CommandSender> commandExecutor, final ExceptionHandler exceptionHandler)
    {
        final SubCommand<CommandSender> subCmd = new SubCommandImpl(name, aliases, this);
        subCmd.setCommandExecutor(commandExecutor);
        subCmd.setExceptionHandler(exceptionHandler);
        this.registerSubCommand(subCmd);
        return subCmd;
    }

    @Override
    public void registerSubCommand(final SubCommand<CommandSender> subCommand)
    {
        this.checkSubCommandsMap();
        this.subCommandMap.put(subCommand.getName(), subCommand);
    }

    @Override
    public SubCommand<CommandSender> unregisterSubCommand(final String subCommand)
    {
        if (this.subCommandMap == null)
        {
            return null;
        }
        return this.subCommandMap.remove(subCommand);
    }

    @Override
    public Map<String, SubCommand<CommandSender>> getSubCommandMap()
    {
        if (this.subCommandMap == null)
        {
            return new HashMap<>(1);
        }
        return new HashMap<>(this.subCommandMap);
    }

    @Override
    public com.gotofinal.darkrise.core.commands.Arguments createArguments(String[] args)
    {
        return new Arguments(args);
    }

    @Override
    public final void dispatch(final CommandSender sender, final String label, final Matcher matchedPattern, final String[] args)
    {
        Objects.requireNonNull(sender, "sender can't be null");
        Objects.requireNonNull(label, "label can't be null");
        Objects.requireNonNull(matchedPattern, "matchedPattern can't be null");
        Objects.requireNonNull(args, "args can't be null");
        try
        {
            if ((this.subCommandMap != null) && (args.length > 0))
            {
                final String[] newArgs;
                if (args.length == 1)
                {
                    newArgs = EMPTY_ARGS;
                }
                else
                {
                    newArgs = new String[args.length - 1];
                    System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                }
                for (final SubCommand<CommandSender> subCommand : this.subCommandMap.values())
                {
                    if (subCommand.tryDispatch(sender, args[0], newArgs))
                    {
                        return;
                    }
                }
            }
            this.execute(sender, label, matchedPattern, args);
        }
        catch (Exception e)
        {
            if (this.exceptionHandler != null)
            {
                final Optional<Exception> opt = this.exceptionHandler.handle(e);
                if (opt.isPresent())
                {
                    e = opt.get();
                }
            }
            if (e != null)
            {
                e.printStackTrace();
            }
        }
    }

    protected void execute(final CommandSender sender, final String label, final Matcher matchedPattern, final String[] args)
    {
        if (this.commandExecutor != null)
        {
            Objects.requireNonNull(sender, "sender can't be null");
            Objects.requireNonNull(label, "label can't be null");
            Objects.requireNonNull(matchedPattern, "matchedPattern can't be null");
            Objects.requireNonNull(args, "args can't be null");
            matchedPattern.reset();
            matchedPattern.find();
            this.commandExecutor.runCommand(sender, this, label, matchedPattern, new Arguments(args));
        }
    }

    @Override
    public int hashCode()
    {
        int result = this.name.hashCode();
        result = (31 * result) + this.pattern.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (! (o instanceof CommandImpl))
        {
            return false;
        }

        final CommandImpl command = (CommandImpl) o;

        return this.name.equals(command.name) && this.pattern.equals(command.pattern);

    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("name", this.name).append("pattern", this.pattern).append("subCommandMap", (this.subCommandMap == null) ? "null" : this.subCommandMap.keySet()).toString();
    }
}
