package com.gotofinal.darkrise.core.nms;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.common.io.OutputSupplier;
import com.google.common.primitives.Primitives;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class NbtFactory
{
    // Convert between NBT id and the equivalent class in java
    private static final BiMap<Integer, Class<?>> NBT_CLASS = HashBiMap.create();
    private static final BiMap<Integer, NbtType>  NBT_ENUM  = HashBiMap.create();

    /**
     * Whether or not to enable stream compression.
     *
     * @author Kristian
     */
    public enum StreamOptions
    {
        NO_COMPRESSION,
        GZIP_COMPRESSION,
    }

    private enum NbtType
    {
        TAG_END(0, Void.class),
        TAG_BYTE(1, byte.class),
        TAG_SHORT(2, short.class),
        TAG_INT(3, int.class),
        TAG_LONG(4, long.class),
        TAG_FLOAT(5, float.class),
        TAG_DOUBLE(6, double.class),
        TAG_BYTE_ARRAY(7, byte[].class),
        TAG_INT_ARRAY(11, int[].class),
        TAG_STRING(8, String.class),
        TAG_LIST(9, List.class),
        TAG_COMPOUND(10, Map.class);

        // Unique NBT id
        public final int id;

        NbtType(final int id, final Class<?> type)
        {
            this.id = id;
            NBT_CLASS.put(id, type);
            NBT_ENUM.put(id, this);
        }

        private String getFieldName()
        {
            if (this == TAG_COMPOUND)
            {
                return "map";
            }
            else if (this == TAG_LIST)
            {
                return "list";
            }
            else
            {
                return "data";
            }
        }
    }

    // The NBT base class
    private Class<?> BASE_CLASS;
    private Class<?> STREAM_TOOLS;
    private Class<?> READ_LIMITER_CLASS;
    private Method   NBT_CREATE_TAG;
    private Method   NBT_GET_TYPE;
    private Field    NBT_LIST_TYPE;
    private final Field[] DATA_FIELD = new Field[12];

    // CraftItemStack
    private Class<?> CRAFT_STACK;
    private Field    CRAFT_HANDLE;
    private Field    STACK_TAG;

    // Loading/saving compounds
    private LoadCompoundMethod LOAD_COMPOUND;
    private Method             SAVE_COMPOUND;

    // Shared instance
    private static NbtFactory INSTANCE;

    /**
     * Represents a root NBT compound.
     * <p>
     * All changes to this map will be reflected in the underlying NBT compound. Values may only be one of the following:
     * <ul>
     * <li>Primitive types</li>
     * <li>{@link String String}</li>
     * <li>{@link NbtList}</li>
     * <li>{@link NbtCompound}</li>
     * </ul>
     * </p>
     * See also:
     * <ul>
     * <li>{@link NbtFactory#createCompound()}</li>
     * <li>{@link NbtFactory#fromCompound(Object)}</li>
     * </ul>
     *
     * @author Kristian
     */
    public final class NbtCompound extends ConvertedMap
    {
        private NbtCompound(final Object handle)
        {
            super(handle, NbtFactory.this.getDataMap(handle));
        }

        // Simplifiying access to each value
        public Byte getByte(final String key, final Byte defaultValue)
        {
            return this.containsKey(key) ? (Byte) this.get(key) : defaultValue;
        }

        public Short getShort(final String key, final Short defaultValue)
        {
            return this.containsKey(key) ? (Short) this.get(key) : defaultValue;
        }

        public Integer getInteger(final String key, final Integer defaultValue)
        {
            return this.containsKey(key) ? (Integer) this.get(key) : defaultValue;
        }

        public Long getLong(final String key, final Long defaultValue)
        {
            return this.containsKey(key) ? (Long) this.get(key) : defaultValue;
        }

        public Float getFloat(final String key, final Float defaultValue)
        {
            return this.containsKey(key) ? (Float) this.get(key) : defaultValue;
        }

        public Double getDouble(final String key, final Double defaultValue)
        {
            return this.containsKey(key) ? (Double) this.get(key) : defaultValue;
        }

        public String getString(final String key, final String defaultValue)
        {
            return this.containsKey(key) ? (String) this.get(key) : defaultValue;
        }

        public byte[] getByteArray(final String key, final byte[] defaultValue)
        {
            return this.containsKey(key) ? (byte[]) this.get(key) : defaultValue;
        }

        public int[] getIntegerArray(final String key, final int[] defaultValue)
        {
            return this.containsKey(key) ? (int[]) this.get(key) : defaultValue;
        }

        /**
         * Retrieve the list by the given name.
         *
         * @param key       - the name of the list.
         * @param createNew - whether or not to create a new list if its missing.
         *
         * @return An existing list, a new list or NULL.
         */
        public NbtList getList(final String key, final boolean createNew)
        {
            NbtList list = (NbtList) this.get(key);

            if ((list == null) && createNew)
            {
                this.put(key, list = createList());
            }
            return list;
        }

        /**
         * Retrieve the map by the given name.
         *
         * @param key       - the name of the map.
         * @param createNew - whether or not to create a new map if its missing.
         *
         * @return An existing map, a new map or NULL.
         */
        public NbtCompound getMap(final String key, final boolean createNew)
        {
            return this.getMap(Collections.singletonList(key), createNew);
        }
        // Done

        /**
         * Set the value of an entry at a given location.
         * <p>
         * Every element of the path (except the end) are assumed to be compounds, and will
         * be created if they are missing.
         *
         * @param path  - the path to the entry.
         * @param value - the new value of this entry.
         *
         * @return This compound, for chaining.
         */
        public NbtCompound putPath(final String path, final Object value)
        {
            final List<String> entries = this.getPathElements(path);
            final Map<String, Object> map = this.getMap(entries.subList(0, entries.size() - 1), true);

            map.put(entries.get(entries.size() - 1), value);
            return this;
        }

        /**
         * Retrieve the value of a given entry in the tree.
         * <p>
         * Every element of the path (except the end) are assumed to be compounds. The
         * retrieval operation will be cancelled if any of them are missing.
         *
         * @param path - path to the entry.
         *
         * @return The value, or NULL if not found.
         */
        @SuppressWarnings("unchecked")
        public <T> T getPath(final String path)
        {
            final List<String> entries = this.getPathElements(path);
            final NbtCompound map = this.getMap(entries.subList(0, entries.size() - 1), false);

            if (map != null)
            {
                return (T) map.get(entries.get(entries.size() - 1));
            }
            return null;
        }

        /**
         * Save the content of a NBT compound to a stream.
         * <p>
         * Use {@link Files#newOutputStreamSupplier(java.io.File)} to provide a stream supplier to a file.
         *
         * @param stream - the output stream.
         * @param option - whether or not to compress the output.
         *
         * @throws IOException If anything went wrong.
         */
        public void saveTo(final OutputSupplier<? extends OutputStream> stream, final StreamOptions option) throws IOException
        {
            saveStream(this, stream, option);
        }

        /**
         * Retrieve a map from a given path.
         *
         * @param path      - path of compounds to look up.
         * @param createNew - whether or not to create new compounds on the way.
         *
         * @return The map at this location.
         */
        private NbtCompound getMap(final Iterable<String> path, final boolean createNew)
        {
            NbtCompound current = this;

            for (final String entry : path)
            {
                NbtCompound child = (NbtCompound) current.get(entry);

                if (child == null)
                {
                    if (! createNew)
                    {
                        return null;
                    }
                    current.put(entry, child = createCompound());
                }
                current = child;
            }
            return current;
        }

        /**
         * Split the path into separate elements.
         *
         * @param path - the path to split.
         *
         * @return The elements.
         */
        private List<String> getPathElements(final String path)
        {
            return Lists.newArrayList(Splitter.on(".").omitEmptyStrings().split(path));
        }
    }

    /**
     * Represents a root NBT list.
     * See also:
     * <ul>
     * <li>{@link NbtFactory#createNbtList()}</li>
     * <li>{@link NbtFactory#fromList(Object)}</li>
     * </ul>
     *
     * @author Kristian
     */
    public final class NbtList extends ConvertedList
    {
        private NbtList(final Object handle)
        {
            super(handle, NbtFactory.this.getDataList(handle));
        }
    }

    /**
     * Represents an object that provides a view of a native NMS class.
     *
     * @author Kristian
     */
    @FunctionalInterface
    public interface Wrapper
    {
        /**
         * Retrieve the underlying native NBT tag.
         *
         * @return The underlying NBT.
         */
        Object getHandle();
    }

    /**
     * Retrieve or construct a shared NBT factory.
     *
     * @return The factory.
     */
    private static NbtFactory get()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new NbtFactory();
        }
        return INSTANCE;
    }

    /**
     * Construct an instance of the NBT factory by deducing the class of NBTBase.
     */
    private NbtFactory()
    {
        if (this.BASE_CLASS == null)
        {
            try
            {
                // Keep in mind that I do use hard-coded field names - but it's okay as long as we're dealing
                // with CraftBukkit or its derivatives. This does not work in MCPC+ however.
                final ClassLoader loader = NbtFactory.class.getClassLoader();

                final String packageName = this.getPackageName();
                final Class<?> offlinePlayer = loader.loadClass(packageName + ".CraftOfflinePlayer");

                // Prepare NBT
                final Class<?> COMPOUND_CLASS = getMethod(0, Modifier.STATIC, offlinePlayer, "getData").getReturnType();
                this.BASE_CLASS = COMPOUND_CLASS.getSuperclass();
                this.NBT_GET_TYPE = getMethod(0, Modifier.STATIC, this.BASE_CLASS, "getTypeId");
                this.NBT_CREATE_TAG = getMethod(Modifier.STATIC, 0, this.BASE_CLASS, "createTag", byte.class);

                // Prepare CraftItemStack
                this.CRAFT_STACK = loader.loadClass(packageName + ".inventory.CraftItemStack");
                this.CRAFT_HANDLE = getField(null, this.CRAFT_STACK, "handle");
                this.STACK_TAG = getField(null, this.CRAFT_HANDLE.getType(), "tag");

                // Loading/saving
                final String nmsPackage = this.BASE_CLASS.getPackage().getName();
                this.initializeNMS(loader, nmsPackage);

                this.LOAD_COMPOUND = (this.READ_LIMITER_CLASS != null) ? new LoadMethodSkinUpdate(this.STREAM_TOOLS, this.READ_LIMITER_CLASS) : new LoadMethodWorldUpdate(this.STREAM_TOOLS);
                this.SAVE_COMPOUND = getMethod(Modifier.STATIC, 0, this.STREAM_TOOLS, null, this.BASE_CLASS, DataOutput.class);

            } catch (final ClassNotFoundException e)
            {
                throw new IllegalStateException("Unable to find offline player.", e);
            }
        }
    }

    private void initializeNMS(final ClassLoader loader, final String nmsPackage)
    {
        try
        {
            this.STREAM_TOOLS = loader.loadClass(nmsPackage + ".NBTCompressedStreamTools");
            this.READ_LIMITER_CLASS = loader.loadClass(nmsPackage + ".NBTReadLimiter");
        } catch (final ClassNotFoundException e)
        {
            // Ignore - we will detect this later
        }
    }

    private String getPackageName()
    {
        final Server server = Bukkit.getServer();
        final String name = (server != null) ? server.getClass().getPackage().getName() : null;

        if ((name != null) && name.contains("craftbukkit"))
        {
            return name;
        }
        else
        {
            // Fallback
            return "org.bukkit.craftbukkit.v1_7_R3";
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getDataMap(final Object handle)
    {
        return (Map<String, Object>) getFieldValue(this.getDataField(NbtType.TAG_COMPOUND, handle), handle);
    }

    @SuppressWarnings("unchecked")
    private List<Object> getDataList(final Object handle)
    {
        return (List<Object>) getFieldValue(this.getDataField(NbtType.TAG_LIST, handle), handle);
    }

    /**
     * Construct a new NBT list of an unspecified type.
     *
     * @return The NBT list.
     */
    public static NbtList createList(final Object... content)
    {
        return createList(Arrays.asList(content));
    }

    /**
     * Construct a new NBT list of an unspecified type.
     *
     * @return The NBT list.
     */
    public static NbtList createList(final Iterable<?> iterable)
    {
        final NbtList list = get().new NbtList(INSTANCE.createNbtTag(NbtType.TAG_LIST, null));

        // Add the content as well
        for (final Object obj : iterable)
        {
            list.add(obj);
        }
        return list;
    }

    /**
     * Construct a new NBT compound.
     * <p>
     * Use {@link NbtCompound#asMap()} to modify it.
     *
     * @return The NBT compound.
     */
    public static NbtCompound createCompound()
    {
        return get().new NbtCompound(INSTANCE.createNbtTag(NbtType.TAG_COMPOUND, null));
    }

    /**
     * Construct a new NBT wrapper from a list.
     *
     * @param nmsList - the NBT list.
     *
     * @return The wrapper.
     */
    public static NbtList fromList(final Object nmsList)
    {
        return get().new NbtList(nmsList);
    }

    /**
     * Load the content of a file from a stream.
     * <p>
     * Use {@link Files#newInputStreamSupplier(java.io.File)} to provide a stream from a file.
     *
     * @param stream - the stream supplier.
     * @param option - whether or not to decompress the input stream.
     *
     * @return The decoded NBT compound.
     *
     * @throws IOException If anything went wrong.
     */
    public static NbtCompound fromStream(final Supplier<? extends InputStream> stream, final StreamOptions option) throws IOException
    {
        try (InputStream input = stream.get(); DataInputStream data = new DataInputStream(new BufferedInputStream((option == StreamOptions.GZIP_COMPRESSION) ? new GZIPInputStream(input) : input)))
        {
            return fromCompound(get().LOAD_COMPOUND.loadNbt(data));
        }
    }

    /**
     * Save the content of a NBT compound to a stream.
     * <p>
     * Use {@link Files#newOutputStreamSupplier(java.io.File)} to provide a stream supplier to a file.
     *
     * @param source - the NBT compound to save.
     * @param stream - the stream.
     * @param option - whether or not to compress the output.
     *
     * @throws IOException If anything went wrong.
     */
    public static void saveStream(final NbtCompound source, final OutputSupplier<? extends OutputStream> stream, final StreamOptions option) throws IOException
    {
        OutputStream output = null;
        DataOutputStream data = null;
        boolean suppress = true;

        try
        {
            output = stream.getOutput();
            data = new DataOutputStream((option == StreamOptions.GZIP_COMPRESSION) ? new GZIPOutputStream(output) : output);

            invokeMethod(get().SAVE_COMPOUND, null, source.getHandle(), data);
            suppress = false;

        } finally
        {
            if (data != null)
            {
                Closeables.close(data, suppress);
            }
            else if (output != null)
            {
                Closeables.close(output, suppress);
            }
        }
    }

    /**
     * Construct a new NBT wrapper from a compound.
     *
     * @param nmsCompound - the NBT compund.
     *
     * @return The wrapper.
     */
    public static NbtCompound fromCompound(final Object nmsCompound)
    {
        return get().new NbtCompound(nmsCompound);
    }

    /**
     * Set the NBT compound tag of a given item stack.
     * <p>
     * The item stack must be a wrapper for a CraftItemStack. Use
     * {@link MinecraftReflection#getBukkitItemStack(ItemStack)} if not.
     *
     * @param stack    - the item stack, cannot be air.
     * @param compound - the new NBT compound, or NULL to remove it.
     *
     * @throws IllegalArgumentException If the stack is not a CraftItemStack, or it represents air.
     */
    public static void setItemTag(final ItemStack stack, final NbtCompound compound)
    {
        checkItemStack(stack);
        final Object nms = getFieldValue(get().CRAFT_HANDLE, stack);

        // Now update the tag compound
        setFieldValue(get().STACK_TAG, nms, compound.getHandle());
    }

    /**
     * Construct a wrapper for an NBT tag stored (in memory) in an item stack. This is where
     * auxillary data such as enchanting, name and lore is stored. It does not include items
     * material, damage value or count.
     * <p>
     * The item stack must be a wrapper for a CraftItemStack.
     *
     * @param stack - the item stack.
     *
     * @return A wrapper for its NBT tag.
     */
    public static NbtCompound fromItemTag(final ItemStack stack)
    {
        checkItemStack(stack);
        final Object nms = getFieldValue(get().CRAFT_HANDLE, stack);
        final Object tag = getFieldValue(get().STACK_TAG, nms);

        // Create the tag if it doesn't exist
        if (tag == null)
        {
            final NbtCompound compound = createCompound();
            setItemTag(stack, compound);
            return compound;
        }
        return fromCompound(tag);
    }

    /**
     * Retrieve a CraftItemStack version of the stack.
     *
     * @param stack - the stack to convert.
     *
     * @return The CraftItemStack version.
     */
    public static ItemStack getCraftItemStack(final ItemStack stack)
    {
        // Any need to convert?
        if ((stack == null) || get().CRAFT_STACK.isAssignableFrom(stack.getClass()))
        {
            return stack;
        }
        try
        {
            // Call the private constructor
            final Constructor<?> caller = INSTANCE.CRAFT_STACK.getDeclaredConstructor(ItemStack.class);
            caller.setAccessible(true);
            return (ItemStack) caller.newInstance(stack);
        } catch (final Exception e)
        {
            throw new IllegalStateException("Unable to convert " + stack + " + to a CraftItemStack.");
        }
    }

    /**
     * Ensure that the given stack can store arbitrary NBT information.
     *
     * @param stack - the stack to check.
     */
    private static void checkItemStack(final ItemStack stack)
    {
        if (stack == null)
        {
            throw new IllegalArgumentException("Stack cannot be NULL.");
        }
        if (! get().CRAFT_STACK.isAssignableFrom(stack.getClass()))
        {
            throw new IllegalArgumentException("Stack must be a CraftItemStack.");
        }
        if (stack.getType() == Material.AIR)
        {
            throw new IllegalArgumentException("ItemStacks representing air cannot store NMS information.");
        }
    }

    /**
     * Convert wrapped List and Map objects into their respective NBT counterparts.
     *
     * @param name  - the name of the NBT element to create.
     * @param value - the value of the element to create. Can be a List or a Map.
     *
     * @return The NBT element.
     */
    private Object unwrapValue(final Object value)
    {
        if (value == null)
        {
            return null;
        }

        if (value instanceof Wrapper)
        {
            return ((Wrapper) value).getHandle();

        }
        else if (value instanceof List)
        {
            throw new IllegalArgumentException("Can only insert a WrappedList.");
        }
        else if (value instanceof Map)
        {
            throw new IllegalArgumentException("Can only insert a WrappedCompound.");

        }
        else
        {
            return this.createNbtTag(this.getPrimitiveType(value), value);
        }
    }

    /**
     * Convert a given NBT element to a primitive wrapper or List/Map equivalent.
     * <p>
     * All changes to any mutable objects will be reflected in the underlying NBT element(s).
     *
     * @param nms - the NBT element.
     *
     * @return The wrapper equivalent.
     */
    private Object wrapNative(final Object nms)
    {
        if (nms == null)
        {
            return null;
        }

        if (this.BASE_CLASS.isAssignableFrom(nms.getClass()))
        {
            final NbtType type = this.getNbtType(nms);

            // Handle the different types
            switch (type)
            {
                case TAG_COMPOUND:
                    return new NbtCompound(nms);
                case TAG_LIST:
                    return new NbtList(nms);
                default:
                    return getFieldValue(this.getDataField(type, nms), nms);
            }
        }
        throw new IllegalArgumentException("Unexpected type: " + nms);
    }

    /**
     * Construct a new NMS NBT tag initialized with the given value.
     *
     * @param type  - the NBT type.
     * @param value - the value, or NULL to keep the original value.
     *
     * @return The created tag.
     */
    private Object createNbtTag(final NbtType type, final Object value)
    {
        final Object tag = invokeMethod(this.NBT_CREATE_TAG, null, (byte) type.id);

        if (value != null)
        {
            setFieldValue(this.getDataField(type, tag), tag, value);
        }
        return tag;
    }

    /**
     * Retrieve the field where the NBT class stores its value.
     *
     * @param type - the NBT type.
     * @param nms  - the NBT class instance.
     *
     * @return The corresponding field.
     */
    private Field getDataField(final NbtType type, final Object nms)
    {
        if (this.DATA_FIELD[type.id] == null)
        {
            this.DATA_FIELD[type.id] = getField(nms, null, type.getFieldName());
        }
        return this.DATA_FIELD[type.id];
    }

    /**
     * Retrieve the NBT type from a given NMS NBT tag.
     *
     * @param nms - the native NBT tag.
     *
     * @return The corresponding type.
     */
    private NbtType getNbtType(final Object nms)
    {
        final int type = (Byte) invokeMethod(this.NBT_GET_TYPE, nms);
        return NBT_ENUM.get(type);
    }

    /**
     * Retrieve the nearest NBT type for a given primitive type.
     *
     * @param primitive - the primitive type.
     *
     * @return The corresponding type.
     */
    private NbtType getPrimitiveType(final Object primitive)
    {
        final NbtType type = NBT_ENUM.get(NBT_CLASS.inverse().get(Primitives.unwrap(primitive.getClass())));

        // Display the illegal value at least
        if (type == null)
        {
            throw new IllegalArgumentException(String.format("Illegal type: %s (%s)", primitive.getClass(), primitive));
        }
        return type;
    }

    /**
     * Invoke a method on the given target instance using the provided parameters.
     *
     * @param method - the method to invoke.
     * @param target - the target.
     * @param params - the parameters to supply.
     *
     * @return The result of the method.
     */
    private static Object invokeMethod(final Method method, final Object target, final Object... params)
    {
        try
        {
            return method.invoke(target, params);
        } catch (final Exception e)
        {
            throw new RuntimeException("Unable to invoke method " + method + " for " + target, e);
        }
    }

    private static void setFieldValue(final Field field, final Object target, final Object value)
    {
        try
        {
            field.set(target, value);
        } catch (final Exception e)
        {
            throw new RuntimeException("Unable to set " + field + " for " + target, e);
        }
    }

    private static Object getFieldValue(final Field field, final Object target)
    {
        try
        {
            return field.get(target);
        } catch (final Exception e)
        {
            throw new RuntimeException("Unable to retrieve " + field + " for " + target, e);
        }
    }

    /**
     * Search for the first publically and privately defined method of the given name and parameter count.
     *
     * @param requireMod - modifiers that are required.
     * @param bannedMod  - modifiers that are banned.
     * @param clazz      - a class to start with.
     * @param methodName - the method name, or NULL to skip.
     * @param params     - the expected parameters.
     *
     * @return The first method by this name.
     *
     * @throws IllegalStateException If we cannot find this method.
     */
    private static Method getMethod(final int requireMod, final int bannedMod, final Class<?> clazz, final String methodName, final Class<?>... params)
    {
        for (final Method method : clazz.getDeclaredMethods())
        {
            // Limitation: Doesn't handle overloads
            if (((method.getModifiers() & requireMod) == requireMod) &&
                        ((method.getModifiers() & bannedMod) == 0) &&
                        ((methodName == null) || method.getName().equals(methodName)) &&
                        Arrays.equals(method.getParameterTypes(), params))
            {

                method.setAccessible(true);
                return method;
            }
        }
        // Search in every superclass
        if (clazz.getSuperclass() != null)
        {
            return getMethod(requireMod, bannedMod, clazz.getSuperclass(), methodName, params);
        }
        throw new IllegalStateException(String.format("Unable to find method %s (%s).", methodName, Arrays.asList(params)));
    }

    /**
     * Search for the first publically and privately defined field of the given name.
     *
     * @param instance  - an instance of the class with the field.
     * @param clazz     - an optional class to start with, or NULL to deduce it from instance.
     * @param fieldName - the field name.
     *
     * @return The first field by this name.
     *
     * @throws IllegalStateException If we cannot find this field.
     */
    private static Field getField(final Object instance, Class<?> clazz, final String fieldName)
    {
        if (clazz == null)
        {
            clazz = instance.getClass();
        }
        // Ignore access rules
        for (final Field field : clazz.getDeclaredFields())
        {
            if (field.getName().equals(fieldName))
            {
                field.setAccessible(true);
                return field;
            }
        }
        // Recursively fild the correct field
        if (clazz.getSuperclass() != null)
        {
            return getField(instance, clazz.getSuperclass(), fieldName);
        }
        throw new IllegalStateException("Unable to find field " + fieldName + " in " + instance);
    }

    /**
     * Represents a class for caching wrappers.
     *
     * @author Kristian
     */
    private final class CachedNativeWrapper
    {
        // Don't recreate wrapper objects
        private final ConcurrentMap<Object, Object> cache = new MapMaker().weakKeys().makeMap();

        public Object wrap(final Object value)
        {
            Object current = this.cache.get(value);

            if (current == null)
            {
                current = NbtFactory.this.wrapNative(value);

                // Only cache composite objects
                if ((current instanceof ConvertedMap) || (current instanceof ConvertedList))
                {
                    this.cache.put(value, current);
                }
            }
            return current;
        }
    }

    /**
     * Represents a map that wraps another map and automatically
     * converts entries of its type and another exposed type.
     *
     * @author Kristian
     */
    private class ConvertedMap extends AbstractMap<String, Object> implements Wrapper
    {
        private final Object              handle;
        private final Map<String, Object> original;

        private final CachedNativeWrapper cache = new CachedNativeWrapper();

        public ConvertedMap(final Object handle, final Map<String, Object> original)
        {
            this.handle = handle;
            this.original = original;
        }

        // For converting back and forth
        protected Object wrapOutgoing(final Object value)
        {
            return this.cache.wrap(value);
        }

        protected Object unwrapIncoming(final Object wrapped)
        {
            return NbtFactory.this.unwrapValue(wrapped);
        }

        // Modification
        @Override
        public Object put(final String key, final Object value)
        {
            return this.wrapOutgoing(this.original.put((String) key, this.unwrapIncoming(value)));
        }

        // Performance
        @Override
        public Object get(final Object key)
        {
            return this.wrapOutgoing(this.original.get(key));
        }

        @Override
        public Object remove(final Object key)
        {
            return this.wrapOutgoing(this.original.remove(key));
        }

        @Override
        public boolean containsKey(final Object key)
        {
            return this.original.containsKey(key);
        }

        @Override
        public Set<Entry<String, Object>> entrySet()
        {
            return new AbstractSet<Entry<String, Object>>()
            {
                @Override
                public boolean add(final Entry<String, Object> e)
                {
                    final String key = e.getKey();
                    final Object value = e.getValue();

                    ConvertedMap.this.original.put(key, ConvertedMap.this.unwrapIncoming(value));
                    return true;
                }

                @Override
                public int size()
                {
                    return ConvertedMap.this.original.size();
                }

                @Override
                public Iterator<Entry<String, Object>> iterator()
                {
                    return ConvertedMap.this.iterator();
                }
            };
        }

        private Iterator<Entry<String, Object>> iterator()
        {
            final Iterator<Entry<String, Object>> proxy = this.original.entrySet().iterator();

            return new Iterator<Entry<String, Object>>()
            {
                @Override
                public boolean hasNext()
                {
                    return proxy.hasNext();
                }

                @Override
                public Entry<String, Object> next()
                {
                    final Entry<String, Object> entry = proxy.next();

                    return new SimpleEntry<>(entry.getKey(), ConvertedMap.this.wrapOutgoing(entry.getValue()));
                }

                @Override
                public void remove()
                {
                    proxy.remove();
                }
            };
        }

        @Override
        public Object getHandle()
        {
            return this.handle;
        }
    }

    /**
     * Represents a list that wraps another list and converts elements
     * of its type and another exposed type.
     *
     * @author Kristian
     */
    private class ConvertedList extends AbstractList<Object> implements Wrapper
    {
        private final Object handle;

        private final List<Object> original;
        private final CachedNativeWrapper cache = new CachedNativeWrapper();

        public ConvertedList(final Object handle, final List<Object> original)
        {
            if (NbtFactory.this.NBT_LIST_TYPE == null)
            {
                NbtFactory.this.NBT_LIST_TYPE = getField(handle, null, "type");
            }
            this.handle = handle;
            this.original = original;
        }

        protected Object wrapOutgoing(final Object value)
        {
            return this.cache.wrap(value);
        }

        protected Object unwrapIncoming(final Object wrapped)
        {
            return NbtFactory.this.unwrapValue(wrapped);
        }

        @Override
        public Object get(final int index)
        {
            return this.wrapOutgoing(this.original.get(index));
        }

        @Override
        public int size()
        {
            return this.original.size();
        }

        @Override
        public Object set(final int index, final Object element)
        {
            return this.wrapOutgoing(this.original.set(index, this.unwrapIncoming(element)));
        }

        @Override
        public void add(final int index, final Object element)
        {
            final Object nbt = this.unwrapIncoming(element);

            // Set the list type if its the first element
            if (this.size() == 0)
            {
                setFieldValue(NbtFactory.this.NBT_LIST_TYPE, this.handle, (byte) NbtFactory.this.getNbtType(nbt).id);
            }
            this.original.add(index, nbt);
        }

        @Override
        public Object remove(final int index)
        {
            return this.wrapOutgoing(this.original.remove(index));
        }

        @Override
        public boolean remove(final Object o)
        {
            return this.original.remove(this.unwrapIncoming(o));
        }

        @Override
        public Object getHandle()
        {
            return this.handle;
        }
    }

    /**
     * Represents a method for loading an NBT compound.
     *
     * @author Kristian
     */
    private abstract static class LoadCompoundMethod
    {
        protected Method staticMethod;

        protected void setMethod(final Method method)
        {
            this.staticMethod = method;
            this.staticMethod.setAccessible(true);
        }

        /**
         * Load an NBT compound from a given stream.
         *
         * @param input - the input stream.
         *
         * @return The loaded NBT compound.
         */
        public abstract Object loadNbt(DataInput input);
    }

    /**
     * Load an NBT compound from the NBTCompressedStreamTools static method in 1.7.2 - 1.7.5
     */
    private static class LoadMethodWorldUpdate extends LoadCompoundMethod
    {
        public LoadMethodWorldUpdate(final Class<?> streamClass)
        {
            this.setMethod(getMethod(Modifier.STATIC, 0, streamClass, null, DataInput.class));
        }

        @Override
        public Object loadNbt(final DataInput input)
        {
            return invokeMethod(this.staticMethod, null, input);
        }
    }

    /**
     * Load an NBT compound from the NBTCompressedStreamTools static method in 1.7.8
     */
    private static class LoadMethodSkinUpdate extends LoadCompoundMethod
    {
        private Object readLimiter;

        public LoadMethodSkinUpdate(final Class<?> streamClass, final Class<?> readLimiterClass)
        {
            this.setMethod(getMethod(Modifier.STATIC, 0, streamClass, null, DataInput.class, readLimiterClass));

            // Find the unlimited read limiter
            for (final Field field : readLimiterClass.getDeclaredFields())
            {
                if (readLimiterClass.isAssignableFrom(field.getType()))
                {
                    try
                    {
                        this.readLimiter = field.get(null);
                    } catch (final Exception e)
                    {
                        throw new RuntimeException("Cannot retrieve read limiter.", e);
                    }
                }
            }
        }

        @Override
        public Object loadNbt(final DataInput input)
        {
            return invokeMethod(this.staticMethod, null, input, this.readLimiter);
        }
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString()).toString();
    }
}
