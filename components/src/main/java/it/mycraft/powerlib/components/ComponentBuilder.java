package it.mycraft.powerlib.components;

import it.mycraft.powerlib.bukkit.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent helpers around DataComponent setters introduced in Spigot 1.20.5.
 * Each fluent call accumulates a {@code Consumer<ItemMeta>} that is applied lazily
 * via {@link #applyTo(ItemStack)}.
 *
 * <p><strong>Runtime requirement:</strong> Minecraft 1.20.5+. Calling fluent
 * methods on older servers will compile (the JVM doesn't resolve methods
 * eagerly) but applyTo will throw NoSuchMethodError at first invocation.</p>
 */
public final class ComponentBuilder {

    private final List<Consumer<ItemMeta>> steps = new ArrayList<>();

    private ComponentBuilder() {}

    public static ComponentBuilder create() {
        return new ComponentBuilder();
    }

    public ComponentBuilder unbreakable(boolean enabled) {
        steps.add(meta -> meta.setUnbreakable(enabled));
        return this;
    }

    public ComponentBuilder enchantmentGlintOverride(boolean enabled) {
        steps.add(meta -> meta.setEnchantmentGlintOverride(enabled));
        return this;
    }

    public ComponentBuilder customModelData(int data) {
        steps.add(meta -> meta.setCustomModelData(data));
        return this;
    }

    public ComponentBuilder food(int nutrition, float saturation, boolean canAlwaysEat) {
        steps.add(meta -> {
            org.bukkit.inventory.meta.components.FoodComponent food = meta.getFood();
            food.setNutrition(nutrition);
            food.setSaturation(saturation);
            food.setCanAlwaysEat(canAlwaysEat);
            meta.setFood(food);
        });
        return this;
    }

    public ComponentBuilder toolDamagePerBlock(int damage) {
        steps.add(meta -> {
            org.bukkit.inventory.meta.components.ToolComponent tool = meta.getTool();
            tool.setDamagePerBlock(damage);
            meta.setTool(tool);
        });
        return this;
    }

    public void applyTo(ItemBuilder builder) {
        builder.addBuildStep(meta -> steps.forEach(s -> s.accept(meta)));
    }

    public void applyTo(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return;
        steps.forEach(s -> s.accept(meta));
        stack.setItemMeta(meta);
    }
}
