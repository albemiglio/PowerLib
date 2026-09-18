package com.nexomc.nexo.api.events.furniture;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Stand-in for Nexo's own furniture place event, under Nexo's package name. Placement has no Bukkit
 * counterpart at all, so this is the only source PowerLib's {@code NexoFurniturePlaceEvent} can have.
 */
public class NexoFurniturePlaceEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    /**
     * Loosely typed on purpose: PowerLib reads it reflectively, so a test can hand in the mechanic of a
     * drifted Nexo build — one whose class no longer exposes {@code getItemID()} at all.
     */
    private final Object mechanic;
    private final Entity baseEntity;
    private final Block block;
    private final ItemStack itemInHand;
    private final EquipmentSlot hand;

    private boolean cancelled;

    public NexoFurniturePlaceEvent(Player player, Object mechanic, Entity baseEntity,
                                   Block block, ItemStack itemInHand, EquipmentSlot hand) {
        this.player = player;
        this.mechanic = mechanic;
        this.baseEntity = baseEntity;
        this.block = block;
        this.itemInHand = itemInHand;
        this.hand = hand;
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

    public Block getBlock() {
        return block;
    }

    public ItemStack getItemInHand() {
        return itemInHand;
    }

    public EquipmentSlot getHand() {
        return hand;
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
