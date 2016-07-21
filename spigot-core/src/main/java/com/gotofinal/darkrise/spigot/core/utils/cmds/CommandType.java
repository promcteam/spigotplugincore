package com.gotofinal.darkrise.spigot.core.utils.cmds;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public enum CommandType
{
    PLAYER
            {
                @Override
                public void invoke(final CommandSender user, final String command, final R... r)
                {
                    Bukkit.dispatchCommand(user, repl(command, user, r));
                }
            },
    OP
            {
                @Override
                public void invoke(final CommandSender user, final String command, final R... r)
                {
                    final boolean isOp = user.isOp();
                    try
                    {
                        if (! isOp) // don't op if he had op
                        {
                            user.setOp(true);
                        }
                        Bukkit.dispatchCommand(user, repl(command, user, r));
                        if (! isOp) // don't de-op if he had op
                        {
                            user.setOp(false);
                        }
                    } finally // for sure... shit happens
                    {
                        if (! isOp)
                        {
                            user.setOp(false);
                        }
                    }
                }
            },
    CONSOLE
            {
                @Override
                public void invoke(final CommandSender user, final String command, final R... r)
                {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), repl(command, user, r));
                }
            };

    public abstract void invoke(CommandSender user, String command, R... r);

    private static String repl(final String command, final CommandSender user, final R... reps)
    {
        final String[] keys = new String[reps.length + 1];
        final String[] values = new String[reps.length + 1];
        int i = 0;
        values[i] = user.getName();
        keys[i++] = "{player}";
        for (final R rep : reps)
        {
            values[i] = rep.getTo();
            keys[i++] = rep.getFrom();
        }
        return StringUtils.replaceEach(command, keys, values);
    }
}
