package it.mycraft.powerlib.configuration;

/**
 * Legacy config manager.
 *
 * @deprecated use {@link it.mycraft.powerlib.common.configuration.ConfigManager} instead
 */
@Deprecated
public abstract class ConfigManager {

    /**
     * @param file the config file name
     * @return the loaded configuration for that file
     */
    public abstract Configuration get(String file);

    /**
     * Creates the config file from a bundled resource of a different name.
     *
     * @param file   the config file name
     * @param source the bundled resource name to copy from
     * @return the new file
     */
    public abstract Configuration create(String file, String source);

    /**
     * Same as {@link #create(String, String)} but the source name equals the new one.
     *
     * @param file the config file name
     * @return the new file
     */
    public abstract Configuration create(String file);

    /**
     * Saves the config file changes and updates it in the local Map
     *
     * @param file The config file name
     */
    public abstract void save(String file);

    /**
     * Reloads a config file
     *
     * @param file The config file name
     * @author Original code from JavaPlugin.class
     */
    public abstract void reload(String file);

    /**
     * Reloads all config files
     */
    public abstract void reloadAll();
}
