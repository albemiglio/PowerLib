package it.mycraft.powerlib.bungee.config;

import it.mycraft.powerlib.common.configuration.ConfigManager;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * @author AlbeMiglio
 */
public class BungeeConfigManager extends ConfigManager {

    /**
     * Creates a config manager rooted at the plugin's data folder and jar.
     *
     * @param plugin the owning plugin
     */
    public BungeeConfigManager(Plugin plugin) {
        super(plugin.getDataFolder(), plugin.getFile());
    }
}
