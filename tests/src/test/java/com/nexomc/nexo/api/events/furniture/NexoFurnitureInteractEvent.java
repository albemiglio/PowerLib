package com.nexomc.nexo.api.events.furniture;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Stand-in for Nexo's own furniture interact event, under Nexo's package name so PowerLib's bridge binds
 * to it exactly as it would on a live server. Only the surface the bridge reads is reproduced: the three
 * {@code setXxx(Result)} setters are what makes cancelling a furniture interaction actually stick, and a
 * plain Bukkit event cannot express them.
 */
public class NexoFurnitureInteractEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    /**
     * Loosely typed on purpose: PowerLib reads it reflectively, so a test can hand in the mechanic of a
     * drifted Nexo build — one whose class no longer exposes {@code getItemID()} at all.
     */
    private final Object mechanic;
    private final Entity baseEntity;
    private final EquipmentSlot hand;
    private final RuntimeException baseEntityFailure;

    private boolean cancelled;
    private Result useFurniture = Result.DEFAULT;
    private Result useItemInHand = Result.DEFAULT;
    private Result canRunAction = Result.DEFAULT;

    public NexoFurnitureInteractEvent(Player player, Object mechanic, Entity baseEntity,
                                      EquipmentSlot hand) {
        this(player, mechanic, baseEntity, hand, null);
    }

    /**
     * @param baseEntityFailure what {@link #getBaseEntity()} throws instead of answering, the way Nexo's
     *                          own Kotlin accessor does for furniture that is already gone
     */
    public NexoFurnitureInteractEvent(Player player, Object mechanic, Entity baseEntity, EquipmentSlot hand,
                                      RuntimeException baseEntityFailure) {
        this.player = player;
        this.mechanic = mechanic;
        this.baseEntity = baseEntity;
        this.hand = hand;
        this.baseEntityFailure = baseEntityFailure;
    }

    public Player getPlayer() {
        return player;
    }

    public Object getMechanic() {
        return mechanic;
    }

    public Entity getBaseEntity() {
        if (baseEntityFailure != null) {
            throw baseEntityFailure;
        }
        return baseEntity;
    }

    public EquipmentSlot getHand() {
        return hand;
    }

    public Result getUseFurniture() {
        return useFurniture;
    }

    public void setUseFurniture(Result useFurniture) {
        this.useFurniture = useFurniture;
    }

    public Result getUseItemInHand() {
        return useItemInHand;
    }

    public void setUseItemInHand(Result useItemInHand) {
        this.useItemInHand = useItemInHand;
    }

    public Result getCanRunAction() {
        return canRunAction;
    }

    public void setCanRunAction(Result canRunAction) {
        this.canRunAction = canRunAction;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
