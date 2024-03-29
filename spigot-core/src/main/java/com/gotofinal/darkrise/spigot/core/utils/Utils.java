package com.gotofinal.darkrise.spigot.core.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gotofinal.darkrise.core.BlockLocation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class Utils
{
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private Utils()
    {
    }

    public static BlockLocation fromBlock(Block block)
    {
        return new BlockLocation(block.getX(), block.getY(), block.getZ());
    }

    public static Integer parseInt(final String num)
    {
        try
        {
            return Integer.valueOf(num);
        } catch (final Exception ignored)
        {
            return null;
        }
    }

    public static String fixColors(final String string)
    {
        if (string == null)
        {
            return null;
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String[] fixColors(final String... strings)
    {
        if (strings == null)
        {
            return EMPTY_STRING_ARRAY;
        }
        for (int i = 0; i < strings.length; i++)
        {
            strings[i] = fixColors(strings[i]);
        }
        return strings;
    }

    public static List<String> fixColors(final List<String> list)
    {
        if (list == null)
        {
            return null;
        }
        for (int i = 0; i < list.size(); i++)
        {
            list.set(i, fixColors(list.get(i)));
        }
        return list;
    }

    public static List<String> removeColors(final List<String> list)
    {
        if (list == null)
        {
            return null;
        }
        for (int i = 0; i < list.size(); i++)
        {
            list.set(i, removeColors(list.get(i)));
        }
        return list;
    }

    public static String[] removeColors(final String... strings)
    {
        if (strings == null)
        {
            return EMPTY_STRING_ARRAY;
        }
        for (int i = 0; i < strings.length; i++)
        {
            strings[i] = removeColors(strings[i]);
        }
        return strings;
    }

    public static String removeColors(final String string)
    {
        if (string == null)
        {
            return null;
        }
        final char[] b = string.toCharArray();
        for (int i = 0; i < (b.length - 1); i++)
        {
            if ((b[i] == ChatColor.COLOR_CHAR) && ("0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > - 1))
            {
                b[i] = '&';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    public static Integer toInt(final String str)
    {
        try
        {
            return Integer.parseInt(str);
        } catch (final Exception e)
        {
            return null;
        }
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

    @SuppressWarnings("MagicNumber")
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
            return new ArrayList<>(1);
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
        final DeserializationWorker w = DeserializationWorker.startUnsafe(map);

        final Type type = w.getEnum("type", Type.BALL);
        final boolean trail = w.getBoolean("trail");
        final boolean flicker = w.getBoolean("flicker");
        final List<Color> colors = simpleDeserializeColors(w.<Collection<String>>getTypedObject("colors"));
        final List<Color> fadeColors = simpleDeserializeColors(w.<Collection<String>>getTypedObject("fadeColors"));
        return FireworkEffect.builder().with(type).trail(trail).flicker(flicker).withColor(colors).withFade(fadeColors).build();
    }

    public static List<FireworkEffect> simpleDeserializeEffects(final Collection<Map<Object, Object>> list)
    {
        if (list == null)
        {
            return new ArrayList<>(1);
        }
        final List<FireworkEffect> result = new ArrayList<>(list.size());
        for (final Map<Object, Object> map : list)
        {
            result.add(simpleDeserializeEffect(map));
        }
        return result;
    }

    @SuppressWarnings("MagicNumber")
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
            return new ArrayList<>(1);
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
        final SerializationBuilder b = SerializationBuilder.start(5);
        b.append("type", effect.getType());
        b.append("trail", effect.hasTrail());
        b.append("flicker", effect.hasFlicker());
        b.append("colors", simpleSerializeColors(effect.getColors()));
        b.append("fadeColors", simpleSerializeColors(effect.getFadeColors()));
        return b.build();
    }

    public static List<Map<String, Object>> simpleSerializeEffects(final Collection<FireworkEffect> effects)
    {
        if (effects == null)
        {
            return new ArrayList<>(1);
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

    private static final Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);

    public static long parseDateDiff(final String time, final boolean future)
    {
        return parseDateDiff(time, System.currentTimeMillis(), future);
    }

    public static long parseDateDiff(final String time, final long from, final boolean future)
    {
        final Matcher m = timePattern.matcher(time);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        boolean found = false;
        while (m.find())
        {
            if ((m.group() == null) || m.group().isEmpty())
            {
                continue;
            }
            for (int i = 0; i < m.groupCount(); i++)
            {
                if ((m.group(i) != null) && ! m.group(i).isEmpty())
                {
                    found = true;
                    break;
                }
            }
            if (found)
            {
                if ((m.group(1) != null) && ! m.group(1).isEmpty())
                {
                    years = Integer.parseInt(m.group(1));
                }
                if ((m.group(2) != null) && ! m.group(2).isEmpty())
                {
                    months = Integer.parseInt(m.group(2));
                }
                if ((m.group(3) != null) && ! m.group(3).isEmpty())
                {
                    weeks = Integer.parseInt(m.group(3));
                }
                if ((m.group(4) != null) && ! m.group(4).isEmpty())
                {
                    days = Integer.parseInt(m.group(4));
                }
                if ((m.group(5) != null) && ! m.group(5).isEmpty())
                {
                    hours = Integer.parseInt(m.group(5));
                }
                if ((m.group(6) != null) && ! m.group(6).isEmpty())
                {
                    minutes = Integer.parseInt(m.group(6));
                }
                if ((m.group(7) != null) && ! m.group(7).isEmpty())
                {
                    seconds = Integer.parseInt(m.group(7));
                }
                break;
            }
        }
        if (! found)
        {
            throw new RuntimeException("Wrong time: " + time);
        }
        final Calendar c = new GregorianCalendar();
        c.setTimeInMillis(from);
        if (years > 0)
        {
            c.add(Calendar.YEAR, years * (future ? 1 : - 1));
        }
        if (months > 0)
        {
            c.add(Calendar.MONTH, months * (future ? 1 : - 1));
        }
        if (weeks > 0)
        {
            c.add(Calendar.WEEK_OF_YEAR, weeks * (future ? 1 : - 1));
        }
        if (days > 0)
        {
            c.add(Calendar.DAY_OF_MONTH, days * (future ? 1 : - 1));
        }
        if (hours > 0)
        {
            c.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : - 1));
        }
        if (minutes > 0)
        {
            c.add(Calendar.MINUTE, minutes * (future ? 1 : - 1));
        }
        if (seconds > 0)
        {
            c.add(Calendar.SECOND, seconds * (future ? 1 : - 1));
        }
        final Calendar max = new GregorianCalendar();
        max.setTimeInMillis(from);
        max.add(Calendar.YEAR, 10);
        if (c.after(max))
        {
            return max.getTimeInMillis();
        }
        return c.getTimeInMillis();
    }

}
