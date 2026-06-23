package it.mycraft.powerlib.bungee;

import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.plugin.Plugin;

public class PowerLib {

    private static BungeeAudiences adventure;

    @Getter
    private static Plugin plugin;

    public static void inject(Plugin plugin) {
        PowerLib.plugin = plugin;
        adventure = BungeeAudiences.create(plugin);
    }

    public static @NonNull BungeeAudiences adventure() {
        if(adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }
}

