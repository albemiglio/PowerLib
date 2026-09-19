package it.mycraft.powerlib.bukkit;

import be.seeseemelk.mockbukkit.MockBukkit;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Covers the {@link PowerLib#inject(Plugin)} / {@link PowerLib#shutdown()} pair.
 *
 * <p>{@code inject} creates a {@link BukkitAudiences}, which holds listeners and a scheduler task on the
 * owning plugin: without a matching {@code shutdown} every reload left one behind and accumulated another.
 */
class PowerLibLifecycleTest {

    private Plugin plugin;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin("PowerLibTest");
    }

    @AfterEach
    void tearDown() {
        PowerLib.shutdown();
        MockBukkit.unmock();
    }

    @Test
    void shutdownReleasesTheAdventurePlatform() {
        PowerLib.inject(plugin);
        assertThat(PowerLib.adventure()).isNotNull();
        assertThat(PowerLib.getPlugin()).isSameAs(plugin);

        PowerLib.shutdown();

        assertThatThrownBy(PowerLib::adventure).isInstanceOf(IllegalStateException.class);
        assertThat(PowerLib.getPlugin()).isNull();
    }

    @Test
    void injectingTwiceReplacesTheAdventurePlatformInsteadOfLeakingIt() {
        PowerLib.inject(plugin);
        BukkitAudiences first = PowerLib.adventure();

        PowerLib.inject(plugin);

        assertThat(PowerLib.adventure()).isNotSameAs(first);
    }

    @Test
    void shutdownIsSafeToCallTwiceAndBeforeAnyInject() {
        assertThatThrownBy(PowerLib::adventure).isInstanceOf(IllegalStateException.class);

        PowerLib.shutdown();
        PowerLib.inject(plugin);
        PowerLib.shutdown();
        PowerLib.shutdown();

        assertThatThrownBy(PowerLib::adventure).isInstanceOf(IllegalStateException.class);
    }
}
