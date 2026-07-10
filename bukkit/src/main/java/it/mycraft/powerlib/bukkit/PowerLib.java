package it.mycraft.powerlib.bukkit;

import it.mycraft.powerlib.bukkit.listeners.NexoListener;
import lombok.NonNull;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class PowerLib {

    private static BukkitAudiences adventure;
    private static NexoListener nexoListener;

    public static void inject(Plugin plugin) {
        // idempotente: un eventuale inject precedente (es. reload) viene chiuso prima
        // di ricrearlo, per non accumulare listener Nexo e istanze BukkitAudiences.
        shutdown();
        adventure = BukkitAudiences.create(plugin);
        // Il bridge furniture serve solo con Nexo presente; grazie a softdepend: [Nexo]
        // Nexo e' gia' caricato a questo punto se installato.
        if (Bukkit.getPluginManager().isPluginEnabled("Nexo")) {
            nexoListener = new NexoListener(plugin);
        }
    }

    /**
     * Libera le risorse statiche di PowerLib. Da chiamare su onDisable del plugin
     * che ha invocato {@link #inject(Plugin)}: deregistra i listener Nexo e chiude Adventure.
     */
    public static void shutdown() {
        if (nexoListener != null) {
            HandlerList.unregisterAll(nexoListener);
            nexoListener = null;
        }
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
    }

    public static boolean isNexoAvailable() {
        return nexoListener != null && nexoListener.isNexoAvailable();
    }

    public static @NonNull BukkitAudiences adventure() {
        if(adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }
}
