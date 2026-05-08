package it.mycraft.powerlib.bukkit.compat;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistryCompatTest {

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void glowEnchantReturnsAnEnchantment() {
        Enchantment glow = RegistryCompat.glowEnchant();
        assertThat(glow).isNotNull();
    }

    @Test
    void potionEffectStrengthResolves() {
        PotionEffectType strength = RegistryCompat.potionEffect("strength");
        assertThat(strength).isNotNull();
    }

    @Test
    void potionEffectSlownessResolves() {
        assertThat(RegistryCompat.potionEffect("slowness")).isNotNull();
    }

    @Test
    void potionEffectJumpBoostResolves() {
        assertThat(RegistryCompat.potionEffect("jump_boost")).isNotNull();
    }

    @Test
    void potionEffectInstantDamageResolves() {
        assertThat(RegistryCompat.potionEffect("instant_damage")).isNotNull();
    }

    @Test
    void enchantmentLookupIsCached() {
        Enchantment first = RegistryCompat.enchantment("unbreaking");
        Enchantment second = RegistryCompat.enchantment("unbreaking");
        assertThat(first).isSameAs(second);
    }
}
