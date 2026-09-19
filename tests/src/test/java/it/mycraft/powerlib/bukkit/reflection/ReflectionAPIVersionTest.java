package it.mycraft.powerlib.bukkit.reflection;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Locks {@link ReflectionAPI}'s server-version detection against servers whose CraftBukkit package is not
 * versioned — every Paper build since 1.20.5, plus MockBukkit here.
 *
 * <p>The version segment used to be read as {@code getPackage().getName().split("\\.")[3]} from a
 * {@code static final} initialiser: on such a server that index is out of range, so the very first touch
 * of the class threw {@link ExceptionInInitializerError} and the class could never be loaded again.
 * Simply referencing the class in a test is therefore the regression check.
 */
class ReflectionAPIVersionTest {

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void loadsOnAServerWhosePackageCarriesNoVersionSegment() {
        assertThat(Bukkit.getServer().getClass().getPackage().getName().split("\\.")).hasSizeLessThan(4);

        assertThatCode(ReflectionAPI::getVersion).doesNotThrowAnyException();
        assertThat(ReflectionAPI.getVersion()).isNull();
    }

    @Test
    void stillReportsTheMinecraftVersionWithoutTheLegacyPackage() {
        assertThat(ReflectionAPI.getNumericalVersion())
                .isEqualTo(Integer.parseInt(Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]));
    }
}
