package it.mycraft.powerlib.bukkit.utils;

import be.seeseemelk.mockbukkit.MockBukkit;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers the reflective binding to ItemsAdder. The stand-in {@code CustomStack} on the test classpath
 * stands in for the real plugin, so these tests fail if the bridge ever stops resolving the API it
 * expects — which is the whole point of dropping the compile-time dependency.
 */
class ItemsAdderUtilsTest {

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        CustomStack.clear();
    }

    @AfterEach
    void tearDown() {
        CustomStack.clear();
        MockBukkit.unmock();
    }

    @Test
    void bindsToTheItemsAdderApi() {
        assertThat(ItemsAdderUtils.isAvailable()).isTrue();
    }

    @Test
    void buildsARegisteredItem() {
        CustomStack.register("novaverse:badge", new ItemStack(Material.PAPER, 3));

        ItemStack built = ItemsAdderUtils.itemStackFromId("novaverse:badge");

        assertThat(built).isNotNull();
        assertThat(built.getType()).isEqualTo(Material.PAPER);
        assertThat(built.getAmount()).isEqualTo(3);
    }

    @Test
    void unknownIdYieldsNoItem() {
        assertThat(ItemsAdderUtils.itemStackFromId("novaverse:nope")).isNull();
    }

    @Test
    void nullIdYieldsNoItem() {
        assertThat(ItemsAdderUtils.itemStackFromId(null)).isNull();
    }
}
