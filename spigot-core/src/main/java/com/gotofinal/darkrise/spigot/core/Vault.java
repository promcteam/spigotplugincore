package com.gotofinal.darkrise.spigot.core;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

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

    public static boolean pay(final Player player, final double money)
    {
        validate(player);
        EconomyResponse economyResponse = eco.withdrawPlayer(player, money);
        return economyResponse.transactionSuccess();
    }

    public static double getMoney(final Player player)
    {
        validate(player);
        return eco.getBalance(player);
    }

    public static boolean addMoney(final Player player, final double money)
    {
        validate(player);
        return eco.depositPlayer(player, money).transactionSuccess();
    }

    public static void reset(final Player player)
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

    private static void validate(final Player player)
    {
        Validate.isTrue(vault, "You must have Vault plugin to use money as cost!");
        Validate.notNull(eco, "No economy plugin!");
        Validate.notNull(player, "Player can't be null");
    }
}
