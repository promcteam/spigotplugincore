package com.gotofinal.darkrise.spigot.core;

import java.io.File;

import com.gotofinal.darkrise.core.annotation.InvokeOn.InvokeType;
import com.gotofinal.darkrise.core.annotation.InvokeOnAnnotation;
import com.gotofinal.darkrise.core.commands.CommandMap;
import com.gotofinal.darkrise.spigot.core.command.CommandMapImpl;
import com.gotofinal.darkrise.spigot.core.config.CoreConfig;
import com.gotofinal.darkrise.spigot.core.config.elements.BlockTypeTemplateElement;
import com.gotofinal.darkrise.spigot.core.config.elements.ConfigurationSerializableTemplateDeserializer;
import com.gotofinal.darkrise.spigot.core.config.elements.ConfigurationSerializableTemplateElement;
import com.gotofinal.darkrise.spigot.core.config.elements.DelayedCommandTemplateElement;
import com.gotofinal.darkrise.spigot.core.config.elements.PotionEffectTemplateElement;
import com.gotofinal.darkrise.spigot.core.listeners.PlayerListener;
import com.gotofinal.darkrise.spigot.core.utils.cmds.DelayedCommand;
import com.gotofinal.darkrise.spigot.core.utils.item.BookDataBuilder;
import com.gotofinal.darkrise.spigot.core.utils.item.EnchantmentStorageBuilder;
import com.gotofinal.darkrise.spigot.core.utils.item.FireworkBuilder;
import com.gotofinal.darkrise.spigot.core.utils.item.FireworkEffectBuilder;
import com.gotofinal.darkrise.spigot.core.utils.item.ItemBuilder;
import com.gotofinal.darkrise.spigot.core.utils.item.LeatherArmorBuilder;
import com.gotofinal.darkrise.spigot.core.utils.item.MapBuilder;
import com.gotofinal.darkrise.spigot.core.utils.item.PotionDataBuilder;
import com.gotofinal.darkrise.spigot.core.utils.item.SkullBuilder;
import com.gotofinal.messages.Init;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import org.diorite.cfg.system.deserializers.TemplateDeserializers;
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
        TemplateElements.getElements().addFirst(BlockType.class.getName(), BlockTypeTemplateElement.INSTANCE);
        TemplateElements.getElements().addFirst(DelayedCommand.class.getName(), DelayedCommandTemplateElement.INSTANCE);
        TemplateElements.getElements().addFirst(DelayedCommand.class.getName(), new ConfigurationSerializableTemplateElement<>(DelayedCommand.class));
        TemplateElements.getElements().addFirst(BookDataBuilder.class.getName(), new ConfigurationSerializableTemplateElement<>(BookDataBuilder.class));
        TemplateElements.getElements().addFirst(EnchantmentStorageBuilder.class.getName(), new ConfigurationSerializableTemplateElement<>(EnchantmentStorageBuilder.class));
        TemplateElements.getElements().addFirst(FireworkBuilder.class.getName(), new ConfigurationSerializableTemplateElement<>(FireworkBuilder.class));
        TemplateElements.getElements().addFirst(FireworkEffectBuilder.class.getName(), new ConfigurationSerializableTemplateElement<>(FireworkEffectBuilder.class));
        TemplateElements.getElements().addFirst(ItemBuilder.class.getName(), new ConfigurationSerializableTemplateElement<>(ItemBuilder.class));
        TemplateElements.getElements().addFirst(LeatherArmorBuilder.class.getName(), new ConfigurationSerializableTemplateElement<>(LeatherArmorBuilder.class));
        TemplateElements.getElements().addFirst(MapBuilder.class.getName(), new ConfigurationSerializableTemplateElement<>(MapBuilder.class));
        TemplateElements.getElements().addFirst(PotionDataBuilder.class.getName(), new ConfigurationSerializableTemplateElement<>(PotionDataBuilder.class));
        TemplateElements.getElements().addFirst(SkullBuilder.class.getName(), new ConfigurationSerializableTemplateElement<>(SkullBuilder.class));
        TemplateElements.getElements().addLast(ConfigurationSerializable.class.getName(), new ConfigurationSerializableTemplateElement<>(ConfigurationSerializable.class));

        TemplateDeserializers.getElements().addFirst(DelayedCommand.class.getName(), new ConfigurationSerializableTemplateDeserializer<>(DelayedCommand.class));
        TemplateDeserializers.getElements().addFirst(BookDataBuilder.class.getName(), new ConfigurationSerializableTemplateDeserializer<>(BookDataBuilder.class));
        TemplateDeserializers.getElements().addFirst(EnchantmentStorageBuilder.class.getName(), new ConfigurationSerializableTemplateDeserializer<>(EnchantmentStorageBuilder.class));
        TemplateDeserializers.getElements().addFirst(FireworkBuilder.class.getName(), new ConfigurationSerializableTemplateDeserializer<>(FireworkBuilder.class));
        TemplateDeserializers.getElements().addFirst(FireworkEffectBuilder.class.getName(), new ConfigurationSerializableTemplateDeserializer<>(FireworkEffectBuilder.class));
        TemplateDeserializers.getElements().addFirst(ItemBuilder.class.getName(), new ConfigurationSerializableTemplateDeserializer<>(ItemBuilder.class));
        TemplateDeserializers.getElements().addFirst(LeatherArmorBuilder.class.getName(), new ConfigurationSerializableTemplateDeserializer<>(LeatherArmorBuilder.class));
        TemplateDeserializers.getElements().addFirst(MapBuilder.class.getName(), new ConfigurationSerializableTemplateDeserializer<>(MapBuilder.class));
        TemplateDeserializers.getElements().addFirst(PotionDataBuilder.class.getName(), new ConfigurationSerializableTemplateDeserializer<>(PotionDataBuilder.class));
        TemplateDeserializers.getElements().addFirst(SkullBuilder.class.getName(), new ConfigurationSerializableTemplateDeserializer<>(SkullBuilder.class));
        TemplateDeserializers.getElements().addFirst(ConfigurationSerializable.class.getName(), new ConfigurationSerializableTemplateDeserializer<>(ConfigurationSerializable.class));
    }

    private static DarkRiseCore instance;

    {
        instance = this;
        com.gotofinal.darkrise.core.DarkRisePlugin.Handler.setPlugin(this);
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
