package com.gotofinal.darkrise.spigot.core.config.elements;

import java.io.IOException;
import java.util.Map;

import com.gotofinal.darkrise.spigot.core.utils.cmds.DelayedCommand;

import org.diorite.cfg.system.CfgEntryData;
import org.diorite.cfg.system.elements.TemplateElement;

public class DelayedCommandTemplateElement extends TemplateElement<DelayedCommand>
{
    public static final DelayedCommandTemplateElement INSTANCE = new DelayedCommandTemplateElement();

    public DelayedCommandTemplateElement()
    {
        super(DelayedCommand.class);
    }

    @Override
    protected boolean canBeConverted0(final Class<?> aClass)
    {
        return Map.class.isAssignableFrom(aClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected DelayedCommand convertObject0(final Object obj) throws UnsupportedOperationException
    {
        if (obj instanceof Map)
        {
            return new DelayedCommand((Map<String, Object>) obj);
        }
        throw this.getException(obj);
    }

    @Override
    protected DelayedCommand convertDefault0(final Object obj, final Class<?> fieldType) throws UnsupportedOperationException
    {
        return this.convertObject0(obj);
    }

    @Override
    public void appendValue(final Appendable writer, final CfgEntryData field, final Object source, final Object element, final int level, final ElementPlace elementPlace) throws IOException
    {

    }
}
