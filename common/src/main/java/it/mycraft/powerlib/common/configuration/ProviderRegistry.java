package it.mycraft.powerlib.common.configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds the {@link ConfigurationProvider} instances available at runtime, registering the YAML and
 * JSON providers when their backing libraries (SnakeYAML, Gson) are on the classpath.
 */
public class ProviderRegistry {

    private static final Map<Class<? extends ConfigurationProvider>, ConfigurationProvider> providers = new HashMap<>();

    static {
        try {
            providers.put(YamlConfiguration.class, new YamlConfiguration());
        } catch (NoClassDefFoundError ex) {
            // Ignore, no SnakeYAML
        }

        try {
            providers.put(JsonConfiguration.class, new JsonConfiguration());
        } catch (NoClassDefFoundError ex) {
            // Ignore, no Gson
        }
    }

    /**
     * @param provider the provider class to look up
     * @return the registered provider instance, or {@code null} if its library is unavailable
     */
    public static ConfigurationProvider getProvider(Class<? extends ConfigurationProvider> provider) {
        return providers.get(provider);
    }
}
