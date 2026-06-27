package it.mycraft.powerlib.bukkit.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when a player right-clicks a Nexo furniture or custom block. Carries the Nexo id (and the raw
 * Nexo mechanic as an {@code Object}) so downstream plugins can react without a direct Nexo dependency.
 */
@Getter
public class NexoFurnitureInteractEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String furnitureId;
    private final Object nexoFurniture;

    @Setter
    private boolean cancelled;

    /**
     * Creates the event.
     *
     * @param player        the player who interacted
     * @param furnitureId   the Nexo id of the furniture or custom block
     * @param nexoFurniture the raw Nexo mechanic object
     */
    public NexoFurnitureInteractEvent(Player player, String furnitureId, Object nexoFurniture) {
        this.player = player;
        this.furnitureId = furnitureId;
        this.nexoFurniture = nexoFurniture;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Returns the handler list for this event type, as required by the Bukkit event system.
     *
     * @return the shared handler list
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
