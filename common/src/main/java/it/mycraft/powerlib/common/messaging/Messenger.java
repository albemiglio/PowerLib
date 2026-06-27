package it.mycraft.powerlib.common.messaging;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Typed publish/subscribe over a {@link MessageTransport}. Encodes outbound payloads and dispatches
 * inbound frames to the subscribers of their {@link Channel}, decoded to the channel's type.
 */
public final class Messenger {

    private final MessageTransport transport;
    private final Map<String, Channel<?>> channels = new ConcurrentHashMap<>();
    private final Map<String, List<Consumer<Object>>> subscribers = new ConcurrentHashMap<>();

    /**
     * Creates a messenger over the given transport and starts listening for inbound frames.
     *
     * @param transport the underlying byte transport
     */
    public Messenger(MessageTransport transport) {
        this.transport = transport;
        transport.listen(this::receive);
    }

    /**
     * Encodes and sends a payload on the given channel.
     *
     * @param channel the channel to publish on
     * @param payload the payload to send
     * @param <T>     the payload type
     */
    public <T> void publish(Channel<T> channel, T payload) {
        transport.send(channel.name(), channel.encode(payload));
    }

    /**
     * Registers a handler invoked with every decoded payload received on the given channel.
     *
     * @param channel the channel to subscribe to
     * @param handler the handler for decoded payloads
     * @param <T>     the payload type
     */
    @SuppressWarnings("unchecked")
    public <T> void subscribe(Channel<T> channel, Consumer<T> handler) {
        channels.put(channel.name(), channel);
        subscribers.computeIfAbsent(channel.name(), key -> new CopyOnWriteArrayList<>())
                .add((Consumer<Object>) handler);
    }

    private void receive(String name, byte[] data) {
        Channel<?> channel = channels.get(name);
        if (channel == null) {
            return;
        }
        Object payload = channel.decode(data);
        List<Consumer<Object>> subs = subscribers.get(name);
        if (subs != null) {
            for (Consumer<Object> sub : subs) {
                sub.accept(payload);
            }
        }
    }
}
