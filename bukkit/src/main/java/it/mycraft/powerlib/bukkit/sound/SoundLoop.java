package it.mycraft.powerlib.bukkit.sound;

import it.mycraft.powerlib.bukkit.PowerLib;
import it.mycraft.powerlib.bukkit.scheduler.BukkitScheduler;
import it.mycraft.powerlib.common.scheduler.PowerScheduler;
import it.mycraft.powerlib.common.scheduler.Task;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Repeats a {@link PowerSound} to a changing set of listeners until it is stopped — a ringing phone, a
 * running alarm, a siren.
 *
 * <p>Minecraft has no looping sound: a client plays a sound once and stops. A ringtone is therefore
 * "play it again every N ticks, and silence whatever is still playing when it ends". The second half is
 * the part that is easy to forget and impossible to recover from — a long sound left playing keeps going
 * client-side until the player relogs — which is why {@code stopSound} lives here rather than in every
 * consumer. Repeating a short sound that ends on its own can get away without it; playing a real audio
 * file cannot.
 *
 * <p><b>Prefer {@link #update(Collection)} over paired start/stop calls.</b> A looping sound has as many
 * ways to end as the state driving it — answered, rejected, cancelled, timed out, disconnected — and one
 * forgotten stop is a stuck ringtone. Recomputing "who should be hearing this right now" from that state
 * on a tick and handing the result to {@code update} makes the leak unrepresentable. {@link #start(Player)}
 * and {@link #stop(Player)} stay available for the cases where the transition really is explicit.
 *
 * <p>The repeating task exists only while somebody is listening: it starts with the first listener and is
 * cancelled with the last, so an idle loop costs nothing. Each listener keeps its own countdown, so
 * joining mid-cycle never cuts anyone else's playback short.
 *
 * <p>Listeners are held by {@link UUID}, never as {@code Player} references, and one that goes offline is
 * dropped on the next tick — the sound went with the connection.
 *
 * <p>Requires {@link PowerLib#inject(org.bukkit.plugin.Plugin)} to have run, and every method must be
 * called from the server's main thread: this drives Bukkit state and is not synchronised. Call
 * {@link #stopAll()} when the owning plugin disables, so no client is left ringing.
 */
public final class SoundLoop {

    private final PowerSound sound;
    private final long periodTicks;
    private final PowerScheduler scheduler = new BukkitScheduler();
    private final Map<UUID, Long> countdowns = new HashMap<>();
    private Task task;

    /**
     * Builds a loop repeating at the sound's own {@code loop-ticks} interval.
     *
     * @param sound the sound to repeat, which must be {@link PowerSound#isLooping() looping}
     * @throws IllegalArgumentException if the sound is {@code null} or carries no repeat interval
     */
    public SoundLoop(PowerSound sound) {
        this(sound, sound == null ? 0L : sound.getLoopTicks());
    }

    /**
     * Builds a loop repeating at an explicit interval, for when the cadence comes from somewhere other
     * than the sound's own config entry.
     *
     * @param sound       the sound to repeat
     * @param periodTicks the interval between repeats, in ticks
     * @throws IllegalArgumentException if the sound is {@code null} or the interval is not positive
     */
    public SoundLoop(PowerSound sound, long periodTicks) {
        if (sound == null) {
            throw new IllegalArgumentException("A SoundLoop needs a sound");
        }
        if (periodTicks < 1L) {
            // Clamping to 1 would replay the sound every tick, which is a deafening way to find out about
            // a config typo. A sound that should not repeat is a one-shot, not a loop without a period.
            throw new IllegalArgumentException("A SoundLoop needs a positive period, got " + periodTicks
                    + "; use PowerSound.play(...) for a one-shot");
        }
        this.sound = sound;
        this.periodTicks = periodTicks;
    }

    /**
     * Starts the loop for a player, playing the sound once immediately. Does nothing if they are already
     * listening, so it is safe to call every tick.
     *
     * @param player the listener, ignored if {@code null} or offline
     */
    public void start(Player player) {
        if (player == null || !player.isOnline() || countdowns.containsKey(player.getUniqueId())) {
            return;
        }
        sound.play(player);
        countdowns.put(player.getUniqueId(), periodTicks);
        if (task == null || task.isCancelled()) {
            task = scheduler.runTimer(this::tick, 1L, 1L);
        }
    }

    /**
     * Stops the loop for a player and silences what is still playing on their client.
     *
     * @param player the listener, ignored if {@code null}
     */
    public void stop(Player player) {
        if (player != null) {
            stop(player.getUniqueId());
        }
    }

    /**
     * Stops the loop for a player id, silencing their client if they are still online.
     *
     * @param playerId the listener's id, ignored if {@code null} or not listening
     */
    public void stop(UUID playerId) {
        if (playerId == null || countdowns.remove(playerId) == null) {
            return;
        }
        sound.stop(Bukkit.getPlayer(playerId));
        if (countdowns.isEmpty()) {
            cancelTask();
        }
    }

    /** Stops the loop for everyone. Call this when the owning plugin disables. */
    public void stopAll() {
        for (UUID playerId : new ArrayList<>(countdowns.keySet())) {
            stop(playerId);
        }
    }

    /**
     * Makes {@code listeners} the exact set of players hearing this loop: newcomers start hearing it,
     * anyone no longer listed is stopped and silenced, everyone else keeps their own cadence.
     *
     * @param listeners who should be hearing the loop right now, {@code null} meaning nobody
     */
    public void update(Collection<? extends Player> listeners) {
        List<Player> online = new ArrayList<>();
        Set<UUID> wanted = new HashSet<>();
        if (listeners != null) {
            for (Player player : listeners) {
                if (player != null && player.isOnline() && wanted.add(player.getUniqueId())) {
                    online.add(player);
                }
            }
        }
        for (UUID playerId : new ArrayList<>(countdowns.keySet())) {
            if (!wanted.contains(playerId)) {
                stop(playerId);
            }
        }
        for (Player player : online) {
            start(player);
        }
    }

    /**
     * @param playerId the listener's id
     * @return whether the loop is currently running for that player
     */
    public boolean isPlayingFor(UUID playerId) {
        return playerId != null && countdowns.containsKey(playerId);
    }

    /** @return how many players are hearing the loop right now */
    public int size() {
        return countdowns.size();
    }

    /** @return the repeated sound */
    public PowerSound getSound() {
        return sound;
    }

    /** @return the interval between repeats, in ticks */
    public long getPeriodTicks() {
        return periodTicks;
    }

    private void tick() {
        Iterator<Map.Entry<UUID, Long>> entries = countdowns.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<UUID, Long> entry = entries.next();
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null || !player.isOnline()) {
                entries.remove(); // nothing to silence: the sound went with the connection
                continue;
            }
            long remaining = entry.getValue() - 1L;
            if (remaining > 0L) {
                entry.setValue(remaining);
                continue;
            }
            sound.play(player);
            entry.setValue(periodTicks);
        }
        if (countdowns.isEmpty()) {
            cancelTask();
        }
    }

    private void cancelTask() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
