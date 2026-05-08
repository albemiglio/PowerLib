package it.mycraft.powerlib.bukkit;

import it.mycraft.powerlib.bukkit.compat.NexoSupport;
import it.mycraft.powerlib.bukkit.listeners.NexoListener;
import lombok.NonNull;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.Plugin;

public class PowerLib {

    private static BukkitAudiences adventure;

    public static void inject(Plugin plugin) {
        adventure = BukkitAudiences.create(plugin);
        new NexoListener(plugin);
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
