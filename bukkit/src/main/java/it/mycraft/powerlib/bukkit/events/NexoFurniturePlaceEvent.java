package it.mycraft.powerlib.bukkit.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Fired when a Nexo furniture is placed.
 * This event wraps Nexo's native NexoFurniturePlaceEvent
 * so downstream plugins can react without a direct Nexo dependency.
 */
@Getter
public class NexoFurniturePlaceEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String furnitureId;
    private final Object nexoFurniture;
    private final Block block;
    private final Location location;
    private final ItemStack itemInHand;
    private final EquipmentSlot hand;

    @Setter
    private boolean cancelled;

    public NexoFurniturePlaceEvent(
            Player player,
            String furnitureId,
            Object nexoFurniture,
            Block block,
            ItemStack itemInHand,
            EquipmentSlot hand) {
        this.player = player;
        this.furnitureId = furnitureId;
        this.nexoFurniture = nexoFurniture;
        this.block = block;
        this.location = resolveLocation(nexoFurniture, block);
        this.itemInHand = itemInHand;
        this.hand = hand;
    }

    private Location resolveLocation(Object nexoFurniture, Block block) {
        if (nexoFurniture instanceof org.bukkit.entity.Entity entity) {
            return entity.getLocation();
        }
        if (block != null) {
            return block.getLocation();
        }
        return null;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
