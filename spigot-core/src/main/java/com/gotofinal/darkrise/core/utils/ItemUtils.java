package com.gotofinal.darkrise.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class ItemUtils
{
    private ItemUtils()
    {
    }

    /**
     * Compact given array, it will create the smallest possible array with given items,
     * so it will join duplicated items etc.
     *
     * @param respectStackSize if method should respect max stack size.
     * @param itemsToCopact    item to compact.
     *
     * @return compacted array of items.
     */
    public static ItemStack[] compact(final boolean respectStackSize, final ItemStack... itemsToCopact)
    {
        final ItemStack[] items = new ItemStack[itemsToCopact.length];
        int j = 0;
        for (final ItemStack itemStack : itemsToCopact)
        {
            items[j++] = (itemStack == null) ? null : itemStack.clone();
        }

        for (int i = 0, itemsLength = items.length; i < itemsLength; i++)
        {
            final ItemStack item = items[i];
            if ((item == null) || (item.getType() == Material.AIR))
            {
                continue;
            }
            for (int k = i + 1; k < itemsLength; k++)
            {
                final ItemStack item2 = items[k];
                if (item.isSimilar(item2))
                {
                    if (respectStackSize)
                    {
                        final int space = item.getMaxStackSize() - item.getAmount();
                        if (space > 0)
                        {
                            final int toAdd = item2.getAmount();
                            if (space > toAdd)
                            {
                                item.setAmount(item.getAmount() + toAdd);
                                items[k] = null;
                            }
                            else
                            {
                                item.setAmount(item.getAmount() + space);
                                item2.setAmount(toAdd - space);
                            }
                        }
                    }
                    else
                    {
                        item.setAmount(item.getAmount() + item2.getAmount());
                        items[k] = null;
                    }
                }

            }
        }
        final List<ItemStack> result = new ArrayList<>(items.length);
        for (final ItemStack item : items)
        {
            if ((item == null) || (item.getType() == Material.AIR))
            {
                continue;
            }
            result.add(item);
        }
        return result.toArray(new ItemStack[result.size()]);
    }
}
