package com.gotofinal.darkrise.spigot.core.utils;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PlayerUtils
{
    private PlayerUtils()
    {
    }

    public static List<String> getOnlinePlayersNames()
    {
        return Bukkit.getOnlinePlayers().parallelStream().map(Player::getName).collect(Collectors.toList());
    }

    public static List<String> getOnlinePlayersNames(final String prefix)
    {
        final String lcPrefix = prefix.toLowerCase();
        return Bukkit.getOnlinePlayers().parallelStream().map(Player::getName).filter(s -> s.toLowerCase().startsWith(lcPrefix)).sorted().collect(Collectors.toList());
    }

    public static Collection<? extends Player> getRawPlayers()
    {
        return Bukkit.getOnlinePlayers();
    }

//    public static void forEach(final Packet<?> packet)
//    {
//        forEach(player -> player.getNetworkManager().sendPacket(packet));
//    }
//
//    public static void forEachExcept(final Player except, final Packet<?> packet)
//    {
//        //noinspection ObjectEquality
//        forEach(p -> p != except, player -> player.getNetworkManager().sendPacket(packet));
//    }
//
//    public static void forEach(final Packet<?>[] packets)
//    {
//        forEach(player -> player.getNetworkManager().sendPackets(packets));
//    }
//
//    public static void forEachExcept(final Player except, final Packet<?>[] packets)
//    {
//        //noinspection ObjectEquality
//        forEach(p -> p != except, player -> player.getNetworkManager().sendPackets(packets));
//    }

    public static Collection<Player> getOnlinePlayers(final Predicate<Player> predicate)
    {
        return Bukkit.getOnlinePlayers().stream().filter(predicate).collect(Collectors.toSet());
    }

//    public static void forEach(final Predicate<Player> predicate, final Packet<?> packet)
//    {
//        forEach(predicate, player -> player.getNetworkManager().sendPacket(packet));
//    }
//
//    public static void forEachExcept(final Player except, final Predicate<Player> predicate, final Packet<?> packet)
//    {
//        //noinspection ObjectEquality
//        forEach(p -> (p != except) && predicate.test(p), player -> player.getNetworkManager().sendPacket(packet));
//    }
//
//    public static void forEach(final Predicate<Player> predicate, final Packet<?>[] packets)
//    {
//        forEach(predicate, player -> player.getNetworkManager().sendPackets(packets));
//    }
//
//    public static void forEachExcept(final Player except, final Predicate<Player> predicate, final Packet<?>[] packets)
//    {
//        //noinspection ObjectEquality
//        forEach(p -> (p != except) && predicate.test(p), player -> player.getNetworkManager().sendPackets(packets));
//    }

    public static void forEachExcept(final Player except, final Consumer<Player> consumer)
    {
        //noinspection ObjectEquality
        Bukkit.getOnlinePlayers().stream().filter(p -> p != except).forEach(consumer);
    }

    public static void forEach(final Consumer<Player> consumer)
    {
        Bukkit.getOnlinePlayers().forEach(consumer);
    }

    public static void forEachExcept(final Player except, final Predicate<Player> predicate, final Consumer<Player> consumer)
    {
        //noinspection ObjectEquality
        Bukkit.getOnlinePlayers().stream().filter(p -> (p != except) && predicate.test(p)).forEach(consumer);
    }

    public static void forEach(final Predicate<Player> predicate, final Consumer<Player> consumer)
    {
        Bukkit.getOnlinePlayers().stream().filter(predicate).forEach(consumer);
    }
}
