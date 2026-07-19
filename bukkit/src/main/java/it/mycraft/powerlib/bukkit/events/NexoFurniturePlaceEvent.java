package it.mycraft.powerlib.bukkit.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Fired when a player places a Nexo furniture. Carries the Nexo id (and the raw Nexo mechanic as an
 * {@code Object}) so downstream plugins can react without a direct Nexo dependency.
 * <p>
 * Unlike interact and break, placement has no Bukkit fallback: it is only fired when Nexo exposes its
 * native furniture place event, because the placed furniture cannot be identified reliably from a plain
 * block-place event.
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

    /**
     * Creates the event.
     *
     * @param player        the player who placed the furniture
     * @param furnitureId   the Nexo id of the placed furniture
     * @param nexoFurniture the raw Nexo mechanic object
     * @param block         the block the furniture was placed against, if any; when it is {@code null}
     *                      and the mechanic is not an entity, {@code getLocation()} is {@code null} too
     * @param itemInHand    the item used to place the furniture
     * @param hand          the hand the furniture was placed with
     */
    public NexoFurniturePlaceEvent(Player player, String furnitureId, Object nexoFurniture, Block block,
                                   ItemStack itemInHand, EquipmentSlot hand) {
        this.player = player;
        this.furnitureId = furnitureId;
        this.nexoFurniture = nexoFurniture;
        this.block = block;
        this.location = resolveLocation(nexoFurniture, block);
        this.itemInHand = itemInHand;
        this.hand = hand;
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

    private static Location resolveLocation(Object nexoFurniture, Block block) {
        if (nexoFurniture instanceof Entity entity) {
            return entity.getLocation();
        }
        return block != null ? block.getLocation() : null;
    }
}
