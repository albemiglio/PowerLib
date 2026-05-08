package it.mycraft.powerlib.bukkit.item;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersistentDataTest {

    private Plugin plugin;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin("PowerLibTest");
    }

    @AfterEach
    void tearDown() { MockBukkit.unmock(); }

    @Test
    void setPersistentDataRoundTrip() {
        NamespacedKey key = new NamespacedKey(plugin, "owner");

        ItemStack stack = new ItemBuilder()
                .setMaterial(Material.DIAMOND_SWORD)
                .setPersistentData(key, PersistentDataType.STRING, "alice")
                .build();

        String value = stack.getItemMeta().getPersistentDataContainer()
                .get(key, PersistentDataType.STRING);
        assertThat(value).isEqualTo("alice");
    }

    @Test
    void multiplePersistentDataKeysCoexist() {
        NamespacedKey nameKey = new NamespacedKey(plugin, "name");
        NamespacedKey countKey = new NamespacedKey(plugin, "count");

        ItemStack stack = new ItemBuilder()
                .setMaterial(Material.DIAMOND_SWORD)
                .setPersistentData(nameKey, PersistentDataType.STRING, "excalibur")
                .setPersistentData(countKey, PersistentDataType.INTEGER, 42)
                .build();

        var pdc = stack.getItemMeta().getPersistentDataContainer();
        assertThat(pdc.get(nameKey, PersistentDataType.STRING)).isEqualTo("excalibur");
        assertThat(pdc.get(countKey, PersistentDataType.INTEGER)).isEqualTo(42);
    }
}
