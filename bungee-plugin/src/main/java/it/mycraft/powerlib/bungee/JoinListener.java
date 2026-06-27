package it.mycraft.powerlib.bungee;

import it.mycraft.powerlib.common.chat.Message;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Notifies players with the {@code powerlib.update} permission when a newer PowerLib release is available.
 */
public class JoinListener implements Listener {

    private final PowerLibPlugin instance;

    /**
     * Creates the listener bound to the owning plugin.
     *
     * @param instance the PowerLib plugin instance
     */
    public JoinListener(PowerLibPlugin instance) {
        this.instance = instance;
    }

    /**
     * Checks for updates on player login and, if one is available, sends a clickable update notice.
     *
     * @param event the player login event
     */
    @EventHandler
    public void onJoin(PostLoginEvent event) {
        if(!instance.getConfiguration().getBoolean("check-for-updates")) {
            return;
        }
        ProxyServer.getInstance().getScheduler().runAsync(instance, () -> {
            ProxiedPlayer player = event.getPlayer();
            if (!player.hasPermission("powerlib.update")) {
                return;
            }
            if (instance.getUpdater().needsUpdate()) {
                Message m = new Message("&c&lYour version of PowerLib is outdated!");
                Message m1 = new Message("&a&nClick here to update to v{version}!")
                        .addPlaceHolder("{version}", instance.getUpdater().getLatestVersion());
                TextComponent update = new TextComponent((m1.getText()));
                update.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                        "https://github.com/AlbeMiglio/PowerLib/releases/latest"));
                m.send(player);
                player.sendMessage(update);
            }
        });
    }
}
