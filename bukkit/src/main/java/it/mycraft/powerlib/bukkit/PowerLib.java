package it.mycraft.powerlib.bukkit;

import it.mycraft.powerlib.bukkit.compat.NexoSupport;
import it.mycraft.powerlib.bukkit.inventory.internal.PagedInventoryListener;
import it.mycraft.powerlib.bukkit.listeners.NexoListener;
import lombok.NonNull;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PowerLib {

    private static BukkitAudiences adventure;
    private static boolean injected;

    public static void inject(Plugin plugin) {
        if (injected) return;
        adventure = BukkitAudiences.create(plugin);
        if (NexoSupport.isAvailable()) {
            new NexoListener(plugin);
        }
        Bukkit.getPluginManager().registerEvents(new PagedInventoryListener(), plugin);
        injected = true;
    }

    public static void disable() {
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
        injected = false;
    }

    public static boolean isNexoAvailable() {
        return NexoSupport.isAvailable();
    }

    public static @NonNull BukkitAudiences adventure() {
        if (adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }
}
