package it.mycraft.powerlib.common.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Backfill must add missing keys WITHOUT rewriting the user's file: comments, blank lines and
 * existing values have to survive byte-for-byte. These tests pin that contract on the pure
 * text-merge helper.
 */
class ConfigTextMergeTest {

    private static Configuration yaml(String s) {
        return ConfigurationProvider.getProvider(YamlConfiguration.class).load(s);
    }

    @Test
    void nothingMissingReturnsInputUnchanged() {
        String user = "# header\nname: Custom\n";
        String out = ConfigTextMerge.merge(user, yaml("name: Default\n"));
        assertEquals(user, out, "when nothing is missing the file must be returned untouched");
    }

    @Test
    void missingTopLevelKeyIsAppendedAndCommentsSurvive() {
        String user = ""
                + "# ==== My server config ====\n"
                + "# the display name, keep it short\n"
                + "name: Custom\n";
        Configuration defaults = yaml("name: Default\nversion: 2\n");

        String out = ConfigTextMerge.merge(user, defaults);

        assertTrue(out.startsWith(user), "the original text (comments + value) must be preserved verbatim at the top");
        assertTrue(out.contains("# ==== My server config ===="), "header comment must survive");
        assertTrue(out.contains("# the display name, keep it short"), "inline explanatory comment must survive");
        assertTrue(out.contains("name: Custom"), "user value must survive");

        Configuration reread = yaml(out);
        assertEquals("Custom", reread.getString("name"), "user value must still parse");
        assertEquals(2, reread.getInt("version"), "the new default key must be present after merge");
    }

    @Test
    void missingNestedKeyIsInsertedUnderExistingParentAndCommentsSurvive() {
        String user = ""
                + "# top comment\n"
                + "messages:\n"
                + "  # greeting shown on join\n"
                + "  join: 'Welcome %player%'\n"
                + "other: 1\n";
        Configuration defaults = yaml(""
                + "messages:\n"
                + "  join: 'default join'\n"
                + "  quit: 'default quit'\n"
                + "other: 9\n");

        String out = ConfigTextMerge.merge(user, defaults);

        assertTrue(out.contains("# top comment"), "top comment must survive");
        assertTrue(out.contains("# greeting shown on join"), "nested comment must survive");
        assertTrue(out.contains("join: 'Welcome %player%'"), "user nested value must survive");
        assertTrue(out.contains("other: 1"), "unrelated user value must survive");

        Configuration reread = yaml(out);
        assertEquals("Welcome %player%", reread.getString("messages.join"), "user nested value must still parse");
        assertEquals("default quit", reread.getString("messages.quit"), "the new nested key must be inserted under its parent");
        assertEquals(1, reread.getInt("other"), "unrelated value unchanged");
    }

    @Test
    void missingKeyUnderAbsentSectionIsAppendedAsNewSection() {
        // Edge case: parent section 'db' does not exist in the user file at all.
        String user = "name: Custom\n";
        Configuration defaults = yaml("name: Default\ndb:\n  host: localhost\n  port: 5432\n");

        String out = ConfigTextMerge.merge(user, defaults);

        assertTrue(out.startsWith("name: Custom\n"), "existing content preserved verbatim");
        Configuration reread = yaml(out);
        assertEquals("Custom", reread.getString("name"));
        assertEquals("localhost", reread.getString("db.host"), "absent section must be created with its keys");
        assertEquals(5432, reread.getInt("db.port"), "absent section keys must all be added");
    }
}
