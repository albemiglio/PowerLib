package it.mycraft.powerlib.bukkit.compat;

import be.seeseemelk.mockbukkit.MockBukkit;
import it.mycraft.powerlib.common.objects.Pair;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ItemsAdderBridgeTest {

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        ItemsAdderBridge.resetAvailabilityCache();
    }

    @AfterEach
    void tearDown() { MockBukkit.unmock(); }

    @Test
    void isAvailableReturnsFalseWhenItemsAdderPluginNotLoaded() {
        assertThat(ItemsAdderBridge.isAvailable()).isFalse();
    }

    @Test
    void extractDataReturnsEmptyWhenItemsAdderUnavailable() {
        ItemStack stack = new ItemStack(Material.STONE);
        Optional<Pair<String,String>> data = ItemsAdderBridge.extractData(stack);
        assertThat(data).isEmpty();
    }

    @Test
    void buildItemReturnsEmptyWhenItemsAdderUnavailable() {
        Optional<ItemStack> built = ItemsAdderBridge.buildItem("namespace:id", 1);
        assertThat(built).isEmpty();
    }
}
