package com.gotofinal.darkrise.core.nms;


import javax.annotation.Nonnull;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.gotofinal.darkrise.core.nms.NbtFactory.NbtCompound;
import com.gotofinal.darkrise.core.nms.NbtFactory.NbtList;

import org.bukkit.inventory.ItemStack;

public class Attributes
{
    public enum Operation
    {
        ADD_NUMBER(0),
        MULTIPLY_PERCENTAGE(1),
        ADD_PERCENTAGE(2);
        private final int id;

        Operation(final int id)
        {
            this.id = id;
        }

        public int getId()
        {
            return this.id;
        }

        public static Operation fromId(final int id)
        {
            // Linear scan is very fast for small N
            for (final Operation op : values())
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
        private static final ConcurrentMap<String, AttributeType> LOOKUP                = Maps.newConcurrentMap();
        public static final  AttributeType                        GENERIC_MAX_HEALTH    = new AttributeType("generic.maxHealth").register();
        public static final  AttributeType                        GENERIC_FOLLOW_RANGE  = new AttributeType("generic.followRange").register();
        public static final  AttributeType                        GENERIC_ATTACK_DAMAGE = new AttributeType("generic.attackDamage").register();
        public static final AttributeType                        GENERIC_MOVEMENT_SPEED       = new AttributeType("generic.movementSpeed").register();
        public static final AttributeType                        GENERIC_KNOCKBACK_RESISTANCE = new AttributeType("generic.knockbackResistance").register();

        private final String minecraftId;

        /**
         * Construct a new attribute type.
         * <p>
         * Remember to {@link #register()} the type.
         *
         * @param minecraftId - the ID of the type.
         */
        public AttributeType(final String minecraftId)
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
            final AttributeType old = LOOKUP.putIfAbsent(this.minecraftId, this);
            return (old != null) ? old : this;
        }

        /**
         * Retrieve the attribute type associated with a given ID.
         *
         * @param minecraftId The ID to search for.
         *
         * @return The attribute type, or NULL if not found.
         */
        public static AttributeType fromId(final String minecraftId)
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

    public static class Attribute
    {
        private final NbtCompound data;

        private Attribute(final Builder builder)
        {
            this.data = NbtFactory.createCompound();
            this.setAmount(builder.amount);
            this.setOperation(builder.operation);
            this.setAttributeType(builder.type);
            this.setName(builder.name);
            this.setUUID(builder.uuid);
        }

        private Attribute(final NbtCompound data)
        {
            this.data = data;
        }

        public double getAmount()
        {
            return this.data.getDouble("Amount", 0.0);
        }

        public void setAmount(final double amount)
        {
            this.data.put("Amount", amount);
        }

        public Operation getOperation()
        {
            return Operation.fromId(this.data.getInteger("Operation", 0));
        }

        public void setOperation(@Nonnull final Operation operation)
        {
            Preconditions.checkNotNull(operation, "operation cannot be NULL.");
            this.data.put("Operation", operation.getId());
        }

        public AttributeType getAttributeType()
        {
            return AttributeType.fromId(this.data.getString("AttributeName", null));
        }

        public void setAttributeType(@Nonnull final AttributeType type)
        {
            Preconditions.checkNotNull(type, "type cannot be NULL.");
            this.data.put("AttributeName", type.getMinecraftId());
        }

        public String getName()
        {
            return this.data.getString("Name", null);
        }

        public void setName(@Nonnull final String name)
        {
            Preconditions.checkNotNull(name, "name cannot be NULL.");
            this.data.put("Name", name);
        }

        public UUID getUUID()
        {
            return new UUID(this.data.getLong("UUIDMost", null), this.data.getLong("UUIDLeast", null));
        }

        public void setUUID(@Nonnull final UUID id)
        {
            Preconditions.checkNotNull("id", "id cannot be NULL.");
            this.data.put("UUIDLeast", id.getLeastSignificantBits());
            this.data.put("UUIDMost", id.getMostSignificantBits());
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
            private AttributeType type;
            private String        name;
            private UUID          uuid;

            private Builder()
            {
                // Don't make this accessible
            }

            public Builder amount(final double amount)
            {
                this.amount = amount;
                return this;
            }

            public Builder operation(final Operation operation)
            {
                this.operation = operation;
                return this;
            }

            public Builder type(final AttributeType type)
            {
                this.type = type;
                return this;
            }

            public Builder name(final String name)
            {
                this.name = name;
                return this;
            }

            public Builder uuid(final UUID uuid)
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

    public Attributes(final ItemStack stack)
    {
        // Create a CraftItemStack (under the hood)
        this.stack = NbtFactory.getCraftItemStack(stack);

        // Load NBT
        final NbtCompound nbt = NbtFactory.fromItemTag(this.stack);
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
     * @param attribute - the new attribute.
     */
    public void add(final Attribute attribute)
    {
        Preconditions.checkNotNull(attribute.getName(), "must specify an attribute name.");
        this.attributes.add(attribute.data);
    }

    /**
     * Remove the first instance of the given attribute.
     * <p>
     * The attribute will be removed using its UUID.
     *
     * @param attribute - the attribute to remove.
     *
     * @return TRUE if the attribute was removed, FALSE otherwise.
     */
    public boolean remove(final Attribute attribute)
    {
        final UUID uuid = attribute.getUUID();

        for (final Iterator<Attribute> it = this.values().iterator(); it.hasNext(); )
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
     * @param index - the index to look up.
     *
     * @return The attribute at that index.
     */
    public Attribute get(final int index)
    {
        return new Attribute((NbtCompound) this.attributes.get(index));
    }

    // We can't make Attributes itself iterable without splitting it up into separate classes
    public Iterable<Attribute> values()
    {
        return () -> Iterators.transform(Attributes.this.attributes.iterator(), element -> new Attribute((NbtCompound) element));
    }
}