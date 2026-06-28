package it.mycraft.powerlib.bukkit.messaging;

import it.mycraft.powerlib.bukkit.PowerLib;
import it.mycraft.powerlib.common.messaging.Framing;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BukkitMessageTransportTest {

    static ServerMock server;
    static BukkitMessageTransport transport;

    @BeforeAll
    static void up() {
        server = MockBukkit.mock();
        try {
            PowerLib.inject(MockBukkit.createMockPlugin());
        } catch (Throwable ignored) {
            // adventure may be unavailable under MockBukkit; the transport only needs the plugin ref
        }
        transport = new BukkitMessageTransport();
    }

    @AfterAll
    static void down() {
        MockBukkit.unmock();
    }

    @Test
    void receivedFramesAreParsedAndDispatchedToTheHandler() {
        List<String> received = new ArrayList<>();
        transport.listen((channel, data) -> received.add(channel + "=" + new String(data, StandardCharsets.UTF_8)));

        transport.onPluginMessageReceived(BukkitMessageTransport.MC_CHANNEL, server.addPlayer(),
                Framing.frame("auth", "token".getBytes(StandardCharsets.UTF_8)));

        assertEquals(List.of("auth=token"), received);
    }

    @Test
    void framesOnAnotherMinecraftChannelAreIgnored() {
        List<String> received = new ArrayList<>();
        transport.listen((channel, data) -> received.add(channel));

        transport.onPluginMessageReceived("other:channel", server.addPlayer(),
                Framing.frame("auth", new byte[0]));

        assertTrue(received.isEmpty());
    }

    @Test
    void sendingNeverThrows() {
        assertDoesNotThrow(() -> transport.send("chat", "hi".getBytes(StandardCharsets.UTF_8)));
    }
}
