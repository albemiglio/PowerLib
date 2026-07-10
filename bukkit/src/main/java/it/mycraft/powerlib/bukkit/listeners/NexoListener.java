package it.mycraft.powerlib.bukkit.listeners;

import it.mycraft.powerlib.bukkit.events.NexoFurnitureInteractEvent;
import it.mycraft.powerlib.bukkit.events.NexoFurniturePlaceEvent;
import it.mycraft.powerlib.bukkit.utils.NexoUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.Event.Result;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * Central Nexo furniture listener for PowerLib.
 * Detects Nexo furniture interactions using standard Bukkit events
 * and NexoUtils, then fires a {@link NexoFurnitureInteractEvent}
 * so downstream plugins can listen without writing detection code.
 */
public class NexoListener implements Listener {

    private final Plugin plugin;

    public NexoListener(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        registerNativeNexoFurnitureInteractEvent();
        registerNativeNexoFurniturePlaceEvent();
        registerNativeNexoFurnitureBreakEvent();
        plugin.getLogger().info("[PowerLib] Nexo Furniture Bridge enabled.");
    }

    @SuppressWarnings("unchecked")
    private void registerNativeNexoFurnitureInteractEvent() {
        try {
            Class<?> nativeEventClass = Class.forName("com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent");
            if (!Event.class.isAssignableFrom(nativeEventClass)) {
                return;
            }

            EventExecutor executor = (listener, event) -> {
                if (!nativeEventClass.isInstance(event)) {
                    return;
                }
                handleNativeNexoFurnitureEvent(event);
            };

            Bukkit.getPluginManager().registerEvent(
                    (Class<? extends Event>) nativeEventClass,
                    this,
                    EventPriority.NORMAL,
                    executor,
                    plugin,
                    true
            );
            plugin.getLogger().info("[PowerLib] Native Nexo furniture interact event hooked.");
        } catch (ClassNotFoundException ignored) {
            plugin.getLogger().info("[PowerLib] Native Nexo furniture interact event not found; using Bukkit fallback.");
        } catch (Exception ex) {
            plugin.getLogger().log(Level.WARNING, "[PowerLib] Could not hook native Nexo furniture event.", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private void registerNativeNexoFurniturePlaceEvent() {
        try {
            Class<?> nativeEventClass = Class.forName("com.nexomc.nexo.api.events.furniture.NexoFurniturePlaceEvent");
            if (!Event.class.isAssignableFrom(nativeEventClass)) {
                return;
            }

            EventExecutor executor = (listener, event) -> {
                if (!nativeEventClass.isInstance(event)) {
                    return;
                }
                handleNativeNexoFurniturePlaceEvent(event);
            };

            Bukkit.getPluginManager().registerEvent(
                    (Class<? extends Event>) nativeEventClass,
                    this,
                    EventPriority.NORMAL,
                    executor,
                    plugin,
                    true
            );
            plugin.getLogger().info("[PowerLib] Native Nexo furniture place event hooked.");
        } catch (ClassNotFoundException ignored) {
            plugin.getLogger().info("[PowerLib] Native Nexo furniture place event not found.");
        } catch (Exception ex) {
            plugin.getLogger().log(Level.WARNING, "[PowerLib] Could not hook native Nexo furniture place event.", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private void registerNativeNexoFurnitureBreakEvent() {
        try {
            Class<?> nativeEventClass = Class.forName("com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent");
            if (!Event.class.isAssignableFrom(nativeEventClass)) {
                return;
            }

            EventExecutor executor = (listener, event) -> {
                if (!nativeEventClass.isInstance(event)) {
                    return;
                }
                handleNativeNexoFurnitureBreakEvent(event);
            };

            Bukkit.getPluginManager().registerEvent(
                    (Class<? extends Event>) nativeEventClass,
                    this,
                    EventPriority.MONITOR,
                    executor,
                    plugin,
                    true
            );
            plugin.getLogger().info("[PowerLib] Native Nexo furniture break event hooked.");
        } catch (ClassNotFoundException ignored) {
            plugin.getLogger().info("[PowerLib] Native Nexo furniture break event not found.");
        } catch (Exception ex) {
            plugin.getLogger().log(Level.WARNING, "[PowerLib] Could not hook native Nexo furniture break event.", ex);
        }
    }

    private void handleNativeNexoFurniturePlaceEvent(Event event) {
        try {
            EquipmentSlot hand = invoke(event, "getHand", EquipmentSlot.class);
            if (hand != null && hand != EquipmentSlot.HAND) {
                return;
            }

            Player player = invoke(event, "getPlayer", Player.class);
            Object mechanic = invoke(event, "getMechanic", Object.class);
            Object baseEntity = invoke(event, "getBaseEntity", Object.class);
            Block block = invoke(event, "getBlock", Block.class);
            ItemStack itemInHand = invoke(event, "getItemInHand", ItemStack.class);
            if (player == null || mechanic == null || baseEntity == null) {
                return;
            }

            String furnitureId = invokeString(mechanic, "getItemID");
            if (furnitureId == null || furnitureId.isEmpty()) {
                return;
            }

            NexoFurniturePlaceEvent powerEvent =
                    new NexoFurniturePlaceEvent(player, furnitureId, baseEntity, block, itemInHand, hand);
            Bukkit.getPluginManager().callEvent(powerEvent);

            if (powerEvent.isCancelled() && event instanceof Cancellable cancellable) {
                cancellable.setCancelled(true);
            }
        } catch (Exception ex) {
            plugin.getLogger().log(Level.WARNING, "Error handling native Nexo furniture place", ex);
        }
    }

    private void handleNativeNexoFurnitureEvent(Event event) {
        try {
            EquipmentSlot hand = invoke(event, "getHand", EquipmentSlot.class);
            if (hand != null && hand != EquipmentSlot.HAND) {
                return;
            }

            Player player = invoke(event, "getPlayer", Player.class);
            Object mechanic = invoke(event, "getMechanic", Object.class);
            Object baseEntity = invoke(event, "getBaseEntity", Object.class);
            Location interactionPoint = invoke(event, "getInteractionPoint", Location.class);
            if (player == null || mechanic == null || baseEntity == null) {
                return;
            }

            String furnitureId = invokeString(mechanic, "getItemID");
            if (furnitureId == null || furnitureId.isEmpty()) {
                return;
            }

            NexoFurnitureInteractEvent powerEvent =
                    new NexoFurnitureInteractEvent(player, furnitureId, baseEntity);
            Bukkit.getPluginManager().callEvent(powerEvent);

            if (powerEvent.isCancelled()) {
                if (event instanceof Cancellable cancellable) {
                    cancellable.setCancelled(true);
                }
                setResult(event, "setUseFurniture", Result.DENY);
                setResult(event, "setUseItemInHand", Result.DENY);
                setResult(event, "setCanRunAction", Result.DENY);
            }
        } catch (Exception ex) {
            plugin.getLogger().log(Level.WARNING, "Error handling native Nexo furniture interaction", ex);
        }
    }

    private void handleNativeNexoFurnitureBreakEvent(Event event) {
        try {
            Player player = invoke(event, "getPlayer", Player.class);
            Object mechanic = invoke(event, "getMechanic", Object.class);
            Object baseEntity = invoke(event, "getBaseEntity", Object.class);
            if (player == null || mechanic == null || baseEntity == null) {
                return;
            }

            String furnitureId = invokeString(mechanic, "getItemID");
            if (furnitureId == null || furnitureId.isEmpty()) {
                return;
            }

            it.mycraft.powerlib.bukkit.events.NexoFurnitureBreakEvent powerEvent =
                    new it.mycraft.powerlib.bukkit.events.NexoFurnitureBreakEvent(player, furnitureId, baseEntity);
            Bukkit.getPluginManager().callEvent(powerEvent);
        } catch (Exception ex) {
            plugin.getLogger().log(Level.WARNING, "Error handling native Nexo furniture break", ex);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getClickedBlock() == null) return;

        try {
            String furnitureId = NexoUtils.getNexoId(event.getClickedBlock());
            if (furnitureId == null || furnitureId.isEmpty()) return;

            NexoFurnitureInteractEvent powerEvent =
                    new NexoFurnitureInteractEvent(event.getPlayer(), furnitureId, event.getClickedBlock());
            Bukkit.getPluginManager().callEvent(powerEvent);

            if (powerEvent.isCancelled()) {
                event.setCancelled(true);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error detecting Nexo block interaction", e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getRightClicked() == null) return;

        try {
            String furnitureId = NexoUtils.getNexoId(event.getRightClicked());
            if (furnitureId == null || furnitureId.isEmpty()) return;

            NexoFurnitureInteractEvent powerEvent =
                    new NexoFurnitureInteractEvent(event.getPlayer(), furnitureId, event.getRightClicked());
            Bukkit.getPluginManager().callEvent(powerEvent);

            if (powerEvent.isCancelled()) {
                event.setCancelled(true);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error detecting Nexo entity interaction", e);
        }
    }

    public boolean isNexoAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("Nexo");
    }

    private <T> T invoke(Object source, String methodName, Class<T> type) {
        if (source == null) {
            return null;
        }
        try {
            Method method = source.getClass().getMethod(methodName);
            Object value = method.invoke(source);
            return type.isInstance(value) ? type.cast(value) : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private String invokeString(Object source, String methodName) {
        Object value = invoke(source, methodName, Object.class);
        return value instanceof String ? (String) value : null;
    }

    private void setResult(Object source, String methodName, Result result) {
        try {
            Method method = source.getClass().getMethod(methodName, Result.class);
            method.invoke(source, result);
        } catch (Exception ignored) {
        }
    }
}
