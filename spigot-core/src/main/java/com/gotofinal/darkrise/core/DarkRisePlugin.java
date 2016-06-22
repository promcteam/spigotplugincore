package com.gotofinal.darkrise.core;

import java.io.File;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

import com.gotofinal.messages.api.MessageReceiver;
import com.gotofinal.messages.api.chat.component.BaseComponent;
import com.gotofinal.messages.api.messages.Message;
import com.gotofinal.messages.api.messages.Message.MessageData;
import com.gotofinal.messages.api.messages.MessageLoader;
import com.gotofinal.messages.api.messages.Messages;
import com.gotofinal.messages.bukkit.BukkitMessagesAPI;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import org.diorite.utils.collections.arrays.DioriteArrayUtils;

public abstract class DarkRisePlugin extends JavaPlugin
{
    protected Messages          messages;
    protected BukkitMessagesAPI messagesAPI;

    public void runSync(Runnable runnable)
    {
        Bukkit.getScheduler().runTask(this, runnable);
    }

    public void error(final Object obj)
    {
        this.getLogger().warning(obj.toString());
    }

    public void info(final Object obj)
    {
        this.getLogger().info(obj.toString());
    }

    @Override
    public void onEnable()
    {
        Vault.init();
    }

    public void reloadConfigs()
    {

    }

    public void reloadMessages()
    {
        final File langFolder = new File(this.getDataFolder(), "lang");
        this.messagesAPI = new BukkitMessagesAPI(this, Locale.ENGLISH);
        final MessageLoader messageLoader = this.messagesAPI.getMessageLoader();
        this.messages = messageLoader.loadMessages("lang_", langFolder, this.getClass(), "/lang/");
        messageLoader.saveMessages(this.messages, langFolder, "lang_");
    }

    @Override
    public void onLoad()
    {
        this.reloadMessages();
        super.onLoad();
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
    }

    public Messages getMessages()
    {
        return this.messages;
    }

    public Message getMessage(final String... path)
    {
        return this.messages.getMessage(path);
    }

    public Message getMessage(final String path)
    {
        return this.messages.getMessage(path);
    }

    public String getMessageAsString(final String path, final String def, final MessageData... data)
    {
        final Message message = this.messages.getMessage(path);
        if (message == null)
        {
            return def;
        }
        final BaseComponent baseComponent = message.get(null, data);
        return (baseComponent == null) ? def : baseComponent.toLegacyText();
    }

    public BaseComponent getMessageAsComponent(final String path, final BaseComponent def, final MessageData... data)
    {
        final Message message = this.messages.getMessage(path);
        if (message == null)
        {
            return def;
        }
        final BaseComponent baseComponent = message.get(null, data);
        return (baseComponent == null) ? def : baseComponent;
    }

    public boolean sendMessage(final String path, final CommandSender target, final MessageData... data)
    {
        return this.messages.sendMessage(path, this.messagesAPI.wrap(target), data);
    }

    public boolean sendNoPermission(final CommandSender target, final String permission, final MessageData... data)
    {
        return this.messages.sendMessage("noPermissions", this.messagesAPI.wrap(target), DioriteArrayUtils.join(data, new MessageData("permission", permission)));
    }

    public boolean checkPermission(final CommandSender target, final String permission, final MessageData... data)
    {
        if (target.hasPermission(permission))
        {
            return true;
        }
        this.messages.sendMessage("noPermissions", this.messagesAPI.wrap(target), DioriteArrayUtils.join(data, new MessageData("permission", permission)));
        return false;
    }

    public boolean broadcastMessage(final String path, final Collection<? extends CommandSender> targets, final Locale lang, final MessageData... data)
    {
        return this.messages.broadcastMessage(path, targets.stream().map(t -> this.messagesAPI.wrap(t)).collect(Collectors.toList()), lang, data);
    }

    public boolean broadcastStaticMessage(final String path, final Collection<? extends CommandSender> targets, final Locale lang, final MessageData... data)
    {
        return this.messages.broadcastStaticMessage(path, targets.stream().map(t -> this.messagesAPI.wrap(t)).collect(Collectors.toList()), lang, data);
    }

    public boolean broadcastMessage(final String path, final MessageData... data)
    {
        return this.messages.broadcastMessage(path, data);
    }

    public boolean broadcastMessage(final String path, final Collection<? extends CommandSender> targets, final MessageData... data)
    {
        return this.messages.broadcastMessage(path, targets.stream().map(t -> this.messagesAPI.wrap(t)).collect(Collectors.toList()), data);
    }

    public boolean sendMessage(final String path, final MessageReceiver target, final MessageData... data)
    {
        return this.messages.sendMessage(path, target, data);
    }

    public boolean broadcastMessage(final String path, final Iterable<? extends MessageReceiver> targets, final Locale lang, final MessageData... data)
    {
        return this.messages.broadcastMessage(path, targets, lang, data);
    }

    public boolean broadcastMessage(final String path, final Iterable<? extends MessageReceiver> targets, final MessageData... data)
    {
        return this.messages.broadcastMessage(path, targets, data);
    }

    public boolean broadcastStaticMessage(final String path, final Iterable<? extends MessageReceiver> targets, final Locale lang, final MessageData... data)
    {
        return this.messages.broadcastStaticMessage(path, targets, lang, data);
    }

    public Messages getMessages(final String path)
    {
        return this.messages.getMessages(path);
    }

    public boolean sendMessage(final String path, final MessageReceiver target, final Locale lang, final MessageData... data)
    {
        return this.messages.sendMessage(path, target, lang, data);
    }

    public boolean broadcastStaticMessage(final String path, final Locale lang, final MessageData... data)
    {
        return this.messages.broadcastStaticMessage(path, lang, data);
    }

    public boolean broadcastMessage(final String path, final Locale lang, final MessageData... data)
    {
        return this.messages.broadcastMessage(path, lang, data);
    }

    public Messages getMessages(final String... path)
    {
        return this.messages.getMessages(path);
    }
}
