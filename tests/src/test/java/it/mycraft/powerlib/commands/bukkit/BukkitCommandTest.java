package it.mycraft.powerlib.commands.bukkit;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import it.mycraft.powerlib.commands.Args;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class BukkitCommandTest {

    private MockPlugin plugin;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin("PowerLibTest");
    }

    @AfterEach
    void tearDown() { MockBukkit.unmock(); }

    @Test
    void executorIsInvokedWithParsedArgs() {
        AtomicReference<Integer> received = new AtomicReference<>();
        BukkitCommandBuilder.create("count")
                .argument(Args.integer("n"))
                .executor((sender, ctx) -> received.set(ctx.get("n", Integer.class).orElse(-1)))
                .register(plugin);

        Bukkit.dispatchCommand(MockBukkit.getMock().getConsoleSender(), "count 7");

        assertThat(received.get()).isEqualTo(7);
    }
}
