package com.gotofinal.darkrise.spigot.core;

import java.io.File;

import com.gotofinal.darkrise.core.annotation.InvokeOn.InvokeType;
import com.gotofinal.darkrise.core.annotation.InvokeOnAnnotation;
import com.gotofinal.darkrise.core.commands.CommandMap;
import com.gotofinal.darkrise.spigot.core.command.CommandMapImpl;
import com.gotofinal.darkrise.spigot.core.config.CoreConfig;
import com.gotofinal.darkrise.spigot.core.config.elements.DelayedCommandTemplateElement;
import com.gotofinal.darkrise.spigot.core.config.elements.PotionEffectTemplateElement;
import com.gotofinal.darkrise.spigot.core.listeners.PlayerListener;
import com.gotofinal.darkrise.spigot.core.utils.cmds.DelayedCommand;
import com.gotofinal.messages.Init;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import org.diorite.cfg.system.elements.TemplateElements;

public class DarkRiseCore extends DarkRisePlugin
{
    private final CommandMap<CommandSender> commandMap = new CommandMapImpl();
    private CoreConfig config;

    public CommandMap<CommandSender> getCommandMap()
    {
        return this.commandMap;
    }

    @Override
    public DarkRiseCore getCore()
    {
        return this;
    }

//    public PluginCommandBuilder createCommand(final com.gotofinal.darkrise.core.DarkRisePlugin dioritePlugin, final String name)
//    {
//        return PluginCommandBuilderImpl.start(dioritePlugin, name);
//    }

    public CoreConfig getCfg()
    {
        return this.config;
    }

    static
    {
        TemplateElements.getElements().addFirst(PotionEffect.class.getName(), PotionEffectTemplateElement.INSTANCE);
        TemplateElements.getElements().addFirst(DelayedCommand.class.getName(), DelayedCommandTemplateElement.INSTANCE);
    }

    private static DarkRiseCore instance;

    {
        instance = this;
    }

    public static DarkRiseCore getInstance()
    {
        return instance;
    }

    @Override
    public void onEnable()
    {
        super.onEnable();
        this.reloadConfigs();
        InvokeOnAnnotation.invoke(InvokeType.ENABLE_CORE);
    }

    @Override
    public void reloadConfigs()
    {
        this.config = this.loadConfigFile(new File(this.getDataFolder(), "config.yml"), CoreConfig.class);
        double scaleHealth = this.config.getScaleHealth();
        for (final Player player : Bukkit.getOnlinePlayers())
        {
            PlayerListener.setHealthScale(player, scaleHealth);
        }
    }

    @Override
    public void onLoad()
    {
        Init.load();
        super.onLoad();
        InvokeOnAnnotation.invoke(InvokeType.LOAD_CORE);
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        InvokeOnAnnotation.invoke(InvokeType.DISABLE_CORE);
    }
}
