package com.nexomc.nexo.api.events.furniture;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Stand-in for Nexo's own furniture break event, under Nexo's package name. It replaces the
 * {@code EntityDamageByEntityEvent} heuristic with the break Nexo actually performed.
 */
public class NexoFurnitureBreakEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    /**
     * Loosely typed on purpose: PowerLib reads it reflectively, so a test can hand in the mechanic of a
     * drifted Nexo build — one whose class no longer exposes {@code getItemID()} at all.
     */
    private final Object mechanic;
    private final Entity baseEntity;

    private boolean cancelled;

    public NexoFurnitureBreakEvent(Player player, Object mechanic, Entity baseEntity) {
        this.player = player;
        this.mechanic = mechanic;
        this.baseEntity = baseEntity;
    }

    public Player getPlayer() {
        return player;
    }

    public Object getMechanic() {
        return mechanic;
    }

    public Entity getBaseEntity() {
        return baseEntity;
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
