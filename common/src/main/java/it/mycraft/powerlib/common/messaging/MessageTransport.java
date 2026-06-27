package it.mycraft.powerlib.common.messaging;

import java.util.function.BiConsumer;

/**
 * The wire underneath {@link Messenger}: raw, channel-tagged byte frames. Each platform module
 * contributes one (plugin messaging, Redis, sockets, ...) registered via {@code META-INF/services}.
 */
public interface MessageTransport {

    /**
     * Sends a raw frame on the given channel.
     *
     * @param channel the logical channel name
     * @param data    the raw payload bytes
     */
    void send(String channel, byte[] data);

    /**
     * Registers the single handler invoked for every inbound frame. Called once by {@link Messenger}.
     *
     * @param handler receives the channel name and the raw payload of each inbound frame
     */
    void listen(BiConsumer<String, byte[]> handler);
}
