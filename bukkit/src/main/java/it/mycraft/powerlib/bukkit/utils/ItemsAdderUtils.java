package it.mycraft.powerlib.bukkit.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

/**
 * Reflection bridge to the <a href="https://itemsadder.devs.beer">ItemsAdder</a> custom-item plugin.
 *
 * <p><b>Why reflection and not a normal compile-time dependency?</b> For the same reason
 * {@link NexoUtils} uses one, plus a build-time argument of its own: {@code dev.lone:api-itemsadder} is
 * served by a single repository, so every PowerLib build — including the ones that never touch an
 * ItemsAdder item — fails when that host is unreachable. Binding at runtime keeps the integration
 * working while the build depends only on repositories PowerLib already relies on.
 *
 * <p>On servers without ItemsAdder this bridge stays inert and every accessor returns {@code null}.
 * Method handles are resolved once. "ItemsAdder not installed" is silent; "ItemsAdder installed but its
 * API does not match" is logged once, so version drift surfaces instead of being swallowed.
 *
 * <p>Plugins using this must {@code softdepend} on ItemsAdder in their {@code plugin.yml} so its
 * classes are reachable from the plugin classloader.
 */
public final class ItemsAdderUtils {

    private static final boolean AVAILABLE;
    private static Method isInRegistry; // CustomStack.isInRegistry(String) -> boolean
    private static Method getInstance;  // CustomStack.getInstance(String) -> CustomStack
    private static Method getItemStack; // <CustomStack>.getItemStack() -> ItemStack

    static {
        AVAILABLE = bind();
    }

    private ItemsAdderUtils() {
    }

    private static boolean bind() {
        Class<?> customStack;
        try {
            customStack = Class.forName("dev.lone.itemsadder.api.CustomStack");
        } catch (ClassNotFoundException | LinkageError absent) {
            return false; // ItemsAdder not installed — stay inert, quietly
        }
        try {
            isInRegistry = customStack.getMethod("isInRegistry", String.class);
            getInstance = customStack.getMethod("getInstance", String.class);
            getItemStack = customStack.getMethod("getItemStack");
            return true;
        } catch (NoSuchMethodException drift) {
            Bukkit.getLogger().warning("[PowerLib] ItemsAdder is installed but its API does not match ("
                    + drift.getMessage() + "); ItemsAdder integration disabled. Update PowerLib or ItemsAdder.");
            return false;
        }
    }

    /**
     * Whether the ItemsAdder API was found and bound on this server.
     *
     * @return {@code true} if the ItemsAdder API is available
     */
    public static boolean isAvailable() {
        return AVAILABLE;
    }

    /**
     * Builds the ItemsAdder item registered under the given id.
     *
     * @param itemId the ItemsAdder id, either {@code namespace:id} or a bare {@code id}
     * @return the built item, or {@code null} if the id is unknown / ItemsAdder is unavailable
     */
    public static ItemStack itemStackFromId(String itemId) {
        if (!AVAILABLE || itemId == null) return null;
        try {
            if (!Boolean.TRUE.equals(isInRegistry.invoke(null, itemId))) return null;
            Object stack = getInstance.invoke(null, itemId);
            return stack == null ? null : (ItemStack) getItemStack.invoke(stack);
        } catch (ReflectiveOperationException | RuntimeException unavailable) {
            return null;
        }
    }
}
