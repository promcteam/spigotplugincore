package com.gotofinal.darkrise.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import org.apache.commons.lang.Validate;

import net.milkbowl.vault.economy.Economy;

public final class Vault
{
    private static boolean vault;
    private static Economy eco;

    private Vault()
    {
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
    }

    public static boolean canPay(final Player player, final double money)
    {
        validate(player);
        return eco.has(player, money);
    }

    public static void pay(final Player player, final double money)
    {
        validate(player);
        eco.withdrawPlayer(player, money);
    }

    public static int getMoney(final Player player)
    {
        validate(player);
        return (int) eco.getBalance(player);
    }

    private static void validate(final Player player)
    {
        Validate.isTrue(vault, "You must have Vault plugin to use money as cost!");
        Validate.notNull(eco, "No economy plugin!");
        Validate.notNull(player, "Player can't be null");
    }
}
