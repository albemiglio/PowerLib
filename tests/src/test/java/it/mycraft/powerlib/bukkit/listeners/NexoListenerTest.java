package it.mycraft.powerlib.bukkit.listeners;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.entity.SimpleEntityMock;
import it.mycraft.powerlib.bukkit.events.NexoFurnitureBreakEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the Nexo break bridge in {@link NexoListener}.
 *
 * <p>With Nexo absent under MockBukkit, {@link it.mycraft.powerlib.bukkit.utils.NexoUtils} returns a
 * {@code null} id, so the two {@code @EventHandler} entry points ({@code onBlockBreak}/{@code
 * onEntityBreak}) exercise the "not a Nexo object -> do nothing" path. The re-fire + cancel-propagation
 * contract is driven directly through the private {@code fireBreak} with a chosen id, since that path is
 * unreachable through the public handlers without a live Nexo install.
 */
class NexoListenerTest {

    private ServerMock server;
    private Plugin plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin("PowerLibTest");
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    private static NexoListener newListener() throws ReflectiveOperationException {
        Constructor<NexoListener> ctor = NexoListener.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        return ctor.newInstance();
    }

    private static void invokeFireBreak(NexoListener listener, Player player, String id, Object furniture,
                                        Cancellable source) throws ReflectiveOperationException {
        Method m = NexoListener.class.getDeclaredMethod(
                "fireBreak", Player.class, String.class, Object.class, Cancellable.class);
        m.setAccessible(true);
        m.invoke(listener, player, id, furniture, source);
    }

    private static Cancellable simpleCancellable() {
        return new Cancellable() {
            private boolean cancelled;

            @Override
            public boolean isCancelled() {
                return cancelled;
            }

            @Override
            public void setCancelled(boolean cancel) {
                this.cancelled = cancel;
            }
        };
    }

    @Test
    void fireBreakEmitsEventAndPropagatesCancellationToTheSource() throws ReflectiveOperationException {
        AtomicReference<NexoFurnitureBreakEvent> captured = new AtomicReference<>();
        server.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onBreak(NexoFurnitureBreakEvent event) {
                captured.set(event);
                event.setCancelled(true);
            }
        }, plugin);

        PlayerMock player = server.addPlayer();
        Object mechanic = new Object();
        Cancellable source = simpleCancellable();

        invokeFireBreak(newListener(), player, "chair", mechanic, source);

        assertThat(captured.get()).isNotNull();
        assertThat(captured.get().getPlayer()).isSameAs(player);
        assertThat(captured.get().getFurnitureId()).isEqualTo("chair");
        assertThat(captured.get().getNexoFurniture()).isSameAs(mechanic);
        // The handler cancelled the bridge event, which must cancel the originating Bukkit event.
        assertThat(source.isCancelled()).isTrue();
    }

    @Test
    void fireBreakLeavesTheSourceUncancelledWhenNoHandlerCancels() throws ReflectiveOperationException {
        server.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onBreak(NexoFurnitureBreakEvent event) {
                // observe only, never cancel
            }
        }, plugin);

        Cancellable source = simpleCancellable();
        invokeFireBreak(newListener(), server.addPlayer(), "chair", new Object(), source);

        assertThat(source.isCancelled()).isFalse();
    }

    @Test
    void fireBreakIgnoresNullAndEmptyIds() throws ReflectiveOperationException {
        AtomicReference<NexoFurnitureBreakEvent> captured = new AtomicReference<>();
        server.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onBreak(NexoFurnitureBreakEvent event) {
                captured.set(event);
            }
        }, plugin);

        NexoListener listener = newListener();
        Cancellable nullId = simpleCancellable();
        Cancellable emptyId = simpleCancellable();

        invokeFireBreak(listener, server.addPlayer(), null, new Object(), nullId);
        invokeFireBreak(listener, server.addPlayer(), "", new Object(), emptyId);

        assertThat(captured.get()).isNull();
        assertThat(nullId.isCancelled()).isFalse();
        assertThat(emptyId.isCancelled()).isFalse();
    }

    @Test
    void onBlockBreakOfANonNexoBlockDoesNothing() throws ReflectiveOperationException {
        PlayerMock player = server.addPlayer();
        BlockBreakEvent event =
                new BlockBreakEvent(server.addSimpleWorld("world").getBlockAt(0, 0, 0), player);

        newListener().onBlockBreak(event);

        assertThat(event.isCancelled()).isFalse();
    }

    @Test
    void onEntityBreakByAPlayerOfANonNexoEntityDoesNothing() throws ReflectiveOperationException {
        Player damager = server.addPlayer();
        Entity furniture = new SimpleEntityMock(server);
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                damager, furniture, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0);

        newListener().onEntityBreak(event);

        assertThat(event.isCancelled()).isFalse();
    }

    @Test
    void onEntityBreakByANonPlayerIsIgnored() throws ReflectiveOperationException {
        Entity damager = new SimpleEntityMock(server);
        Entity victim = new SimpleEntityMock(server);
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(
                damager, victim, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 1.0);

        newListener().onEntityBreak(event);

        assertThat(event.isCancelled()).isFalse();
    }
}
