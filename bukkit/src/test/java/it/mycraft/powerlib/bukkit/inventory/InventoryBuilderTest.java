package it.mycraft.powerlib.bukkit.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InventoryBuilderTest {

    static ServerMock server;

    @BeforeAll
    static void up() {
        server = MockBukkit.mock();
    }

    @AfterAll
    static void down() {
        MockBukkit.unmock();
    }

    @Test
    void placeholderWithRegexCharsIsReplacedLiterally() {
        // Both the placeholder token ({, }) and the value ($) contain regex
        // metacharacters; literal replacement must insert the value verbatim,
        // whereas a regex replaceAll would throw or mangle the $ group reference.
        InventoryBuilder builder = new InventoryBuilder().setRows(1)
                .setTitle("Coins: {count}")
                .addPlaceHolder("{count}", "$5 (50%)");
        assertEquals("Coins: $5 (50%)", builder.getTitle(),
                "a placeholder containing regex metacharacters must be replaced literally");
    }

    @Test
    void builderCanBuildMoreThanOnce() {
        InventoryBuilder builder = new InventoryBuilder().setRows(1)
                .setTitle("Menu")
                .setItem(0, new ItemStack(Material.DIAMOND));
        builder.build();
        Inventory second = builder.build();
        assertNotNull(second.getItem(0), "building twice must not wipe the configured items");
        assertEquals(Material.DIAMOND, second.getItem(0).getType());
    }
}
