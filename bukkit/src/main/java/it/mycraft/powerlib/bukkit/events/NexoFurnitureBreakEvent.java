package it.mycraft.powerlib.bukkit.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when a Nexo furniture is broken.
 * This event wraps Nexo's native NexoFurnitureBreakEvent
 * so downstream plugins can clean up state without a direct Nexo dependency.
 */
@Getter
public class NexoFurnitureBreakEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String furnitureId;
    private final Object nexoFurniture;

    public NexoFurnitureBreakEvent(Player player, String furnitureId, Object nexoFurniture) {
        this.player = player;
        this.furnitureId = furnitureId;
        this.nexoFurniture = nexoFurniture;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
