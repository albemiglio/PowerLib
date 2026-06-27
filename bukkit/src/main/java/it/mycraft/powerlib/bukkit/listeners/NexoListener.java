package it.mycraft.powerlib.bukkit.listeners;

import it.mycraft.powerlib.bukkit.events.NexoFurnitureInteractEvent;
import it.mycraft.powerlib.bukkit.utils.NexoUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;

/**
 * Detects right-click interactions with Nexo furniture / custom blocks (via {@link NexoUtils}) and
 * re-fires them as a dependency-free {@link NexoFurnitureInteractEvent}. Registered only when Nexo is
 * present, via {@link #register(Plugin)}.
 */
public final class NexoListener implements Listener {

    private NexoListener() {
    }

    /**
     * Registers the Nexo bridge if (and only if) Nexo is available on this server; otherwise a no-op.
     *
     * @param plugin the plugin to register the listener under
     */
    public static void register(Plugin plugin) {
        if (!NexoUtils.isAvailable()) {
            return;
        }
        Bukkit.getPluginManager().registerEvents(new NexoListener(), plugin);
        plugin.getLogger().info("[PowerLib] Nexo furniture bridge enabled.");
    }

    /**
     * Re-fires right-clicks on Nexo custom blocks as a {@link NexoFurnitureInteractEvent}.
     *
     * @param event the originating interact event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK
                || event.getHand() != EquipmentSlot.HAND
                || event.getClickedBlock() == null) {
            return;
        }
        fire(event.getPlayer(), NexoUtils.getNexoId(event.getClickedBlock()), event.getClickedBlock(), event);
    }

    /**
     * Re-fires right-clicks on Nexo furniture entities as a {@link NexoFurnitureInteractEvent}.
     *
     * @param event the originating interact event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        fire(event.getPlayer(), NexoUtils.getNexoId(event.getRightClicked()), event.getRightClicked(), event);
    }

    private void fire(Player player, String furnitureId, Object nexoFurniture, Cancellable source) {
        if (furnitureId == null || furnitureId.isEmpty()) {
            return;
        }
        NexoFurnitureInteractEvent event = new NexoFurnitureInteractEvent(player, furnitureId, nexoFurniture);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            source.setCancelled(true);
        }
    }
}
