package it.mycraft.powerlib.bukkit.sound;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Tests {@link PowerSound}'s name resolution and config parsing.
 *
 * <p>The centre of gravity is the <b>resource-pack key</b>: resolving a name to an {@code org.bukkit.Sound}
 * and playing that object can only ever reach sounds the server has in its registry, so a pack sound like
 * {@code nexo:phone.ring} silently degrades to a fallback. Playing by key is what removes that ceiling, and
 * these tests pin the behaviour that makes it work — a namespaced miss is a pack sound, not a typo.
 *
 * <p>The mock server carries no sound registry, so vanilla-name resolution is not exercised here; it must
 * degrade to "unknown" instead of throwing, which is asserted below.
 */
class PowerSoundTest {

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void passesNamespacedKeysThroughSoTheyReachAResourcePack() {
        // The server cannot know a pack's sounds, so an explicit namespace is taken at its word.
        assertThat(PowerSound.key("nexo:phone.ring")).isEqualTo("nexo:phone.ring");
        assertThat(PowerSound.key("itemsadder:siren.wail")).isEqualTo("itemsadder:siren.wail");
    }

    @Test
    void lowercasesKeysBecauseTheProtocolOnlyAcceptsLowercase() {
        assertThat(PowerSound.key("  NEXO:Phone.Ring  ")).isEqualTo("nexo:phone.ring");
    }

    @Test
    void treatsBlankAndNoneAsSilenced() {
        assertThat(PowerSound.key(null)).isNull();
        assertThat(PowerSound.key("")).isNull();
        assertThat(PowerSound.key("   ")).isNull();
        assertThat(PowerSound.key("none")).isNull();
        assertThat(PowerSound.key("NONE")).isNull();
    }

    @Test
    void rejectsAnUnnamespacedNameThatMatchesNoVanillaSound() {
        // Without a namespace there is nothing to defer to: it can only be a misspelt vanilla name.
        assertThat(PowerSound.key("deffo_not_a_sound")).isNull();
    }

    @Test
    void survivesAServerWithNoReadableSoundRegistry() {
        assertThatCode(() -> PowerSound.key("BLOCK_NOTE_BLOCK_PLING")).doesNotThrowAnyException();
    }

    @Test
    void parsesTheCompactForm() {
        PowerSound sound = PowerSound.parse("nexo:phone.ring;0.5;1.25;PLAYERS;40");

        assertThat(sound).isNotNull();
        assertThat(sound.getKey()).isEqualTo("nexo:phone.ring");
        assertThat(sound.getVolume()).isEqualTo(0.5f);
        assertThat(sound.getPitch()).isEqualTo(1.25f);
        assertThat(sound.getCategory()).isEqualTo(SoundCategory.PLAYERS);
        assertThat(sound.getLoopTicks()).isEqualTo(40L);
        assertThat(sound.isLooping()).isTrue();
    }

    @Test
    void fillsInDefaultsForOmittedCompactFields() {
        PowerSound sound = PowerSound.parse("nexo:phone.ring");

        assertThat(sound).isNotNull();
        assertThat(sound.getVolume()).isEqualTo(1.0f);
        assertThat(sound.getPitch()).isEqualTo(1.0f);
        assertThat(sound.getCategory()).isEqualTo(SoundCategory.MASTER);
        assertThat(sound.getLoopTicks()).isZero();
        assertThat(sound.isLooping()).isFalse();
    }

    @Test
    void keepsPlayingWhenOnlyTheCategoryIsWrong() {
        // A bad category is not worth silencing the event over.
        PowerSound sound = PowerSound.parse("nexo:phone.ring;1.0;1.0;NOT_A_CATEGORY");

        assertThat(sound).isNotNull();
        assertThat(sound.getCategory()).isEqualTo(SoundCategory.MASTER);
    }

    @Test
    void readsTheSectionForm() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("sounds.incoming-call.sound", "nexo:phone.ring");
        config.set("sounds.incoming-call.volume", 0.8);
        config.set("sounds.incoming-call.pitch", 1.1);
        config.set("sounds.incoming-call.category", "players");
        config.set("sounds.incoming-call.loop-ticks", 32);

        PowerSound sound = PowerSound.fromConfig(config.getConfigurationSection("sounds"), "incoming-call");

        assertThat(sound).isNotNull();
        assertThat(sound.getKey()).isEqualTo("nexo:phone.ring");
        assertThat(sound.getVolume()).isEqualTo(0.8f);
        assertThat(sound.getPitch()).isEqualTo(1.1f);
        assertThat(sound.getCategory()).isEqualTo(SoundCategory.PLAYERS);
        assertThat(sound.getLoopTicks()).isEqualTo(32L);
    }

    @Test
    void readsTheStringFormFromTheSamePath() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("sounds.sms", "nexo:phone.sms;0.6");

        PowerSound sound = PowerSound.fromConfig(config.getConfigurationSection("sounds"), "sms");

        assertThat(sound).isNotNull();
        assertThat(sound.getKey()).isEqualTo("nexo:phone.sms");
        assertThat(sound.getVolume()).isEqualTo(0.6f);
    }

    @Test
    void honoursEnabledFalseAsASilencedEvent() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("sounds.incoming-call.enabled", false);
        config.set("sounds.incoming-call.sound", "nexo:phone.ring");

        assertThat(PowerSound.fromConfig(config.getConfigurationSection("sounds"), "incoming-call")).isNull();
    }

    @Test
    void returnsNullForMissingEntriesInsteadOfThrowing() {
        YamlConfiguration config = new YamlConfiguration();

        assertThat(PowerSound.fromConfig(config, "nope")).isNull();
        assertThat(PowerSound.fromConfig(null, "nope")).isNull();
        assertThat(PowerSound.fromConfig(null)).isNull();
        assertThat(PowerSound.parse(null)).isNull();
    }

    @Test
    void survivesACompactFormMadeOnlyOfSeparators() {
        // split() drops trailing empty fields, so ";" and ";;;;" yield a zero-length array: reading
        // parts[0] directly would throw on a config value an admin can perfectly well type.
        assertThat(PowerSound.parse(";")).isNull();
        assertThat(PowerSound.parse(";;;;")).isNull();
        assertThat(PowerSound.parse(";1.0;1.0")).isNull();
    }

    @Test
    void ignoresTrailingEmptyCompactFields() {
        PowerSound sound = PowerSound.parse("nexo:phone.ring;;;;");

        assertThat(sound).isNotNull();
        assertThat(sound.getKey()).isEqualTo("nexo:phone.ring");
        assertThat(sound.getVolume()).isEqualTo(1.0f);
        assertThat(sound.getCategory()).isEqualTo(SoundCategory.MASTER);
    }

    @Test
    void refusesALoopWithoutAPeriodRatherThanReplayingEveryTick() {
        PowerSound oneShot = PowerSound.parse("nexo:phone.ring");

        assertThatIllegalArgumentException().isThrownBy(() -> new SoundLoop(oneShot));
        assertThatIllegalArgumentException().isThrownBy(() -> new SoundLoop(oneShot, 0L));
        assertThatIllegalArgumentException().isThrownBy(() -> new SoundLoop(null, 20L));
    }

    @Test
    void takesThePeriodFromTheSoundsOwnLoopTicks() {
        SoundLoop loop = new SoundLoop(PowerSound.parse("nexo:phone.ring;1.0;1.0;PLAYERS;40"));

        assertThat(loop.getPeriodTicks()).isEqualTo(40L);
        assertThat(loop.size()).isZero();
        assertThat(loop.isPlayingFor(null)).isFalse();
    }
}
