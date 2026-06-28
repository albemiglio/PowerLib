package it.mycraft.powerlib.bukkit.item;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers the guard/early-return paths of {@link ItemUtils#compare}. The full positive-comparison
 * branch is intentionally not exercised here: it ends in {@code new NBTItem(...).getCompound()},
 * and the de.tr7zw NBT-API needs real NMS ({@code ITEMSTACK_NMSCOPY}) that MockBukkit does not
 * provide, so it cannot run as a unit test without a live server.
 */
class ItemUtilsTest {

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void nullOrAirIsNeverEqual() {
        ItemStack stone = new ItemStack(Material.STONE);
        assertThat(ItemUtils.compare(null, stone)).isFalse();
        assertThat(ItemUtils.compare(stone, null)).isFalse();
        assertThat(ItemUtils.compare(new ItemStack(Material.AIR), stone)).isFalse();
        assertThat(ItemUtils.compare(stone, new ItemStack(Material.AIR))).isFalse();
    }

    @Test
    void sameReferenceIsEqual() {
        ItemStack stone = new ItemStack(Material.STONE);
        assertThat(ItemUtils.compare(stone, stone)).isTrue();
    }

    @Test
    void differentAmountsAreNotEqualWhenAmountMatters() {
        ItemStack a = new ItemStack(Material.STONE, 1);
        ItemStack b = new ItemStack(Material.STONE, 2);
        assertThat(ItemUtils.compare(a, b, false)).isFalse();
    }

}
