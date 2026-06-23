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

    public static <T> Channel<T> of(String name, Codec<T> codec) {
        return new Channel<>(name, codec);
    }

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
