package com.gotofinal.darkrise.spigot.core.nms;

import javax.annotation.Nonnull;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.gotofinal.darkrise.spigot.core.nms.NbtFactory.NbtCompound;
import com.gotofinal.darkrise.spigot.core.nms.NbtFactory.NbtList;
import com.gotofinal.darkrise.spigot.core.utils.DeserializationWorker;
import com.gotofinal.darkrise.spigot.core.utils.SerializationBuilder;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import org.diorite.utils.math.DioriteRandomUtils;

public class Attributes
{
    public enum Slot
    {
        MAIN_HAND("mainhand"),
        OFF_HAND("offhand"),
        HEAD("head"),
        CHEST("chest"),
        LEGS("legs"),
        FEET("feet"),
        ANY(null);
        private final String value;

        Slot(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return this.value;
        }

        public static Slot getSlotFor(String slot)
        {
            if (slot == null)
            {
                return ANY;
            }
            for (Slot s : values())
            {
                if (slot.equalsIgnoreCase(s.name()) || slot.equals(s.value))
                {
                    return s;
                }
            }
            return ANY;
        }
    }

    public enum Operation
    {
        ADD_NUMBER(0),
        MULTIPLY_PERCENTAGE(1),
        ADD_PERCENTAGE(2);
        private final int id;

        Operation(int id)
        {
            this.id = id;
        }

        public int getId()
        {
            return this.id;
        }

        public static Operation fromId(int id)
        {
            // Linear scan is very fast for small N
            for (Operation op : values())
            {
                if (op.getId() == id)
                {
                    return op;
                }
            }
            throw new IllegalArgumentException("Corrupt operation ID " + id + " detected.");
        }
    }

    public static class AttributeType
    {
        private static final ConcurrentMap<String, AttributeType> LOOKUP                       = Maps.newConcurrentMap();
        public static final  AttributeType                        GENERIC_MAX_HEALTH           = new AttributeType("generic.maxHealth").register();
        public static final  AttributeType                        GENERIC_FOLLOW_RANGE         = new AttributeType("generic.followRange").register();
        public static final  AttributeType                        GENERIC_ATTACK_DAMAGE        = new AttributeType("generic.attackDamage").register();
        public static final  AttributeType                        GENERIC_MOVEMENT_SPEED       = new AttributeType("generic.movementSpeed").register();
        public static final  AttributeType                        GENERIC_KNOCKBACK_RESISTANCE = new AttributeType("generic.knockbackResistance").register();

        private final String minecraftId;

        /**
         * Construct a new attribute type.
         * <p>
         * Remember to {@link #register()} the type.
         *
         * @param minecraftId
         *         - the ID of the type.
         */
        public AttributeType(String minecraftId)
        {
            this.minecraftId = minecraftId;
        }

        /**
         * Retrieve the associated minecraft ID.
         *
         * @return The associated ID.
         */
        public String getMinecraftId()
        {
            return this.minecraftId;
        }

        /**
         * Register the type in the central registry.
         *
         * @return The registered type.
         */
        // Constructors should have no side-effects!
        public AttributeType register()
        {
            AttributeType old = LOOKUP.putIfAbsent(this.minecraftId, this);
            return (old != null) ? old : this;
        }

        /**
         * Retrieve the attribute type associated with a given ID.
         *
         * @param minecraftId
         *         The ID to search for.
         *
         * @return The attribute type, or NULL if not found.
         */
        public static AttributeType fromId(String minecraftId)
        {
            return LOOKUP.get(minecraftId);
        }

        /**
         * Retrieve every registered attribute type.
         *
         * @return Every type.
         */
        public static Iterable<AttributeType> values()
        {
            return LOOKUP.values();
        }
    }

    public static class Attribute implements ConfigurationSerializable
    {
        private final NbtCompound data;

        public Attribute(Map<String, Object> map)
        {
            this.data = NbtFactory.createCompound();
            DeserializationWorker dw = DeserializationWorker.start(map);
            this.setAmount(dw.getDouble("value", 1));
            this.setOperation(dw.getEnum("operation", Operation.ADD_PERCENTAGE));
            this.setSlot(Slot.getSlotFor(dw.getString("slot", Slot.ANY.name())));
            this.setAttributeType(AttributeType.fromId(dw.getString("type", AttributeType.GENERIC_MAX_HEALTH.getMinecraftId())));
            this.setName(dw.getString("name", this.getAttributeType().getMinecraftId() + ":" + DioriteRandomUtils.nextInt(1000)));
            this.setUUID(dw.getUUID("uuid", UUID.randomUUID()));
        }

        private Attribute(Builder builder)
        {
            this.data = NbtFactory.createCompound();
            this.setAmount(builder.amount);
            this.setOperation(builder.operation);
            this.setSlot(builder.slot);
            this.setAttributeType(builder.type);
            this.setName(builder.name);
            this.setUUID(builder.uuid);
        }

        private Attribute(NbtCompound data)
        {
            this.data = data;
        }

        public Slot getSlot()
        {
            return Slot.getSlotFor(this.data.getString("Slot", null));
        }

        public double getAmount()
        {
            return this.data.getDouble("Amount", 0.0);
        }

        public void setAmount(double amount)
        {
            this.data.put("Amount", amount);
        }

        public Operation getOperation()
        {
            return Operation.fromId(this.data.getInteger("Operation", 0));
        }

        public void setSlot(Slot slot)
        {
            if (slot.value == null)
            {
                this.data.remove("Slot");
            }
            else
            {
                this.data.put("Slot", slot.value);
            }
        }

        public void setOperation(@Nonnull Operation operation)
        {
            Preconditions.checkNotNull(operation, "operation cannot be NULL.");
            this.data.put("Operation", operation.getId());
        }

        public AttributeType getAttributeType()
        {
            return AttributeType.fromId(this.data.getString("AttributeName", null));
        }

        public void setAttributeType(@Nonnull AttributeType type)
        {
            Preconditions.checkNotNull(type, "type cannot be NULL.");
            this.data.put("AttributeName", type.getMinecraftId());
        }

        public String getName()
        {
            return this.data.getString("Name", null);
        }

        public void setName(@Nonnull String name)
        {
            Preconditions.checkNotNull(name, "name cannot be NULL.");
            this.data.put("Name", name);
        }

        public UUID getUUID()
        {
            return new UUID(this.data.getLong("UUIDMost", null), this.data.getLong("UUIDLeast", null));
        }

        public void setUUID(@Nonnull UUID id)
        {
            Preconditions.checkNotNull("id", "id cannot be NULL.");
            this.data.put("UUIDLeast", id.getLeastSignificantBits());
            this.data.put("UUIDMost", id.getMostSignificantBits());
        }

        @Override
        public Map<String, Object> serialize()
        {
            return SerializationBuilder.start(6).append("type", this.getAttributeType().getMinecraftId()).append("operation", this.getOperation())
                                       .append("value", this.getAmount()).append("slot", this.getSlot())
                                       .append("name", this.getName()).append("uuid", this.getUUID().toString()).build();
        }

        /**
         * Construct a new attribute builder with a random UUID and default operation of adding numbers.
         *
         * @return The attribute builder.
         */
        public static Builder newBuilder()
        {
            return new Builder().uuid(UUID.randomUUID()).operation(Operation.ADD_NUMBER);
        }

        // Makes it easier to construct an attribute
        public static class Builder
        {
            private double amount;
            private Operation operation = Operation.ADD_NUMBER;
            private Slot      slot      = Slot.ANY;
            private AttributeType type;
            private String        name;
            private UUID          uuid;

            private Builder()
            {
                // Don't make this accessible
            }

            public Builder amount(double amount)
            {
                this.amount = amount;
                return this;
            }

            public Builder operation(Operation operation)
            {
                this.operation = operation;
                return this;
            }

            public Builder slot(Slot slot)
            {
                this.slot = slot;
                return this;
            }

            public Builder type(AttributeType type)
            {
                this.type = type;
                return this;
            }

            public Builder name(String name)
            {
                this.name = name;
                return this;
            }

            public Builder uuid(UUID uuid)
            {
                this.uuid = uuid;
                return this;
            }

            public Attribute build()
            {
                return new Attribute(this);
            }
        }
    }

    // This may be modified
    public final  ItemStack stack;
    private final NbtList   attributes;

    public Attributes(ItemStack stack)
    {
        // Create a CraftItemStack (under the hood)
        this.stack = NbtFactory.getCraftItemStack(stack);

        // Load NBT
        NbtCompound nbt = NbtFactory.fromItemTag(this.stack);
        this.attributes = nbt.getList("AttributeModifiers", true);
    }

    /**
     * Retrieve the modified item stack.
     *
     * @return The modified item stack.
     */
    public ItemStack getStack()
    {
        return this.stack;
    }

    /**
     * Retrieve the number of attributes.
     *
     * @return Number of attributes.
     */
    public int size()
    {
        return this.attributes.size();
    }

    /**
     * Add a new attribute to the list.
     *
     * @param attribute
     *         - the new attribute.
     */
    public void add(Attribute attribute)
    {
        Preconditions.checkNotNull(attribute.getName(), "must specify an attribute name.");
        this.attributes.add(attribute.data);
    }

    /**
     * Remove the first instance of the given attribute.
     * <p>
     * The attribute will be removed using its UUID.
     *
     * @param attribute
     *         - the attribute to remove.
     *
     * @return TRUE if the attribute was removed, FALSE otherwise.
     */
    public boolean remove(Attribute attribute)
    {
        UUID uuid = attribute.getUUID();

        for (Iterator<Attribute> it = this.values().iterator(); it.hasNext(); )
        {
            if (Objects.equal(it.next().getUUID(), uuid))
            {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public void clear()
    {
        this.attributes.clear();
    }

    /**
     * Retrieve the attribute at a given index.
     *
     * @param index
     *         - the index to look up.
     *
     * @return The attribute at that index.
     */
    public Attribute get(int index)
    {
        return new Attribute((NbtCompound) this.attributes.get(index));
    }

    // We can't make Attributes itself iterable without splitting it up into separate classes
    public Iterable<Attribute> values()
    {
        return () -> Iterators.transform(Attributes.this.attributes.iterator(), element -> new Attribute((NbtCompound) element));
    }
}