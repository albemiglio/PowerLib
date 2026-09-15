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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

/**
 * Detects interactions with Nexo furniture / custom blocks and re-fires them as dependency-free
 * {@link NexoFurnitureInteractEvent} / {@link NexoFurnitureBreakEvent} / {@link NexoFurniturePlaceEvent}.
 * Registered only when Nexo is present, via {@link #register(Plugin)}.
 *
 * <p><b>Two sources, never both for the same action.</b> When the installed Nexo build publishes its own
 * furniture events they are hooked <em>by name</em> (reflection — see {@link NexoUtils} for why PowerLib
 * never compiles against Nexo) and become the authoritative source: they report the furniture Nexo really
 * acted on, they cover placement — which no Bukkit event exposes — and cancelling them lets PowerLib also
 * deny Nexo's own follow-up actions ({@code setUseFurniture} / {@code setUseItemInHand} /
 * {@code setCanRunAction}), which cancelling a Bukkit event cannot do. The Bukkit handlers below then stand
 * down for <em>furniture</em> and keep serving Nexo <em>custom blocks</em>, which the native furniture
 * events do not cover. On an older Nexo build the Bukkit handlers carry everything, as before.
 *
 * <p>Every reflective handle is resolved once at registration, never per event.
 */
public final class NexoListener implements Listener {

    private static final String NATIVE_EVENTS = "com.nexomc.nexo.api.events.furniture.";

    /** The registered bridge, kept so {@link #unregister()} can detach exactly it. */
    private static NexoListener registered;

    private static Method denyUseFurniture;
    private static Method denyUseItemInHand;
    private static Method denyCanRunAction;

    /** {@code Mechanic.getItemID()}; resolved on Nexo's base class so Kotlin subclasses stay reachable. */
    private static Method mechanicItemId;

    /** Null when this Nexo build has no such native event: the Bukkit handlers then cover it. */
    private NativeEvent interact;
    private NativeEvent place;
    private NativeEvent broken;

    private NexoListener() {
    }

    /**
     * Registers the Nexo bridge if (and only if) Nexo is available on this server; otherwise a no-op.
     * Registering again replaces the previous bridge.
     *
     * @param plugin the plugin to register the listener under
     */
    public static void register(Plugin plugin) {
        if (!NexoUtils.isAvailable()) {
            return;
        }
        unregister();

        NexoListener listener = bindNativeEvents();
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        hook(plugin, listener, listener.interact, listener::onNativeInteract);
        hook(plugin, listener, listener.place, listener::onNativePlace);
        hook(plugin, listener, listener.broken, listener::onNativeBreak);
        registered = listener;

        plugin.getLogger().info("[PowerLib] Nexo furniture bridge enabled ("
                + (listener.interact == null ? "Bukkit events" : "native Nexo events") + ").");
    }

    /**
     * Detaches the bridge registered by {@link #register(Plugin)}, if any. Safe to call twice.
     */
    public static void unregister() {
        if (registered != null) {
            HandlerList.unregisterAll(registered);
            registered = null;
        }
    }

    /**
     * Builds a bridge with every native handle resolved, without touching the plugin manager. Split from
     * {@link #register(Plugin)} so the native paths are unit-testable against stand-in event classes
     * carrying Nexo's package name (see {@code NexoListenerTest}); when Nexo is absent every handle stays
     * null and only the Bukkit handlers do anything.
     *
     * @return a bridge bound to whatever native Nexo events this server exposes
     */
    static NexoListener bindNativeEvents() {
        NexoListener listener = new NexoListener();
        listener.interact = NativeEvent.of("NexoFurnitureInteractEvent");
        listener.place = NativeEvent.of("NexoFurniturePlaceEvent");
        listener.broken = NativeEvent.of("NexoFurnitureBreakEvent");
        if (listener.interact != null) {
            denyUseFurniture = denySetter(listener.interact.type, "setUseFurniture");
            denyUseItemInHand = denySetter(listener.interact.type, "setUseItemInHand");
            denyCanRunAction = denySetter(listener.interact.type, "setCanRunAction");
        }
        mechanicItemId = NativeEvent.accessor(optionalClass("com.nexomc.nexo.mechanics.Mechanic"), "getItemID");
        return listener;
    }

    private static void hook(Plugin plugin, NexoListener listener, NativeEvent nativeEvent, EventExecutor executor) {
        if (nativeEvent != null) {
            Bukkit.getPluginManager()
                    .registerEvent(nativeEvent.type, listener, EventPriority.NORMAL, executor, plugin, true);
        }
    }

    void onNativeInteract(Listener listener, Event event) {
        if (!(interact.read(interact.player, event) instanceof Player player)
                || !isMainHand(interact.read(interact.hand, event))) {
            return;
        }
        String furnitureId = furnitureId(interact.read(interact.mechanic, event));
        if (isBlank(furnitureId)) {
            return;
        }
        Object baseEntity = interact.read(interact.baseEntity, event);
        NexoFurnitureInteractEvent fired = new NexoFurnitureInteractEvent(player, furnitureId, baseEntity);
        Bukkit.getPluginManager().callEvent(fired);
        if (!fired.isCancelled()) {
            return;
        }
        cancel(event);
        // Cancelling Nexo's event is not enough on its own: Nexo decides separately whether the furniture
        // reacts, whether the held item is used, and whether the furniture's configured action runs.
        deny(denyUseFurniture, event);
        deny(denyUseItemInHand, event);
        deny(denyCanRunAction, event);
    }

    void onNativePlace(Listener listener, Event event) {
        if (!(place.read(place.player, event) instanceof Player player)
                || !isMainHand(place.read(place.hand, event))) {
            return;
        }
        String furnitureId = furnitureId(place.read(place.mechanic, event));
        if (isBlank(furnitureId)) {
            return;
        }
        NexoFurniturePlaceEvent fired = new NexoFurniturePlaceEvent(
                player,
                furnitureId,
                place.read(place.baseEntity, event),
                place.read(place.block, event) instanceof Block block ? block : null,
                place.read(place.itemInHand, event) instanceof ItemStack item ? item : null,
                place.read(place.hand, event) instanceof EquipmentSlot slot ? slot : null);
        Bukkit.getPluginManager().callEvent(fired);
        if (fired.isCancelled()) {
            cancel(event);
        }
    }

    void onNativeBreak(Listener listener, Event event) {
        if (!(broken.read(broken.player, event) instanceof Player player)) {
            return;
        }
        fireBreak(player,
                furnitureId(broken.read(broken.mechanic, event)),
                broken.read(broken.baseEntity, event),
                cancellableOf(event));
    }

    /**
     * Re-fires right-clicks on Nexo custom blocks as a {@link NexoFurnitureInteractEvent}. Furniture is
     * skipped whenever Nexo reports it natively.
     *
     * @param event the originating interact event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK
                || event.getHand() != EquipmentSlot.HAND
                || event.getClickedBlock() == null
                || reportedNatively(interact, event.getClickedBlock())) {
            return;
        }
        fire(event.getPlayer(), NexoUtils.getNexoId(event.getClickedBlock()), event.getClickedBlock(), event);
    }

    /**
     * Re-fires right-clicks on Nexo furniture entities as a {@link NexoFurnitureInteractEvent}, on servers
     * whose Nexo build has no native interact event.
     *
     * @param event the originating interact event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || reportedNatively(interact, event.getRightClicked())) {
            return;
        }
        fire(event.getPlayer(), NexoUtils.getNexoId(event.getRightClicked()), event.getRightClicked(), event);
    }

    /**
     * Re-fires the breaking of a Nexo custom block (or a furniture's barrier hitbox) as a
     * {@link NexoFurnitureBreakEvent}. Furniture is skipped whenever Nexo reports it natively.
     *
     * @param event the originating block break event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        if (reportedNatively(broken, event.getBlock())) {
            return;
        }
        fireBreak(event.getPlayer(), NexoUtils.getNexoId(event.getBlock()), event.getBlock(), event);
    }

    /**
     * Re-fires a player breaking a Nexo furniture entity as a {@link NexoFurnitureBreakEvent}, on servers
     * whose Nexo build has no native break event: furniture is removed by damaging its entity, so the
     * player's hit is the break signal.
     *
     * @param event the originating damage event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityBreak(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }
        Entity furniture = event.getEntity();
        if (reportedNatively(broken, furniture)) {
            return;
        }
        fireBreak(player, NexoUtils.getNexoId(furniture), furniture, event);
    }

    private void fire(Player player, String furnitureId, Object nexoFurniture, Cancellable source) {
        if (isBlank(furnitureId)) {
            return;
        }
        NexoFurnitureInteractEvent event = new NexoFurnitureInteractEvent(player, furnitureId, nexoFurniture);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled() && source != null) {
            source.setCancelled(true);
        }
    }

    private void fireBreak(Player player, String furnitureId, Object nexoFurniture, Cancellable source) {
        if (isBlank(furnitureId)) {
            return;
        }
        NexoFurnitureBreakEvent event = new NexoFurnitureBreakEvent(player, furnitureId, nexoFurniture);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled() && source != null) {
            source.setCancelled(true);
        }
    }

    /** Whether the native counterpart of {@code nativeEvent} already reports this target. */
    private static boolean reportedNatively(NativeEvent nativeEvent, Object target) {
        if (nativeEvent == null) {
            return false;
        }
        return target instanceof Block block
                ? NexoUtils.isFurniture(block)
                : target instanceof Entity entity && NexoUtils.isFurniture(entity);
    }

    /**
     * The Nexo id carried by the mechanic the native event already resolved — no second lookup, and it is
     * the furniture Nexo itself acted on.
     */
    private static String furnitureId(Object mechanic) {
        if (mechanic == null) {
            return null;
        }
        Method accessor = mechanicItemId;
        if (accessor == null || !accessor.getDeclaringClass().isInstance(mechanic)) {
            accessor = NativeEvent.accessor(mechanic.getClass(), "getItemID");
            mechanicItemId = accessor;
        }
        if (accessor == null) {
            return null;
        }
        try {
            return accessor.invoke(mechanic) instanceof String id ? id : null;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
    }

    private static Class<?> optionalClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException | LinkageError absent) {
            return null;
        }
    }

    /** Nexo passes the hand only on some events; when it is absent the interaction is the main hand. */
    private static boolean isMainHand(Object hand) {
        return !(hand instanceof EquipmentSlot slot) || slot == EquipmentSlot.HAND;
    }

    private static boolean isBlank(String furnitureId) {
        return furnitureId == null || furnitureId.isEmpty();
    }

    private static Cancellable cancellableOf(Event event) {
        return event instanceof Cancellable cancellable ? cancellable : null;
    }

    private static void cancel(Event event) {
        if (event instanceof Cancellable cancellable) {
            cancellable.setCancelled(true);
        }
    }

    private static Method denySetter(Class<?> type, String name) {
        try {
            return type.getMethod(name, Event.Result.class);
        } catch (NoSuchMethodException | LinkageError absent) {
            return null;
        }
    }

    private static void deny(Method setter, Object event) {
        if (setter == null) {
            return;
        }
        try {
            setter.invoke(event, Event.Result.DENY);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // Nexo dropped or changed the setter: the event itself is already cancelled.
        }
    }

    /**
     * A native Nexo event class and its accessors, resolved once. An accessor this Nexo build does not
     * expose stays {@code null} and simply reads as {@code null}.
     */
    private static final class NativeEvent {

        private final Class<? extends Event> type;
        private final Method player;
        private final Method mechanic;
        private final Method baseEntity;
        private final Method block;
        private final Method itemInHand;
        private final Method hand;

        private NativeEvent(Class<? extends Event> type) {
            this.type = type;
            this.player = accessor(type, "getPlayer");
            this.mechanic = accessor(type, "getMechanic");
            this.baseEntity = accessor(type, "getBaseEntity");
            this.block = accessor(type, "getBlock");
            this.itemInHand = accessor(type, "getItemInHand");
            this.hand = accessor(type, "getHand");
        }

        @SuppressWarnings("unchecked")
        static NativeEvent of(String simpleName) {
            try {
                Class<?> type = Class.forName(NATIVE_EVENTS + simpleName);
                return Event.class.isAssignableFrom(type)
                        ? new NativeEvent((Class<? extends Event>) type)
                        : null;
            } catch (ClassNotFoundException | LinkageError absent) {
                return null; // older Nexo build — the Bukkit handlers cover what they can
            }
        }

        static Method accessor(Class<?> type, String name) {
            if (type == null) {
                return null;
            }
            try {
                return type.getMethod(name);
            } catch (NoSuchMethodException | LinkageError absent) {
                return null;
            }
        }

        Object read(Method accessor, Object event) {
            if (accessor == null || !type.isInstance(event)) {
                return null;
            }
            try {
                return accessor.invoke(event);
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                return null;
            }
        }
    }
}
