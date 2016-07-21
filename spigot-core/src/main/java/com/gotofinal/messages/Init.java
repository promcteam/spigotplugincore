package com.gotofinal.messages;

import com.gotofinal.darkrise.core.BlockLocation;
import com.gotofinal.darkrise.core.DarkRisePlugin;
import com.gotofinal.darkrise.core.PlayerLocation;
import com.gotofinal.darkrise.spigot.core.DarkRiseCore;
import com.gotofinal.darkrise.spigot.core.Vault;
import com.gotofinal.messages.api.chat.placeholder.PlaceholderType;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Init
{
    public static final PlaceholderType<Location>    LOCATION    = PlaceholderType.create("location", Location.class);
    public static final PlaceholderType<Enchantment> ENCHANTMENT = PlaceholderType.create("enchantment", Enchantment.class);
    public static final PlaceholderType<Command>     COMMAND     = PlaceholderType.create("command", Command.class);
    public static final PlaceholderType<Plugin>      PLUGIN      = PlaceholderType.create("plugin", Plugin.class);
    public static final PlaceholderType<JavaPlugin>  JAVA_PLUGIN = PlaceholderType.create("javaPlugin", JavaPlugin.class, PLUGIN);
    public static final PlaceholderType<ItemStack>   ITEM        = PlaceholderType.create("item", ItemStack.class);

    public static final PlaceholderType<CommandSender> SENDER        = PlaceholderType.create("sender", CommandSender.class);
    public static final PlaceholderType<Entity>        ENTITY        = PlaceholderType.create("entity", Entity.class);
    public static final PlaceholderType<LivingEntity>  LIVING_ENTITY = PlaceholderType.create("livingEntity", LivingEntity.class, ENTITY);
    public static final PlaceholderType<Player>        PLAYER        = PlaceholderType.create("player", Player.class, ENTITY, SENDER);

    public static final PlaceholderType<DarkRisePlugin> RISE_PLUGIN     = PlaceholderType.create("risePlugin", DarkRisePlugin.class);
    public static final PlaceholderType<DarkRiseCore>   CORE            = PlaceholderType.create("core", DarkRiseCore.class, RISE_PLUGIN);
    public static final PlaceholderType<BlockLocation>  BLOCK_LOCATION  = PlaceholderType.create("blockLocation", BlockLocation.class);
    public static final PlaceholderType<PlayerLocation> PLAYER_LOCATION = PlaceholderType.create("playerLocation", PlayerLocation.class);

    public static final PlaceholderType<World> WORLD = PlaceholderType.create("world", World.class);

    public static void load()
    {
        BLOCK_LOCATION.registerItem("x", BlockLocation::getX);
        BLOCK_LOCATION.registerItem("y", BlockLocation::getY);
        BLOCK_LOCATION.registerItem("z", BlockLocation::getZ);
        PLAYER_LOCATION.registerItem("x", (location) -> ((int) (location.getX() * 10)) / 10);
        PLAYER_LOCATION.registerItem("y", (location) -> ((int) (location.getY() * 10)) / 10);
        PLAYER_LOCATION.registerItem("z", (location) -> ((int) (location.getZ() * 10)) / 10);

        RISE_PLUGIN.registerItem("name", DarkRisePlugin::getName);

        SENDER.registerItem("name", CommandSender::getName);
//        ENTITY.registerItem("name", Entity::getName);
        ENTITY.registerItem("entityId", Entity::getEntityId);
        ENTITY.registerItem("type", e -> e.getType().name().toLowerCase());
        ENTITY.registerItem("uuid", e -> e.getUniqueId().toString());
        LIVING_ENTITY.registerItem("health", LivingEntity::getHealth);
        LIVING_ENTITY.registerItem("maxHealth", LivingEntity::getMaxHealth);
        LIVING_ENTITY.registerItem("remainingAir", LivingEntity::getRemainingAir);
        LIVING_ENTITY.registerItem("health", LivingEntity::getHealth);
        LOCATION.registerItem("x", Location::getBlockX);
        LOCATION.registerItem("y", Location::getBlockY);
        LOCATION.registerItem("z", Location::getBlockZ);
        LOCATION.registerItem("yaw", Location::getYaw);
        LOCATION.registerItem("pitch", Location::getPitch);
        LOCATION.registerItem("exactX", Location::getX);
        LOCATION.registerItem("exactY", Location::getY);
        LOCATION.registerItem("exactZ", Location::getZ);
        LOCATION.registerItem("world", l -> l.getWorld().getName());
        WORLD.registerItem("name", World::getName);
        WORLD.registerItem("difficulty", w -> w.getDifficulty().name().toLowerCase());
        WORLD.registerItem("environment", w -> w.getEnvironment().name().toLowerCase());
        WORLD.registerItem("seed", World::getSeed);
        WORLD.registerItem("uuid", w -> w.getUID().toString());
        WORLD.registerItem("type", w -> w.getWorldType().name().toLowerCase());
        WORLD.registerItem("folder", w -> w.getWorldFolder().getPath());
        ITEM.registerItem("amount", ItemStack::getAmount);
        ITEM.registerItem("durability", ItemStack::getDurability);
        ITEM.registerItem("material", i -> i.getType().name().toLowerCase());
        ITEM.registerItem("displayName", i -> i.getItemMeta().getDisplayName());
        ITEM.registerItem("lore", i -> i.getItemMeta().getLore());

        ENCHANTMENT.registerItem("id", Enchantment::getId);
        ENCHANTMENT.registerItem("name", Enchantment::getName);
        ENCHANTMENT.registerItem("maxLevel", Enchantment::getMaxLevel);
        ENCHANTMENT.registerItem("startLevel", Enchantment::getStartLevel);
        ENCHANTMENT.registerItem("target", e -> e.getItemTarget().name().toLowerCase());

        COMMAND.registerItem("name", Command::getName);
        COMMAND.registerItem("description", Command::getDescription);
        COMMAND.registerItem("label", Command::getLabel);
        COMMAND.registerItem("permission", Command::getPermission);
        COMMAND.registerItem("permissionMessage", Command::getPermissionMessage);
        COMMAND.registerItem("usage", Command::getUsage);
        COMMAND.registerItem("aliases", Command::getAliases);

        PLUGIN.registerItem("name", Plugin::getName);
        PLUGIN.registerItem("description", Plugin::getDescription);
        PLUGIN.registerItem("folder", p -> p.getDataFolder().toPath());
        JAVA_PLUGIN.registerItem("authors", p -> p.getDescription().getAuthors());
        JAVA_PLUGIN.registerItem("depend", p -> p.getDescription().getDepend());
        JAVA_PLUGIN.registerItem("fullName", p -> p.getDescription().getFullName());
        JAVA_PLUGIN.registerItem("main", p -> p.getDescription().getMain());
        JAVA_PLUGIN.registerItem("website", p -> p.getDescription().getWebsite());
        JAVA_PLUGIN.registerItem("version", p -> p.getDescription().getVersion());
        JAVA_PLUGIN.registerItem("prefix", p -> p.getDescription().getPrefix());
        JAVA_PLUGIN.registerItem("softDepend", p -> p.getDescription().getSoftDepend());
        JAVA_PLUGIN.registerItem("loadBefore", p -> p.getDescription().getLoadBefore());

        RISE_PLUGIN.registerChild("core", CORE, p -> ((DarkRiseCore) p.getCore()));
        PLAYER.registerChild("world", WORLD, Player::getWorld);
        ENTITY.registerChild("location", LOCATION, Entity::getLocation);
        ENTITY.registerChild("loc", LOCATION, Entity::getLocation);
        LOCATION.registerChild("world", WORLD, Location::getWorld);
        Init.PLAYER.registerItem("money", (player) -> (int) Vault.getMoney(player));
    }
}
