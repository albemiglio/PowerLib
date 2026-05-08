package it.mycraft.powerlib.bukkit.compat;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class NexoSupport {

    private static final Logger LOGGER = Logger.getLogger(NexoSupport.class.getName());

    private static volatile Boolean availableCache;
    private static Class<?> nexoItemsClass;
    private static Method itemFromIdMethod;
    private static Method nexoItemBuilderBuildMethod;

    static {
        try {
            nexoItemsClass = Class.forName("com.nexomc.nexo.api.NexoItems");
            itemFromIdMethod = nexoItemsClass.getMethod("itemFromId", String.class);
            // Returned object's #build() method
            Class<?> nexoItemBuilderClass = Class.forName("com.nexomc.nexo.items.ItemBuilder");
            nexoItemBuilderBuildMethod = nexoItemBuilderClass.getMethod("build");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            LOGGER.log(Level.FINE, "Nexo API not on classpath", e);
        }
    }

    private NexoSupport() {}

    public static boolean isAvailable() {
        Boolean cached = availableCache;
        if (cached != null) return cached;
        boolean enabled = nexoItemsClass != null
                && Bukkit.getPluginManager().isPluginEnabled("Nexo");
        availableCache = enabled;
        return enabled;
    }

    public static void resetAvailabilityCache() {
        availableCache = null;
    }

    public static Optional<ItemStack> buildItem(String nexoId, int amount) {
        if (!isAvailable()) return Optional.empty();
        try {
            Object nexoBuilder = itemFromIdMethod.invoke(null, nexoId);
            if (nexoBuilder == null) return Optional.empty();
            ItemStack stack = (ItemStack) nexoItemBuilderBuildMethod.invoke(nexoBuilder);
            if (stack == null) return Optional.empty();
            stack.setAmount(amount);
            return Optional.of(stack);
        } catch (ReflectiveOperationException e) {
            LOGGER.log(Level.WARNING, "Nexo buildItem failed for id=" + nexoId, e);
            return Optional.empty();
        }
    }
}
