package it.mycraft.powerlib.bukkit.sound;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.sound.AudioExperience;
import it.mycraft.powerlib.bukkit.PowerLib;
import it.mycraft.powerlib.common.scheduler.Task;
import org.bukkit.SoundCategory;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Tests {@link SoundLoop}, the part of the sound API that can leave something behind.
 *
 * <p>A one-shot that goes wrong is a missing sound; a loop that goes wrong is a ringtone nobody can
 * silence short of a relog, plus a repeating task nobody cancels. The assertions below are therefore about
 * the two ends rather than the playback: that the sound really is stopped on the client when a listener
 * leaves, and that the task exists exactly while somebody is listening.
 */
class SoundLoopTest {

    private static final long PERIOD = 40L;

    private ServerMock server;
    private PowerSound ring;
    private SoundLoop loop;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        server.addSimpleWorld("world");
        Plugin plugin = MockBukkit.createMockPlugin("PowerLibTest");
        PowerLib.inject(plugin); // the repeating task is scheduled for the injected plugin
        ring = PowerSound.parse("nexo:phone.ring;0.8;1.2;PLAYERS;" + PERIOD);
        loop = new SoundLoop(ring);
    }

    @AfterEach
    void tearDown() {
        PowerLib.shutdown();
        MockBukkit.unmock();
    }

    @Test
    void refusesToBuildWithoutASound() {
        // The javadoc promises IllegalArgumentException, and the one-argument form has to reject the null
        // before it reads the period off it — otherwise the failure is a NullPointerException instead.
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new SoundLoop(null))
                .withMessageContaining("needs a sound");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new SoundLoop(null, 20L))
                .withMessageContaining("needs a sound");
    }

    @Test
    void refusesAPeriodThatWouldReplayEveryTick() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new SoundLoop(ring, 0L))
                .withMessageContaining("positive period");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new SoundLoop(ring, -1L))
                .withMessageContaining("positive period");
    }

    @Test
    void exposesWhatItWasBuiltFrom() {
        SoundLoop explicit = new SoundLoop(ring, 7L);

        assertThat(explicit.getSound()).isSameAs(ring);
        assertThat(explicit.getPeriodTicks()).isEqualTo(7L);
        assertThat(loop.getPeriodTicks()).isEqualTo(PERIOD);
    }

    @Test
    void startPlaysTheSoundOnceImmediately() {
        PlayerMock player = server.addPlayer();

        loop.start(player);

        assertThat(loop.size()).isOne();
        assertThat(loop.isPlayingFor(player.getUniqueId())).isTrue();
        assertThat(player.getHeardSounds()).hasSize(1);
        AudioExperience heard = player.getHeardSounds().get(0);
        assertThat(heard.getSound()).isEqualTo("nexo:phone.ring");
        assertThat(heard.getCategory()).isEqualTo(SoundCategory.PLAYERS);
        assertThat(heard.getVolume()).isEqualTo(0.8f);
        assertThat(heard.getPitch()).isEqualTo(1.2f);
    }

    @Test
    void repeatsOnlyOncePerPeriod() {
        PlayerMock player = server.addPlayer();

        loop.start(player);
        server.getScheduler().performTicks(PERIOD - 1);
        assertThat(player.getHeardSounds()).hasSize(1);

        server.getScheduler().performTicks(1);
        assertThat(player.getHeardSounds()).hasSize(2);

        server.getScheduler().performTicks(PERIOD);
        assertThat(player.getHeardSounds()).hasSize(3);
    }

    @Test
    void everyListenerKeepsItsOwnCountdown() {
        PlayerMock first = server.addPlayer();
        PlayerMock late = server.addPlayer();

        loop.start(first);
        server.getScheduler().performTicks(PERIOD / 2);
        loop.start(late);

        // Half a period later the first one repeats and the latecomer does not: joining mid-cycle must not
        // drag everybody onto the same cadence.
        server.getScheduler().performTicks(PERIOD / 2);
        assertThat(first.getHeardSounds()).hasSize(2);
        assertThat(late.getHeardSounds()).hasSize(1);
    }

    @Test
    void startIsSafeToCallEveryTick() {
        PlayerMock player = server.addPlayer();

        loop.start(player);
        loop.start(player);
        loop.start(player);

        assertThat(loop.size()).isOne();
        assertThat(player.getHeardSounds()).hasSize(1);
    }

    @Test
    void startIgnoresNobodyAndTheOffline() {
        PlayerMock gone = server.addPlayer();
        gone.disconnect();

        loop.start(null);
        loop.start(gone);

        assertThat(loop.size()).isZero();
        assertThat(gone.getHeardSounds()).isEmpty();
    }

    @Test
    void stopSilencesTheSoundOnTheClient() {
        SilenceRecordingPlayer player = addRecordingPlayer("ringing");
        loop.start(player);

        loop.stop(player);

        assertThat(player.silenced).containsExactly("nexo:phone.ring/PLAYERS");
        assertThat(loop.size()).isZero();
        assertThat(loop.isPlayingFor(player.getUniqueId())).isFalse();
    }

    @Test
    void stopEndsTheRepeatingTaskWithTheLastListener() {
        PlayerMock player = server.addPlayer();
        loop.start(player);
        assertThat(repeatingTask()).isNotNull();

        loop.stop(player);

        assertThat(repeatingTask()).isNull();
        server.getScheduler().performTicks(PERIOD * 2);
        assertThat(player.getHeardSounds()).hasSize(1);
    }

    @Test
    void startsTheTaskAgainWhenSomebodyComesBack() {
        PlayerMock player = server.addPlayer();
        loop.start(player);
        loop.stop(player);

        loop.start(player);
        server.getScheduler().performTicks(PERIOD);

        // Two from the two starts, one from the repeat: the loop is running again, not silently idle.
        assertThat(player.getHeardSounds()).hasSize(3);
    }

    @Test
    void stopIgnoresNobodyAndStrangers() {
        PlayerMock player = server.addPlayer();
        loop.start(player);

        loop.stop((org.bukkit.entity.Player) null);
        loop.stop((UUID) null);
        loop.stop(UUID.randomUUID());

        assertThat(loop.size()).isOne();
    }

    @Test
    void stopSilencesEvenAListenerWhoAlreadyLeft() {
        PlayerMock player = server.addPlayer();
        loop.start(player);
        UUID id = player.getUniqueId();
        player.disconnect();

        assertThatCode(() -> loop.stop(id)).doesNotThrowAnyException();
        assertThat(loop.size()).isZero();
    }

    @Test
    void dropsAListenerWhoDisconnected() {
        PlayerMock staying = server.addPlayer();
        PlayerMock leaving = server.addPlayer();
        loop.start(staying);
        loop.start(leaving);

        leaving.disconnect();
        server.getScheduler().performTicks(1);

        assertThat(loop.size()).isOne();
        assertThat(loop.isPlayingFor(staying.getUniqueId())).isTrue();
    }

    @Test
    void endsTheTaskWhenTheLastListenerDisconnects() {
        PlayerMock player = server.addPlayer();
        loop.start(player);

        player.disconnect();
        server.getScheduler().performTicks(1);

        assertThat(loop.size()).isZero();
        assertThat(repeatingTask()).isNull();
    }

    @Test
    void stopAllSilencesEveryListener() {
        SilenceRecordingPlayer first = addRecordingPlayer("first");
        SilenceRecordingPlayer second = addRecordingPlayer("second");
        loop.start(first);
        loop.start(second);

        loop.stopAll();

        assertThat(loop.size()).isZero();
        assertThat(first.silenced).containsExactly("nexo:phone.ring/PLAYERS");
        assertThat(second.silenced).containsExactly("nexo:phone.ring/PLAYERS");
        assertThat(repeatingTask()).isNull();
    }

    @Test
    void updateMakesTheGivenSetTheExactAudience() {
        SilenceRecordingPlayer leaving = addRecordingPlayer("leaving");
        PlayerMock staying = server.addPlayer();
        PlayerMock joining = server.addPlayer();
        loop.update(Arrays.asList(leaving, staying));

        loop.update(Arrays.asList(staying, joining));

        assertThat(loop.isPlayingFor(leaving.getUniqueId())).isFalse();
        assertThat(loop.isPlayingFor(staying.getUniqueId())).isTrue();
        assertThat(loop.isPlayingFor(joining.getUniqueId())).isTrue();
        assertThat(leaving.silenced).containsExactly("nexo:phone.ring/PLAYERS");
        // Whoever stays keeps their countdown instead of being restarted on every recompute.
        assertThat(staying.getHeardSounds()).hasSize(1);
        assertThat(joining.getHeardSounds()).hasSize(1);
    }

    @Test
    void updateWithNobodyStopsEveryone() {
        SilenceRecordingPlayer player = addRecordingPlayer("ringing");
        loop.update(Collections.singletonList(player));

        loop.update(null);

        assertThat(loop.size()).isZero();
        assertThat(player.silenced).containsExactly("nexo:phone.ring/PLAYERS");
        assertThat(repeatingTask()).isNull();
    }

    @Test
    void updateSkipsNullsDuplicatesAndTheOffline() {
        PlayerMock player = server.addPlayer();
        PlayerMock gone = server.addPlayer();
        gone.disconnect();

        loop.update(Arrays.asList(null, player, player, gone));

        assertThat(loop.size()).isOne();
        assertThat(loop.isPlayingFor(player.getUniqueId())).isTrue();
        assertThat(player.getHeardSounds()).hasSize(1);
    }

    @Test
    void isPlayingForNobodyIsFalse() {
        assertThat(loop.isPlayingFor(null)).isFalse();
        assertThat(loop.isPlayingFor(UUID.randomUUID())).isFalse();
    }

    /**
     * The repeating task the loop is holding, or {@code null} when it is idle.
     *
     * <p>"An idle loop costs nothing" is a claim about the scheduler, and a leaked timer is invisible from
     * outside: it keeps ticking over an empty map and nothing a test can read changes. MockBukkit's
     * scheduler does not implement {@code getPendingTasks}, so the handle is read where the loop keeps it.
     */
    private Task repeatingTask() {
        try {
            Field field = SoundLoop.class.getDeclaredField("task");
            field.setAccessible(true);
            return (Task) field.get(loop);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("SoundLoop no longer keeps its repeating task in a 'task' field", e);
        }
    }

    private SilenceRecordingPlayer addRecordingPlayer(String name) {
        SilenceRecordingPlayer player = new SilenceRecordingPlayer(server, name);
        server.addPlayer(player);
        return player;
    }

    /**
     * A player that remembers what was silenced on it. MockBukkit records the sounds a player hears but not
     * the ones stopped on it, and "stopped the ringtone" is exactly the half of a loop worth asserting.
     */
    private static final class SilenceRecordingPlayer extends PlayerMock {

        private final List<String> silenced = new ArrayList<>();

        private SilenceRecordingPlayer(ServerMock server, String name) {
            super(server, name);
        }

        @Override
        public void stopSound(String sound, SoundCategory category) {
            silenced.add(sound + "/" + category);
        }
    }
}
