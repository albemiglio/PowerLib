package it.mycraft.powerlib.bukkit.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when a player breaks a Nexo furniture or custom block. Carries the Nexo id (and the raw
 * Nexo mechanic as an {@code Object}) so downstream plugins can react without a direct Nexo dependency.
 *
 * <p>Counterpart of {@link NexoFurnitureInteractEvent}: plugins that keep per-furniture state keyed by
 * location need a break signal to release that state when the furniture goes away.
 */
@Getter
public class NexoFurnitureBreakEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String furnitureId;
    private final Object nexoFurniture;

    @Setter
    private boolean cancelled;

    /**
     * Creates the event.
     *
     * @param player        the player who broke the furniture or custom block
     * @param furnitureId   the Nexo id of the furniture or custom block
     * @param nexoFurniture the raw Nexo mechanic object
     */
    public NexoFurnitureBreakEvent(Player player, String furnitureId, Object nexoFurniture) {
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
