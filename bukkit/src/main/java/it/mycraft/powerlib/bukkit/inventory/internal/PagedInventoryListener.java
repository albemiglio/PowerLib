package it.mycraft.powerlib.bukkit.inventory.internal;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PagedInventoryListener implements Listener {

    /** Bukkit guarantees an InventoryCloseEvent before PlayerQuitEvent on disconnect,
     *  so this map self-cleans on close. ConcurrentHashMap because track() is public
     *  and may be called from async code that opens a GUI. */
    private static final Map<UUID, OpenedPagedInventory> OPEN = new ConcurrentHashMap<>();

    public static void track(Player player, OpenedPagedInventory state) {
        OPEN.put(player.getUniqueId(), state);
    }

    public static OpenedPagedInventory get(Player player) {
        return OPEN.get(player.getUniqueId());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        OpenedPagedInventory state = OPEN.get(player.getUniqueId());
        if (state == null || !state.getInventory().equals(event.getInventory())) return;
        event.setCancelled(true);
        state.handleClick(player, event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        OPEN.remove(event.getPlayer().getUniqueId());
    }
}
