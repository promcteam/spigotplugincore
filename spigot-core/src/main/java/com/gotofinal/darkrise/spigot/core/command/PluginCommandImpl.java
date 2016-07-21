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

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gotofinal.darkrise.core.DarkRisePlugin;
import com.gotofinal.darkrise.core.commands.PluginCommand;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bukkit.command.CommandSender;

public class PluginCommandImpl extends MainCommandImpl implements PluginCommand<CommandSender>
{
    private final DarkRisePlugin plugin;

    public PluginCommandImpl(final String name, final Pattern pattern, final int priority, final DarkRisePlugin plugin)
    {

        super(name, pattern, priority);
        this.plugin = plugin;
    }

    public PluginCommandImpl(final String name, final Pattern pattern, final DarkRisePlugin plugin)
    {
        super(name, pattern);
        this.plugin = plugin;
    }

    public PluginCommandImpl(final String name, final DarkRisePlugin plugin)
    {
        super(name);
        this.plugin = plugin;
    }

    public PluginCommandImpl(final String name, final Collection<String> aliases, final int priority, final DarkRisePlugin plugin)
    {
        super(name, aliases, priority);
        this.plugin = plugin;
    }

    public PluginCommandImpl(final String name, final Collection<String> aliases, final DarkRisePlugin plugin)
    {
        super(name, aliases);
        this.plugin = plugin;
    }

    @Override
    public DarkRisePlugin getPlugin()
    {
        return this.plugin;
    }


    @Override
    public Matcher matcher(final String name)
    {
        if (name.toLowerCase().startsWith(this.plugin.getName().toLowerCase() + COMMAND_PLUGIN_SEPARATOR))
        {
            return super.matcher(name.replace(this.plugin.getName().toLowerCase() + COMMAND_PLUGIN_SEPARATOR, ""));
        }
        else
        {
            return super.matcher(name);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("plugin", this.plugin).toString();
    }
}
