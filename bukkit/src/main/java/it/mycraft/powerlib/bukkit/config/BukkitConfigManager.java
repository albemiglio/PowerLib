package it.mycraft.powerlib.bukkit.config;

import it.mycraft.powerlib.common.configuration.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * @author AlbeMiglio
 */
public class BukkitConfigManager extends ConfigManager {

    public BukkitConfigManager(JavaPlugin plugin) {
        super(plugin.getDataFolder(),
                new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()));
    }
}
