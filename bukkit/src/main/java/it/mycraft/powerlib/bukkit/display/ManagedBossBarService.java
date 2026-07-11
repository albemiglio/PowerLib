package it.mycraft.powerlib.bukkit.display;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class ManagedBossBarService implements Listener, AutoCloseable {

    private final Plugin plugin;
    private final Map<String, ActiveBossBar> bars = new HashMap<>();

    public ManagedBossBarService(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void show(String id, Player player, DisplayMessageSpec spec, Map<String, ?> placeholders, double progress) {
        if (player == null) {
            hide(id);
            return;
        }
        show(id, java.util.List.of(player), spec, placeholders, progress);
    }

    public void show(String id, Collection<? extends Player> audience,
                     DisplayMessageSpec spec, Map<String, ?> placeholders, double progress) {
        if (id == null || id.isBlank() || spec == null || !spec.enabled()) {
            return;
        }
        if (audience == null || audience.isEmpty()) {
            hide(id);
            return;
        }

        String title = DisplayRenderer.format(DisplayRenderer.bossBarTitle(spec), placeholders);
        if (title.isEmpty()) {
            hide(id);
            return;
        }

        ActiveBossBar active = bars.computeIfAbsent(id, ignored -> new ActiveBossBar());
        BossBar bar = active.bar;
        if (bar == null) {
            bar = Bukkit.createBossBar(title, spec.barColor(), spec.barStyle());
            active.bar = bar;
        }

        bar.setTitle(title);
        bar.setColor(spec.barColor());
        bar.setStyle(spec.barStyle());
        bar.setProgress(Math.max(0.0D, Math.min(1.0D, progress)));
        syncAudience(bar, audience);
        bar.setVisible(!bar.getPlayers().isEmpty());
        scheduleExpiry(id, active, spec.durationTicks());
    }

    public void hide(String id) {
        ActiveBossBar active = bars.remove(id);
        if (active == null) {
            return;
        }
        active.cancelExpiry();
        active.removeBar();
    }

    public void clear() {
        for (ActiveBossBar active : bars.values()) {
            active.cancelExpiry();
            active.removeBar();
        }
        bars.clear();
    }

    @Override
    public void close() {
        clear();
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        for (ActiveBossBar active : bars.values()) {
            if (active.bar != null) {
                active.bar.removePlayer(event.getPlayer());
                active.bar.setVisible(!active.bar.getPlayers().isEmpty());
            }
        }
    }

    private void syncAudience(BossBar bar, Collection<? extends Player> audience) {
        Set<UUID> wanted = new HashSet<>();
        for (Player player : audience) {
            if (player != null && player.isOnline()) {
                wanted.add(player.getUniqueId());
            }
        }

        for (Player current : new ArrayList<>(bar.getPlayers())) {
            if (!wanted.contains(current.getUniqueId())) {
                bar.removePlayer(current);
            }
        }
        for (Player player : audience) {
            if (player != null && player.isOnline()) {
                bar.addPlayer(player);
            }
        }
    }

    private void scheduleExpiry(String id, ActiveBossBar active, long durationTicks) {
        active.cancelExpiry();
        if (durationTicks <= 0L || !plugin.isEnabled()) {
            return;
        }
        active.expiryTask = Bukkit.getScheduler().runTaskLater(plugin, () -> hide(id), durationTicks);
    }

    private static final class ActiveBossBar {
        private BossBar bar;
        private BukkitTask expiryTask;

        private void cancelExpiry() {
            if (expiryTask != null) {
                expiryTask.cancel();
                expiryTask = null;
            }
        }

        private void removeBar() {
            if (bar != null) {
                bar.removeAll();
                bar.setVisible(false);
                bar = null;
            }
        }
    }
}
