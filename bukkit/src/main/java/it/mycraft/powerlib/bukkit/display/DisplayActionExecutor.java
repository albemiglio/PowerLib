package it.mycraft.powerlib.bukkit.display;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Executes the common display actions used by Novaverse-style YAML modules.
 */
public final class DisplayActionExecutor {

    private final DisplayRenderer renderer;

    public DisplayActionExecutor(DisplayRenderer renderer) {
        this.renderer = renderer;
    }

    public void execute(Player player, ConfigurationSection actions) {
        execute(player, actions, Map.of());
    }

    public void execute(Player player, ConfigurationSection actions, Map<String, ?> placeholders) {
        if (player == null || !player.isOnline() || actions == null) {
            return;
        }
        sendMessage(player, actions, placeholders);
        sendActionBar(player, actions, placeholders);
        sendTitle(player, actions, placeholders);
        sendBossBar(player, actions, placeholders);
    }

    private void sendMessage(Player player, ConfigurationSection actions, Map<String, ?> placeholders) {
        String message = actions.getString("message");
        if (message == null || message.isEmpty()) {
            return;
        }
        DisplayMessageSpec spec = DisplayMessageSpec.builder()
                .type(DisplayType.CHAT)
                .message(message)
                .build();
        renderer.send(player, spec, placeholders);
    }

    private void sendActionBar(Player player, ConfigurationSection actions, Map<String, ?> placeholders) {
        String message = actions.getString("action-bar", actions.getString("actionbar"));
        if (message == null || message.isEmpty()) {
            return;
        }
        DisplayMessageSpec spec = DisplayMessageSpec.builder()
                .type(DisplayType.ACTION_BAR)
                .message(message)
                .build();
        renderer.send(player, spec, placeholders);
    }

    private void sendTitle(Player player, ConfigurationSection actions, Map<String, ?> placeholders) {
        ConfigurationSection section = actions.getConfigurationSection("title");
        if (section == null) {
            return;
        }
        renderer.send(player, DisplayMessageSpec.from(section, DisplayType.TITLE), placeholders);
    }

    private void sendBossBar(Player player, ConfigurationSection actions, Map<String, ?> placeholders) {
        ConfigurationSection section = actions.getConfigurationSection("boss-bar");
        if (section == null) {
            section = actions.getConfigurationSection("bossbar");
        }
        if (section == null) {
            return;
        }
        renderer.send(player, DisplayMessageSpec.from(section, DisplayType.BOSS_BAR), placeholders);
    }
}