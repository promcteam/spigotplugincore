package com.gotofinal.darkrise.spigot.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import org.diorite.utils.math.ByteRange;
import org.diorite.utils.math.DoubleRange;
import org.diorite.utils.math.FloatRange;
import org.diorite.utils.math.IntRange;
import org.diorite.utils.math.LongRange;
import org.diorite.utils.math.ShortRange;

public final class SerializationBuilder
{
    private final Map<String, Object> data;

    private SerializationBuilder(final int size)
    {
        this.data = new LinkedHashMap<>(size);
    }

    public SerializationBuilder append(final Object str, final Enum<?> object)
    {
        this.data.put(str.toString(), object.name());
        return this;
    }

    public SerializationBuilder append(final String str, final Enum<?> object)
    {
        this.data.put(str, object.name());
        return this;
    }

    public SerializationBuilder append(final Object str, Object object)
    {
        return this.append(str.toString(), object);
    }

    public SerializationBuilder append(final String str, final ConfigurationSerializable object)
    {
        return this.append(str, object.serialize());
    }

    public <T> SerializationBuilder append(final String str, final Map<T, ?> object, Function<T, String> keyToString)
    {
        SerializationBuilder sb = SerializationBuilder.start(object.size());
        for (final Entry<T, ?> entry : object.entrySet())
        {
            sb.append(keyToString.apply(entry.getKey()), entry.getValue());
        }
        return this.append(str, sb);
    }

    public SerializationBuilder appendMap(final String str, final Map<?, ?> object)
    {
        SerializationBuilder sb = SerializationBuilder.start(object.size());
        for (final Entry<?, ?> entry : object.entrySet())
        {
            sb.append(entry.getKey().toString(), entry.getValue());
        }
        return this.append(str, sb);
    }

    @SuppressWarnings("TailRecursion")
    public SerializationBuilder append(final String str, Object object)
    {
        if (object instanceof Enum)
        {
            return this.append(str, (Enum<?>) object);
        }
        if (object instanceof ByteRange)
        {
            return this.append(str, ((ByteRange) object).getMin() + "-" + ((ByteRange) object).getMax());
        }
        if (object instanceof ShortRange)
        {
            return this.append(str, ((ShortRange) object).getMin() + "-" + ((ShortRange) object).getMax());
        }
        if (object instanceof IntRange)
        {
            return this.append(str, ((IntRange) object).getMin() + "-" + ((IntRange) object).getMax());
        }
        if (object instanceof LongRange)
        {
            return this.append(str, ((LongRange) object).getMin() + "-" + ((LongRange) object).getMax());
        }
        if (object instanceof FloatRange)
        {
            return this.append(str, ((FloatRange) object).getMin() + "-" + ((FloatRange) object).getMax());
        }
        if (object instanceof DoubleRange)
        {
            return this.append(str, ((DoubleRange) object).getMin() + "-" + ((DoubleRange) object).getMax());
        }
        if (object instanceof ConfigurationSerializable)
        {
            return this.append(str, ((ConfigurationSerializable) object));
        }
        if (object instanceof Iterable)
        {
            Iterable<?> iterable = (Iterable<?>) object;
            Collection<Object> objects = new ArrayList<>((iterable instanceof Collection) ? ((Collection<?>) iterable).size() : 10);
            for (Object o : iterable)
            {
                if (o instanceof ConfigurationSerializable)
                {
                    objects.add(((ConfigurationSerializable) o).serialize());
                }
                else if (o instanceof Enum)
                {
                    objects.add(((Enum<?>) o).name());
                }
                else
                {
                    objects.add(o);
                }
            }
            object = objects;
        }
        this.data.put(str, object);
        return this;
    }

    public SerializationBuilder append(final String str, final SerializationBuilder object)
    {
        this.data.put(str, object.data);
        return this;
    }

    public SerializationBuilder append(final Object str, final SerializationBuilder object)
    {
        this.data.put(str.toString(), object.data);
        return this;
    }

    public SerializationBuilder appendCollection(final String str, final Collection<? extends ConfigurationSerializable> objects)
    {
        return this.append(str, objects.stream().map(ConfigurationSerializable::serialize).collect(Collectors.toList()));
    }

    public SerializationBuilder appendLoc(final String str, final Location location)
    {
        return this.append(str, start(6).append("x", location.getX()).append("y", location.getY()).append("z", location.getZ()).append("world", (location.getWorld() == null) ? null : location.getWorld().getName()).append("pitch", location.getPitch()).append("yaw", location.getYaw()).build());
    }

    public SerializationBuilder append(final Map<String, Object> object)
    {
        this.data.putAll(object);
        return this;
    }

    public Map<String, Object> build()
    {
        return this.data;
    }

    public static SerializationBuilder start(final int size)
    {
        return new SerializationBuilder(size);
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("data", this.data).toString();
    }
}
