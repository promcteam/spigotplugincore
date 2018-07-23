package com.gotofinal.darkrise.spigot.core;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.gotofinal.darkrise.core.annotation.InvokeOn.InvokeType;
import com.gotofinal.darkrise.core.annotation.InvokeOnAnnotation;
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

    {
        com.gotofinal.darkrise.core.DarkRisePlugin.Handler.addPlugin(this);
    }

    public CommandMap<CommandSender> getCommandMap()
    {
        return DarkRiseCore.getInstance().getCommandMap();
    }

    public void runSync(Runnable runnable)
    {
        Bukkit.getScheduler().runTask(this, runnable);
    }

    public void error(Object obj)
    {
        this.getLogger().warning(obj.toString());
    }

    @Override
    public void onEnable()
    {
        Vault.init();
        super.onEnable();
        InvokeOnAnnotation.invoke(InvokeType.ENABLE_OF, this);
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
            this.sendMessage("noPermissions", sender, new MessageData("permission", s));
            return false;
        }
        return true;
    }

    public <T> T loadConfigFile(File f, Class<T> type)
    {
        T config;
        try
        {
            Template<T> cfgTemp = TemplateCreator.getTemplate(type);
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
                catch (IOException e)
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
                catch (IOException e)
                {
                    throw new RuntimeException("Can't create configuration file!", e);
                }
            }
            try
            {
                cfgTemp.dump(f, config, false);
            }
            catch (IOException e)
            {
                throw new RuntimeException("Can't dump configuration file!", e);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Can't create configuration file!", e);
        }
        return config;
    }

    public BukkitScheduler getScheduler()
    {
        return this.getServer().getScheduler();
    }

    public <T> Future<T> callSyncMethod(Callable<T> callable)
    {
        return this.getScheduler().callSyncMethod(this, callable);
    }

    public void cancelTask(int i)
    {
        this.getScheduler().cancelTask(i);
    }

    public void cancelTasks(Plugin plugin)
    {
        this.getScheduler().cancelTasks(plugin);
    }

    public void cancelTasks()
    {
        this.getScheduler().cancelTasks(this);
    }

    public void cancelAllTasks()
    {
        this.getScheduler().cancelTasks(this);
    }

    public boolean isCurrentlyRunning(int i)
    {
        return this.getScheduler().isCurrentlyRunning(i);
    }

    public boolean isQueued(int i)
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

    public BukkitTask runTask(Runnable runnable) throws IllegalArgumentException
    {
        return this.getScheduler().runTask(this, runnable);
    }

    public BukkitTask runTaskAsynchronously(Runnable runnable) throws IllegalArgumentException
    {
        return this.getScheduler().runTaskAsynchronously(this, runnable);
    }

    public BukkitTask runTaskLater(Runnable runnable, long l) throws IllegalArgumentException
    {
        return this.getScheduler().runTaskLater(this, runnable, l);
    }

    public BukkitTask runTaskLaterAsynchronously(Runnable runnable, long l) throws IllegalArgumentException
    {
        return this.getScheduler().runTaskLaterAsynchronously(this, runnable, l);
    }

    public BukkitTask runTaskTimer(Runnable runnable, long l, long l1) throws IllegalArgumentException
    {
        return this.getScheduler().runTaskTimer(this, runnable, l, l1);
    }

    public BukkitTask runTaskTimerAsynchronously(Runnable runnable, long l, long l1) throws IllegalArgumentException
    {
        return this.getScheduler().runTaskTimerAsynchronously(this, runnable, l, l1);
    }

    public void reloadMessages()
    {
        File langFolder = new File(this.getDataFolder(), "lang");
        this.messagesAPI = new BukkitMessagesAPI(this, Locale.forLanguageTag("en"));
        MessageLoader messageLoader = this.messagesAPI.getMessageLoader();
        Messages messages = messageLoader.loadMessages("lang_", langFolder, this.getClass(), "/lang/");
        messageLoader.saveMessages(messages, langFolder, "lang_");
        DarkRiseCore core = this.getCore();
        if (this != core)
        {
            core.messages.joinMessages(messages);
            this.messages = core.messages;
        }
        else
        {
            if (this.messages != null)
            {
                this.messages.joinMessages(messages);
            }
            else
            {
                this.messages = messages;
            }
        }
    }

    @Override
    public void onLoad()
    {
        this.reloadMessages();
        super.onLoad();
        InvokeOnAnnotation.invoke(InvokeType.LOAD_OF, this);
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        InvokeOnAnnotation.invoke(InvokeType.DISABLE_OF, this);
    }

    public Messages getMessages()
    {
        return this.messages;
    }

    public Message getMessage(String... path)
    {
        return this.messages.getMessage(path);
    }

    public Message getMessage(String path)
    {
        return this.messages.getMessage(path);
    }

    public String getMessageAsString(String path, String def, MessageData... data)
    {
        Message message = this.messages.getMessage(path);
        if (message == null)
        {
            return def;
        }
        BaseComponent[] baseComponent = message.get(null, data);
        return (baseComponent == null) ? def : ComponentUtils.toLegacyText(baseComponent);
    }

    public BaseComponent[] getMessageAsComponent(String path, BaseComponent[] def, MessageData... data)
    {
        Message message = this.messages.getMessage(path);
        if (message == null)
        {
            return def;
        }
        BaseComponent[] baseComponent = message.get(null, data);
        return ((baseComponent == null) || (baseComponent.length == 0)) ? def : baseComponent;
    }

    public BaseComponent[] getMessageAsComponent(String path, MessageData... data)
    {
        return this.getMessageAsComponent(path, DioriteArrayUtils.getEmptyObjectArray(BaseComponent.class), data);
    }

    public boolean sendMessage(String path, CommandSender target, MessageData... data)
    {
        return this.messages.sendMessage(path, this.messagesAPI.wrap(target), data);
    }

    public boolean broadcastMessage(String path, Collection<? extends CommandSender> targets, Locale lang, MessageData... data)
    {
        return this.messages.broadcastMessage(path, targets.stream().map(t -> this.messagesAPI.wrap(t)).collect(Collectors.toList()), lang, data);
    }

    public boolean broadcastStaticMessage(String path, Collection<? extends CommandSender> targets, Locale lang, MessageData... data)
    {
        return this.messages.broadcastStaticMessage(path, targets.stream().map(t -> this.messagesAPI.wrap(t)).collect(Collectors.toList()), lang, data);
    }

    public boolean broadcastMessage(String path, MessageData... data)
    {
        return this.messages.broadcastMessage(path, data);
    }

    public boolean broadcastMessage(String path, Collection<? extends CommandSender> targets, MessageData... data)
    {
        return this.messages.broadcastMessage(path, targets.stream().map(t -> this.messagesAPI.wrap(t)).collect(Collectors.toList()), data);
    }

    public boolean sendMessage(String path, MessageReceiver target, MessageData... data)
    {
        return this.messages.sendMessage(path, target, data);
    }

    public boolean broadcastMessage(String path, Iterable<? extends MessageReceiver> targets, Locale lang, MessageData... data)
    {
        return this.messages.broadcastMessage(path, targets, lang, data);
    }

    public boolean broadcastMessage(String path, Iterable<? extends MessageReceiver> targets, MessageData... data)
    {
        return this.messages.broadcastMessage(path, targets, data);
    }

    public boolean broadcastStaticMessage(String path, Iterable<? extends MessageReceiver> targets, Locale lang, MessageData... data)
    {
        return this.messages.broadcastStaticMessage(path, targets, lang, data);
    }

    public Messages getMessages(String path)
    {
        return this.messages.getMessages(path);
    }

    public boolean sendMessage(String path, MessageReceiver target, Locale lang, MessageData... data)
    {
        return this.messages.sendMessage(path, target, lang, data);
    }

    public boolean broadcastStaticMessage(String path, Locale lang, MessageData... data)
    {
        return this.messages.broadcastStaticMessage(path, lang, data);
    }

    public boolean broadcastMessage(String path, Locale lang, MessageData... data)
    {
        return this.messages.broadcastMessage(path, lang, data);
    }

    public Messages getMessages(String... path)
    {
        return this.messages.getMessages(path);
    }
}
