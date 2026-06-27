package it.mycraft.powerlib.common.messaging;

import java.nio.charset.StandardCharsets;

/**
 * Serializes a payload to and from bytes for transport. Supply a JSON codec to share DTOs between
 * proxy and backend, or use {@link #string()} for plain text.
 */
public interface Codec<T> {

    /**
     * @param value the payload to serialize
     * @return the encoded bytes
     */
    byte[] encode(T value);

    /**
     * @param data the encoded bytes
     * @return the deserialized payload
     */
    T decode(byte[] data);

    /** @return a codec that encodes strings as UTF-8 bytes */
    static Codec<String> string() {
        return new Codec<>() {
            @Override
            public byte[] encode(String value) {
                return value.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String decode(byte[] data) {
                return new String(data, StandardCharsets.UTF_8);
            }
        };
    }
}
