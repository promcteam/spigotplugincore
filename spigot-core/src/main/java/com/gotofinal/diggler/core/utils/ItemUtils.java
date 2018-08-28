package com.gotofinal.diggler.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemUtils
{

    private ItemUtils()
    {
    }

    @SuppressWarnings("deprecation")
    public static Material getMaterial(final String mat)
    {
        Material material = Material.getMaterial(mat);
        if (material == null)
        {
            material = Material.getMaterial(mat, true);

            if (material != null)
            {
                return material;
            }

            material = Material.matchMaterial(mat);
            if (material == null)
            {
                try
                {
                    final int id = Integer.parseInt(mat);
                    Bukkit.getLogger().severe("SpigotCore attempts to get a material by it's id. Please change it to a name. ID: " + mat);
                    Thread.dumpStack();
                    for(Material m : Material.class.getEnumConstants())
                    {
                        if(m.getId() == id)
                        {
                            return m;
                        }
                    }
                } catch (final Exception ignored)
                {
                    return Material.AIR;
                }
            }
        }
        return material;
    }

    public static Color simpleDeserializeColor(final String string)
    {
        if (string == null)
        {
            return null;
        }
        return Color.fromRGB(Integer.parseInt(string, 16));
    }

    public static List<Color> simpleDeserializeColors(final Collection<String> strings)
    {
        if (strings == null)
        {
            return new ArrayList<>(0);
        }
        final List<Color> result = new ArrayList<>(strings.size());
        for (final String str : strings)
        {
            result.add(simpleDeserializeColor(str));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static FireworkEffect simpleDeserializeEffect(final Map<Object, Object> map)
    {
        if (map == null)
        {
            return null;
        }
        final FireworkEffect.Type type = FireworkEffect.Type.valueOf(map.get("type").toString());
        final boolean trail = (boolean) map.get("trail");
        final boolean flicker = (boolean) map.get("flicker");
        final List<Color> colors = simpleDeserializeColors((Collection<String>) map.get("colors"));
        final List<Color> fadeColors = simpleDeserializeColors((Collection<String>) map.get("fadeColors"));
        return FireworkEffect.builder().with(type).trail(trail).flicker(flicker).withColor(colors).withFade(fadeColors).build();
    }

    public static List<FireworkEffect> simpleDeserializeEffects(final Collection<Map<Object, Object>> list)
    {
        if (list == null)
        {
            return new ArrayList<>(0);
        }
        final List<FireworkEffect> result = new ArrayList<>(list.size());
        for (final Map<Object, Object> map : list)
        {
            result.add(simpleDeserializeEffect(map));
        }
        return result;
    }

    public static String simpleSerializeColor(final Color color)
    {
        if (color == null)
        {
            return null;
        }
        return Integer.toString(color.asRGB(), 16);
    }

    public static List<String> simpleSerializeColors(final Collection<Color> colors)
    {
        if (colors == null)
        {
            return new ArrayList<>(0);
        }
        final List<String> result = new ArrayList<>(colors.size());
        for (final Color color : colors)
        {
            result.add(simpleSerializeColor(color));
        }
        return result;
    }

    public static Map<String, Object> simpleSerializeEffect(final FireworkEffect effect)
    {
        if (effect == null)
        {
            return null;
        }
        final Map<String, Object> map = new HashMap<>(5);
        map.put("type", effect.getType().name());
        map.put("trail", effect.hasTrail());
        map.put("flicker", effect.hasFlicker());
        map.put("colors", simpleSerializeColors(effect.getColors()));
        map.put("fadeColors", simpleSerializeColors(effect.getFadeColors()));
        return map;
    }

    public static List<Map<String, Object>> simpleSerializeEffects(final Collection<FireworkEffect> effects)
    {
        if (effects == null)
        {
            return new ArrayList<>(0);
        }
        final List<Map<String, Object>> result = new ArrayList<>(effects.size());
        for (final FireworkEffect effect : effects)
        {
            result.add(simpleSerializeEffect(effect));
        }
        return result;
    }

    public static ItemMeta getItemMeta(final ItemStack itemStack)
    {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
        {
            return Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        }
        return meta;
    }
}
