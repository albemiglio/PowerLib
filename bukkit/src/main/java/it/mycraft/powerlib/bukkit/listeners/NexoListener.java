package it.mycraft.powerlib.bukkit.listeners;

import it.mycraft.powerlib.bukkit.events.NexoFurnitureBreakEvent;
import it.mycraft.powerlib.bukkit.events.NexoFurnitureInteractEvent;
import it.mycraft.powerlib.bukkit.utils.NexoUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;

/**
 * Detects interactions with Nexo furniture / custom blocks (via {@link NexoUtils}) and re-fires them as
 * dependency-free {@link NexoFurnitureInteractEvent} / {@link NexoFurnitureBreakEvent}. Registered only
 * when Nexo is present, via {@link #register(Plugin)}.
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

    /**
     * Re-fires the breaking of a Nexo custom block (or a furniture's barrier hitbox) as a
     * {@link NexoFurnitureBreakEvent}.
     *
     * @param event the originating block break event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        fireBreak(event.getPlayer(), NexoUtils.getNexoId(event.getBlock()), event.getBlock(), event);
    }

    /**
     * Re-fires a player breaking a Nexo furniture entity as a {@link NexoFurnitureBreakEvent}. Furniture
     * is removed by damaging its entity, so the player's hit is the break signal.
     *
     * @param event the originating damage event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityBreak(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }
        Entity furniture = event.getEntity();
        fireBreak(player, NexoUtils.getNexoId(furniture), furniture, event);
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

    private void fireBreak(Player player, String furnitureId, Object nexoFurniture, Cancellable source) {
        if (furnitureId == null || furnitureId.isEmpty()) {
            return;
        }
        NexoFurnitureBreakEvent event = new NexoFurnitureBreakEvent(player, furnitureId, nexoFurniture);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            source.setCancelled(true);
        }
    }
}
