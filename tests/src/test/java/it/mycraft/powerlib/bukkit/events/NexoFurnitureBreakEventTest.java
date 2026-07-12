package it.mycraft.powerlib.bukkit.events;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.event.HandlerList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link NexoFurnitureBreakEvent}: the fields it carries, its cancellable contract, and
 * the {@link HandlerList} wiring the Bukkit event system requires.
 */
class NexoFurnitureBreakEventTest {

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void carriesPlayerIdAndRawMechanic() {
        PlayerMock player = MockBukkit.getMock().addPlayer();
        Object mechanic = new Object();

        NexoFurnitureBreakEvent event = new NexoFurnitureBreakEvent(player, "chair", mechanic);

        assertThat(event.getPlayer()).isSameAs(player);
        assertThat(event.getFurnitureId()).isEqualTo("chair");
        assertThat(event.getNexoFurniture()).isSameAs(mechanic);
        assertThat(event.isCancelled()).isFalse();
    }

    @Test
    void isCancellable() {
        NexoFurnitureBreakEvent event =
                new NexoFurnitureBreakEvent(MockBukkit.getMock().addPlayer(), "chair", null);

        event.setCancelled(true);

        assertThat(event.isCancelled()).isTrue();
    }

    @Test
    void exposesANonNullSharedHandlerList() {
        // A Bukkit Event whose HandlerList is missing/null throws when a plugin registers for it, so the
        // instance getHandlers() and the static getHandlerList() must both exist and be the same list.
        NexoFurnitureBreakEvent event =
                new NexoFurnitureBreakEvent(MockBukkit.getMock().addPlayer(), "chair", null);

        HandlerList fromInstance = event.getHandlers();
        HandlerList fromStatic = NexoFurnitureBreakEvent.getHandlerList();

        assertThat(fromInstance).isNotNull();
        assertThat(fromStatic).isNotNull();
        assertThat(fromInstance).isSameAs(fromStatic);
    }
}
