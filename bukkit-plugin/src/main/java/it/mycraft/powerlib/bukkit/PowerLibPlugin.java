package it.mycraft.powerlib.bukkit;

import it.mycraft.powerlib.bukkit.config.BukkitConfigManager;
import it.mycraft.powerlib.bukkit.updater.PluginUpdater;
import it.mycraft.powerlib.common.chat.Message;
import it.mycraft.powerlib.common.configuration.ConfigManager;
import it.mycraft.powerlib.common.configuration.Configuration;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import static net.kyori.adventure.text.event.ClickEvent.openUrl;

/**
 * PowerLib Official Source code
 *
 * @author AlbeMiglio, FranFrau
 * https://www.github.com/AlbeMiglio/PowerLib
 */
@Getter
public class PowerLibPlugin extends JavaPlugin implements Listener {

    @Getter
    @Setter
    private static PowerLibPlugin instance;
    private ConfigManager bukkitConfigManager;
    private PluginUpdater updater;
    private boolean checkForUpdates;

    @Override
    public void onEnable() {
        registerInstances();
        registerListeners();
        new Metrics(this, 11161);
    }

    /**
     * Returns the plugin's main configuration ({@code config.yml}).
     *
     * @return the loaded configuration
     */
    public Configuration getConfiguration() {
        return this.bukkitConfigManager.get("config.yml");
    }

    private void registerInstances() {
        setInstance(this);
        PowerLib.inject(this);
        this.bukkitConfigManager = new BukkitConfigManager(this);
        this.bukkitConfigManager.create("config.yml");
        this.checkForUpdates = getConfiguration().getBoolean("check-for-updates");
        this.updater = new PluginUpdater(this).setGitHubURL("AlbeMiglio", "PowerLib");
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                if(!checkForUpdates) {
                    return;
                }
                Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
                    Player player = event.getPlayer();
                    if (!player.isOp() && !player.hasPermission("powerlib.update")) {
                        return;
                    }
                    if (PowerLibPlugin.getInstance().getUpdater().needsUpdate()) {
                        Message m = new Message("&c&lYour version of PowerLib is outdated!");
                        m.appendLines(Component
                                        .text("&a&nClick here to update to v{version}!")
                                        .clickEvent(openUrl("https://github.com/AlbeMiglio/PowerLib/releases/latest")))
                                .addPlaceHolder("{version}", updater.getLatestVersion());
                        m.send(player);
                    }
                });
            }
        }, this);
    }
}
