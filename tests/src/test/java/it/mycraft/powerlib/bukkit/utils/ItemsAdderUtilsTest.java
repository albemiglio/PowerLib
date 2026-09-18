package it.mycraft.powerlib.bukkit.utils;

import be.seeseemelk.mockbukkit.MockBukkit;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers the reflective binding to ItemsAdder. The stand-in {@code CustomStack} on the test classpath
 * stands in for the real plugin, so these tests fail if the bridge ever stops resolving the API it
 * expects — which is the whole point of dropping the compile-time dependency.
 */
class ItemsAdderUtilsTest {

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        CustomStack.clear();
    }

    @AfterEach
    void tearDown() {
        CustomStack.clear();
        // The drift tests below re-run bind() against other classes; put the real handles back so the
        // rest of the suite still sees a bound bridge.
        assertThat(ItemsAdderUtils.bind("dev.lone.itemsadder.api.CustomStack")).isTrue();
        MockBukkit.unmock();
    }

    /**
     * Runs {@code body} while collecting the warnings PowerLib logs through Bukkit.
     *
     * @param body what to run
     * @return every warning message logged while it ran
     */
    private static List<String> warningsWhile(Runnable body) {
        List<String> warnings = new ArrayList<>();
        Handler collector = new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (record.getLevel() == Level.WARNING) {
                    warnings.add(record.getMessage());
                }
            }

            @Override
            public void flush() {
                // nothing is buffered
            }

            @Override
            public void close() {
                // nothing to release
            }
        };
        Logger logger = Bukkit.getLogger();
        logger.addHandler(collector);
        try {
            body.run();
        } finally {
            logger.removeHandler(collector);
        }
        return warnings;
    }

    @Test
    void bindsToTheItemsAdderApi() {
        assertThat(ItemsAdderUtils.isAvailable()).isTrue();
    }

    @Test
    void buildsARegisteredItem() {
        CustomStack.register("novaverse:badge", new ItemStack(Material.PAPER, 3));

        ItemStack built = ItemsAdderUtils.itemStackFromId("novaverse:badge");

        assertThat(built).isNotNull();
        assertThat(built.getType()).isEqualTo(Material.PAPER);
        assertThat(built.getAmount()).isEqualTo(3);
    }

    @Test
    void unknownIdYieldsNoItem() {
        assertThat(ItemsAdderUtils.itemStackFromId("novaverse:nope")).isNull();
    }

    @Test
    void nullIdYieldsNoItem() {
        assertThat(ItemsAdderUtils.itemStackFromId(null)).isNull();
    }

    @Test
    void anApiThatThrowsYieldsNoItemInsteadOfPropagating() {
        // ItemsAdder is installed but not done loading: the real plugin throws on registry lookups, and
        // a plugin building its items at startup must get null back rather than a crashing enable.
        CustomStack.register("novaverse:badge", new ItemStack(Material.PAPER));
        CustomStack.failWith(new IllegalStateException("ItemsAdder is not loaded yet"));

        assertThat(ItemsAdderUtils.itemStackFromId("novaverse:badge")).isNull();
    }

    @Test
    void withoutItemsAdderInstalledTheBridgeBindsNothingAndStaysSilent() {
        List<String> warnings = warningsWhile(
                () -> assertThat(ItemsAdderUtils.bind("dev.lone.itemsadder.api.Absent")).isFalse());

        // "not installed" is the normal case on most servers: it must never nag the console.
        assertThat(warnings).isEmpty();
    }

    @Test
    void anItemsAdderWhoseApiDriftedIsReportedAndDisabled() {
        // A CustomStack that no longer carries isInRegistry/getInstance/getItemStack: the bridge must
        // refuse to bind and say which member went missing, instead of failing later on every lookup.
        List<String> warnings = warningsWhile(
                () -> assertThat(ItemsAdderUtils.bind("java.lang.Object")).isFalse());

        assertThat(warnings).hasSize(1);
        assertThat(warnings.get(0))
                .contains("its API does not match")
                .contains("isInRegistry")
                .contains("ItemsAdder integration disabled");
    }
}
