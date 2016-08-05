package com.gotofinal.darkrise.spigot.core;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

public final class Vault
{
    private static boolean    vault;
    private static Economy    eco;
    private static Permission permission;

    private Vault()
    {
    }

    public static String format(final double amount)
    {
        return eco.format(amount);
    }

    public static Permission getPermission()
    {
        return permission;
    }

    static void init()
    {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null)
        {
            return;
        }
        vault = true;
        final RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null)
        {
            eco = economyProvider.getProvider();
        }
        final RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null)
        {
            permission = permissionProvider.getProvider();
        }
    }

    public static boolean canPay(final OfflinePlayer player, final double money)
    {
        validate(player);
        return eco.has(player, money);
    }

    public static boolean pay(final OfflinePlayer player, final double money)
    {
        validate(player);
        EconomyResponse economyResponse = eco.withdrawPlayer(player, money);
        return economyResponse.transactionSuccess();
    }

    public static double getMoney(final OfflinePlayer player)
    {
        validate(player);
        return eco.getBalance(player);
    }

    public static boolean addMoney(final OfflinePlayer player, final double money)
    {
        validate(player);
        return eco.depositPlayer(player, money).transactionSuccess();
    }

    public static void reset(final OfflinePlayer player)
    {
        validate(player);
        eco.depositPlayer(player, getMoney(player));
        try
        {
            eco.createPlayerAccount(player);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void validate(final OfflinePlayer player)
    {
        Validate.isTrue(vault, "You must have Vault plugin to use money as cost!");
        Validate.notNull(eco, "No economy plugin!");
        Validate.notNull(player, "Player can't be null");
    }
}
