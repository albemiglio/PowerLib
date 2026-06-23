package it.mycraft.powerlib.bukkit;

import it.mycraft.powerlib.bukkit.adapters.BukkitAudienceProvider;
import it.mycraft.powerlib.common.chat.AudienceProvider;
import org.junit.jupiter.api.Test;

import java.util.ServiceLoader;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AudienceProviderServiceTest {

    @Test
    void serviceLoaderDiscoversTheBukkitProvider() {
        AudienceProvider provider = ServiceLoader.load(AudienceProvider.class).findFirst().orElse(null);
        assertNotNull(provider, "META-INF/services must register an AudienceProvider");
        assertInstanceOf(BukkitAudienceProvider.class, provider);
    }
}
