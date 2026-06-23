package it.mycraft.powerlib.common.messaging;

import java.util.ServiceLoader;

/**
 * Resolves the platform's {@link MessageTransport} via {@link ServiceLoader} and exposes a ready
 * {@link Messenger} over it. Returns null if no transport is registered.
 */
public final class Messengers {

    private static final Messenger INSTANCE = init();

    private Messengers() {
    }

    private static Messenger init() {
        MessageTransport transport =
                ServiceLoader.load(MessageTransport.class, Messengers.class.getClassLoader())
                        .findFirst()
                        .orElse(null);
        return transport == null ? null : new Messenger(transport);
    }

    public static Messenger get() {
        return INSTANCE;
    }
}
