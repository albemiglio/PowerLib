package it.mycraft.powerlib.configuration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Original fragments from bungeecord
 *
 * @author md_5
 * @deprecated use {@link it.mycraft.powerlib.common.configuration.ConfigurationProvider} instead
 */
@Deprecated
public abstract class ConfigurationProvider {

    private static final Map<Class<? extends ConfigurationProvider>, ConfigurationProvider> providers = new HashMap<>();

    static {
        try {
            providers.put(YamlConfiguration.class, new YamlConfiguration());
        } catch (NoClassDefFoundError ex) {
            // Ignore, no SnakeYAML
        }

        try {
            providers.put(JsonConfiguration.class, new JsonConfiguration());
        } catch (NoClassDefFoundError ex) {
            // Ignore, no Gson
        }
    }

    /**
     * @param provider the provider class (e.g. {@code YamlConfiguration.class})
     * @return the registered provider instance, or {@code null} if its library is unavailable
     */
    public static ConfigurationProvider getProvider(Class<? extends ConfigurationProvider> provider) {
        return providers.get(provider);
    }

    /*------------------------------------------------------------------------*/

    /**
     * Saves the configuration to a file.
     *
     * @param config the configuration to save
     * @param file   the destination file
     * @throws IOException if writing fails
     */
    public abstract void save(Configuration config, File file) throws IOException;

    /**
     * Saves the configuration to a writer.
     *
     * @param config the configuration to save
     * @param writer the destination writer
     */
    public abstract void save(Configuration config, Writer writer);

    /**
     * @param file the source file
     * @return the loaded configuration
     * @throws IOException if reading fails
     */
    public abstract Configuration load(File file) throws IOException;

    /**
     * @param file     the source file
     * @param defaults defaults consulted for missing paths
     * @return the loaded configuration
     * @throws IOException if reading fails
     */
    public abstract Configuration load(File file, Configuration defaults) throws IOException;

    /**
     * @param reader the source reader
     * @return the loaded configuration
     */
    public abstract Configuration load(Reader reader);

    /**
     * @param reader   the source reader
     * @param defaults defaults consulted for missing paths
     * @return the loaded configuration
     */
    public abstract Configuration load(Reader reader, Configuration defaults);

    /**
     * @param is the source stream
     * @return the loaded configuration
     */
    public abstract Configuration load(InputStream is);

    /**
     * @param is       the source stream
     * @param defaults defaults consulted for missing paths
     * @return the loaded configuration
     */
    public abstract Configuration load(InputStream is, Configuration defaults);

    /**
     * @param string the raw configuration text
     * @return the loaded configuration
     */
    public abstract Configuration load(String string);

    /**
     * @param string   the raw configuration text
     * @param defaults defaults consulted for missing paths
     * @return the loaded configuration
     */
    public abstract Configuration load(String string, Configuration defaults);
}
