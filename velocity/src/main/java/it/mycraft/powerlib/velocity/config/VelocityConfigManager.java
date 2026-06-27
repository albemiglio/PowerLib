package it.mycraft.powerlib.velocity.config;

import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginDescription;
import it.mycraft.powerlib.common.configuration.ConfigManager;

import java.io.File;
import java.net.URISyntaxException;

/**
 * @author AlbeMiglio
 */
public class VelocityConfigManager extends ConfigManager {

    /**
     * Creates a config manager rooted at the plugin's data folder under the Velocity install directory.
     *
     * @param pluginDescription the plugin description used to locate the data folder and plugin jar
     */
    public VelocityConfigManager(PluginDescription pluginDescription) {
        super(dataFolder(pluginDescription), pluginJar(pluginDescription));
    }

    private static File serverRoot() {
        try {
            return new File(Plugin.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Unable to resolve the Velocity install directory", ex);
        }
    }

    private static File pluginJar(PluginDescription description) {
        return new File(serverRoot(), description.getSource().get().toString());
    }

    private static File dataFolder(PluginDescription description) {
        return new File(serverRoot() + "/plugins/" + description.getId());
    }
}
