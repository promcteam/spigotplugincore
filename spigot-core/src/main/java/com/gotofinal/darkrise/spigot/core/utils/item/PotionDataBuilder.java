package com.gotofinal.darkrise.spigot.core.utils.item;

import java.util.LinkedHashMap;
import java.util.Map;

import com.gotofinal.darkrise.spigot.core.utils.DeserializationWorker;
import com.gotofinal.darkrise.spigot.core.utils.SerializationBuilder;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class PotionDataBuilder extends DataBuilder
{
    protected Map<PotionEffectType, PotionData> potions = new LinkedHashMap<>(5);
    protected org.bukkit.potion.PotionData basePotionDate;

    public PotionDataBuilder()
    {
    }

    @SuppressWarnings("unchecked")
    public PotionDataBuilder(final Map<String, Object> map)
    {
        final DeserializationWorker w = DeserializationWorker.start(map);
        String mainName = w.getString("main");
        PotionType potionType = w.getEnum("potionType", PotionType.class, PotionType.WATER);
        boolean isExtended = w.getBoolean("isExtended", false);
        boolean isUpgraded = w.getBoolean("isUpgraded", false);
        this.basePotionDate = new org.bukkit.potion.PotionData(potionType, isExtended, isUpgraded);
        final Map<Object, Map<Object, Object>> effects = w.getTypedObject("effects");
        for (final Map.Entry<Object, Map<Object, Object>> entry : effects.entrySet())
        {
            final PotionEffectType type = PotionEffectType.getByName(entry.getKey().toString());

            final DeserializationWorker effect = DeserializationWorker.startUnsafe(entry.getValue());
            final byte power = effect.getByte("power");
            final int time = effect.getInt("time");
            final boolean ambient = effect.getBoolean("ambient");
            this.potions.put(type, new PotionData(power, time, ambient));
        }
    }

    public Map<PotionEffectType, PotionData> getPotions()
    {
        return this.potions;
    }

    public org.bukkit.potion.PotionData getBasePotionDate()
    {
        return this.basePotionDate;
    }

    public PotionDataBuilder potions(final Map<PotionEffectType, PotionData> potions)
    {
        this.potions = potions;
        return this;
    }

    public PotionDataBuilder add(final PotionEffectType type, final byte power, final int time, final boolean ambient)
    {
        this.potions.put(type, new PotionData(power, time, ambient));
        return this;
    }

    public PotionDataBuilder add(final PotionEffectType type, final int power, final int time, final boolean ambient)
    {
        return this.add(type, (byte) power, time, ambient);
    }

    public PotionDataBuilder add(final PotionEffectType type, final byte power, final int time)
    {
        return this.add(type, power, time, false);
    }

    public PotionDataBuilder add(final PotionEffectType type, final int power, final int time)
    {
        return this.add(type, (byte) power, time);
    }

    public PotionDataBuilder add(final PotionEffectType type, final byte power)
    {
        return this.add(type, power, 0);
    }

    public PotionDataBuilder add(final PotionEffectType type, final int time)
    {
        return this.add(type, 0, time);
    }

    public PotionDataBuilder remove(final PotionEffectType type)
    {
        this.potions.remove(type);
        return this;
    }

    public PotionDataBuilder clear()
    {
        this.potions.clear();
        return this;
    }

    public PotionDataBuilder potionData(final org.bukkit.potion.PotionData type)
    {
        this.basePotionDate = type;
        return this;
    }

    @Override
    public void apply(final ItemMeta itemMeta)
    {
        if (! (itemMeta instanceof PotionMeta))
        {
            return;
        }
        final PotionMeta meta = (PotionMeta) itemMeta;
        meta.clearCustomEffects();
        for (final Map.Entry<PotionEffectType, PotionData> entry : this.potions.entrySet())
        {
            final PotionData data = entry.getValue();
            meta.addCustomEffect(new PotionEffect(entry.getKey(), data.time, data.power, data.ambient), true);
        }
        if (this.basePotionDate != null)
        {
            meta.setBasePotionData(this.basePotionDate);
        }
    }

    @Override
    public PotionDataBuilder use(final ItemMeta itemMeta)
    {
        if (! (itemMeta instanceof PotionMeta))
        {
            return null;
        }
        final PotionMeta meta = (PotionMeta) itemMeta;
        if (meta.hasCustomEffects())
        {
            this.basePotionDate = meta.getBasePotionData();
        }
        for (final PotionEffect effect : meta.getCustomEffects())
        {
            this.potions.put(effect.getType(), new PotionData((byte) effect.getAmplifier(), effect.getDuration(), effect.isAmbient()));
        }
        return this;
    }

    @Override
    public String getType()
    {
        return "potion";
    }

    @Override
    public Map<String, Object> serialize()
    {
        final SerializationBuilder b = SerializationBuilder.start(3).append(super.serialize());
        if (this.basePotionDate != null)
        {
            b.append("potionType", this.basePotionDate.getType());
            b.append("isExtended", this.basePotionDate.isExtended());
            b.append("isUpgraded", this.basePotionDate.isUpgraded());
        }
        else
        {
            b.append("potionType", PotionType.WATER);
            b.append("isExtended", false);
            b.append("isUpgraded", false);
        }
        final SerializationBuilder effects = SerializationBuilder.start(this.potions.size());
        for (final Map.Entry<PotionEffectType, PotionData> entry : this.potions.entrySet())
        {
            final SerializationBuilder effect = SerializationBuilder.start(3);
            final PotionData potionData = entry.getValue();
            effect.append("power", potionData.power);
            effect.append("time", potionData.time);
            effect.append("ambient", potionData.ambient);
            effects.append(entry.getKey().getName(), effect);
        }
        b.append("effects", effects);
        return b.build();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("potions", this.potions).append("main", this.basePotionDate).toString();
    }

    public class PotionData
    {
        private final byte    power;
        private final int     time;
        private final boolean ambient;

        public PotionData(final byte power, final int time, final boolean ambient)
        {
            this.power = power;
            this.time = time;
            this.ambient = ambient;
        }

        public byte getPower()
        {
            return this.power;
        }

        public int getTime()
        {
            return this.time;
        }

        public boolean isAmbient()
        {
            return this.ambient;
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("power", this.power).append("time", this.time).append("ambient", this.ambient).toString();
        }
    }

    public static PotionDataBuilder start()
    {
        return new PotionDataBuilder();
    }

}
