package it.mycraft.powerlib.bukkit.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class NexoFurnitureInteractEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String furnitureId;
    private final Object nexoFurniture;

    @Setter
    private boolean cancelled;

    public NexoFurnitureInteractEvent(Player player, String furnitureId, Object nexoFurniture) {
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
