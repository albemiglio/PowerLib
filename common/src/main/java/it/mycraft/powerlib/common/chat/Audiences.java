package it.mycraft.powerlib.common.chat;

import java.util.ServiceLoader;

/**
 * Resolves the platform's {@link AudienceProvider} via {@link ServiceLoader}. The implementation is
 * contributed by the platform module (bukkit/bungee/velocity) through {@code META-INF/services}.
 */
public final class Audiences {

    private static final AudienceProvider PROVIDER =
            ServiceLoader.load(AudienceProvider.class, Audiences.class.getClassLoader())
                    .findFirst()
                    .orElse(null);

    private Audiences() {
    }

    /**
     * @return the platform audience provider, or null if none is registered (no platform on the classpath)
     */
    public static AudienceProvider getProvider() {
        return PROVIDER;
    }
}
