package it.mycraft.powerlib.common.messaging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Multiplexes several logical channels over a single transport channel: a frame carries the logical
 * channel name followed by its raw payload. Used by the platform {@link MessageTransport}s that ride
 * one Minecraft plugin-messaging channel.
 */
public final class Framing {

    private Framing() {
    }

    public record Frame(String channel, byte[] data) {
    }

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
