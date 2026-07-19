package it.mycraft.powerlib.bukkit.listeners;

import it.mycraft.powerlib.bukkit.events.NexoFurnitureBreakEvent;
import it.mycraft.powerlib.bukkit.events.NexoFurnitureInteractEvent;
import it.mycraft.powerlib.bukkit.events.NexoFurniturePlaceEvent;
import it.mycraft.powerlib.bukkit.utils.NexoUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Detects interactions with Nexo furniture / custom blocks and re-fires them as dependency-free
 * {@link NexoFurnitureInteractEvent} / {@link NexoFurnitureBreakEvent}. Registered only when Nexo is
 * present, via {@link #register(Plugin)}.
 * <p>
 * Two detection paths are used. When Nexo exposes its own furniture events they are hooked reflectively
 * and preferred: Nexo resolves the furniture mechanic itself, so the id is reliable even though the
 * clickable part of a furniture is an interaction entity or a barrier hitbox rather than the base entity.
 * The Bukkit path ({@link NexoUtils}) stays registered as a fallback and keeps serving Nexo custom blocks,
 * which the native furniture events do not cover.
 */
public final class NexoListener implements Listener {

    private static final String NATIVE_INTERACT_EVENT = "com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent";
    private static final String NATIVE_BREAK_EVENT = "com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent";
    private static final String NATIVE_PLACE_EVENT = "com.nexomc.nexo.api.events.furniture.NexoFurniturePlaceEvent";

    private boolean nativeInteractHooked;
    private boolean nativeBreakHooked;

    private NexoListener() {
    }

    /**
     * Registers the Nexo bridge if (and only if) Nexo is available on this server; otherwise a no-op.
     *
     * @param plugin the plugin to register the listener under
     */
    public static void register(Plugin plugin) {
        if (!NexoUtils.isAvailable()) {
            return;
        }
        NexoListener listener = new NexoListener();
        listener.nativeInteractHooked =
                listener.hookNative(plugin, NATIVE_INTERACT_EVENT, listener::handleNativeInteract);
        listener.nativeBreakHooked =
                listener.hookNative(plugin, NATIVE_BREAK_EVENT, listener::handleNativeBreak);
        listener.hookNative(plugin, NATIVE_PLACE_EVENT, listener::handleNativePlace);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        plugin.getLogger().info("[PowerLib] Nexo furniture bridge enabled.");
    }

    /**
     * Re-fires right-clicks on Nexo custom blocks as a {@link NexoFurnitureInteractEvent}.
     *
     * @param event the originating interact event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK
                || event.getHand() != EquipmentSlot.HAND
                || event.getClickedBlock() == null) {
            return;
        }
        fire(event.getPlayer(), NexoUtils.getNexoId(event.getClickedBlock()), event.getClickedBlock(), event);
    }

    /**
     * Re-fires right-clicks on Nexo furniture entities as a {@link NexoFurnitureInteractEvent}.
     *
     * @param event the originating interact event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        fire(event.getPlayer(), NexoUtils.getNexoId(event.getRightClicked()), event.getRightClicked(), event);
    }

    /**
     * Re-fires the breaking of a Nexo custom block (or a furniture's barrier hitbox) as a
     * {@link NexoFurnitureBreakEvent}.
     *
     * @param event the originating block break event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        fireBreak(event.getPlayer(), NexoUtils.getNexoId(event.getBlock()), event.getBlock(), event);
    }

    /**
     * Re-fires a player breaking a Nexo furniture entity as a {@link NexoFurnitureBreakEvent}. Furniture
     * is removed by damaging its entity, so the player's hit is the break signal.
     *
     * @param event the originating damage event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityBreak(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }
        Entity furniture = event.getEntity();
        fireBreak(player, NexoUtils.getNexoId(furniture), furniture, event);
    }

    /**
     * Hooks a Nexo event by name, so PowerLib keeps compiling and running without Nexo on the classpath.
     *
     * @return whether the event was found and hooked
     */
    @SuppressWarnings("unchecked")
    private boolean hookNative(Plugin plugin, String className, Consumer<Event> handler) {
        try {
            Class<?> nativeEvent = Class.forName(className);
            if (!Event.class.isAssignableFrom(nativeEvent)) {
                return false;
            }
            Bukkit.getPluginManager().registerEvent(
                    (Class<? extends Event>) nativeEvent,
                    this,
                    EventPriority.NORMAL,
                    (listener, event) -> {
                        if (nativeEvent.isInstance(event)) {
                            handler.accept(event);
                        }
                    },
                    plugin,
                    true
            );
            return true;
        } catch (ClassNotFoundException absent) {
            // Older Nexo builds without the furniture events: the Bukkit fallback below stays in charge.
            return false;
        } catch (Exception ex) {
            plugin.getLogger().log(Level.WARNING, "[PowerLib] Could not hook the native Nexo event " + className, ex);
            return false;
        }
    }

    private void handleNativeInteract(Event event) {
        EquipmentSlot hand = invoke(event, "getHand", EquipmentSlot.class);
        if (hand != null && hand != EquipmentSlot.HAND) {
            return;
        }
        Player player = invoke(event, "getPlayer", Player.class);
        Object baseEntity = invoke(event, "getBaseEntity", Object.class);
        String furnitureId = furnitureId(event);
        if (player == null || baseEntity == null || furnitureId == null) {
            return;
        }

        NexoFurnitureInteractEvent bridged = new NexoFurnitureInteractEvent(player, furnitureId, baseEntity);
        Bukkit.getPluginManager().callEvent(bridged);
        if (!bridged.isCancelled()) {
            return;
        }
        if (event instanceof Cancellable cancellable) {
            cancellable.setCancelled(true);
        }
        // Nexo decides what to do with an interaction through these results, not only through cancellation.
        setResult(event, "setUseFurniture", Result.DENY);
        setResult(event, "setUseItemInHand", Result.DENY);
        setResult(event, "setCanRunAction", Result.DENY);
    }

    private void handleNativePlace(Event event) {
        EquipmentSlot hand = invoke(event, "getHand", EquipmentSlot.class);
        if (hand != null && hand != EquipmentSlot.HAND) {
            return;
        }
        Player player = invoke(event, "getPlayer", Player.class);
        Object baseEntity = invoke(event, "getBaseEntity", Object.class);
        String furnitureId = furnitureId(event);
        if (player == null || baseEntity == null || furnitureId == null) {
            return;
        }

        NexoFurniturePlaceEvent bridged = new NexoFurniturePlaceEvent(player, furnitureId, baseEntity,
                invoke(event, "getBlock", Block.class), invoke(event, "getItemInHand", ItemStack.class), hand);
        Bukkit.getPluginManager().callEvent(bridged);
        if (bridged.isCancelled() && event instanceof Cancellable cancellable) {
            cancellable.setCancelled(true);
        }
    }

    private void handleNativeBreak(Event event) {
        Player player = invoke(event, "getPlayer", Player.class);
        Object baseEntity = invoke(event, "getBaseEntity", Object.class);
        String furnitureId = furnitureId(event);
        if (player == null || baseEntity == null || furnitureId == null) {
            return;
        }

        NexoFurnitureBreakEvent bridged = new NexoFurnitureBreakEvent(player, furnitureId, baseEntity);
        Bukkit.getPluginManager().callEvent(bridged);
        if (bridged.isCancelled() && event instanceof Cancellable cancellable) {
            cancellable.setCancelled(true);
        }
    }

    private void fire(Player player, String furnitureId, Object nexoFurniture, Cancellable source) {
        if (skip(furnitureId, nativeInteractHooked)) {
            return;
        }
        NexoFurnitureInteractEvent event = new NexoFurnitureInteractEvent(player, furnitureId, nexoFurniture);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            source.setCancelled(true);
        }
    }

    private void fireBreak(Player player, String furnitureId, Object nexoFurniture, Cancellable source) {
        if (skip(furnitureId, nativeBreakHooked)) {
            return;
        }
        NexoFurnitureBreakEvent event = new NexoFurnitureBreakEvent(player, furnitureId, nexoFurniture);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            source.setCancelled(true);
        }
    }

    /**
     * Whether the Bukkit fallback must stand down: the native hook already bridged this furniture, and
     * firing again would deliver the same interaction twice. Custom blocks are never covered by the
     * native furniture events, so they always go through.
     */
    private boolean skip(String furnitureId, boolean nativeHooked) {
        return furnitureId == null || furnitureId.isEmpty() || (nativeHooked && NexoUtils.isKnownFurniture(furnitureId));
    }

    private String furnitureId(Event event) {
        Object mechanic = invoke(event, "getMechanic", Object.class);
        if (mechanic == null) {
            return null;
        }
        Object id = invoke(mechanic, "getItemID", Object.class);
        return id instanceof String furnitureId && !furnitureId.isEmpty() ? furnitureId : null;
    }

    private <T> T invoke(Object source, String methodName, Class<T> type) {
        if (source == null) {
            return null;
        }
        try {
            Method method = source.getClass().getMethod(methodName);
            Object value = method.invoke(source);
            return type.isInstance(value) ? type.cast(value) : null;
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private void setResult(Object source, String methodName, Result result) {
        try {
            Method method = source.getClass().getMethod(methodName, Result.class);
            method.invoke(source, result);
        } catch (ReflectiveOperationException ignored) {
            // Not every Nexo build exposes every result setter; cancellation above is the baseline.
        }
    }
}
