package it.mycraft.powerlib.bukkit.inventory;

import it.mycraft.powerlib.bukkit.inventory.internal.OpenedPagedInventory;
import it.mycraft.powerlib.bukkit.inventory.internal.PagedInventoryListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public final class PagedInventoryBuilder {

    private final int rows;
    private final String title;
    private List<ItemStack> items = new ArrayList<>();
    private BiFunction<ItemStack, Integer, ItemStack> renderer;
    private NavigationLayout navigation;
    private final Map<Integer, BiConsumer<Player, InventoryClickEvent>> slotHandlers = new HashMap<>();
    private BiConsumer<Player, Integer> onPageChange;
    private ItemStack filler;

    private PagedInventoryBuilder(int rows, String title) {
        this.rows = rows;
        this.title = title;
    }

    public static PagedInventoryBuilder create(int rows, String title) {
        if (rows < 2 || rows > 6) throw new IllegalArgumentException("rows must be between 2 and 6");
        return new PagedInventoryBuilder(rows, title);
    }

    public PagedInventoryBuilder items(List<ItemStack> items) { this.items = new ArrayList<>(items); return this; }
    public PagedInventoryBuilder renderer(BiFunction<ItemStack, Integer, ItemStack> renderer) { this.renderer = renderer; return this; }
    public PagedInventoryBuilder navigation(NavigationLayout layout) { this.navigation = layout; return this; }
    public PagedInventoryBuilder onClick(int slot, BiConsumer<Player, InventoryClickEvent> handler) { slotHandlers.put(slot, handler); return this; }
    public PagedInventoryBuilder onPageChange(BiConsumer<Player, Integer> h) { this.onPageChange = h; return this; }
    public PagedInventoryBuilder filler(ItemStack filler) { this.filler = filler; return this; }

    public void open(Player player) {
        NavigationLayout nav = navigation != null ? navigation : NavigationLayout.bottomRow(rows);
        Inventory inv = Bukkit.createInventory(null, rows * 9, title);
        OpenedPagedInventory state = new OpenedPagedInventory(
                inv, items, rows, renderer, slotHandlers,
                nav.getPrevSlot(), nav.getNextSlot(), onPageChange, filler);
        PagedInventoryListener.track(player, state);
        state.render(player);
        player.openInventory(inv);
    }
}
