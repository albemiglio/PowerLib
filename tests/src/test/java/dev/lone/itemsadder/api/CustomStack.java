package dev.lone.itemsadder.api;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Test stand-in for ItemsAdder's {@code CustomStack}, published under ItemsAdder's own package so
 * {@code ItemsAdderUtils} binds to it exactly as it would to the real plugin at runtime. It mirrors only
 * the three members the bridge resolves: {@code isInRegistry}, {@code getInstance} and {@code getItemStack}.
 *
 * <p>Same approach as the Nexo stand-ins under {@code com.nexomc}: it keeps the reflective binding under
 * test without putting the real plugin (or its repository) on the build's classpath.
 */
public class CustomStack {

    private static final Map<String, ItemStack> REGISTRY = new HashMap<>();

    /** When set, every registry lookup throws, the way the real plugin does before its items are loaded. */
    private static RuntimeException failure;

    private final ItemStack itemStack;

    private CustomStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static void register(String id, ItemStack itemStack) {
        REGISTRY.put(id, itemStack);
    }

    public static void clear() {
        REGISTRY.clear();
        failure = null;
    }

    /**
     * Makes every subsequent registry lookup throw.
     *
     * @param thrown what the lookup throws, or {@code null} to look items up normally again
     */
    public static void failWith(RuntimeException thrown) {
        failure = thrown;
    }

    public static boolean isInRegistry(String id) {
        if (failure != null) {
            throw failure;
        }
        return REGISTRY.containsKey(id);
    }

    public static CustomStack getInstance(String id) {
        ItemStack stack = REGISTRY.get(id);
        return stack == null ? null : new CustomStack(stack);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
