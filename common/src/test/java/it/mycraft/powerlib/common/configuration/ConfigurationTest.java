package it.mycraft.powerlib.common.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurationTest {

    @Test
    void yamlLoadsAndReadsTypedValues() {
        Configuration cfg = ConfigurationProvider.getProvider(YamlConfiguration.class)
                .load("name: PowerLib\nversion: 2\nnested:\n  flag: true\n");
        assertEquals("PowerLib", cfg.getString("name"));
        assertEquals(2, cfg.getInt("version"));
        assertTrue(cfg.getBoolean("nested.flag"));
    }
}
