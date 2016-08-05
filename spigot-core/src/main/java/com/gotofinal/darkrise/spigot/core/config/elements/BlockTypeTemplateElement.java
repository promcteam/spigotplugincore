package com.gotofinal.darkrise.spigot.core.config.elements;

import com.gotofinal.darkrise.spigot.core.BlockType;

import org.diorite.cfg.system.elements.SimpleStringTemplateElement;

public class BlockTypeTemplateElement extends SimpleStringTemplateElement<BlockType>
{
    public static final BlockTypeTemplateElement INSTANCE = new BlockTypeTemplateElement();

    public BlockTypeTemplateElement()
    {
        super(BlockType.class);
    }

    @Override
    protected String convertToString(final BlockType blockType)
    {
        return blockType.toConfigString();
    }

    @Override
    protected BlockType simpleConvert(final Object o) throws UnsupportedOperationException
    {
        if (o instanceof String)
        {
            return BlockType.fromConfigString((String) o);
        }
        throw this.getException(o);
    }

    @Override
    protected boolean canBeConverted0(final Class<?> aClass)
    {
        return String.class.isAssignableFrom(aClass);
    }
}
