package it.mycraft.powerlib.common.scheduler;

import java.util.ServiceLoader;

/**
 * Resolves the platform's {@link PowerScheduler} via {@link ServiceLoader}. The implementation is
 * contributed by the platform module (bukkit/bungee/velocity) through {@code META-INF/services}.
 */
public final class Schedulers {

    private static final PowerScheduler INSTANCE =
            ServiceLoader.load(PowerScheduler.class, Schedulers.class.getClassLoader())
                    .findFirst()
                    .orElse(null);

    private Schedulers() {
    }

    /**
     * @return the platform scheduler, or null if none is registered (no platform on the classpath)
     */
    public static PowerScheduler get() {
        return INSTANCE;
    }
}
