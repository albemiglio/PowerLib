package it.mycraft.powerlib.components;

import be.seeseemelk.mockbukkit.MockBukkit;
import it.mycraft.powerlib.bukkit.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentBuilderTest {

    @BeforeEach
    void setUp() { MockBukkit.mock(); }

    @AfterEach
    void tearDown() { MockBukkit.unmock(); }

    @Test
    void unbreakableIsApplied() {
        ItemBuilder builder = new ItemBuilder().setMaterial(Material.DIAMOND_PICKAXE);
        ComponentBuilder.create().unbreakable(true).applyTo(builder);
        ItemStack stack = builder.build();
        assertThat(stack.getItemMeta().isUnbreakable()).isTrue();
    }

    @Test
    void customModelDataIsApplied() {
        ItemBuilder builder = new ItemBuilder().setMaterial(Material.DIAMOND);
        ComponentBuilder.create().customModelData(1234).applyTo(builder);
        ItemStack stack = builder.build();
        assertThat(stack.getItemMeta().getCustomModelData()).isEqualTo(1234);
    }
}
