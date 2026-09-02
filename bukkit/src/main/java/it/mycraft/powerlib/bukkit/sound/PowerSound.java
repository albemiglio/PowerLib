package it.mycraft.powerlib.bukkit.sound;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A configurable sound, played by <em>key</em> rather than by {@link org.bukkit.Sound} constant, so one
 * config entry accepts both a vanilla sound and one shipped by a resource pack — Nexo, ItemsAdder or a
 * hand-made pack alike.
 *
 * <p><b>Why the key and not the Sound object.</b> A pack sound exists only client-side, in the pack's
 * {@code sounds.json}: the server's sound registry never contains it. Resolving a name to a {@code Sound}
 * and playing that object therefore <em>cannot</em> reach a custom sound — the lookup misses and the value
 * looks like a typo. Playing by key has no such ceiling: the server just forwards the string, so
 * {@code nexo:phone.ring} works with no Nexo API involved at all. Writing the key in the config is the
 * whole integration.
 *
 * <p>Keeping the type out of the signatures also keeps PowerLib portable: {@code org.bukkit.Sound} is an
 * <em>enum</em> up to 1.20.x and an <em>interface</em> from 1.21.3 on, so code compiled against one shape
 * breaks on the other. The {@code String} overloads of {@code playSound}/{@code stopSound}, and the
 * {@link Registry} used below to recognise vanilla names, are identical across the whole supported range.
 *
 * <p><b>Name resolution</b> follows what already proved itself in production: a namespaced or dotted name
 * is looked up in the vanilla registry, and an enum-style name ({@code BLOCK_NOTE_BLOCK_PLING}) goes
 * through a reverse map built from that same registry — the constant name cannot be converted to the key
 * mechanically, since {@code ENTITY_EXPERIENCE_ORB_PICKUP} is {@code entity.experience_orb.pickup} and not
 * {@code entity.experience.orb.pickup}. What is new is the miss: a name the registry does not know but
 * that carries an explicit namespace is passed through as a pack sound instead of being rejected.
 *
 * <p><b>Config shape.</b> Either a section:
 * <pre>
 * incoming-call:
 *   sound: "nexo:phone.ring"   # or a vanilla name: BLOCK_NOTE_BLOCK_PLING
 *   volume: 1.0
 *   pitch: 1.0
 *   category: PLAYERS
 *   loop-ticks: 40             # only read by SoundLoop; 0 = one-shot
 * </pre>
 * or the compact one-line form {@code "nexo:phone.ring;1.0;1.0;PLAYERS;40"}, where every field after the
 * key may be omitted.
 *
 * <p>An empty value (or {@code none}) means "no sound" and yields {@code null} from every factory, so an
 * admin can silence a single event without commenting anything out. An unnamespaced name the registry does
 * not know is a typo and is logged at load time, rather than failing silently at playback.
 *
 * <p>Instances are immutable and meant to be built once at config load and reused. Playback drives Bukkit
 * and must happen on the server's main thread.
 *
 * @see SoundLoop
 */
public final class PowerSound {

    private static final float DEFAULT_VOLUME = 1.0f;
    private static final float DEFAULT_PITCH = 1.0f;
    private static final SoundCategory DEFAULT_CATEGORY = SoundCategory.MASTER;
    private static final String VANILLA_NAMESPACE = "minecraft";

    /**
     * Enum-style name to registry key, built on first use. Names that collide once normalised do not
     * overwrite each other: the first wins, so the mapping stays stable between restarts.
     *
     * <p>{@link #key(String)} is public and static, so nothing stops a consumer from calling it off the
     * main thread. The field is therefore {@code volatile} and only ever assigned a fully-built immutable
     * map: a race can at worst build the map twice, which is idempotent, and never publish a half-built
     * one. Reading the registry itself needs no lock — it does not change after startup.
     */
    private static volatile Map<String, String> legacyNames;

    private final String key;
    private final SoundCategory category;
    private final float volume;
    private final float pitch;
    private final long loopTicks;

    private PowerSound(String key, SoundCategory category, float volume, float pitch, long loopTicks) {
        this.key = key;
        this.category = category;
        this.volume = volume;
        this.pitch = pitch;
        this.loopTicks = loopTicks;
    }

    /**
     * Builds a sound from already-parsed values.
     *
     * @param sound     the vanilla name or namespaced key; blank or {@code none} means "no sound"
     * @param category  the category to play under, {@code null} for {@link SoundCategory#MASTER}
     * @param volume    the volume, which also scales how far the sound carries
     * @param pitch     the pitch
     * @param loopTicks the interval between repeats used by {@link SoundLoop}, {@code 0} for a one-shot
     * @return the sound, or {@code null} if {@code sound} names nothing playable
     */
    public static PowerSound of(String sound, SoundCategory category, float volume, float pitch, long loopTicks) {
        String key = key(sound);
        if (key == null) {
            return null;
        }
        return new PowerSound(key, category == null ? DEFAULT_CATEGORY : category,
                volume, pitch, Math.max(0L, loopTicks));
    }

    /**
     * Reads a sound from {@code parent} at {@code path}, accepting both the section form and the compact
     * string form.
     *
     * @param parent the section holding the entry, may be {@code null}
     * @param path   the key of the entry inside {@code parent}
     * @return the sound, or {@code null} if absent, disabled, or unresolvable
     */
    public static PowerSound fromConfig(ConfigurationSection parent, String path) {
        if (parent == null || path == null) {
            return null;
        }
        ConfigurationSection section = parent.getConfigurationSection(path);
        if (section != null) {
            return fromConfig(section);
        }
        return parent.isString(path) ? parse(parent.getString(path), describe(parent, path)) : null;
    }

    /**
     * Reads a sound from its own section. A section carrying {@code enabled: false} is treated as silenced.
     *
     * @param section the section holding {@code sound} and, optionally, {@code enabled}, {@code volume},
     *                {@code pitch}, {@code category} and {@code loop-ticks}
     * @return the sound, or {@code null} if absent, disabled, or unresolvable
     */
    public static PowerSound fromConfig(ConfigurationSection section) {
        if (section == null || !section.getBoolean("enabled", true)) {
            return null;
        }
        String name = section.getString("sound", "");
        String key = key(name, section.getCurrentPath());
        if (key == null) {
            return null;
        }
        return new PowerSound(key,
                category(section.getString("category"), section.getCurrentPath()),
                (float) section.getDouble("volume", DEFAULT_VOLUME),
                (float) section.getDouble("pitch", DEFAULT_PITCH),
                Math.max(0L, section.getLong("loop-ticks", 0L)));
    }

    /**
     * Parses the compact form {@code "key;volume;pitch;category;loop-ticks"}. Every field after the key is
     * optional and falls back to its default.
     *
     * @param compact the value to parse
     * @return the sound, or {@code null} if blank, disabled, or unresolvable
     */
    public static PowerSound parse(String compact) {
        return parse(compact, null);
    }

    private static PowerSound parse(String compact, String where) {
        if (compact == null) {
            return null;
        }
        // Attenzione: split scarta i campi vuoti in coda, quindi ";" e ";;;;" danno un array di
        // lunghezza zero e un parts[0] diretto lancerebbe. part() tratta l'indice mancante come assente.
        String[] parts = compact.split(";");
        String key = key(part(parts, 0), where);
        if (key == null) {
            return null;
        }
        return new PowerSound(key,
                category(part(parts, 3), where),
                (float) number(part(parts, 1), DEFAULT_VOLUME, where),
                (float) number(part(parts, 2), DEFAULT_PITCH, where),
                Math.max(0L, (long) number(part(parts, 4), 0.0d, where)));
    }

    /**
     * Resolves a configured sound name to the key sent to the client.
     *
     * <p>Vanilla names are recognised through the server's sound registry, in any of the spellings admins
     * actually write: {@code minecraft:block.bell.use}, {@code block.bell.use} or the enum-style
     * {@code BLOCK_BELL_USE}. A name the registry does not know is a resource-pack sound when it carries an
     * explicit namespace ({@code nexo:phone.ring}) and is returned untouched, since the server has no way
     * to verify a pack's contents; without a namespace it can only be a typo and yields {@code null}.
     *
     * @param sound the configured name, may be {@code null}
     * @return the key to send to the client, or {@code null} if the value is blank, {@code none}, or an
     * unnamespaced name that matches no vanilla sound
     */
    public static String key(String sound) {
        return key(sound, null);
    }

    private static String key(String sound, String where) {
        if (sound == null) {
            return null;
        }
        String trimmed = sound.trim();
        if (trimmed.isEmpty() || trimmed.equalsIgnoreCase("none")) {
            return null; // the documented way to silence one event
        }

        String normalized = trimmed.toLowerCase(Locale.ROOT);
        String vanilla = registryKey(normalized);
        if (vanilla == null) {
            vanilla = registryKey(normalized.replace('_', '.'));
        }
        if (vanilla == null) {
            vanilla = legacyNames().get(normalized);
        }
        if (vanilla != null) {
            return vanilla;
        }

        // Unknown to the server. With a namespace that is a deliberate "it comes from the pack", which is
        // the whole point of playing by key; without one it is a misspelt vanilla name.
        if (normalized.indexOf(':') >= 0) {
            return normalized;
        }
        warn("unknown sound '" + trimmed + "'", where);
        return null;
    }

    /**
     * Plays the sound to a single player, at their own position, so nobody else hears it.
     *
     * @param player the listener, ignored if {@code null} or offline
     */
    public void play(Player player) {
        if (player != null && player.isOnline()) {
            play(player, player.getLocation());
        }
    }

    /**
     * Plays the sound to a single player, as if it came from {@code location}.
     *
     * @param player   the listener, ignored if {@code null} or offline
     * @param location where the sound comes from
     */
    public void play(Player player, Location location) {
        if (player == null || !player.isOnline() || location == null) {
            return;
        }
        player.playSound(location, key, category, volume, pitch);
    }

    /**
     * Plays the sound in the world, so every player in range hears it.
     *
     * @param location where the sound comes from
     */
    public void playAt(Location location) {
        if (location == null || location.getWorld() == null) {
            return;
        }
        location.getWorld().playSound(location, key, category, volume, pitch);
    }

    /**
     * Stops this sound for a player, which is what makes a long or looping sound interruptible: without it
     * a ringtone keeps playing client-side after the call is over, and nothing short of a relog silences
     * it. Repeating a short sound does not need this; playing a real audio file does.
     *
     * @param player the listener, ignored if {@code null} or offline
     */
    public void stop(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }
        player.stopSound(key, category);
    }

    /** @return the key sent to the client, never {@code null} */
    public String getKey() {
        return key;
    }

    /** @return the category this sound plays under */
    public SoundCategory getCategory() {
        return category;
    }

    /** @return the volume */
    public float getVolume() {
        return volume;
    }

    /** @return the pitch */
    public float getPitch() {
        return pitch;
    }

    /** @return the configured repeat interval in ticks, {@code 0} when the sound is a one-shot */
    public long getLoopTicks() {
        return loopTicks;
    }

    /** @return whether a repeat interval was configured, so this sound is meant for a {@link SoundLoop} */
    public boolean isLooping() {
        return loopTicks > 0L;
    }

    @Override
    public String toString() {
        return "PowerSound{" + key + ", " + category + ", volume=" + volume
                + ", pitch=" + pitch + ", loopTicks=" + loopTicks + '}';
    }

    /**
     * Looks a name up in the vanilla sound registry.
     *
     * <p>Held as {@code Keyed} rather than {@code Sound} on purpose: the registry and {@link NamespacedKey}
     * kept the same shape across every supported server version, the {@code Sound} type did not.
     */
    private static String registryKey(String name) {
        try {
            NamespacedKey key = NamespacedKey.fromString(
                    name.indexOf(':') >= 0 ? name : VANILLA_NAMESPACE + ":" + name);
            if (key == null) {
                return null;
            }
            Keyed sound = Registry.SOUNDS.get(key);
            return sound == null ? null : sound.getKey().toString();
        } catch (RuntimeException | LinkageError unavailable) {
            // A malformed key, or no readable registry at all. Either way this is a miss, and the caller
            // falls through to the pack-sound path — a library must not die on a server it cannot inspect.
            return null;
        }
    }

    private static Map<String, String> legacyNames() {
        Map<String, String> cached = legacyNames;
        if (cached != null) {
            return cached;
        }
        Map<String, String> byLegacyName = new HashMap<>();
        try {
            for (Keyed sound : Registry.SOUNDS) {
                NamespacedKey key = sound.getKey();
                byLegacyName.putIfAbsent(key.getKey().replace('.', '_'), key.toString());
            }
        } catch (RuntimeException | LinkageError unavailable) {
            // No registry to read (an old or stubbed server): namespaced keys still resolve, and an
            // enum-style name simply reports as unknown rather than bringing the plugin down. Not cached,
            // so a server that can answer later still gets its vanilla names.
            return Map.of();
        }
        cached = Map.copyOf(byLegacyName);
        legacyNames = cached;
        return cached;
    }

    private static SoundCategory category(String name, String where) {
        if (name == null || name.trim().isEmpty()) {
            return DEFAULT_CATEGORY;
        }
        try {
            return SoundCategory.valueOf(name.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException unknown) {
            warn("unknown sound category '" + name.trim() + "'", where);
            return DEFAULT_CATEGORY;
        }
    }

    private static double number(String value, double fallback, String where) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException notANumber) {
            warn("'" + value.trim() + "' is not a number", where);
            return fallback;
        }
    }

    private static String part(String[] parts, int index) {
        return index < parts.length ? parts[index] : null;
    }

    private static String describe(ConfigurationSection parent, String path) {
        String current = parent.getCurrentPath();
        return current == null || current.isEmpty() ? path : current + "." + path;
    }

    private static void warn(String problem, String where) {
        Bukkit.getLogger().warning("[PowerLib] " + problem
                + (where == null || where.isEmpty() ? "" : " at " + where) + "; ignoring it.");
    }
}
