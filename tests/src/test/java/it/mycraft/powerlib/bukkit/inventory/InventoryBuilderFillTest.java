package it.mycraft.powerlib.bukkit.inventory;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers the fill helpers (whole inventory, borders, chess borders, rows, columns) and the
 * open-to-player path of {@link InventoryBuilder}.
 */
class InventoryBuilderFillTest {

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void fillInventoryFillsEverySlot() {
        Inventory inv = new InventoryBuilder().setRows(3).setTitle("Fill")
                .fillInventory(new ItemStack(Material.GRAY_STAINED_GLASS_PANE))
                .build();
        for (int i = 0; i < inv.getSize(); i++) {
            assertThat(inv.getItem(i)).as("slot " + i).isNotNull();
        }
    }

    @Test
    void fillBorderFillsEdgesAndLeavesCenterEmpty() {
        Inventory inv = new InventoryBuilder().setRows(3).setTitle("Border")
                .fillBorder(new ItemStack(Material.BLACK_STAINED_GLASS_PANE))
                .build();
        // Corners and edges are filled.
        assertThat(inv.getItem(0)).isNotNull();
        assertThat(inv.getItem(8)).isNotNull();
        assertThat(inv.getItem(18)).isNotNull();
        assertThat(inv.getItem(26)).isNotNull();
        // Center of a 3-row inventory (slot 13) is left empty.
        assertThat(inv.getItem(13)).isNull();
    }

    @Test
    void fillChessBorderAlternatesItems() {
        ItemStack white = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemStack black = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        Inventory inv = new InventoryBuilder().setRows(3).setTitle("Chess")
                .fillChessBorder(white, black)
                .build();
        assertThat(inv.getItem(0)).isEqualTo(white);
        assertThat(inv.getItem(1)).isEqualTo(black);
    }

    @Test
    void fillChessBorderSingleRowReturnsEarly() {
        ItemStack white = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemStack black = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        Inventory inv = new InventoryBuilder().setRows(1).setTitle("OneRow")
                .fillChessBorder(white, black)
                .build();
        assertThat(inv.getItem(0)).isEqualTo(white);
        assertThat(inv.getItem(1)).isEqualTo(black);
    }

    @Test
    void fillChessBorderLargeInventoryExercisesAllBranches() {
        // A 6-row inventory drives the t==5 special-case branch in fillChessBorder.
        ItemStack white = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemStack black = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        Inventory inv = new InventoryBuilder().setRows(6).setTitle("Big")
                .fillChessBorder(white, black)
                .build();
        assertThat(inv.getItem(0)).isNotNull();
        assertThat(inv.getItem(inv.getSize() - 1)).isNotNull();
    }

    @Test
    void fillRowFillsNineSlots() {
        Inventory inv = new InventoryBuilder().setRows(3).setTitle("Row")
                .fillRow(2, new ItemStack(Material.STONE))
                .build();
        for (int i = 9; i <= 17; i++) {
            assertThat(inv.getItem(i)).as("slot " + i).isNotNull();
        }
        assertThat(inv.getItem(0)).isNull();
    }

    @Test
    void fillColumnFillsTheColumn() {
        Inventory inv = new InventoryBuilder().setRows(3).setTitle("Col")
                .fillColumn(1, new ItemStack(Material.STONE))
                .build();
        assertThat(inv.getItem(0)).isNotNull();
        assertThat(inv.getItem(9)).isNotNull();
        assertThat(inv.getItem(18)).isNotNull();
    }

    @Test
    void setItemPlacesAtSlot() {
        Inventory inv = new InventoryBuilder().setRows(1).setTitle("Single")
                .setItem(4, new ItemStack(Material.DIAMOND))
                .build();
        assertThat(inv.getItem(4)).isNotNull();
        assertThat(inv.getItem(4).getType()).isEqualTo(Material.DIAMOND);
    }

    @Test
    void openShowsInventoryToPlayer() {
        PlayerMock player = MockBukkit.getMock().addPlayer();
        new InventoryBuilder().setRows(1).setTitle("Open")
                .setItem(0, new ItemStack(Material.DIAMOND))
                .open(player);
        assertThat(player.getOpenInventory().getTopInventory().getItem(0)).isNotNull();
    }
}
