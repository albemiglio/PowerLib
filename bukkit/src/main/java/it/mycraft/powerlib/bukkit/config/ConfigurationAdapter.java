package it.mycraft.powerlib.bukkit.config;

import it.mycraft.powerlib.common.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Converts a Bukkit {@link FileConfiguration} into the library's native {@link Configuration}.
 */
public class ConfigurationAdapter {

    /**
     * Copies every key/value pair from the given Bukkit configuration into a native {@link Configuration}.
     *
     * @param fileConfiguration the Bukkit configuration to adapt
     * @return the equivalent native configuration
     */
    public static Configuration adapt(FileConfiguration fileConfiguration) {
        Configuration config = new Configuration();
        fileConfiguration.getKeys(true).forEach((k) -> {
            config.set(k, fileConfiguration.get(k));
        });
        return config;
    }
}
