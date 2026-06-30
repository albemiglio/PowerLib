package it.mycraft.powerlib.common.messaging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Multiplexes several logical channels over a single transport channel: a frame carries the logical
 * channel name followed by its raw payload. Used by the platform {@link MessageTransport}s that ride
 * one Minecraft plugin-messaging channel.
 */
public final class Framing {

    private Framing() {
    }

    /**
     * A decoded frame: a logical channel name and its raw payload.
     *
     * @param channel the logical channel name
     * @param data    the raw payload bytes
     */
    public record Frame(String channel, byte[] data) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Frame other)) return false;
            return Objects.equals(channel, other.channel) && Arrays.equals(data, other.data);
        }

        @Override
        public int hashCode() {
            return 31 * Objects.hashCode(channel) + Arrays.hashCode(data);
        }

        @Override
        public String toString() {
            return "Frame[channel=" + channel + ", data=" + Arrays.toString(data) + "]";
        }
    }

    /**
     * Encodes a logical channel name and payload into a single frame.
     *
     * @param channel the logical channel name
     * @param data    the raw payload bytes
     * @return the encoded frame
     */
    public static byte[] frame(String channel, byte[] data) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(bytes)) {
            out.writeUTF(channel);
            out.writeInt(data.length);
            out.write(data);
        } catch (IOException e) {
            throw new UncheckedIOException(e); // a ByteArrayOutputStream never actually throws
        }
        return bytes.toByteArray();
    }

    /**
     * Decodes a frame produced by {@link #frame(String, byte[])}.
     *
     * @param frame the encoded frame bytes
     * @return the decoded channel name and payload
     */
    public static Frame parse(byte[] frame) {
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(frame))) {
            String channel = in.readUTF();
            byte[] data = new byte[in.readInt()];
            in.readFully(data);
            return new Frame(channel, data);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
