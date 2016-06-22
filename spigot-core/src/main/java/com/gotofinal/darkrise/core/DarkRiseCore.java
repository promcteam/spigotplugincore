package com.gotofinal.darkrise.core;

import com.caversia.plugins.economy.model.CustomItem;
import com.gotofinal.darkrise.core.config.elements.PotionEffectTemplateElement;
import com.gotofinal.diggler.core.CorePlugin;
import com.gotofinal.messages.api.chat.placeholder.PlaceholderType;
import com.gotofinal.messages.main.bukkit.MessagesAPI;

import org.bukkit.potion.PotionEffect;

import org.apache.commons.lang3.StringUtils;

import org.diorite.cfg.system.elements.TemplateElements;

public class DarkRiseCore extends CorePlugin // TODO temp
{
    public static final PlaceholderType<CustomItem> CUSTOM_ITEM = PlaceholderType.create("customItem", CustomItem.class);

    static
    {
        MessagesAPI.PLAYER.registerItem("money", Vault::getMoney);

        CUSTOM_ITEM.registerItem("displayName", CustomItem::getDisplayName);
        CUSTOM_ITEM.registerItem("material", CustomItem::getMaterial);
        CUSTOM_ITEM.registerItem("name", CustomItem::getName);
        CUSTOM_ITEM.registerItem("lore", c -> StringUtils.join(c.getLore(), '\n'));
        CUSTOM_ITEM.registerItem("enchantments", c -> StringUtils.join(c.getEnchantments(), ", "));

        CUSTOM_ITEM.registerChild("item", MessagesAPI.ITEM, c -> c.asItemStack(1));


        TemplateElements.getElements().addFirst(PotionEffect.class.getName(), PotionEffectTemplateElement.INSTANCE);
        try
        {
            Class.forName(MessagesAPI.class.getName());
        } catch (final ClassNotFoundException e)
        {
            throw new AssertionError(e);
        }
    }

    private static DarkRiseCore instance;

    {
        instance = this;
    }

    public static DarkRiseCore getInstance()
    {
        return instance;
    }

    @Override
    public void onEnable()
    {
        super.onEnable();

    }

    @Override
    public void onLoad()
    {
        super.onLoad();
    }

    @Override
    public void onDisable()
    {
        super.onDisable();

    }
}
