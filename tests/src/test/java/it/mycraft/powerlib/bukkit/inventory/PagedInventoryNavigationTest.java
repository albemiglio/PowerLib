package it.mycraft.powerlib.bukkit.inventory;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import it.mycraft.powerlib.bukkit.inventory.internal.OpenedPagedInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Drives the {@link OpenedPagedInventory} state machine directly (render, paging, filler, slot
 * handlers, page-change callbacks) plus the {@link NavigationLayout} factories — the parts a plain
 * open()/getItem() smoke test can't reach.
 */
class PagedInventoryNavigationTest {

    private PlayerMock player;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        player = MockBukkit.getMock().addPlayer();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    private static List<ItemStack> items(int n) {
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < n; i++) list.add(new ItemStack(Material.STONE, 1));
        return list;
    }

    private InventoryClickEvent clickAt(InventoryView view, int rawSlot) {
        return new InventoryClickEvent(view, org.bukkit.event.inventory.InventoryType.SlotType.CONTAINER,
                rawSlot, org.bukkit.event.inventory.ClickType.LEFT,
                org.bukkit.event.inventory.InventoryAction.PICKUP_ALL);
    }

    @Test
    void navigationLayoutBottomRowComputesSlots() {
        NavigationLayout nav = NavigationLayout.bottomRow(6);
        assertThat(nav.getPrevSlot()).isEqualTo(45);
        assertThat(nav.getNextSlot()).isEqualTo(53);
    }

    @Test
    void navigationLayoutCustomUsesGivenSlots() {
        NavigationLayout nav = NavigationLayout.custom(3, 7);
        assertThat(nav.getPrevSlot()).isEqualTo(3);
        assertThat(nav.getNextSlot()).isEqualTo(7);
    }

    @Test
    void nextAndPrevPageNavigateAndFireCallback() {
        AtomicInteger lastPage = new AtomicInteger(-1);
        int rows = 6;
        int contentSlots = (rows - 1) * 9; // 45 per page
        NavigationLayout nav = NavigationLayout.bottomRow(rows);
        Inventory inv = Bukkit.createInventory(null, rows * 9, "Paged");
        // Two full pages worth of content.
        OpenedPagedInventory state = new OpenedPagedInventory(
                inv, items(contentSlots * 2), rows, null, new java.util.HashMap<>(),
                nav.getPrevSlot(), nav.getNextSlot(), (p, page) -> lastPage.set(page), null);
        state.render(player);
        player.openInventory(inv);
        InventoryView view = player.getOpenInventory();

        assertThat(state.getPage()).isEqualTo(0);

        state.handleClick(player, clickAt(view, nav.getNextSlot()));
        assertThat(state.getPage()).isEqualTo(1);
        assertThat(lastPage.get()).isEqualTo(1);

        state.handleClick(player, clickAt(view, nav.getPrevSlot()));
        assertThat(state.getPage()).isEqualTo(0);
        assertThat(lastPage.get()).isEqualTo(0);
    }

    @Test
    void nextPageDoesNothingOnLastPage() {
        int rows = 2;
        NavigationLayout nav = NavigationLayout.bottomRow(rows);
        Inventory inv = Bukkit.createInventory(null, rows * 9, "Paged");
        // Only a handful of items -> a single page.
        OpenedPagedInventory state = new OpenedPagedInventory(
                inv, items(3), rows, null, new java.util.HashMap<>(),
                nav.getPrevSlot(), nav.getNextSlot(), null, null);
        state.render(player);
        player.openInventory(inv);

        state.handleClick(player, clickAt(player.getOpenInventory(), nav.getNextSlot()));
        assertThat(state.getPage()).isEqualTo(0);
    }

    @Test
    void prevPageDoesNothingOnFirstPage() {
        int rows = 2;
        NavigationLayout nav = NavigationLayout.bottomRow(rows);
        Inventory inv = Bukkit.createInventory(null, rows * 9, "Paged");
        OpenedPagedInventory state = new OpenedPagedInventory(
                inv, items(3), rows, null, new java.util.HashMap<>(),
                nav.getPrevSlot(), nav.getNextSlot(), null, null);
        state.render(player);
        player.openInventory(inv);

        state.handleClick(player, clickAt(player.getOpenInventory(), nav.getPrevSlot()));
        assertThat(state.getPage()).isEqualTo(0);
    }

    @Test
    void rendererTransformsVisibleItems() {
        int rows = 2;
        Inventory inv = Bukkit.createInventory(null, rows * 9, "Paged");
        OpenedPagedInventory state = new OpenedPagedInventory(
                inv, items(2), rows, (item, idx) -> new ItemStack(Material.DIAMOND),
                new java.util.HashMap<>(), 9, 17, null, null);
        state.render(player);
        assertThat(inv.getItem(0).getType()).isEqualTo(Material.DIAMOND);
    }

    @Test
    void fillerFillsEmptyContentSlots() {
        int rows = 2;
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        Inventory inv = Bukkit.createInventory(null, rows * 9, "Paged");
        OpenedPagedInventory state = new OpenedPagedInventory(
                inv, items(1), rows, null, new java.util.HashMap<>(), 9, 17, null, filler);
        state.render(player);
        // Slot 0 carries the single item; remaining content slots get the filler.
        assertThat(inv.getItem(0).getType()).isEqualTo(Material.STONE);
        assertThat(inv.getItem(1)).isEqualTo(filler);
    }

    @Test
    void slotHandlerIsInvokedForRegisteredSlot() {
        AtomicBoolean clicked = new AtomicBoolean(false);
        int rows = 2;
        java.util.Map<Integer, java.util.function.BiConsumer<org.bukkit.entity.Player, InventoryClickEvent>> handlers =
                new java.util.HashMap<>();
        handlers.put(3, (p, e) -> clicked.set(true));
        Inventory inv = Bukkit.createInventory(null, rows * 9, "Paged");
        OpenedPagedInventory state = new OpenedPagedInventory(
                inv, items(5), rows, null, handlers, 9, 17, null, null);
        state.render(player);
        player.openInventory(inv);

        state.handleClick(player, clickAt(player.getOpenInventory(), 3));
        assertThat(clicked.get()).isTrue();
    }

    @Test
    void builderFluentSettersDoNotThrowAndOpen() {
        // Exercises every PagedInventoryBuilder setter through to open().
        PagedInventoryBuilder.create(3, "Build")
                .items(items(50))
                .renderer((item, idx) -> item)
                .navigation(NavigationLayout.custom(18, 26))
                .onClick(5, (p, e) -> { })
                .onPageChange((p, page) -> { })
                .filler(new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
                .open(player);
        assertThat(player.getOpenInventory().getTopInventory().getItem(0)).isNotNull();
    }
}
