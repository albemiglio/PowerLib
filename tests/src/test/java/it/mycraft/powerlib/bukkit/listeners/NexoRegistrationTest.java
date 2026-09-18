package it.mycraft.powerlib.bukkit.listeners;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.SimpleEntityMock;
import com.nexomc.nexo.api.events.furniture.StubFurnitureMechanic;
import it.mycraft.powerlib.bukkit.events.NexoFurnitureInteractEvent;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Covers how the bridge attaches itself to a plugin. {@code register(Plugin)} refuses to do anything
 * without a live Nexo, so the wiring it delegates to — {@code attach} — is driven directly; what it wires
 * is then verified the only way that proves it works, by firing a native Nexo event (the stand-in under
 * {@code com.nexomc.nexo.api.events.furniture}) through the plugin manager and watching for PowerLib's
 * own event coming back out.
 *
 * <p>Nexo's events are registered dynamically, by class and executor rather than by annotation, so
 * nothing but an end-to-end dispatch shows that the executor really reached the bridge.
 */
class NexoRegistrationTest {

    private ServerMock server;
    private Plugin plugin;
    private List<NexoFurnitureInteractEvent> republished;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin("PowerLibTest");
        republished = new ArrayList<>();
        server.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onInteract(NexoFurnitureInteractEvent event) {
                republished.add(event);
            }
        }, plugin);
    }

    @AfterEach
    void tearDown() {
        NexoListener.unregister(); // never leave a bridge attached to the next test's handler lists
        MockBukkit.unmock();
    }

    private Event nativeInteract(String furnitureId) {
        return new com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent(
                server.addPlayer(), new StubFurnitureMechanic(furnitureId), new SimpleEntityMock(server),
                EquipmentSlot.HAND);
    }

    @Test
    void attachingWiresNexosOwnEventsToTheBridge() {
        NexoListener.attach(plugin);

        server.getPluginManager().callEvent(nativeInteract("chair"));

        assertThat(republished).hasSize(1);
        assertThat(republished.get(0).getFurnitureId()).isEqualTo("chair");
    }

    @Test
    void attachingTwiceLeavesASingleBridgeListening() {
        // Without the detach that opens attach(), a plugin reloading PowerLib would stack bridges and
        // every furniture interaction would be republished once per reload.
        NexoListener.attach(plugin);
        NexoListener.attach(plugin);

        server.getPluginManager().callEvent(nativeInteract("chair"));

        assertThat(republished).hasSize(1);
    }

    @Test
    void unregisteringDetachesTheBridgeFromNexosEvents() {
        NexoListener.attach(plugin);

        NexoListener.unregister();
        server.getPluginManager().callEvent(nativeInteract("chair"));

        assertThat(republished).isEmpty();
    }

    @Test
    void unregisteringWithoutAnAttachedBridgeIsANoOp() {
        assertThatCode(NexoListener::unregister).doesNotThrowAnyException();

        NexoListener.attach(plugin);
        NexoListener.unregister();
        assertThatCode(NexoListener::unregister).doesNotThrowAnyException();

        server.getPluginManager().callEvent(nativeInteract("chair"));
        assertThat(republished).isEmpty();
    }

    @Test
    void registeringWithoutNexoInstalledAttachesNothing() {
        // The guard that keeps PowerLib inert on the servers that do not run Nexo, which is most of them.
        NexoListener.register(plugin);

        server.getPluginManager().callEvent(nativeInteract("chair"));

        assertThat(republished).isEmpty();
    }
}
