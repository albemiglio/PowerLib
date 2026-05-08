package it.mycraft.powerlib.bukkit.compat;

import it.mycraft.powerlib.common.objects.Pair;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Single home for ItemsAdder reflection. Falls back gracefully when
 * ItemsAdder is not present on the server.
 */
public final class ItemsAdderBridge {

    private static final Logger LOGGER = Logger.getLogger(ItemsAdderBridge.class.getName());

    private static volatile Boolean availableCache;
    private static Class<?> customStackClass;
    private static Method byItemStackMethod;
    private static Method getNamespacedIDMethod;
    private static Method customStackGetItemStackMethod;
    private static Method getInstanceMethod;
    private static Method isInRegistryMethod;

    static {
        try {
            customStackClass = Class.forName("dev.lone.itemsadder.api.CustomStack");
            byItemStackMethod = customStackClass.getMethod("byItemStack", ItemStack.class);
            getNamespacedIDMethod = customStackClass.getMethod("getNamespacedID");
            customStackGetItemStackMethod = customStackClass.getMethod("getItemStack");
            getInstanceMethod = customStackClass.getMethod("getInstance", String.class);
            isInRegistryMethod = customStackClass.getMethod("isInRegistry", String.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            LOGGER.log(Level.FINE, "ItemsAdder API not on classpath", e);
        }
    }

    private ItemsAdderBridge() {}

    public static boolean isAvailable() {
        Boolean cached = availableCache;
        if (cached != null) return cached;
        boolean enabled = customStackClass != null
                && Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
        availableCache = enabled;
        return enabled;
    }

    /** Reset the availability cache. Public for tests. */
    public static void resetAvailabilityCache() {
        availableCache = null;
    }

    public static Optional<Pair<String, String>> extractData(ItemStack itemStack) {
        if (!isAvailable() || itemStack == null) return Optional.empty();
        try {
            Object customStack = byItemStackMethod.invoke(null, itemStack);
            if (customStack == null) return Optional.empty();
            String namespacedId = (String) getNamespacedIDMethod.invoke(customStack);
            if (namespacedId == null || !namespacedId.contains(":")) return Optional.empty();
            String[] split = namespacedId.split(":", 2);
            return Optional.of(new Pair<>(split[0], split[1]));
        } catch (ReflectiveOperationException e) {
            LOGGER.log(Level.WARNING, "ItemsAdder extractData failed", e);
            return Optional.empty();
        }
    }

    public static Optional<ItemStack> buildItem(String namespacedId, int amount) {
        if (!isAvailable()) return Optional.empty();
        try {
            // Check registry first when available, to match prior behaviour
            if (isInRegistryMethod != null) {
                Object inRegistry = isInRegistryMethod.invoke(null, namespacedId);
                if (Boolean.FALSE.equals(inRegistry)) return Optional.empty();
            }
            Object customStack = getInstanceMethod.invoke(null, namespacedId);
            if (customStack == null) return Optional.empty();
            ItemStack stack = (ItemStack) customStackGetItemStackMethod.invoke(customStack);
            if (stack == null) return Optional.empty();
            stack.setAmount(amount);
            return Optional.of(stack);
        } catch (ReflectiveOperationException e) {
            LOGGER.log(Level.WARNING, "ItemsAdder buildItem failed", e);
            return Optional.empty();
        }
    }
}
