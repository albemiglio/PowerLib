package it.mycraft.powerlib.bukkit.listeners;

import it.mycraft.powerlib.bukkit.events.NexoFurnitureInteractEvent;
import it.mycraft.powerlib.bukkit.utils.NexoUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class NexoListener implements Listener {

    private final Plugin plugin;

    public NexoListener(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("Nexo Furniture Bridge enabled.");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getClickedBlock() == null) return;

        try {
            String furnitureId = NexoUtils.getNexoId(event.getClickedBlock());
            if (furnitureId == null || furnitureId.isEmpty()) return;

            NexoFurnitureInteractEvent powerEvent =
                    new NexoFurnitureInteractEvent(event.getPlayer(), furnitureId, event.getClickedBlock());
            Bukkit.getPluginManager().callEvent(powerEvent);

            if (powerEvent.isCancelled()) {
                event.setCancelled(true);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error detecting Nexo block interaction", e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getRightClicked() == null) return;

        try {
            String furnitureId = NexoUtils.getNexoId(event.getRightClicked());
            if (furnitureId == null || furnitureId.isEmpty()) return;

            NexoFurnitureInteractEvent powerEvent =
                    new NexoFurnitureInteractEvent(event.getPlayer(), furnitureId, event.getRightClicked());
            Bukkit.getPluginManager().callEvent(powerEvent);

            if (powerEvent.isCancelled()) {
                event.setCancelled(true);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error detecting Nexo entity interaction", e);
        }
    }
}
