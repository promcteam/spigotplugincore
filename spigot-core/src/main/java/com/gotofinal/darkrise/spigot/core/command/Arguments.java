/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016. Diorite (by Bartłomiej Mazur (aka GotoFinal))
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.gotofinal.darkrise.spigot.core.command;

import com.gotofinal.darkrise.core.commands.exceptions.InvalidCommandArgumentException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Class used to read command parametrs instead of String[] to make some stuff easier/simpler
 */
public class Arguments extends com.gotofinal.darkrise.core.commands.Arguments
{
    /**
     * Construct new arguments wrapper.
     * WARN: it don't make copy/clone of given array!
     *
     * @param args string array to wrap.
     */
    public Arguments(final String[] args)
    {
        super(args);
    }

    /**
     * Get selected argument as {@link Player}, may return null if player is offline.
     *
     * @param index index of element, 0 is first element.
     *
     * @return {@link Player} or null if player is offline.
     *
     * @throws ArrayIndexOutOfBoundsException if {@code args.length > index}
     */
    public Player asPlayer(final int index) throws ArrayIndexOutOfBoundsException
    {
        return Bukkit.getPlayer(this.asString(index));
    }

    /**
     * Get selected argument as {@link org.bukkit.OfflinePlayer}.
     *
     * @param index index of element, 0 is first element.
     *
     * @return {@link org.bukkit.OfflinePlayer}.
     *
     * @throws ArrayIndexOutOfBoundsException if {@code args.length > index}
     */
    public OfflinePlayer asOfflinePlayer(final int index) throws ArrayIndexOutOfBoundsException
    {
        return Bukkit.getOfflinePlayer(this.asString(index));
    }

    /**
     * Read x, y, z (and yaw, pitch if needed) coordinates.
     * If argument starts from <B>~</B> then returned coordinates are relative to given entity.
     *
     * @param startIndex   index of first coordinate, 0 is first element.
     * @param withRotation if yaw and pitch should be also read.
     * @param entity       entity to get origin location used to get relative coordinates.
     *
     * @return x, y, z, yaw, pitch as {@link ImmutableLocation}
     *
     * @throws InvalidCommandArgumentException if any of numbers can't be parsed to coordinate.
     * @throws ArrayIndexOutOfBoundsException  if {@code args.length > startIndex + 2} or + 4 if withRotation is true.
     */
    public Location readCoordinates(final int startIndex, final boolean withRotation, final Entity entity) throws ArrayIndexOutOfBoundsException, InvalidCommandArgumentException
    {
        return this.readCoordinates(startIndex, withRotation, entity.getLocation());
    }

    /**
     * Read x, y, z (and yaw, pitch if needed) coordinates.
     * If argument starts from <B>~</B> then returned coordinates are relative to given location.
     *
     * @param startIndex   index of first coordinate, 0 is first element.
     * @param withRotation if yaw and pitch should be also read.
     * @param origin       used to get relative coordinates, if null {@link ImmutableLocation#ZERO} is used.
     *
     * @return x, y, z, yaw, pitch as {@link ImmutableLocation}
     *
     * @throws InvalidCommandArgumentException if any of numbers can't be parsed to coordinate.
     * @throws ArrayIndexOutOfBoundsException  if {@code args.length > startIndex + 2} or + 4 if withRotation is true.
     */
    public Location readCoordinates(final int startIndex, final boolean withRotation, Location origin) throws ArrayIndexOutOfBoundsException, InvalidCommandArgumentException
    {
        this.check(startIndex + (withRotation ? 4 : 2));
        if (origin == null)
        {
            origin = new Location(null, 0, 0, 0);
        }
        final double x = this.readCoordinate(startIndex, origin.getX());
        final double y = this.readCoordinate(startIndex + 1, origin.getY());
        final double z = this.readCoordinate(startIndex + 2, origin.getZ());
        if (! withRotation)
        {
            return new Location(origin.getWorld(), x, y, z, origin.getYaw(), origin.getPitch());
        }
        return new Location(origin.getWorld(), x, y, z, this.readRotation(startIndex + 3, origin.getYaw()), this.readRotation(startIndex + 4, origin.getPitch()));
    }
}
