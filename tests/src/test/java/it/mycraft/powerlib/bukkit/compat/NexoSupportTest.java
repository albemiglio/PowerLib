package it.mycraft.powerlib.bukkit.compat;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NexoSupportTest {

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        NexoSupport.resetAvailabilityCache();
    }

    @AfterEach
    void tearDown() { MockBukkit.unmock(); }

    @Test
    void isAvailableFalseWhenNexoNotLoaded() {
        assertThat(NexoSupport.isAvailable()).isFalse();
    }

    @Test
    void buildItemReturnsEmptyWhenNexoNotLoaded() {
        assertThat(NexoSupport.buildItem("test", 1)).isEmpty();
    }
}
