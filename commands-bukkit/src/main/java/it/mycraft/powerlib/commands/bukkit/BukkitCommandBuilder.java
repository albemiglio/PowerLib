package it.mycraft.powerlib.commands.bukkit;

import it.mycraft.powerlib.commands.CommandBuilder;
import org.bukkit.plugin.Plugin;

public final class BukkitCommandBuilder extends CommandBuilder {

    private BukkitCommandBuilder(String name) {
        super(name);
    }

    public static BukkitCommandBuilder create(String name) {
        return new BukkitCommandBuilder(name);
    }

    @Override
    protected CommandBuilder newChild(String name) {
        return new BukkitCommandBuilder(name);
    }

    @Override
    public void register(Object pluginRaw) {
        if (!(pluginRaw instanceof Plugin)) {
            throw new IllegalArgumentException("Bukkit registration expects an org.bukkit.plugin.Plugin");
        }
        Plugin plugin = (Plugin) pluginRaw;
        if (PaperBrigadierAdapter.isAvailable()) {
            PaperBrigadierAdapter.register(plugin, this);
        } else {
            BukkitCommandFallback.register(plugin, this);
        }
    }
}
