package it.mycraft.powerlib.commands.bukkit;

import it.mycraft.powerlib.commands.CommandBuilder;
import org.bukkit.plugin.Plugin;

/**
 * Reflective adapter for Paper's Brigadier API (1.20.4+).
 * Detection: io.papermc.paper.command.brigadier.Commands present on classpath.
 *
 * For now this adapter delegates back to the BukkitCommand fallback because
 * Paper-Brigadier wiring requires per-event-loop registration via the
 * lifecycle manager — implementing that fully is a follow-up. The detection
 * still exists so the fallback is explicit and future-extension is easy.
 */
final class PaperBrigadierAdapter {

    private PaperBrigadierAdapter() {}

    static boolean isAvailable() {
        try {
            Class.forName("io.papermc.paper.command.brigadier.Commands");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    static void register(Plugin plugin, CommandBuilder builder) {
        // TODO(future): use LifecycleEventManager to register a Brigadier literal node.
        // For now, fall through.
        BukkitCommandFallback.register(plugin, builder);
    }
}
