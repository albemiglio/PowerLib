package it.mycraft.powerlib.bungee.config;

import it.mycraft.powerlib.common.configuration.ConfigManager;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * @author AlbeMiglio
 */
public class BungeeConfigManager extends ConfigManager {

    public BungeeConfigManager(Plugin plugin) {
        super(plugin.getDataFolder(), plugin.getFile());
    }
}
