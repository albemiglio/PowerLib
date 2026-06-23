package it.mycraft.powerlib.common.messaging;

import java.util.function.BiConsumer;

/**
 * The wire underneath {@link Messenger}: raw, channel-tagged byte frames. Each platform module
 * contributes one (plugin messaging, Redis, sockets, ...) registered via {@code META-INF/services}.
 */
public interface MessageTransport {

    void send(String channel, byte[] data);

    /**
     * Registers the single handler invoked for every inbound frame. Called once by {@link Messenger}.
     */
    void listen(BiConsumer<String, byte[]> handler);
}
