package com.gotofinal.darkrise.spigot.core;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockType
{
    private final Material mat;
    private final byte     type;

    public BlockType(final Material mat, final byte type)
    {
        this.mat = mat;
        this.type = type;
    }


    public Material getMat()
    {
        return this.mat;
    }

    public byte getType()
    {
        return this.type;
    }

    public boolean apply(final Block block)
    {
        return block.setTypeIdAndData(this.mat.getId(), this.type, true);
    }

    public String toConfigString()
    {
        return this.mat.name() + ":" + this.type;
    }

    public static BlockType fromConfigString(final String str)
    {
        final int index = str.indexOf(':');
        if (index == - 1)
        {
            return new BlockType(Material.matchMaterial(str), (byte) 0);
        }
        else
        {
            return new BlockType(Material.matchMaterial(str.substring(0, index)), Byte.parseByte(str.substring(index + 1, str.length())));
        }
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (! (o instanceof BlockType))
        {
            return false;
        }

        final BlockType blockType = (BlockType) o;

        return (this.type == blockType.type) && (this.mat == blockType.mat);

    }

    @Override
    public int hashCode()
    {
        int result = this.mat.hashCode();
        result = (31 * result) + (int) this.type;
        return result;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("mat", this.mat).append("type", this.type).toString();
    }
}
