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
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

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
    void aPlacementWithoutABaseEntityIsLocatedByItsBlock() {
        // Nexo reports the base entity only once it exists; a handler that vetoes a placement still needs
        // to know where it was going, and the target block is the only thing left to say so.
        AtomicReference<NexoFurniturePlaceEvent> fired =
                listenFor(NexoFurniturePlaceEvent.class, event -> { });
        Block block = server.addSimpleWorld("world").getBlockAt(4, 5, 6);

        bridge.onNativePlace(bridge, new com.nexomc.nexo.api.events.furniture.NexoFurniturePlaceEvent(
                server.addPlayer(), new StubFurnitureMechanic("table"), null, block, null,
                EquipmentSlot.HAND));

        assertThat(fired.get()).isNotNull();
        assertThat(fired.get().getLocation()).isEqualTo(block.getLocation());
        assertThat(fired.get().getBlock()).isSameAs(block);
    }

    @Test
    void aPlacementWithNeitherBaseEntityNorBlockHasNoLocation() {
        AtomicReference<NexoFurniturePlaceEvent> fired =
                listenFor(NexoFurniturePlaceEvent.class, event -> { });

        bridge.onNativePlace(bridge, new com.nexomc.nexo.api.events.furniture.NexoFurniturePlaceEvent(
                server.addPlayer(), new StubFurnitureMechanic("table"), null, null, null,
                EquipmentSlot.HAND));

        assertThat(fired.get()).isNotNull();
        assertThat(fired.get().getLocation()).isNull();
    }

    @Test
    void offHandPlacementsAreIgnored() {
        AtomicReference<NexoFurniturePlaceEvent> fired =
                listenFor(NexoFurniturePlaceEvent.class, event -> { });

        bridge.onNativePlace(bridge, new com.nexomc.nexo.api.events.furniture.NexoFurniturePlaceEvent(
                server.addPlayer(), new StubFurnitureMechanic("table"), new SimpleEntityMock(server),
                null, null, EquipmentSlot.OFF_HAND));

        assertThat(fired.get()).isNull();
    }

    @Test
    void anUnnamedMechanicPlacesNothing() {
        AtomicReference<NexoFurniturePlaceEvent> fired =
                listenFor(NexoFurniturePlaceEvent.class, event -> { });

        bridge.onNativePlace(bridge, new com.nexomc.nexo.api.events.furniture.NexoFurniturePlaceEvent(
                server.addPlayer(), new StubFurnitureMechanic(""), new SimpleEntityMock(server),
                null, null, EquipmentSlot.HAND));

        assertThat(fired.get()).isNull();
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
    void aBreakWithoutAPlayerIsIgnored() {
        // Nexo also fires its break event for removals no player caused (a piston, a plugin); PowerLib's
        // break event always names a player, so those have no counterpart to republish.
        AtomicReference<NexoFurnitureBreakEvent> fired =
                listenFor(NexoFurnitureBreakEvent.class, event -> { });

        bridge.onNativeBreak(bridge, new com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent(
                null, new StubFurnitureMechanic("lamp"), new SimpleEntityMock(server)));

        assertThat(fired.get()).isNull();
    }

    // --- mechanics from a drifted Nexo build --------------------------------------------------------

    @Test
    void aMechanicWithoutAnIdAccessorFiresNothing() {
        // PowerLib reads the id off whatever object Nexo hands it. A build that renamed or moved the
        // accessor must leave the bridge silent, not throw out of Nexo's own event.
        AtomicReference<NexoFurnitureBreakEvent> fired =
                listenFor(NexoFurnitureBreakEvent.class, event -> { });

        assertThatCode(() -> bridge.onNativeBreak(bridge,
                new com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent(
                        server.addPlayer(), new Object(), new SimpleEntityMock(server))))
                .doesNotThrowAnyException();

        assertThat(fired.get()).isNull();
    }

    @Test
    void aMechanicWhoseIdLookupThrowsFiresNothing() {
        AtomicReference<NexoFurnitureBreakEvent> fired =
                listenFor(NexoFurnitureBreakEvent.class, event -> { });

        assertThatCode(() -> bridge.onNativeBreak(bridge,
                new com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent(
                        server.addPlayer(),
                        StubFurnitureMechanic.failing(new IllegalStateException("furniture is gone")),
                        new SimpleEntityMock(server))))
                .doesNotThrowAnyException();

        assertThat(fired.get()).isNull();
    }

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

    // --- Bukkit handlers, with the native events bound ------------------------------------------------

    @Test
    void aRightClickOnAnOrdinaryBlockIsRepublishedAsNothing() {
        // Binding Nexo's own events does not switch the Bukkit handlers off: they keep serving Nexo's
        // custom blocks, which the native furniture events never report. An ordinary block is simply not
        // one of them, and must come out the other side untouched.
        AtomicReference<NexoFurnitureInteractEvent> fired =
                listenFor(NexoFurnitureInteractEvent.class, event -> { });
        Block block = server.addSimpleWorld("world").getBlockAt(0, 64, 0);
        PlayerInteractEvent event = new PlayerInteractEvent(server.addPlayer(), Action.RIGHT_CLICK_BLOCK,
                null, block, BlockFace.UP, EquipmentSlot.HAND);

        bridge.onBlockInteract(event);

        assertThat(fired.get()).isNull();
        assertThat(event.isCancelled()).isFalse();
    }

    @Test
    void aRightClickOnAnOrdinaryEntityIsRepublishedAsNothing() {
        AtomicReference<NexoFurnitureInteractEvent> fired =
                listenFor(NexoFurnitureInteractEvent.class, event -> { });
        PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(
                server.addPlayer(), new SimpleEntityMock(server), EquipmentSlot.HAND);

        bridge.onEntityInteract(event);

        assertThat(fired.get()).isNull();
        assertThat(event.isCancelled()).isFalse();
    }

    // --- drifted Nexo builds -------------------------------------------------------------------------

    @Test
    void anAccessorThatThrowsCostsOnlyTheValueItWouldHaveReturned() {
        // Nexo's Kotlin accessors can blow up on furniture that is already gone. The interaction is still
        // worth republishing — minus the piece that could not be read — and must not surface as an
        // exception thrown out of Nexo's own event call.
        AtomicReference<NexoFurnitureInteractEvent> fired =
                listenFor(NexoFurnitureInteractEvent.class, event -> { });

        assertThatCode(() -> bridge.onNativeInteract(bridge,
                new com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent(
                        server.addPlayer(), new StubFurnitureMechanic("chair"), new SimpleEntityMock(server),
                        EquipmentSlot.HAND, new IllegalStateException("furniture is gone"))))
                .doesNotThrowAnyException();

        assertThat(fired.get()).isNotNull();
        assertThat(fired.get().getFurnitureId()).isEqualTo("chair");
        assertThat(fired.get().getNexoFurniture()).isNull();
    }

    @Test
    void anEventOfAnotherTypeIsNotReadAtAll() {
        // Each native event gets its own executor; handing one the wrong event must not make the bridge
        // republish a half-read interaction.
        AtomicReference<NexoFurnitureInteractEvent> fired =
                listenFor(NexoFurnitureInteractEvent.class, event -> { });

        bridge.onNativeInteract(bridge, new com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent(
                server.addPlayer(), new StubFurnitureMechanic("chair"), new SimpleEntityMock(server)));

        assertThat(fired.get()).isNull();
    }

    @Test
    void aBuildWithoutOneOfTheDenySettersStillDeniesTheOthers() throws ReflectiveOperationException {
        // Nexo dropping setCanRunAction must cost exactly that denial, not the whole cancellation.
        setDenyHandle("denyCanRunAction", null);
        listenFor(NexoFurnitureInteractEvent.class, event -> event.setCancelled(true));

        com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent source =
                new com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent(
                        server.addPlayer(), new StubFurnitureMechanic("chair"),
                        new SimpleEntityMock(server), EquipmentSlot.HAND);
        assertThatCode(() -> bridge.onNativeInteract(bridge, source)).doesNotThrowAnyException();

        assertThat(source.isCancelled()).isTrue();
        assertThat(source.getUseFurniture()).isEqualTo(Event.Result.DENY);
        assertThat(source.getUseItemInHand()).isEqualTo(Event.Result.DENY);
        assertThat(source.getCanRunAction()).isEqualTo(Event.Result.DEFAULT);
    }

    @Test
    void aDenySetterThatNoLongerAppliesStillLeavesTheRestDenied() throws ReflectiveOperationException {
        // Same shape as above, for the other half of the drift: the handle resolved, but Nexo moved the
        // setter, so invoking it fails at the call instead of at bind time.
        Method foreign = ForeignSetter.class.getDeclaredMethod("setUseFurniture", Event.Result.class);
        foreign.setAccessible(true);
        setDenyHandle("denyUseFurniture", foreign);
        listenFor(NexoFurnitureInteractEvent.class, event -> event.setCancelled(true));

        com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent source =
                new com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent(
                        server.addPlayer(), new StubFurnitureMechanic("chair"),
                        new SimpleEntityMock(server), EquipmentSlot.HAND);
        assertThatCode(() -> bridge.onNativeInteract(bridge, source)).doesNotThrowAnyException();

        assertThat(source.isCancelled()).isTrue();
        assertThat(source.getUseFurniture()).isEqualTo(Event.Result.DEFAULT);
        assertThat(source.getUseItemInHand()).isEqualTo(Event.Result.DENY);
        assertThat(source.getCanRunAction()).isEqualTo(Event.Result.DENY);
    }

    /**
     * Replaces one of the three resolved deny handles, standing in for a Nexo build that no longer
     * exposes it. Every handle is resolved again by {@code bindNativeEvents()} in {@code setUp}, so the
     * change lasts only for the running test.
     *
     * @param name   the field holding the handle
     * @param handle the handle to put in its place
     */
    private static void setDenyHandle(String name, Method handle) throws ReflectiveOperationException {
        Field field = NexoListener.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(null, handle);
    }

    /** A setter with Nexo's shape, on a class Nexo's event knows nothing about. */
    private static final class ForeignSetter {

        @SuppressWarnings("unused")
        private void setUseFurniture(Event.Result result) {
            // never runs: the point is that invoking it on Nexo's own event fails
        }
    }
}
