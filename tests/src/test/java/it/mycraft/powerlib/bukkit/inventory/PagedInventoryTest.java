package it.mycraft.powerlib.bukkit.inventory;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PagedInventoryTest {

    private PlayerMock player;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        player = MockBukkit.getMock().addPlayer();
    }

    @AfterEach
    void tearDown() { MockBukkit.unmock(); }

    @Test
    void firstPageRendersFirstItems() {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) items.add(new ItemStack(Material.STONE, 1));
        PagedInventoryBuilder.create(6, "Test").items(items).open(player);

        assertThat(player.getOpenInventory().getTopInventory().getItem(0)).isNotNull();
    }

    @Test
    void rowsBelowTwoIsRejected() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> PagedInventoryBuilder.create(1, "Test"));
    }

    @Test
    void rowsAboveSixIsRejected() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> PagedInventoryBuilder.create(7, "Test"));
    }
}
