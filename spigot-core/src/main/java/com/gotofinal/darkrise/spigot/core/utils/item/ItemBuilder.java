package com.gotofinal.darkrise.spigot.core.utils.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gotofinal.darkrise.spigot.core.nms.Attributes;
import com.gotofinal.darkrise.spigot.core.nms.Attributes.Attribute;
import com.gotofinal.darkrise.spigot.core.utils.DeserializationWorker;
import com.gotofinal.darkrise.spigot.core.utils.SerializationBuilder;
import com.gotofinal.darkrise.spigot.core.utils.Utils;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemBuilder implements ConfigurationSerializable
{
    protected Material material    = Material.AIR;
    protected int      amount      = 1;
    protected short    durability  = 0;
    protected boolean  unbreakable = false;
    protected String name;
    protected List<String>              lore        = new ArrayList<>(5);
    protected Map<Enchantment, Integer> enchants    = new LinkedHashMap<>(3);
    protected DataBuilder               dataBuilder = null;
    protected List<ItemFlag>            flags       = new ArrayList<>(5);
    protected List<Attribute>           attributes  = new ArrayList<>(2);
    //  protected UnaryOperator<String> func;

    public ItemBuilder()
    {
    }

    @SuppressWarnings("unchecked")
    public ItemBuilder(Map<String, Object> map)
    {
        DeserializationWorker w = DeserializationWorker.start(map);
        this.material = Utils.getMaterial(w.getString("material", "AIR"));
        this.amount = w.getInt("amount", 1);
        this.durability = w.getShort("durability");
        this.name = w.getString("name", null);
        this.unbreakable = w.getBoolean("unbreakable", false);
        this.attributes.addAll(w.<Map<String, Object>>getList("attributes", new ArrayList<>(1)).stream().map(Attribute::new).collect(Collectors.toList()));
        this.lore = w.getStringList("lore", new ArrayList<>(3));
        this.flags = w.getStringList("flags", new ArrayList<>(1)).stream().map(s -> ItemFlag.valueOf(s.toUpperCase())).collect(Collectors.toList());
        Map<String, Object> enchantsMap = w.getSection("enchants");
        if (enchantsMap != null)
        {
            for (Map.Entry<String, Object> entry : enchantsMap.entrySet())
            {
                this.enchants.put(Enchantment.getByName(entry.getKey()), ((Number) entry.getValue()).intValue());
            }
        }
        this.dataBuilder = DataBuilder.build(w.getTypedObject("data", new HashMap<>(1)));
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).append("material", this.material)
                                                                          .append("amount", this.amount).append("durability", this.durability)
                                                                          .append("name", this.name).append("lore", this.lore).append("enchants", this.enchants)
                                                                          .append("dataBuilder", this.dataBuilder).toString();
    }

    public Material getMaterial()
    {
        return this.material;
    }

    public int getAmount()
    {
        return this.amount;
    }

    public short getDurability()
    {
        return this.durability;
    }

    public List<String> getLore()
    {
        return this.lore;
    }

    public boolean isUnbreakable()
    {
        return this.unbreakable;
    }

    public String getName()
    {
        return this.name;
    }

    public List<ItemFlag> getFlags()
    {
        return this.flags;
    }

    public Map<Enchantment, Integer> getEnchants()
    {
        return this.enchants;
    }

    public DataBuilder getDataBuilder()
    {
        return this.dataBuilder;
    }

    public ItemBuilder unbreakable(boolean flag)
    {
        this.unbreakable = flag;
        return this;
    }

    public ItemBuilder unbreakable(ItemMeta meta)
    {
        this.unbreakable = meta.spigot().isUnbreakable();
        return this;
    }

    public ItemBuilder flag(ItemFlag flag)
    {
        this.flags.add(flag);
        return this;
    }

    public ItemBuilder clearFlags()
    {
        this.flags.clear();
        return this;
    }

    public ItemBuilder flag(ItemFlag... flags)
    {
        Collections.addAll(this.flags, flags);
        return this;
    }

    public ItemBuilder flag(ItemMeta meta)
    {
        this.flags.addAll(meta.getItemFlags());
        return this;
    }

    public ItemBuilder material(Material material)
    {
        this.material = material;
        return this;
    }

    public ItemBuilder material(ItemStack source)
    {
        this.material = source.getType();
        return this;
    }

    public ItemBuilder durability(int damage)
    {
        return this.durability((short) damage);
    }

    public ItemBuilder durability(short damage)
    {
        this.durability = damage;
        return this;
    }

    public ItemBuilder durability(ItemStack source)
    {
        this.durability = source.getDurability();
        return this;
    }

    public ItemBuilder amount(int amount)
    {
        this.amount = amount;
        return this;
    }

    public ItemBuilder amount(ItemStack source)
    {
        this.amount = source.getAmount();
        return this;
    }
//
//    public ItemBuilder apply(final UnaryOperator<String> func)
//    {
//        this.func = func;
//        return this;
//    }

    public ItemBuilder name(String name)
    {
        this.name = name;
        return this;
    }

    public ItemBuilder name(ItemMeta source)
    {
        this.name = Utils.removeColors(source.getDisplayName());
        return this;
    }

    public ItemBuilder clearName()
    {
        this.name = null;
        return this;
    }

    public ItemBuilder lore(List<String> lore)
    {
        this.lore = (lore == null) ? new ArrayList<String>(5) : new ArrayList<>(lore);
        return this;
    }

    public ItemBuilder lore(ItemMeta source)
    {
        this.lore = source.hasLore() ? Utils.removeColors(new ArrayList<>(source.getLore())) : new ArrayList<String>(5);
        return this;
    }

    public ItemBuilder newLoreLine(String lore)
    {
        this.lore.add(lore);
        return this;
    }

    public ItemBuilder newLoreLine(Object lore)
    {
        this.lore.add(lore.toString());
        return this;
    }

    public ItemBuilder newLoreLine(Collection<String> lore)
    {
        this.lore.addAll(lore);
        return this;
    }

    public ItemBuilder newLoreLine(String... lore)
    {
        this.newLoreLine(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder insertLoreLine(int index, String lore)
    {
        this.lore.add(index, lore);
        return this;
    }

    public ItemBuilder insertLoreLine(int index, Collection<String> lore)
    {
        this.lore.addAll(index, lore);
        return this;
    }

    public ItemBuilder insertLoreLine(int index, String... lore)
    {
        this.insertLoreLine(index, Arrays.asList(lore));
        return this;
    }

    public ItemBuilder removeLoreLine(String lore)
    {
        this.lore.remove(lore);
        return this;
    }

    public ItemBuilder removeLoreLine(Collection<String> lore)
    {
        this.lore.removeAll(lore);
        return this;
    }

    public ItemBuilder removeLoreLine(String... lore)
    {
        this.removeLoreLine(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder removeLoreLine(int lore)
    {
        this.lore.remove(lore);
        return this;
    }

    public ItemBuilder setLoreLine(int index, String lore)
    {
        this.lore.set(index, lore);
        return this;
    }

    public ItemBuilder clearLore()
    {
        this.lore.clear();
        return this;
    }

    public ItemBuilder addAttribute(Attribute attribute)
    {
        this.attributes.add(attribute);
        return this;
    }

    public ItemBuilder addAttributes(Iterable<Attribute> attributes)
    {
        for (Attribute attribute : attributes)
        {
            this.attributes.add(attribute);
        }
        return this;
    }

    public ItemBuilder addAttributes(Attribute... attributes)
    {
        Collections.addAll(this.attributes, attributes);
        return this;
    }

    public ItemBuilder withAttributes(Iterable<Attribute> attributes)
    {
        this.attributes.clear();
        for (Attribute attribute : attributes)
        {
            this.attributes.add(attribute);
        }
        return this;
    }

    public ItemBuilder withAttributes(ItemStack item)
    {
        Attributes attributes = new Attributes(item.clone());
        return this.withAttributes(attributes.values());
    }

    public ItemBuilder clearAttributes()
    {
        this.attributes.clear();
        return this;
    }

    public ItemBuilder enchant(Map<Enchantment, Integer> enchants)
    {
        this.enchants = new LinkedHashMap<>(enchants);
        return this;
    }

    public ItemBuilder enchant(ItemMeta source)
    {
        this.enchants = source.hasEnchants() ? new LinkedHashMap<>(source.getEnchants()) : new LinkedHashMap<Enchantment, Integer>(3);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int power)
    {
        this.enchants.put(enchantment, power);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment)
    {
        this.enchant(enchantment, 1);
        return this;
    }

    public ItemBuilder unEnchant(Enchantment enchantment)
    {
        this.enchants.remove(enchantment);
        return this;
    }

    public ItemBuilder clearEnchants()
    {
        this.enchants.clear();
        return this;
    }

    public ItemBuilder data(DataBuilder dataBuilder)
    {
        this.dataBuilder = dataBuilder;
        return this;
    }

    public ItemBuilder data(ItemMeta meta)
    {
        // TODO: maybe find some way to do that more OOP
        if (meta instanceof BookMeta)
        {
            this.dataBuilder = new BookDataBuilder();
        }
        if (meta instanceof EnchantmentStorageMeta)
        {
            this.dataBuilder = new EnchantmentStorageBuilder();
        }
        if (meta instanceof FireworkEffectMeta)
        {
            this.dataBuilder = new FireworkEffectBuilder();
        }
        if (meta instanceof FireworkMeta)
        {
            this.dataBuilder = new FireworkBuilder();
        }
        if (meta instanceof LeatherArmorMeta)
        {
            this.dataBuilder = new LeatherArmorBuilder();
        }
        if (meta instanceof MapMeta)
        {
            this.dataBuilder = new MapBuilder();
        }
        if (meta instanceof PotionMeta)
        {
            this.dataBuilder = new PotionDataBuilder();
        }
        if (meta instanceof SkullMeta)
        {
            this.dataBuilder = new SkullBuilder();
        }
        if (this.dataBuilder != null)
        {
            this.dataBuilder.use(meta);
        }
        return this;
    }

    public ItemStack build()
    {
        ItemStack item = new ItemStack(this.material, this.amount, this.durability);
//        this.applyFunc();
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(this.material);
        if (this.name != null)
        {
            meta.setDisplayName(Utils.fixColors(this.name));
        }
        if ((this.flags != null) && ! this.flags.isEmpty())
        {
            meta.addItemFlags(this.flags.toArray(new ItemFlag[this.flags.size()]));
        }
        if ((this.lore != null) && ! this.lore.isEmpty())
        {
            List<String> lore = new ArrayList<>(this.lore.size() + 5);
            for (String loreLine : this.lore)
            {
                Collections.addAll(lore, loreLine.split("\n"));
            }
//            this.lore.stream().forEach(str -> Collections.addAll(lore, str.split("\n")));
            meta.setLore(Utils.fixColors(lore));
        }
        if (this.enchants != null)
        {
            for (Map.Entry<Enchantment, Integer> entry : this.enchants.entrySet())
            {
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
        }
        if (this.dataBuilder != null)
        {
            this.dataBuilder.apply(meta);
        }
        item.setItemMeta(meta);
        if (! this.attributes.isEmpty())
        {
            Attributes attributes = new Attributes(item);
            for (Attribute attribute : this.attributes)
            {
                attributes.add(attribute);
            }
            item = attributes.getStack();
        }
        return item;
    }

//    private void applyFunc()
//    {
//        if (this.func == null)
//        {
//            return;
//        }
//        if (this.name != null)
//        {
//            this.name = this.func.apply(this.name);
//        }
//        if ((this.lore != null) && ! this.lore.isEmpty())
//        {
//            this.lore = Stream.of(this.func.apply(StringUtils.join(this.lore, '\n')).split("\n")).filter(s -> ! s.equals("<NO-LINE>")).collect(Collectors
// .toList());
//        }
//        if (this.dataBuilder != null)
//        {
//            this.dataBuilder.applyFunc(this.func);
//        }
//    }

    public ItemBuilder reset()
    {
        this.material = Material.AIR;
        this.amount = 1;
        this.durability = 0;
        this.name = null;
        this.attributes.clear();
        if (this.flags != null)
        {
            this.flags.clear();
        }
        else
        {
            this.flags = new ArrayList<>(5);
        }
        if (this.lore != null)
        {
            this.lore.clear();
        }
        else
        {
            this.lore = new ArrayList<>(5);
        }
        if (this.enchants != null)
        {
            this.enchants.clear();
        }
        else
        {
            this.enchants = new LinkedHashMap<>(3);
        }
        this.dataBuilder = null;
        this.unbreakable = false;
        return this;
    }

    @Override
    public Map<String, Object> serialize()
    {
        SerializationBuilder b = SerializationBuilder.start(7);
        b.append("material", this.material);
        b.append("amount", this.amount);
        b.append("durability", this.durability);
        b.append("unbreakable", this.unbreakable);
        b.append("attributes", this.attributes.stream().map(Attribute::serialize).collect(Collectors.toList()));
        b.append("name", this.name);
        b.append("lore", this.lore);
        b.append("flags", (this.flags == null) ? new ArrayList<ItemFlag>(1) : this.flags.stream().map(Enum::name).collect(Collectors.toList()));
        SerializationBuilder enchant = SerializationBuilder.start(this.enchants.size());
        for (Map.Entry<Enchantment, Integer> entry : this.enchants.entrySet())
        {
            enchant.append(entry.getKey().getName(), entry.getValue());
        }
        b.append("enchants", enchant);
        b.append("data", (this.dataBuilder == null) ? null : this.dataBuilder.serialize());
        return b.build();
    }

    public static ItemBuilder newItem(Material material)
    {
        return new ItemBuilder().material(material);
    }

    public static ItemBuilder newItem(ItemStack itemStack)
    {
        if (itemStack == null)
        {
            return new ItemBuilder();
        }
        ItemBuilder itemBuilder = new ItemBuilder().material(itemStack).amount(itemStack).durability(itemStack).withAttributes(itemStack);
        ItemMeta meta = Utils.getItemMeta(itemStack);
        if (meta == null)
        {
            return itemBuilder;
        }
        return itemBuilder.name(meta).lore(meta).enchant(meta).flag(meta).unbreakable(meta).data(meta);
    }
}
