package it.mycraft.powerlib.bungee;

import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * Static entry point holding the owning plugin and the shared Adventure platform on BungeeCord.
 */
public class PowerLib {

    private static BungeeAudiences adventure;

    @Getter
    private static Plugin plugin;

    /**
     * Binds the owning plugin and creates the Adventure platform.
     *
     * @param plugin the plugin that owns this library instance
     */
    public static void inject(Plugin plugin) {
        PowerLib.plugin = plugin;
        adventure = BungeeAudiences.create(plugin);
    }

    /**
     * Returns the shared Adventure platform.
     *
     * @return the Adventure platform
     * @throws IllegalStateException if accessed while the plugin is disabled (not injected)
     */
    public static @NonNull BungeeAudiences adventure() {
        if(adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }
}

