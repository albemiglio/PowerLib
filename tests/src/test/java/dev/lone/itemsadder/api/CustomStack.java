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

    private final ItemStack itemStack;

    private CustomStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static void register(String id, ItemStack itemStack) {
        REGISTRY.put(id, itemStack);
    }

    public static void clear() {
        REGISTRY.clear();
    }

    public static boolean isInRegistry(String id) {
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
