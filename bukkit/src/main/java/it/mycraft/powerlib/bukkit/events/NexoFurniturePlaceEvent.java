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
 * Fired when a player places a Nexo furniture. Carries the Nexo id (and the raw base entity as an
 * {@code Object}) so downstream plugins can react without a direct Nexo dependency.
 *
 * <p>Unlike {@link NexoFurnitureInteractEvent} and {@link NexoFurnitureBreakEvent}, placement cannot be
 * derived from Bukkit events: the furniture entity is spawned by Nexo itself. This event is therefore
 * fired only on servers whose Nexo build exposes a native placement event, and cancelling it cancels the
 * placement itself.
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
     * @param player        the player placing the furniture
     * @param furnitureId   the Nexo id of the furniture being placed
     * @param nexoFurniture the raw Nexo base entity
     * @param block         the block the furniture is placed against, or {@code null}
     * @param itemInHand    the item used to place the furniture, or {@code null}
     * @param hand          the hand holding that item, or {@code null}
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

    /**
     * Where the furniture is being placed: the base entity's location when there is one, otherwise the
     * target block's, otherwise {@code null}.
     *
     * @param nexoFurniture the raw Nexo base entity
     * @param block         the block the furniture is placed against
     * @return the placement location, or {@code null} if neither is known
     */
    private static Location resolveLocation(Object nexoFurniture, Block block) {
        if (nexoFurniture instanceof Entity entity) {
            return entity.getLocation();
        }
        return block == null ? null : block.getLocation();
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
