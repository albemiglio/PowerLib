package it.mycraft.powerlib.bukkit.scheduler;

import it.mycraft.powerlib.bukkit.PowerLib;
import it.mycraft.powerlib.common.scheduler.Schedulers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BukkitSchedulerTest {

    static ServerMock server;

    @BeforeAll
    static void up() {
        server = MockBukkit.mock();
        try {
            PowerLib.inject(MockBukkit.createMockPlugin());
        } catch (Throwable ignored) {
            // adventure platform may be unavailable under MockBukkit; the scheduler only needs the plugin ref
        }
    }

    @AfterAll
    static void down() {
        MockBukkit.unmock();
    }

    @Test
    void serviceLoaderProvidesTheBukkitScheduler() {
        assertInstanceOf(BukkitScheduler.class, Schedulers.get());
    }

    @Test
    void delayedTaskRunsOnlyAfterTheDelay() {
        AtomicBoolean ran = new AtomicBoolean(false);
        Schedulers.get().runLater(() -> ran.set(true), 5);
        assertFalse(ran.get(), "task must not run before its delay");
        server.getScheduler().performTicks(5);
        assertTrue(ran.get(), "task must run after its delay elapses");
    }
}
