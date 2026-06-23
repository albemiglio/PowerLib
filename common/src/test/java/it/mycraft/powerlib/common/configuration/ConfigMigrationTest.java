package it.mycraft.powerlib.common.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigMigrationTest {

    private static Configuration yaml(String s) {
        return ConfigurationProvider.getProvider(YamlConfiguration.class).load(s);
    }

    @Test
    void backfillAddsMissingKeysKeepingUserValues() {
        Configuration user = yaml("name: Custom\nnested:\n  a: 1\n");
        Configuration defaults = yaml("name: Default\nversion: 2\nnested:\n  a: 99\n  b: 5\n");

        boolean changed = ConfigMigration.backfill(user, defaults);

        assertTrue(changed, "backfill must report it added keys");
        assertEquals("Custom", user.getString("name"), "existing user value must be preserved");
        assertEquals(2, user.getInt("version"), "new top-level key must be added");
        assertEquals(1, user.getInt("nested.a"), "existing nested user value must be preserved");
        assertEquals(5, user.getInt("nested.b"), "new nested key must be added");
    }

    @Test
    void backfillReturnsFalseWhenNothingMissing() {
        Configuration user = yaml("a: 1\n");
        Configuration defaults = yaml("a: 9\n");

        assertFalse(ConfigMigration.backfill(user, defaults));
        assertEquals(1, user.getInt("a"));
    }
}
