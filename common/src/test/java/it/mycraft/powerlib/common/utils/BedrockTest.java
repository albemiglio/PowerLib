package it.mycraft.powerlib.common.utils;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The pure (dependency-free) Bedrock helpers. Floodgate isn't on the test classpath, so the reflective
 * helpers must degrade to safe defaults rather than throw.
 */
class BedrockTest {

    @Test
    void floodgateIdHasZeroHighBits() {
        UUID floodgate = new UUID(0L, 0x00025096FE500BE1L); // new UUID(0, xuid)
        assertTrue(Bedrock.isFloodgateId(floodgate));
        assertEquals(floodgate, Bedrock.javaIdFromXuid(0x00025096FE500BE1L));
    }

    @Test
    void realJavaUuidIsNotAFloodgateId() {
        assertFalse(Bedrock.isFloodgateId(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5")));
        assertFalse(Bedrock.isFloodgateId(null));
    }

    @Test
    void stripsThePrefixOnlyWhenPresent() {
        assertEquals("Steve", Bedrock.stripPrefix(".Steve", "."));
        assertEquals("Steve", Bedrock.stripPrefix("Steve", ".")); // no prefix -> unchanged
        assertEquals(".Steve", Bedrock.stripPrefix(".Steve", "")); // empty prefix -> unchanged
        assertNull(Bedrock.stripPrefix(null, "."));
    }

    @Test
    void reflectiveHelpersDegradeSafelyWithoutFloodgate() {
        UUID any = UUID.randomUUID();
        assertFalse(Bedrock.available());
        assertFalse(Bedrock.isFloodgatePlayer(any));
        assertEquals("", Bedrock.prefix());
        assertNull(Bedrock.xuid(any));
        assertNull(Bedrock.javaUuid(any));
    }
}
