package com.gotofinal.darkrise.spigot.core;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.gotofinal.darkrise.core.commands.CommandMap;
import com.gotofinal.diggler.core.CorePlugin;
import com.gotofinal.messages.api.MessageReceiver;
import com.gotofinal.messages.api.chat.utils.ComponentUtils;
import com.gotofinal.messages.api.messages.Message;
import com.gotofinal.messages.api.messages.Message.MessageData;
import com.gotofinal.messages.api.messages.MessageLoader;
import com.gotofinal.messages.api.messages.Messages;
import com.gotofinal.messages.bukkit.BukkitMessagesAPI;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import org.diorite.cfg.system.Template;
import org.diorite.cfg.system.TemplateCreator;
import org.diorite.utils.DioriteUtils;
import org.diorite.utils.collections.arrays.DioriteArrayUtils;

import net.md_5.bungee.api.chat.BaseComponent;

public abstract class DarkRisePlugin extends CorePlugin
{
    protected Messages          messages;
    protected BukkitMessagesAPI messagesAPI;

    public CommandMap<CommandSender> getCommandMap()
    {
        return DarkRiseCore.getInstance().getCommandMap();
    }

    public void runSync(Runnable runnable)
    {
        Bukkit.getScheduler().runTask(this, runnable);
    }

    public void error(final Object obj)
    {
        this.getLogger().warning(obj.toString());
    }

    @Override
    public void onEnable()
    {
        Vault.init();
    }

    public void reloadConfigs()
    {

    }

    public void saveConfigs()
    {

    }

    public boolean checkPermission(org.bukkit.command.CommandSender sender, String s)
    {
        if (! sender.hasPermission(s))
        {
            this.sendMessage("noPermission", sender, new MessageData("permission", s));
            return false;
        }
        return true;
    }

    public <T> T loadConfigFile(final File f, Class<T> type)
    {
        T config;
        try
        {
            final Template<T> cfgTemp = TemplateCreator.getTemplate(type);
            if (f.exists())
            {
                try
                {
                    config = cfgTemp.load(f);
                    if (config == null)
                    {
                        config = cfgTemp.fillDefaults(type.newInstance());
                    }
                }
                catch (final IOException e)
                {
                    throw new RuntimeException("IO exception when loading config file: " + f, e);
                }
            }
            else
            {
                config = cfgTemp.fillDefaults(type.newInstance());
                try
                {
                    DioriteUtils.createFile(f);
                }
                catch (final IOException e)
                {
                    throw new RuntimeException("Can't create configuration file!", e);
                }
            }
            try
            {
                cfgTemp.dump(f, config, false);
            }
            catch (final IOException e)
            {
                throw new RuntimeException("Can't dump configuration file!", e);
            }
        }
        catch (final Exception e)
        {
            throw new RuntimeException("Can't create configuration file!", e);
        }
        return config;
    }

    public BukkitScheduler getScheduler()
    {
        return this.getServer().getScheduler();
    }

    public <T> Future<T> callSyncMethod(final Callable<T> callable)
    {
        return this.getScheduler().callSyncMethod(this, callable);
    }

    public void cancelTask(final int i)
    {
        this.getScheduler().cancelTask(i);
    }

    public void cancelTasks(final Plugin plugin)
    {
        this.getScheduler().cancelTasks(plugin);
    }

    public void cancelTasks()
    {
        this.getScheduler().cancelTasks(this);
    }


    public void cancelAllTasks()
    {
        this.getScheduler().cancelAllTasks();
    }

    public boolean isCurrentlyRunning(final int i)
    {
        return this.getScheduler().isCurrentlyRunning(i);
    }

    public boolean isQueued(final int i)
    {
        return this.getScheduler().isQueued(i);
    }

    public List<BukkitWorker> getActiveWorkers()
    {
        return this.getScheduler().getActiveWorkers();
    }

    public List<BukkitTask> getPendingTasks()
    {
        return this.getScheduler().getPendingTasks();
    }

    public BukkitTask runTask(final Runnable runnable) throws IllegalArgumentException
    {
        return this.getScheduler().runTask(this, runnable);
    }

    public BukkitTask runTaskAsynchronously(final Runnable runnable) throws IllegalArgumentException
    {
        return this.getScheduler().runTaskAsynchronously(this, runnable);
    }

    public BukkitTask runTaskLater(final Runnable runnable, final long l) throws IllegalArgumentException
    {
        return this.getScheduler().runTaskLater(this, runnable, l);
    }

    public BukkitTask runTaskLaterAsynchronously(final Runnable runnable, final long l) throws IllegalArgumentException
    {
        return this.getScheduler().runTaskLaterAsynchronously(this, runnable, l);
    }

    public BukkitTask runTaskTimer(final Runnable runnable, final long l, final long l1) throws IllegalArgumentException
    {
        return this.getScheduler().runTaskTimer(this, runnable, l, l1);
    }

    public BukkitTask runTaskTimerAsynchronously(final Runnable runnable, final long l, final long l1) throws IllegalArgumentException
    {
        return this.getScheduler().runTaskTimerAsynchronously(this, runnable, l, l1);
    }

    public void reloadMessages()
    {
        final File langFolder = new File(this.getDataFolder(), "lang");
        this.messagesAPI = new BukkitMessagesAPI(this, Locale.forLanguageTag("pl"));
        final MessageLoader messageLoader = this.messagesAPI.getMessageLoader();
        Messages messages = messageLoader.loadMessages("lang_", langFolder, this.getClass(), "/lang/");
        DarkRiseCore core = this.getCore();
        if (this != core)
        {
            core.messages.joinMessages(messages);
            this.messages = core.messages;
        }
        else
        {
            this.messages = messages;
        }
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
        final BaseComponent[] baseComponent = message.get(null, data);
        return (baseComponent == null) ? def : ComponentUtils.toLegacyText(baseComponent);
    }

    public BaseComponent[] getMessageAsComponent(final String path, final BaseComponent[] def, final MessageData... data)
    {
        final Message message = this.messages.getMessage(path);
        if (message == null)
        {
            return def;
        }
        final BaseComponent[] baseComponent = message.get(null, data);
        return ((baseComponent == null) || (baseComponent.length == 0)) ? def : baseComponent;
    }

    public BaseComponent[] getMessageAsComponent(final String path, final MessageData... data)
    {
        return this.getMessageAsComponent(path, DioriteArrayUtils.getEmptyObjectArray(BaseComponent.class), data);
    }

    public boolean sendMessage(final String path, final CommandSender target, final MessageData... data)
    {
        return this.messages.sendMessage(path, this.messagesAPI.wrap(target), data);
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
