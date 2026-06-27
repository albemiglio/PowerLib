package it.mycraft.powerlib.bukkit.utils;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

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

    static {
        AVAILABLE = bind();
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
