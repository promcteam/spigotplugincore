package com.gotofinal.darkrise.spigot.core.config.elements;

import java.io.IOException;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import org.diorite.cfg.system.CfgEntryData;
import org.diorite.cfg.system.elements.MapTemplateElement;
import org.diorite.cfg.system.elements.TemplateElement;
import org.diorite.utils.reflections.DioriteReflectionUtils;

public class ConfigurationSerializableTemplateElement<T extends ConfigurationSerializable> extends TemplateElement<T>
{
    public ConfigurationSerializableTemplateElement(Class<T> clazz)
    {
        super(clazz);
    }

    @Override
    protected boolean canBeConverted0(final Class<?> aClass)
    {
        return Map.class.isAssignableFrom(aClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected T convertObject0(final Object obj) throws UnsupportedOperationException
    {
        if (obj instanceof Map)
        {
            return (T) DioriteReflectionUtils.getConstructor(this.fieldType, Map.class).invoke(obj);
        }
        throw this.getException(obj);
    }

    @Override
    protected T convertDefault0(final Object obj, final Class<?> fieldType) throws UnsupportedOperationException
    {
        return this.convertObject0(obj);
    }

    @Override
    public void appendValue(final Appendable writer, final CfgEntryData field, final Object source, final Object element, final int level, final ElementPlace elementPlace) throws IOException
    {
        MapTemplateElement.INSTANCE.appendValue(writer, field, source, ((ConfigurationSerializable) element).serialize(), level, elementPlace);
    }
}
