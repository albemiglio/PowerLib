package it.mycraft.powerlib.bukkit.listeners;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.entity.SimpleEntityMock;
import com.nexomc.nexo.api.events.furniture.StubFurnitureMechanic;
import it.mycraft.powerlib.bukkit.events.NexoFurnitureBreakEvent;
import it.mycraft.powerlib.bukkit.events.NexoFurnitureInteractEvent;
import it.mycraft.powerlib.bukkit.events.NexoFurniturePlaceEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Drives {@link NexoListener}'s native-Nexo paths against stand-in event classes published under Nexo's
 * own package name (see {@code com.nexomc.nexo.api.events.furniture}), so the bridge binds to them exactly
 * as it would on a live server, with no Nexo on the classpath and no compile-time dependency on it.
 *
 * <p>What is worth proving here is what Bukkit events cannot express: placement, which has no Bukkit
 * counterpart at all, and denying Nexo's three follow-up results when a handler cancels an interaction —
 * cancelling the Bukkit event alone leaves Nexo free to use the furniture, consume the held item and run
 * the furniture's configured action.
 */
class NexoNativeBridgeTest {

    private ServerMock server;
    private Plugin plugin;
    private NexoListener bridge;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin("PowerLibTest");
        bridge = NexoListener.bindNativeEvents();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    private <T extends Event> AtomicReference<T> listenFor(Class<T> type, Consumer<T> onEvent) {
        AtomicReference<T> captured = new AtomicReference<>();
        server.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onInteract(NexoFurnitureInteractEvent event) {
                capture(event);
            }

            @EventHandler
            public void onPlace(NexoFurniturePlaceEvent event) {
                capture(event);
            }

            @EventHandler
            public void onBreak(NexoFurnitureBreakEvent event) {
                capture(event);
            }

            private void capture(Event event) {
                if (!type.isInstance(event)) {
                    return;
                }
                T typed = type.cast(event);
                captured.set(typed);
                onEvent.accept(typed);
            }
        }, plugin);
        return captured;
    }

    // --- interact -----------------------------------------------------------------------------------

    @Test
    void nativeInteractIsRepublishedWithTheIdNexoResolved() {
        AtomicReference<NexoFurnitureInteractEvent> fired =
                listenFor(NexoFurnitureInteractEvent.class, event -> { });
        PlayerMock player = server.addPlayer();
        Entity baseEntity = new SimpleEntityMock(server);

        com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent source =
                new com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent(
                        player, new StubFurnitureMechanic("chair"), baseEntity, EquipmentSlot.HAND);
        bridge.onNativeInteract(bridge, source);

        assertThat(fired.get()).isNotNull();
        assertThat(fired.get().getPlayer()).isSameAs(player);
        assertThat(fired.get().getFurnitureId()).isEqualTo("chair");
        assertThat(fired.get().getNexoFurniture()).isSameAs(baseEntity);
        assertThat(source.isCancelled()).isFalse();
        assertThat(source.getUseFurniture()).isEqualTo(Event.Result.DEFAULT);
    }

    @Test
    void cancellingAnInteractionAlsoDeniesNexosThreeFollowUpResults() {
        listenFor(NexoFurnitureInteractEvent.class, event -> event.setCancelled(true));

        com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent source =
                new com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent(
                        server.addPlayer(), new StubFurnitureMechanic("chair"),
                        new SimpleEntityMock(server), EquipmentSlot.HAND);
        bridge.onNativeInteract(bridge, source);

        assertThat(source.isCancelled()).isTrue();
        assertThat(source.getUseFurniture()).isEqualTo(Event.Result.DENY);
        assertThat(source.getUseItemInHand()).isEqualTo(Event.Result.DENY);
        assertThat(source.getCanRunAction()).isEqualTo(Event.Result.DENY);
    }

    @Test
    void offHandInteractionsAreIgnored() {
        AtomicReference<NexoFurnitureInteractEvent> fired =
                listenFor(NexoFurnitureInteractEvent.class, event -> { });

        bridge.onNativeInteract(bridge, new com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent(
                server.addPlayer(), new StubFurnitureMechanic("chair"),
                new SimpleEntityMock(server), EquipmentSlot.OFF_HAND));

        assertThat(fired.get()).isNull();
    }

    @Test
    void anUnnamedMechanicFiresNothing() {
        AtomicReference<NexoFurnitureInteractEvent> fired =
                listenFor(NexoFurnitureInteractEvent.class, event -> { });

        bridge.onNativeInteract(bridge, new com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent(
                server.addPlayer(), new StubFurnitureMechanic(""),
                new SimpleEntityMock(server), EquipmentSlot.HAND));
        bridge.onNativeInteract(bridge, new com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent(
                server.addPlayer(), null, new SimpleEntityMock(server), EquipmentSlot.HAND));

        assertThat(fired.get()).isNull();
    }

    // --- place --------------------------------------------------------------------------------------

    @Test
    void nativePlaceIsRepublishedWithTheFullPlacementContext() {
        AtomicReference<NexoFurniturePlaceEvent> fired =
                listenFor(NexoFurniturePlaceEvent.class, event -> { });
        PlayerMock player = server.addPlayer();
        Entity baseEntity = new SimpleEntityMock(server);
        Block block = server.addSimpleWorld("world").getBlockAt(1, 2, 3);
        ItemStack inHand = new ItemStack(Material.STONE);

        com.nexomc.nexo.api.events.furniture.NexoFurniturePlaceEvent source =
                new com.nexomc.nexo.api.events.furniture.NexoFurniturePlaceEvent(
                        player, new StubFurnitureMechanic("table"), baseEntity, block, inHand,
                        EquipmentSlot.HAND);
        bridge.onNativePlace(bridge, source);

        assertThat(fired.get()).isNotNull();
        assertThat(fired.get().getPlayer()).isSameAs(player);
        assertThat(fired.get().getFurnitureId()).isEqualTo("table");
        assertThat(fired.get().getNexoFurniture()).isSameAs(baseEntity);
        assertThat(fired.get().getBlock()).isSameAs(block);
        assertThat(fired.get().getItemInHand()).isEqualTo(inHand);
        assertThat(fired.get().getHand()).isEqualTo(EquipmentSlot.HAND);
        assertThat(fired.get().getLocation()).isEqualTo(baseEntity.getLocation());
        assertThat(source.isCancelled()).isFalse();
    }

    @Test
    void cancellingAPlacementCancelsNexosOwnEvent() {
        listenFor(NexoFurniturePlaceEvent.class, event -> event.setCancelled(true));

        com.nexomc.nexo.api.events.furniture.NexoFurniturePlaceEvent source =
                new com.nexomc.nexo.api.events.furniture.NexoFurniturePlaceEvent(
                        server.addPlayer(), new StubFurnitureMechanic("table"),
                        new SimpleEntityMock(server), null, null, EquipmentSlot.HAND);
        bridge.onNativePlace(bridge, source);

        assertThat(source.isCancelled()).isTrue();
    }

    // --- break --------------------------------------------------------------------------------------

    @Test
    void nativeBreakIsRepublishedAndCancellationPropagates() {
        AtomicReference<NexoFurnitureBreakEvent> fired =
                listenFor(NexoFurnitureBreakEvent.class, event -> event.setCancelled(true));
        Entity baseEntity = new SimpleEntityMock(server);

        com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent source =
                new com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent(
                        server.addPlayer(), new StubFurnitureMechanic("lamp"), baseEntity);
        bridge.onNativeBreak(bridge, source);

        assertThat(fired.get()).isNotNull();
        assertThat(fired.get().getFurnitureId()).isEqualTo("lamp");
        assertThat(fired.get().getNexoFurniture()).isSameAs(baseEntity);
        assertThat(source.isCancelled()).isTrue();
    }
}
