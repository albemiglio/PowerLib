package it.mycraft.powerlib.common.configuration;

import java.util.*;

/**
 * Original fragments from bungeecord
 * @author md_5
 */
public final class Configuration {

    private static final char SEPARATOR = '.';
    final Map<String, Object> self;
    private final Configuration defaults;

    /**
     * Creates an empty configuration with no defaults.
     */
    public Configuration() {
        this(null);
    }

    /**
     * Creates an empty configuration backed by the given defaults.
     *
     * @param defaults configuration consulted when a path is missing, or {@code null} for none
     */
    public Configuration(Configuration defaults) {
        this(new LinkedHashMap<String, Object>(), defaults);
    }

    Configuration(Map<?, ?> map, Configuration defaults) {
        this.self = new LinkedHashMap<>();
        this.defaults = defaults;

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = (entry.getKey() == null) ? "null" : entry.getKey().toString();

            if (entry.getValue() instanceof Map) {
                this.self.put(key, new Configuration((Map) entry.getValue(), (defaults == null) ? null : defaults.getSection(key)));
            } else {
                this.self.put(key, entry.getValue());
            }
        }
    }

    private Configuration getSectionFor(String path) {
        int index = path.indexOf(SEPARATOR);
        if (index == -1) {
            return this;
        }

        String root = path.substring(0, index);
        Object section = self.get(root);
        if (section == null) {
            section = new Configuration((defaults == null) ? null : defaults.getSection(root));
            self.put(root, section);
        }

        return (Configuration) section;
    }

    private String getChild(String path) {
        int index = path.indexOf(SEPARATOR);
        return (index == -1) ? path : path.substring(index + 1);
    }

    /*------------------------------------------------------------------------*/

    /**
     * Returns the value at the given path, or {@code def} when it is absent.
     *
     * @param path the dotted path
     * @param def  value returned (and stored, if a {@link Configuration} section) when the path is unset
     * @param <T>  the expected value type
     * @return the value at the path, or {@code def}
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String path, T def) {
        Configuration section = getSectionFor(path);
        Object val;
        if (section == this) {
            val = self.get(path);
        } else {
            val = section.get(getChild(path), def);
        }

        if (val == null && def instanceof Configuration) {
            self.put(path, def);
        }

        return (val != null) ? (T) val : def;
    }

    /**
     * @param path the dotted path
     * @return {@code true} if a value is set at the path
     */
    public boolean contains(String path) {
        return get(path, null) != null;
    }

    /**
     * @param path the dotted path
     * @return the value at the path, falling back to the default configuration, or {@code null}
     */
    public Object get(String path) {
        return get(path, getDefault(path));
    }

    /**
     * @param path the dotted path
     * @return the value at the path in the default configuration, or {@code null} if none
     */
    public Object getDefault(String path) {
        return (defaults == null) ? null : defaults.get(path);
    }

    /**
     * Sets the value at the given path, creating intermediate sections as needed.
     * A {@code null} value removes the entry.
     *
     * @param path  the dotted path
     * @param value the value to store, or {@code null} to remove it
     */
    public void set(String path, Object value) {
        if (value instanceof Map) {
            value = new Configuration((Map) value, (defaults == null) ? null : defaults.getSection(path));
        }

        Configuration section = getSectionFor(path);
        if (section == this) {
            if (value == null) {
                self.remove(path);
            } else {
                self.put(path, value);
            }
        } else {
            section.set(getChild(path), value);
        }
    }

    /*------------------------------------------------------------------------*/

    /**
     * @param path the dotted path
     * @return the sub-section at the path, creating an empty one if absent
     */
    public Configuration getSection(String path) {
        Object def = getDefault(path);
        return (Configuration) get(path, (def instanceof Configuration) ? def : new Configuration((defaults == null) ? null : defaults.getSection(path)));
    }

    /**
     * Gets keys, not deep by default.
     *
     * @return top level keys for this section
     */
    public Collection<String> getKeys() {
        return new LinkedHashSet<>(self.keySet());
    }

    /*------------------------------------------------------------------------*/

    /**
     * @param path the dotted path
     * @return the value as a byte, or {@code 0} if absent or not a number
     */
    public byte getByte(String path) {
        Object def = getDefault(path);
        return getByte(path, (def instanceof Number) ? ((Number) def).byteValue() : 0);
    }

    /**
     * @param path the dotted path
     * @param def  fallback when the value is absent or not a number
     * @return the value as a byte, or {@code def}
     */
    public byte getByte(String path, byte def) {
        Object val = get(path, def);
        return (val instanceof Number) ? ((Number) val).byteValue() : def;
    }

    /**
     * @param path the dotted path
     * @return the numeric entries of the list at the path, as bytes
     */
    public List<Byte> getByteList(String path) {
        List<?> list = getList(path);
        List<Byte> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).byteValue());
            }
        }

        return result;
    }

    /**
     * @param path the dotted path
     * @return the value as a short, or {@code 0} if absent or not a number
     */
    public short getShort(String path) {
        Object def = getDefault(path);
        return getShort(path, (def instanceof Number) ? ((Number) def).shortValue() : 0);
    }

    /**
     * @param path the dotted path
     * @param def  fallback when the value is absent or not a number
     * @return the value as a short, or {@code def}
     */
    public short getShort(String path, short def) {
        Object val = get(path, def);
        return (val instanceof Number) ? ((Number) val).shortValue() : def;
    }

    /**
     * @param path the dotted path
     * @return the numeric entries of the list at the path, as shorts
     */
    public List<Short> getShortList(String path) {
        List<?> list = getList(path);
        List<Short> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).shortValue());
            }
        }

        return result;
    }

    /**
     * @param path the dotted path
     * @return the value as an int, or {@code 0} if absent or not a number
     */
    public int getInt(String path) {
        Object def = getDefault(path);
        return getInt(path, (def instanceof Number) ? ((Number) def).intValue() : 0);
    }

    /**
     * @param path the dotted path
     * @param def  fallback when the value is absent or not a number
     * @return the value as an int, or {@code def}
     */
    public int getInt(String path, int def) {
        Object val = get(path, def);
        return (val instanceof Number) ? ((Number) val).intValue() : def;
    }

    /**
     * @param path the dotted path
     * @return the numeric entries of the list at the path, as ints
     */
    public List<Integer> getIntList(String path) {
        List<?> list = getList(path);
        List<Integer> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).intValue());
            }
        }

        return result;
    }

    /**
     * @param path the dotted path
     * @return the value as a long, or {@code 0} if absent or not a number
     */
    public long getLong(String path) {
        Object def = getDefault(path);
        return getLong(path, (def instanceof Number) ? ((Number) def).longValue() : 0);
    }

    /**
     * @param path the dotted path
     * @param def  fallback when the value is absent or not a number
     * @return the value as a long, or {@code def}
     */
    public long getLong(String path, long def) {
        Object val = get(path, def);
        return (val instanceof Number) ? ((Number) val).longValue() : def;
    }

    /**
     * @param path the dotted path
     * @return the numeric entries of the list at the path, as longs
     */
    public List<Long> getLongList(String path) {
        List<?> list = getList(path);
        List<Long> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).longValue());
            }
        }

        return result;
    }

    /**
     * @param path the dotted path
     * @return the value as a float, or {@code 0} if absent or not a number
     */
    public float getFloat(String path) {
        Object def = getDefault(path);
        return getFloat(path, (def instanceof Number) ? ((Number) def).floatValue() : 0);
    }

    /**
     * @param path the dotted path
     * @param def  fallback when the value is absent or not a number
     * @return the value as a float, or {@code def}
     */
    public float getFloat(String path, float def) {
        Object val = get(path, def);
        return (val instanceof Number) ? ((Number) val).floatValue() : def;
    }

    /**
     * @param path the dotted path
     * @return the numeric entries of the list at the path, as floats
     */
    public List<Float> getFloatList(String path) {
        List<?> list = getList(path);
        List<Float> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).floatValue());
            }
        }

        return result;
    }

    /**
     * @param path the dotted path
     * @return the value as a double, or {@code 0} if absent or not a number
     */
    public double getDouble(String path) {
        Object def = getDefault(path);
        return getDouble(path, (def instanceof Number) ? ((Number) def).doubleValue() : 0);
    }

    /**
     * @param path the dotted path
     * @param def  fallback when the value is absent or not a number
     * @return the value as a double, or {@code def}
     */
    public double getDouble(String path, double def) {
        Object val = get(path, def);
        return (val instanceof Number) ? ((Number) val).doubleValue() : def;
    }

    /**
     * @param path the dotted path
     * @return the numeric entries of the list at the path, as doubles
     */
    public List<Double> getDoubleList(String path) {
        List<?> list = getList(path);
        List<Double> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Number) {
                result.add(((Number) object).doubleValue());
            }
        }

        return result;
    }

    /**
     * @param path the dotted path
     * @return the value as a boolean, or {@code false} if absent or not a boolean
     */
    public boolean getBoolean(String path) {
        Object def = getDefault(path);
        return getBoolean(path, (def instanceof Boolean) ? (Boolean) def : false);
    }

    /**
     * @param path the dotted path
     * @param def  fallback when the value is absent or not a boolean
     * @return the value as a boolean, or {@code def}
     */
    public boolean getBoolean(String path, boolean def) {
        Object val = get(path, def);
        return (val instanceof Boolean) ? (Boolean) val : def;
    }

    /**
     * @param path the dotted path
     * @return the boolean entries of the list at the path
     */
    public List<Boolean> getBooleanList(String path) {
        List<?> list = getList(path);
        List<Boolean> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Boolean) {
                result.add((Boolean) object);
            }
        }

        return result;
    }

    /**
     * @param path the dotted path
     * @return the value as a char, or the null character if absent or not a character
     */
    public char getChar(String path) {
        Object def = getDefault(path);
        return getChar(path, (def instanceof Character) ? (Character) def : '\u0000');
    }

    /**
     * @param path the dotted path
     * @param def  fallback when the value is absent or not a character
     * @return the value as a char, or {@code def}
     */
    public char getChar(String path, char def) {
        Object val = get(path, def);
        return (val instanceof Character) ? (Character) val : def;
    }

    /**
     * @param path the dotted path
     * @return the character entries of the list at the path
     */
    public List<Character> getCharList(String path) {
        List<?> list = getList(path);
        List<Character> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof Character) {
                result.add((Character) object);
            }
        }

        return result;
    }

    /**
     * @param path the dotted path
     * @return the value as a string, or an empty string if absent or not a string
     */
    public String getString(String path) {
        Object def = getDefault(path);
        return getString(path, (def instanceof String) ? (String) def : "");
    }

    /**
     * @param path the dotted path
     * @param def  fallback when the value is absent or not a string
     * @return the value as a string, or {@code def}
     */
    public String getString(String path, String def) {
        Object val = get(path, def);
        return (val instanceof String) ? (String) val : def;
    }

    /**
     * @param path the dotted path
     * @return the string entries of the list at the path
     */
    public List<String> getStringList(String path) {
        List<?> list = getList(path);
        List<String> result = new ArrayList<>();

        for (Object object : list) {
            if (object instanceof String) {
                result.add((String) object);
            }
        }

        return result;
    }

    /*------------------------------------------------------------------------*/

    /**
     * @param path the dotted path
     * @return the list at the path, or an empty list if absent or not a list
     */
    public List<?> getList(String path) {
        Object def = getDefault(path);
        return getList(path, (def instanceof List<?>) ? (List<?>) def : Collections.EMPTY_LIST);
    }

    /**
     * @param path the dotted path
     * @param def  fallback when the value is absent or not a list
     * @return the list at the path, or {@code def}
     */
    public List<?> getList(String path, List<?> def) {
        Object val = get(path, def);
        return (val instanceof List<?>) ? (List<?>) val : def;
    }
}
