package com.gotofinal.diggler.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

import com.gotofinal.darkrise.core.DarkRiseCore;
import com.gotofinal.diggler.core.nms.INMSPlayerUtils;
import com.gotofinal.diggler.core.nms.INMSWrapper;
import com.gotofinal.diggler.core.nms.NMSPlayerUtils;
import com.gotofinal.diggler.core.utils.SpammyError;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import org.apache.commons.lang.StringUtils;

public class CorePlugin extends JavaPlugin
{
    private static CorePlugin instance;

    public static CorePlugin getInstance()
    {
        return instance;
    }

    {
        CorePlugin.instance = this;
        SpammyError.setLogger(this.getLogger());
    }

    private static transient String nmsVersion;

    public static String getNMSVersion()
    {
        if (nmsVersion == null)
        {
            nmsVersion = StringUtils.remove(StringUtils.removeStart(Bukkit.getServer().getClass().getPackage().getName(), "org.bukkit.craftbukkit"), '.');
        }
        return nmsVersion;
    }

    @Override
    public void onEnable()
    {
        this.initNMS(this.getNMSVersion());
//        Bukkit.getPluginManager().registerEvents(new Listener()
//        {
//            @EventHandler
//            public void test(AsyncPlayerPreLoginEvent e)
//            {
//                System.out.println(e);
//            }
//        }, this);
    }

    public void initNMS(final String version)
    {
        if (version == null)
        {
            NMSPlayerUtils.setInst(new com.gotofinal.diggler.core.nms.none.NMSPlayerUtils());
        }
        try
        {
            NMSPlayerUtils.setInst((INMSPlayerUtils) Class.forName(getClassName(NMSPlayerUtils.class)).newInstance());
        } catch (final Exception e)
        {
            SpammyError.err("\n\n\n\n==============================\n\nCan't enable CorePlugin, unknown version of NMS (" + version + ")! Plugin may need update.\n\n==============================\n\n\n\n", (int) TimeUnit.HOURS.toSeconds(1), "NMSException");
            final StringWriter stringWriter = new StringWriter(200);
            e.printStackTrace(new PrintWriter(stringWriter));
            SpammyError.err(stringWriter.toString(), (int) TimeUnit.HOURS.toSeconds(1), "NMSException-stack");


            this.initNMS(null);
        }
    }

    private static String getClassName(final Class<? extends INMSWrapper> clazz)
    {
        return clazz.getPackage().getName() + "." + getNMSVersion() + "." + clazz.getSimpleName();
    }

    @Override
    public void onDisable()
    {

    }

    public static int runTaskLater(final Runnable runnable, final int delay)
    {
        return Bukkit.getScheduler().runTaskLater(DarkRiseCore.getInstance(), runnable, delay).getTaskId();
    }

    public static int runTask(final Runnable runnable)
    {
        return Bukkit.getScheduler().runTask(DarkRiseCore.getInstance(), runnable).getTaskId();
    }
}
