package com.gotofinal.darkrise.spigot.core.utils.cmds;

import java.util.function.Supplier;

import org.apache.commons.lang.StringUtils;

public class R
{
    private final String           from;
    private final Supplier<String> to;

    public R(final String from, final String to)
    {
        this.from = (from == null) ? "null" : from;
        this.to = (to == null) ? () -> "null" : () -> to;
    }

    public R(final String from, final Supplier<String> to)
    {
        this.from = (from == null) ? "null" : from;
        this.to = (to == null) ? () -> "null" : to;
    }

    public R(final String from, final Object to)
    {
        this.from = (from == null) ? "null" : from;
        this.to = (to == null) ? () -> "null" : to::toString;
    }

    public String getFrom()
    {
        return this.from;
    }

    public String getTo()
    {
        return this.to.get();
    }

    public Supplier<String> getToSupplier()
    {
        return this.to;
    }

    public String use(final String str)
    {
        return StringUtils.replace(str, this.from, this.to.get());
    }

    @Override
    public String toString()
    {
        return "R{" + "from='" + this.from + '\'' + ", to='" + this.to + '\'' + '}';
    }

    public static R r(final String from, final Supplier<String> to)
    {
        return new R((from == null) ? "null" : from, to);
    }

    public static R r(final String from, final String to)
    {
        return new R((from == null) ? "null" : from, (to == null) ? "null" : to);
    }

    public static R r(final String from, final Object to)
    {
        return new R((from == null) ? "null" : from, (to == null) ? "null" : to.toString());
    }
}
