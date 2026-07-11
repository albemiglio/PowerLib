package it.mycraft.powerlib.bukkit.display;

import it.mycraft.powerlib.bukkit.PowerLib;
import it.mycraft.powerlib.common.utils.ColorAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class DisplayRenderer {

    private final Plugin plugin;

    public DisplayRenderer(Plugin plugin) {
        this.plugin = plugin;
    }

    public void send(Player player, DisplayMessageSpec spec) {
        send(player, spec, Collections.emptyMap());
    }

    public void send(Player player, DisplayMessageSpec spec, Map<String, ?> placeholders) {
        if (player == null || !player.isOnline() || spec == null || !spec.enabled()) {
            return;
        }
        switch (spec.type()) {
            case CHAT -> sendChat(player, spec, placeholders);
            case ACTION_BAR -> sendActionBar(player, spec, placeholders);
            case TITLE -> sendTitle(player, spec, placeholders);
            case BOSS_BAR -> sendTemporaryBossBar(player, spec, placeholders);
        }
    }

    public void send(Collection<? extends Player> players, DisplayMessageSpec spec, Map<String, ?> placeholders) {
        if (players == null || players.isEmpty()) {
            return;
        }
        for (Player player : players) {
            send(player, spec, placeholders);
        }
    }


    private void sendActionBar(Player player, DisplayMessageSpec spec, Map<String, ?> placeholders) {
        String message = format(spec.message(), placeholders);
        if (message.isEmpty()) {
            return;
        }
        try {
            PowerLib.adventure().player(player).sendActionBar(Component.text(message));
        } catch (IllegalStateException ignored) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        }
    }

    private void sendChat(Player player, DisplayMessageSpec spec, Map<String, ?> placeholders) {
        String message = format(spec.message(), placeholders);
        if (!message.isEmpty()) {
            player.sendMessage(message);
        }
    }

    @SuppressWarnings("deprecation")
    private void sendTitle(Player player, DisplayMessageSpec spec, Map<String, ?> placeholders) {
        player.sendTitle(
                format(spec.title(), placeholders),
                format(spec.subtitle(), placeholders),
                spec.fadeInTicks(),
                spec.stayTicks(),
                spec.fadeOutTicks()
        );
    }

    private void sendTemporaryBossBar(Player player, DisplayMessageSpec spec, Map<String, ?> placeholders) {
        String title = format(bossBarTitle(spec), placeholders);
        if (title.isEmpty()) {
            return;
        }
        BossBar bossBar = Bukkit.createBossBar(title, spec.barColor(), spec.barStyle());
        bossBar.setProgress(spec.progress());
        bossBar.addPlayer(player);
        bossBar.setVisible(true);

        if (plugin == null || !plugin.isEnabled()) {
            bossBar.removePlayer(player);
            bossBar.removeAll();
            bossBar.setVisible(false);
            return;
        }
        long durationTicks = spec.durationTicks() > 0L ? spec.durationTicks() : 200L;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            bossBar.removePlayer(player);
            bossBar.removeAll();
            bossBar.setVisible(false);
        }, durationTicks);
    }

    static String format(String raw, Map<String, ?> placeholders) {
        return ColorAPI.color(DisplayPlaceholders.apply(raw, placeholders));
    }

    static String bossBarTitle(DisplayMessageSpec spec) {
        return spec.title().isEmpty() ? spec.message() : spec.title();
    }
}
