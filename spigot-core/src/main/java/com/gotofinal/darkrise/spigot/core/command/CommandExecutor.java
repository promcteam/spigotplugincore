package com.gotofinal.darkrise.spigot.core.command;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import com.gotofinal.darkrise.spigot.core.DarkRiseCore;
import com.gotofinal.darkrise.core.commands.Command;
import com.gotofinal.messages.api.MessageReceiver;
import com.gotofinal.messages.api.chat.utils.ComponentUtils;
import com.gotofinal.messages.api.messages.Message;
import com.gotofinal.messages.api.messages.Message.MessageData;
import com.gotofinal.messages.api.messages.Messages;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;

@FunctionalInterface
public interface CommandExecutor extends com.gotofinal.darkrise.core.commands.CommandExecutor<CommandSender>
{
    @Override
    default void runCommand(CommandSender sender, final Command<CommandSender> command, final String label, Matcher matchedPattern, com.gotofinal.darkrise.core.commands.Arguments args)
    {
        this.runCommand(sender, command, label, matchedPattern, (Arguments) args);
    }

    void runCommand(CommandSender sender, final Command<CommandSender> command, final String label, Matcher matchedPattern, Arguments args);

    default List<String> onTabComplete(final CommandSender sender, final Command<CommandSender> command, final String label, final Matcher matchedPattern, final Arguments args)
    {
        return command.getSubCommandMap().keySet().stream().collect(Collectors.toList());
    }

    @Override
    default List<String> onTabComplete(final CommandSender sender, final Command<CommandSender> command, final String label, final Matcher matchedPattern, final com.gotofinal.darkrise.core.commands.Arguments args)
    {
        return this.onTabComplete(sender, command, label, matchedPattern, (Arguments) args);
    }

    default DarkRiseCore getCore()
    {
        return DarkRiseCore.getInstance();
    }

    default void broadcast(final String message)
    {
        Bukkit.broadcastMessage(message);
    }

    default void broadcast(final BaseComponent component)
    {
        Bukkit.spigot().broadcast(component);
    }

    default void broadcast(final ChatMessageType position, final String message)
    {
        this.broadcast(position, ComponentUtils.fromLegacyText(message));
    }

    default void broadcast(final ChatMessageType position, final BaseComponent... component)
    {
        for (final Player player : Bukkit.getOnlinePlayers())
        {
            player.spigot().sendMessage(position, component);
        }
        Bukkit.getConsoleSender().sendMessage(ComponentUtils.toPlainText(component));
    }

    default BukkitTask sync(final Runnable runnable) throws IllegalArgumentException
    {
        return this.getCore().runTask(runnable);
    }

    default BukkitTask async(final Runnable runnable) throws IllegalArgumentException
    {
        return this.getCore().runTaskAsynchronously(runnable);
    }

    default void error(final Object obj)
    {
        this.getCore().error(obj);
    }

    default void info(final Object obj)
    {
        this.getCore().info(obj);
    }

    default Messages getMessages()
    {
        return this.getCore().getMessages();
    }

    default Message getMessage(final String... path)
    {
        return this.getCore().getMessage(path);
    }

    default Message getMessage(final String path)
    {
        return this.getCore().getMessage(path);
    }

    default String getMessageAsString(final String path, final String def, final MessageData... data)
    {
        return this.getCore().getMessageAsString(path, def, data);
    }

    default BaseComponent[] getMessageAsComponent(final String path, final BaseComponent def, final MessageData... data)
    {
        return this.getCore().getMessageAsComponent(path, new BaseComponent[]{def}, data);
    }

    default BaseComponent[] getMessageAsComponent(final String path, final MessageData... data)
    {
        return this.getCore().getMessageAsComponent(path, data);
    }

    default boolean sendMessage(final String path, final CommandSender target, final MessageData... data)
    {
        return this.getCore().sendMessage(path, target, data);
    }

    default boolean broadcastMessage(final String path, final Collection<? extends CommandSender> targets, final Locale lang, final MessageData... data)
    {
        return this.getCore().broadcastMessage(path, targets, lang, data);
    }

    default boolean broadcastStaticMessage(final String path, final Collection<? extends CommandSender> targets, final Locale lang, final MessageData... data)
    {
        return this.getCore().broadcastStaticMessage(path, targets, lang, data);
    }

    default boolean broadcastMessage(final String path, final MessageData... data)
    {
        return this.getCore().broadcastMessage(path, data);
    }

    default boolean broadcastMessage(final String path, final Collection<? extends CommandSender> targets, final MessageData... data)
    {
        return this.getCore().broadcastMessage(path, targets, data);
    }

    default boolean sendMessage(final String path, final MessageReceiver target, final MessageData... data)
    {
        return this.getCore().sendMessage(path, target, data);
    }

    default boolean broadcastMessage(final String path, final Iterable<? extends MessageReceiver> targets, final Locale lang, final MessageData... data)
    {
        return this.getCore().broadcastMessage(path, targets, lang, data);
    }

    default boolean broadcastMessage(final String path, final Iterable<? extends MessageReceiver> targets, final MessageData... data)
    {
        return this.getCore().broadcastMessage(path, targets, data);
    }

    default boolean broadcastStaticMessage(final String path, final Iterable<? extends MessageReceiver> targets, final Locale lang, final MessageData... data)
    {
        return this.getCore().broadcastStaticMessage(path, targets, lang, data);
    }

    default Messages getMessages(final String path)
    {
        return this.getCore().getMessages(path);
    }

    default boolean sendMessage(final String path, final MessageReceiver target, final Locale lang, final MessageData... data)
    {
        return this.getCore().sendMessage(path, target, lang, data);
    }

    default boolean broadcastStaticMessage(final String path, final Locale lang, final MessageData... data)
    {
        return this.getCore().broadcastStaticMessage(path, lang, data);
    }

    default boolean broadcastMessage(final String path, final Locale lang, final MessageData... data)
    {
        return this.getCore().broadcastMessage(path, lang, data);
    }

    default Messages getMessages(final String... path)
    {
        return this.getCore().getMessages(path);
    }
}
