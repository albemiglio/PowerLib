package it.mycraft.powerlib.bukkit;

import it.mycraft.powerlib.bukkit.listeners.NexoListener;
import it.mycraft.powerlib.bukkit.utils.NexoUtils;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.Plugin;

/**
 * Static entry point for the Bukkit module, holding the owning plugin and the Adventure platform handle.
 */
public class PowerLib {

    private static BukkitAudiences adventure;

    @Getter
    private static Plugin plugin;

    /**
     * Initialises the library for the given plugin: stores it, creates the Adventure platform handle,
     * and registers the Nexo bridge if Nexo is installed. Call once from the plugin's {@code onEnable}.
     *
     * <p>Idempotent: a previous initialisation is shut down first, so a reload does not leak an Adventure
     * platform or leave a second Nexo bridge registered.
     *
     * @param plugin the owning plugin
     */
    public static void inject(Plugin plugin) {
        shutdown();
        PowerLib.plugin = plugin;
        adventure = BukkitAudiences.create(plugin);
        NexoListener.register(plugin); // no-op unless Nexo is installed
    }

    /**
     * Releases what {@link #inject(Plugin)} acquired: detaches the Nexo bridge and closes the Adventure
     * platform. Call from the owning plugin's {@code onDisable}. Safe to call twice.
     */
    public static void shutdown() {
        NexoListener.unregister();
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
        plugin = null;
    }

    /**
     * Whether the Nexo custom-item plugin is installed and its API was bound.
     *
     * @return {@code true} if the Nexo API is available
     */
    public static boolean isNexoAvailable() {
        return NexoUtils.isAvailable();
    }

    /**
     * Returns the Adventure platform handle for sending messages, titles, and sounds.
     *
     * @return the active {@link BukkitAudiences}
     * @throws IllegalStateException if accessed while the plugin is disabled
     */
    public static @NonNull BukkitAudiences adventure() {
        if(adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }
}
