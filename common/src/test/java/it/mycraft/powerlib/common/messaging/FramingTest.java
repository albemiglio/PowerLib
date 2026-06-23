package it.mycraft.powerlib.common.messaging;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FramingTest {

    @Test
    void frameRoundTripsTheChannelNameAndPayload() {
        byte[] frame = Framing.frame("auth", "secret".getBytes(StandardCharsets.UTF_8));

        Framing.Frame parsed = Framing.parse(frame);

        assertEquals("auth", parsed.channel());
        assertArrayEquals("secret".getBytes(StandardCharsets.UTF_8), parsed.data());
    }

    @Test
    void framePreservesBinaryAndEmptyPayloads() {
        byte[] binary = {0, 1, 2, -1, -128, 127};

        Framing.Frame binaryParsed = Framing.parse(Framing.frame("channel", binary));
        Framing.Frame emptyParsed = Framing.parse(Framing.frame("channel", new byte[0]));

        assertArrayEquals(binary, binaryParsed.data());
        assertEquals(0, emptyParsed.data().length);
    }
}
