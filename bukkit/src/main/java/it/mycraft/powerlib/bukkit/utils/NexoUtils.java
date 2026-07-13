package it.mycraft.powerlib.bukkit.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Reflection bridge to the <a href="https://nexomc.com">Nexo</a> custom-item plugin.
 *
 * <p><b>Why reflection and not a normal compile-time dependency?</b> PowerLib is deliberately built for
 * <b>Java 17</b> so a single artifact runs across the whole supported Minecraft range (1.17–1.20.4 servers
 * run on Java 17, newer ones on Java 21+). Nexo, however, is compiled for <b>Java 21</b>. Declaring Nexo
 * as a {@code compileOnly} dependency would force PowerLib's bytecode up to Java 21 (a JDK 17 compiler
 * cannot even read Java 21 class files), breaking that range. Reflection keeps PowerLib on Java 17 while
 * still binding to Nexo's API <em>at runtime</em> — which is sound because Nexo's Java-21 classes can only
 * ever be present on a Java-21 server JVM, where this code runs anyway.
 *
 * <p>Consequently, on servers without Nexo (and on any Java 17 server, where Nexo cannot load at all) this
 * bridge stays inert and every accessor returns {@code null} / {@code false}. All bound Nexo API methods
 * are static (Kotlin {@code @JvmStatic}).
 *
 * <p>Method handles are resolved once. "Nexo not installed" is silent; "Nexo installed but its API does
 * not match" is logged once — unlike a blanket catch, this surfaces version drift instead of hiding it.
 *
 * <p>Plugins using this must {@code softdepend} on Nexo in their {@code plugin.yml} so Nexo's classes
 * are reachable from the plugin classloader.
 */
public final class NexoUtils {

    private static final boolean AVAILABLE;
    private static Method idFromItem;          // NexoItems.idFromItem(ItemStack) -> String
    private static Method itemFromId;          // NexoItems.itemFromId(String) -> nexo ItemBuilder
    private static Method furnitureFromBlock;  // NexoFurniture.furnitureMechanic(Block) -> Mechanic
    private static Method furnitureFromEntity; // NexoFurniture.furnitureMechanic(Entity) -> Mechanic
    private static Method customBlock;         // NexoBlocks.customBlockMechanic(BlockData) -> Mechanic
    private static Method mechanicGetItemID;   // Mechanic.getItemID() -> String
    private static Method nexoBuilderBuild;    // <nexo ItemBuilder>.build() -> ItemStack (resolved lazily)

    // Furniture-replacement bridge. Nexo's furniture placement/removal API has shifted across versions
    // (baseEntity overloads, remove(...) with/without a Drop, place(...) taking a float yaw or a Rotation),
    // so — unlike the strict core bind() above — each of these handles is resolved independently and
    // replaceFurniture degrades gracefully when some are absent. A missing furniture handle must never
    // disable the whole Nexo integration.
    private static Method furnBaseFromEntity;      // NexoFurniture.baseEntity(Entity) -> ItemDisplay
    private static Method furnBaseFromBlock;       // NexoFurniture.baseEntity(Block) -> ItemDisplay
    private static Method furnBaseFromLocation;    // NexoFurniture.baseEntity(Location) -> ItemDisplay
    private static Method furnIsFurniture;         // NexoFurniture.isFurniture(String) -> boolean
    private static Method furnUpdate;              // NexoFurniture.updateFurniture(ItemDisplay)
    private static Method furnConvert;             // NexoFurniture.convertFurniture(ItemDisplay)
    private static Method furnPlace;               // NexoFurniture.place(String, Location, yaw|Rotation, BlockFace)
    private static Method furnRemoveEntityDrop;    // NexoFurniture.remove(Entity, Player, Drop)
    private static Method furnRemoveLocationDrop;  // NexoFurniture.remove(Location, Player, Drop)
    private static Method furnRemoveEntityPlayer;  // NexoFurniture.remove(Entity, Player)
    private static Method furnRemoveLocationPlayer;// NexoFurniture.remove(Location, Player)
    private static Method furnRemoveEntity;        // NexoFurniture.remove(Entity)
    private static Method furnRemoveLocation;      // NexoFurniture.remove(Location)

    // Last non-fatal reason replaceFurniture returned false. Furniture ops run on the Bukkit main thread
    // and callers read this synchronously right after the call, so a single AtomicReference is enough (and
    // avoids the ThreadLocal-cleanup leak a pooled-thread caller would otherwise risk). Empty = "no error yet".
    private static final AtomicReference<String> lastFurnitureError = new AtomicReference<>("");

    static {
        AVAILABLE = bind();
        bindFurniture();
    }

    private NexoUtils() {
    }

    private static boolean bind() {
        Class<?> items, furniture, blocks, mechanic;
        try {
            items = Class.forName("com.nexomc.nexo.api.NexoItems");
            furniture = Class.forName("com.nexomc.nexo.api.NexoFurniture");
            blocks = Class.forName("com.nexomc.nexo.api.NexoBlocks");
            mechanic = Class.forName("com.nexomc.nexo.mechanics.Mechanic");
        } catch (ClassNotFoundException | LinkageError absent) {
            return false; // Nexo not installed (or not loadable on this Java) — stay inert, quietly
        }
        try {
            idFromItem = items.getMethod("idFromItem", ItemStack.class);
            itemFromId = items.getMethod("itemFromId", String.class);
            furnitureFromBlock = furniture.getMethod("furnitureMechanic", Block.class);
            furnitureFromEntity = furniture.getMethod("furnitureMechanic", Entity.class);
            customBlock = blocks.getMethod("customBlockMechanic", BlockData.class);
            mechanicGetItemID = mechanic.getMethod("getItemID");
            return true;
        } catch (NoSuchMethodException drift) {
            Bukkit.getLogger().warning("[PowerLib] Nexo is installed but its API does not match ("
                    + drift.getMessage() + "); Nexo integration disabled. Update PowerLib or Nexo.");
            return false;
        }
    }

    private static void bindFurniture() {
        // Split from the handle-resolution below so the resolution is unit-testable against a stand-in
        // provider (see NexoUtilsFurnitureTest) without a live Nexo on the classpath. When Nexo is absent
        // optionalClass returns null and bindFurnitureHandles is a no-op — replaceFurniture stays inert.
        bindFurnitureHandles(optionalClass("com.nexomc.nexo.api.NexoFurniture"));
    }

    static void bindFurnitureHandles(Class<?> furniture) {
        if (furniture == null) {
            return;
        }
        furnBaseFromEntity = optionalMethod(furniture, "baseEntity", Entity.class);
        furnBaseFromBlock = optionalMethod(furniture, "baseEntity", Block.class);
        furnBaseFromLocation = optionalMethod(furniture, "baseEntity", Location.class);
        furnIsFurniture = optionalMethod(furniture, "isFurniture", String.class);
        furnUpdate = optionalMethod(furniture, "updateFurniture", ItemDisplay.class);
        furnConvert = optionalMethod(furniture, "convertFurniture", ItemDisplay.class);
        furnPlace = findPlaceMethod(furniture);
        Class<?> drop = optionalClass("com.nexomc.nexo.utils.drops.Drop");
        if (drop != null) {
            furnRemoveEntityDrop = optionalMethod(furniture, "remove", Entity.class, Player.class, drop);
            furnRemoveLocationDrop = optionalMethod(furniture, "remove", Location.class, Player.class, drop);
        }
        furnRemoveEntityPlayer = optionalMethod(furniture, "remove", Entity.class, Player.class);
        furnRemoveLocationPlayer = optionalMethod(furniture, "remove", Location.class, Player.class);
        furnRemoveEntity = optionalMethod(furniture, "remove", Entity.class);
        furnRemoveLocation = optionalMethod(furniture, "remove", Location.class);
    }

    private static Method optionalMethod(Class<?> owner, String name, Class<?>... params) {
        try {
            return owner.getMethod(name, params);
        } catch (NoSuchMethodException | LinkageError absent) {
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

    /**
     * Whether the Nexo API was found and bound on this server.
     *
     * @return {@code true} if the Nexo API is available
     */
    public static boolean isAvailable() {
        return AVAILABLE;
    }

    /**
     * @param item the item to inspect
     * @return the Nexo id of the item, or {@code null} if it is not a Nexo item / Nexo is unavailable.
     */
    public static String getNexoId(ItemStack item) {
        if (!AVAILABLE || item == null || item.getType().isAir()) return null;
        return (String) invoke(idFromItem, null, item);
    }

    /**
     * @param block the block to inspect
     * @return the Nexo id of the furniture or custom block at this block, or {@code null}.
     */
    public static String getNexoId(Block block) {
        if (!AVAILABLE || block == null) return null;
        String id = idOf(invoke(furnitureFromBlock, null, block));
        return id != null ? id : idOf(invoke(customBlock, null, block.getBlockData()));
    }

    /**
     * @param entity the entity to inspect
     * @return the Nexo id of the furniture entity, or {@code null}.
     */
    public static String getNexoId(Entity entity) {
        if (!AVAILABLE || entity == null) return null;
        return idOf(invoke(furnitureFromEntity, null, entity));
    }

    /**
     * @param item   the item to test
     * @param nexoId the Nexo id to match against
     * @return whether {@code item} is the Nexo item with the given id.
     */
    public static boolean isNexoItem(ItemStack item, String nexoId) {
        String id = getNexoId(item);
        return id != null && id.equalsIgnoreCase(nexoId);
    }

    /**
     * Builds the Nexo item with the given id, or {@code null} if unknown / Nexo is unavailable.
     *
     * @param nexoId the Nexo id to build
     * @return the built item, or {@code null} if unknown / Nexo is unavailable
     */
    public static ItemStack itemStackFromId(String nexoId) {
        if (!AVAILABLE || nexoId == null) return null;
        Object builder = invoke(itemFromId, null, nexoId);
        if (builder == null) return null;
        try {
            if (nexoBuilderBuild == null) {
                nexoBuilderBuild = builder.getClass().getMethod("build");
            }
            return (ItemStack) nexoBuilderBuild.invoke(builder);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    /**
     * Replaces the Nexo furniture at {@code location} with the furniture registered under
     * {@code newFurnitureId}, on behalf of {@code player}.
     *
     * <p>When the base entity is an {@link ItemDisplay} the swap is done in place (its item stack is
     * updated and Nexo is asked to re-render it); otherwise the old furniture is removed and the new one
     * placed at the same location and yaw. On any failure the method returns {@code false} and records a
     * human-readable reason retrievable via {@link #getLastFurnitureError()}.
     *
     * @param furniture      the current furniture hint (an {@link Entity}, a {@link Block}, or {@code null});
     *                       Nexo's base-entity lookup by location is used regardless
     * @param location       the location of the furniture to replace
     * @param newFurnitureId the Nexo id of the replacement furniture
     * @param player         the player on whose behalf the removal happens (may be {@code null})
     * @return {@code true} if the furniture was replaced; {@code false} otherwise (see {@link #getLastFurnitureError()})
     */
    public static boolean replaceFurniture(Object furniture, Location location, String newFurnitureId, Player player) {
        lastFurnitureError.set("");
        if (newFurnitureId == null || newFurnitureId.isEmpty() || location == null || location.getWorld() == null) {
            lastFurnitureError.set("invalid target id or location");
            return false;
        }
        if (!isFurniture(newFurnitureId)) {
            lastFurnitureError.set("target id is not registered as a Nexo furniture: " + newFurnitureId);
            return false;
        }
        Object baseEntity = getBaseEntity(furniture, location);
        if (baseEntity instanceof ItemDisplay && updateFurnitureDisplay((ItemDisplay) baseEntity, newFurnitureId)) {
            return true;
        }
        Location placeLocation = baseEntity instanceof Entity
                ? ((Entity) baseEntity).getLocation().clone()
                : location.clone();
        float yaw = placeLocation.getYaw();
        if (!removeFurniture(baseEntity, location, player)) {
            if (lastFurnitureError.get().isEmpty()) {
                lastFurnitureError.set("could not remove old furniture");
            }
            return false;
        }
        return placeFurniture(newFurnitureId, placeLocation, yaw, BlockFace.UP);
    }

    /**
     * @return the reason the last {@link #replaceFurniture} call on this thread failed, or an empty string
     * if the last call succeeded / none has run.
     */
    public static String getLastFurnitureError() {
        return lastFurnitureError.get();
    }

    private static boolean updateFurnitureDisplay(ItemDisplay baseEntity, String newFurnitureId) {
        ItemStack newItem = itemStackFromId(newFurnitureId);
        if (newItem == null) {
            lastFurnitureError.set("could not build Nexo item for furniture id: " + newFurnitureId);
            return false;
        }
        try {
            baseEntity.setItemStack(newItem);
            invokeFurnitureUpdater(furnConvert, baseEntity);
            invokeFurnitureUpdater(furnUpdate, baseEntity);
            return true;
        } catch (RuntimeException ex) {
            lastFurnitureError.set("error while updating furniture display to " + newFurnitureId
                    + ": " + ex.getClass().getSimpleName());
            return false;
        }
    }

    private static boolean isFurniture(String furnitureId) {
        if (furnIsFurniture == null) {
            return true; // can't verify — assume it is and let placeFurniture surface the real error
        }
        try {
            return Boolean.TRUE.equals(furnIsFurniture.invoke(null, furnitureId));
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return true;
        }
    }

    private static Object getBaseEntity(Object furniture, Location location) {
        Object fromLocation = invokeBase(furnBaseFromLocation, location);
        if (fromLocation instanceof Entity) {
            return fromLocation;
        }
        if (furniture instanceof Entity) {
            Object fromEntity = invokeBase(furnBaseFromEntity, furniture);
            return fromEntity instanceof Entity ? fromEntity : furniture;
        }
        if (furniture instanceof Block) {
            Object fromBlock = invokeBase(furnBaseFromBlock, furniture);
            if (fromBlock instanceof Entity) {
                return fromBlock;
            }
        }
        return furniture;
    }

    private static boolean removeFurniture(Object furniture, Location location, Player player) {
        Object emptyDrop = emptyDrop();
        if (furniture instanceof Entity) {
            Entity entity = (Entity) furniture;
            if (emptyDrop != null && invokeRemove(furnRemoveEntityDrop, entity, player, emptyDrop)) {
                return true;
            }
            if (invokeRemove(furnRemoveEntityPlayer, entity, player)) {
                return true;
            }
            if (invokeRemove(furnRemoveEntity, entity)) {
                return true;
            }
        }
        if (location != null) {
            if (emptyDrop != null && invokeRemove(furnRemoveLocationDrop, location, player, emptyDrop)) {
                return true;
            }
            if (invokeRemove(furnRemoveLocationPlayer, location, player)) {
                return true;
            }
            if (invokeRemove(furnRemoveLocation, location)) {
                return true;
            }
        }
        lastFurnitureError.set("Nexo remove returned false for " + describeFurniture(furniture, location));
        return false;
    }

    private static boolean placeFurniture(String furnitureId, Location location, float yaw, BlockFace blockFace) {
        if (furnPlace == null) {
            lastFurnitureError.set("No compatible NexoFurniture.place(String, Location, yaw/Rotation, BlockFace) method is available");
            return false;
        }
        try {
            Object rotationArgument = placeRotationArgument(furnPlace, yaw);
            Object placed = furnPlace.invoke(null, furnitureId, location, rotationArgument,
                    blockFace == null ? BlockFace.UP : blockFace);
            if (placed == null) {
                lastFurnitureError.set("Nexo returned null while placing " + furnitureId);
            }
            return placed != null;
        } catch (ReflectiveOperationException | RuntimeException ex) {
            lastFurnitureError.set("error while placing " + furnitureId + ": " + ex.getClass().getSimpleName());
            return false;
        }
    }

    private static Object invokeBase(Method method, Object value) {
        if (method == null || value == null) {
            return null;
        }
        try {
            return method.invoke(null, value);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
    }

    private static boolean invokeRemove(Method method, Object... args) {
        if (method == null) {
            return false;
        }
        try {
            return Boolean.TRUE.equals(method.invoke(null, args));
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return false;
        }
    }

    private static void invokeFurnitureUpdater(Method method, ItemDisplay baseEntity) {
        if (method == null) {
            return;
        }
        try {
            method.invoke(null, baseEntity);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // best effort — re-render is cosmetic; the item stack was already swapped
        }
    }

    private static String describeFurniture(Object furniture, Location location) {
        if (furniture instanceof Entity) {
            Location at = ((Entity) furniture).getLocation();
            String world = at.getWorld() == null ? "?" : at.getWorld().getName();
            return ((Entity) furniture).getType() + "@" + world + ","
                    + at.getBlockX() + "," + at.getBlockY() + "," + at.getBlockZ();
        }
        if (location != null && location.getWorld() != null) {
            return location.getWorld().getName() + ","
                    + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
        }
        return "unknown location";
    }

    private static Object emptyDrop() {
        try {
            return Class.forName("com.nexomc.nexo.utils.drops.Drop").getMethod("emptyDrop").invoke(null);
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return null;
        }
    }

    private static Method findPlaceMethod(Class<?> furnitureClass) {
        for (Method method : furnitureClass.getMethods()) {
            if (!"place".equals(method.getName()) || method.getParameterCount() != 4) {
                continue;
            }
            Class<?>[] params = method.getParameterTypes();
            Class<?> rotation = params[2];
            if (params[0] == String.class && params[1] == Location.class && params[3] == BlockFace.class
                    && (rotation == Float.class || rotation == float.class || rotation == Rotation.class)) {
                return method;
            }
        }
        return null;
    }

    private static Object placeRotationArgument(Method placeMethod, float yaw) {
        return placeMethod.getParameterTypes()[2] == Rotation.class ? yawToRotation(yaw) : yaw;
    }

    private static Rotation yawToRotation(float yaw) {
        float normalized = yaw % 360.0f;
        if (normalized < 0.0f) {
            normalized += 360.0f;
        }
        int index = Math.round(normalized / 45.0f) % 8;
        return switch (index) {
            case 1 -> Rotation.CLOCKWISE_45;
            case 2 -> Rotation.CLOCKWISE;
            case 3 -> Rotation.CLOCKWISE_135;
            case 4 -> Rotation.FLIPPED;
            case 5 -> Rotation.FLIPPED_45;
            case 6 -> Rotation.COUNTER_CLOCKWISE;
            case 7 -> Rotation.COUNTER_CLOCKWISE_45;
            default -> Rotation.NONE;
        };
    }

    private static String idOf(Object mechanic) {
        return mechanic == null ? null : (String) invoke(mechanicGetItemID, mechanic);
    }

    private static Object invoke(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}
