package com.gotofinal.darkrise.spigot.core.config.elements;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import org.apache.commons.lang3.StringUtils;

import org.diorite.cfg.system.elements.SimpleStringTemplateElement;

public class PotionEffectTemplateElement extends SimpleStringTemplateElement<PotionEffect>
{
    public static final PotionEffectTemplateElement INSTANCE = new PotionEffectTemplateElement();

    public PotionEffectTemplateElement()
    {
        super(PotionEffect.class);
    }

    @Override
    protected String convertToString(final PotionEffect potionEffect)
    {
        final StringBuilder stringBuilder = new StringBuilder(100);
        stringBuilder.append(potionEffect.getType().getName()).append(':').append(potionEffect.getDuration());
        final int amplifier = potionEffect.getAmplifier();
        if ((amplifier > 0) || potionEffect.hasParticles() || potionEffect.isAmbient())
        {
            stringBuilder.append(':').append(amplifier);
            if (potionEffect.isAmbient() || potionEffect.hasParticles())
            {
                stringBuilder.append(':').append(potionEffect.isAmbient());
                if (potionEffect.hasParticles())
                {
                    stringBuilder.append(':').append(potionEffect.hasParticles());
                }
            }
        }
        return stringBuilder.toString();
    }

    @Override
    protected PotionEffect simpleConvert(final Object o) throws UnsupportedOperationException
    {
        if (o instanceof String)
        {
            final String[] str = StringUtils.splitPreserveAllTokens((String) o, ':');
            if ((str.length < 2) || (str.length > 5))
            {
                throw this.getException(o);
            }
            final PotionEffectType effectType = PotionEffectType.getByName(str[0]);
            if (effectType == null)
            {
                throw this.getException(o, "No effect type with name: " + str[0] + ", possible names: " + Arrays.stream(PotionEffectType.values()).map(PotionEffectType::getName).collect(Collectors.toList()));
            }
            final int duration = Integer.parseInt(str[1]);
            final int amplifier = (str.length > 2) ? Integer.parseInt(str[2]) : 0;
            final boolean ambient = (str.length <= 3) || Boolean.parseBoolean(str[3]);
            final boolean particles = (str.length <= 4) || Boolean.parseBoolean(str[4]);
            return new PotionEffect(effectType, duration, amplifier, ambient, particles);
        }
        throw this.getException(o);
    }

    @Override
    protected boolean canBeConverted0(final Class<?> aClass)
    {
        return String.class.isAssignableFrom(aClass);
    }
}
