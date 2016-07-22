package com.gotofinal.darkrise.spigot.core.annotation;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.gotofinal.darkrise.core.DarkRisePlugin;
import com.gotofinal.darkrise.core.annotation.EventListener;
import com.gotofinal.darkrise.core.annotation.InvokeOn;
import com.gotofinal.darkrise.core.utils.ReflectionLibraryUtils;
import com.gotofinal.darkrise.spigot.core.DarkRiseCore;

import org.apache.commons.lang3.Validate;
import org.bukkit.event.Listener;

import org.diorite.utils.reflections.ConstructorInvoker;
import org.diorite.utils.reflections.DioriteReflectionUtils;
import org.diorite.utils.reflections.MethodInvoker;

public final class EventListenerAnnotation
{
    private EventListenerAnnotation()
    {
    }

    @SuppressWarnings("unchecked")
    @InvokeOn
    public static void register()
    {
        Map<Class<? extends Listener>, Class<? extends DarkRisePlugin>> listeners = ReflectionLibraryUtils.getRiseReflection().getTypesAnnotatedWith(EventListener.class).stream().peek(c -> Validate.isTrue(Listener.class.isAssignableFrom(c), "Class (" + c.getName() + ") annotated with EventListener must implement Listener interface")).collect(Collectors.toMap(c -> (Class<? extends Listener>) c, c -> c.getAnnotation(EventListener.class).value()));

        for (final Entry<Class<? extends Listener>, Class<? extends DarkRisePlugin>> entry : listeners.entrySet())
        {
            Class<? extends Listener> key = entry.getKey();
            Class<? extends DarkRisePlugin> value = entry.getValue();
            try
            {
                MethodInvoker method = DioriteReflectionUtils.getTypedMethod(value, null, value);
                com.gotofinal.darkrise.spigot.core.DarkRisePlugin plugin = (com.gotofinal.darkrise.spigot.core.DarkRisePlugin) method.invoke(null);

                ConstructorInvoker constructor;
                Listener listener;
                try
                {
                    constructor = DioriteReflectionUtils.getConstructor(key);
                    listener = (Listener) constructor.invoke();
                }
                catch (IllegalStateException e)
                {
                    constructor = DioriteReflectionUtils.getConstructor(key, value);
                    listener = (Listener) constructor.invoke(plugin);
                }

                Listener listenerCpy = listener;
                plugin.getServer().getScheduler().runTask(DarkRiseCore.getInstance(), () -> plugin.getServer().getPluginManager().registerEvents(listenerCpy, plugin));
            }
            catch (Exception e)
            {
                System.err.println("[DarkRise] Can't register " + entry.getKey().getName() + " listener for " + value.getName() + " plugin using @EventListener annotation!");
                e.printStackTrace();
            }
        }
    }
}
