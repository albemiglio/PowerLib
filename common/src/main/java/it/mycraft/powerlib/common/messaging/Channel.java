package it.mycraft.powerlib.common.messaging;

/**
 * A named, typed channel. The same {@code Channel} declaration is shared by proxy and backend so
 * both ends agree on the name and the payload type.
 */
public final class Channel<T> {

    private final String name;
    private final Codec<T> codec;

    private Channel(String name, Codec<T> codec) {
        this.name = name;
        this.codec = codec;
    }

    /**
     * Declares a channel with the given name and payload codec.
     *
     * @param name  the channel name shared by both ends
     * @param codec codec used to encode and decode the payload
     * @param <T>   the payload type
     * @return a new channel declaration
     */
    public static <T> Channel<T> of(String name, Codec<T> codec) {
        return new Channel<>(name, codec);
    }

    /** @return the channel name */
    public String name() {
        return name;
    }

    byte[] encode(T value) {
        return codec.encode(value);
    }

    T decode(byte[] data) {
        return codec.decode(data);
    }
}
