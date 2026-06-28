package it.mycraft.powerlib.bukkit.inventory.internal;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public final class OpenedPagedInventory {
    private final Inventory inventory;
    private final List<ItemStack> items;
    private final int rows;
    private final BiFunction<ItemStack, Integer, ItemStack> renderer;
    private final Map<Integer, BiConsumer<Player, InventoryClickEvent>> slotHandlers;
    private final int prevSlot;
    private final int nextSlot;
    private final BiConsumer<Player, Integer> onPageChange;
    private final ItemStack filler;
    private int page = 0;

    public OpenedPagedInventory(Inventory inventory, List<ItemStack> items, int rows,
                                BiFunction<ItemStack, Integer, ItemStack> renderer,
                                Map<Integer, BiConsumer<Player, InventoryClickEvent>> slotHandlers,
                                int prevSlot, int nextSlot,
                                BiConsumer<Player, Integer> onPageChange,
                                ItemStack filler) {
        this.inventory = inventory;
        this.items = items;
        this.rows = rows;
        this.renderer = renderer;
        this.slotHandlers = slotHandlers;
        this.prevSlot = prevSlot;
        this.nextSlot = nextSlot;
        this.onPageChange = onPageChange;
        this.filler = filler;
    }

    public Inventory getInventory() { return inventory; }
    public int getPage() { return page; }

    public void render(Player player) {
        inventory.clear();
        int contentSlots = (rows - 1) * 9; // last row reserved for nav
        int startIndex = page * contentSlots;
        for (int i = 0; i < contentSlots; i++) {
            int idx = startIndex + i;
            if (idx >= items.size()) {
                if (filler != null) inventory.setItem(i, filler);
                continue;
            }
            ItemStack visual = renderer != null ? renderer.apply(items.get(idx), idx) : items.get(idx);
            inventory.setItem(i, visual);
        }
    }

    public void handleClick(Player player, InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (slot == prevSlot) {
            if (page > 0) {
                page--;
                render(player);
                if (onPageChange != null) onPageChange.accept(player, page);
            }
            return;
        }
        if (slot == nextSlot) {
            int contentSlots = (rows - 1) * 9;
            int maxPages = (int) Math.ceil((double) items.size() / contentSlots);
            if (page < maxPages - 1) {
                page++;
                render(player);
                if (onPageChange != null) onPageChange.accept(player, page);
            }
            return;
        }
        BiConsumer<Player, InventoryClickEvent> h = slotHandlers.get(slot);
        if (h != null) h.accept(player, event);
    }
}
