package it.mycraft.powerlib.common.utils;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Helpers for Bedrock players joining a Java server through Geyser + Floodgate.
 *
 * <p>The pure helpers ({@link #isFloodgateId(UUID)}, {@link #javaIdFromXuid(long)},
 * {@link #stripPrefix(String, String)}) do UUID/prefix math with <b>no dependency</b> and are safe to call
 * anywhere, including at pre-login. The rest reflect into the Floodgate API when it is present and degrade
 * gracefully (returning {@code false}/{@code null}/{@code ""}) when it is not, so a plugin using PowerLib
 * never hard-depends on Floodgate — check {@link #available()} or just call and handle the empty result.
 */
public final class Bedrock {

    private Bedrock() {
    }

    private static final String FLOODGATE_API = "org.geysermc.floodgate.api.FloodgateApi";
    private static final boolean PRESENT = classPresent(FLOODGATE_API);

    private static volatile Object apiInstance;
    private static Method isFloodgatePlayerMethod;
    private static Method playerPrefixMethod;
    private static Method getPlayerMethod;
    private static Method getXuidMethod;
    private static Method correctUniqueIdMethod;

    /**
     * Pure: is this the UUID Floodgate mints for an <em>unlinked</em> Bedrock player? Floodgate builds it as
     * {@code new UUID(0, xuid)}, so the most-significant 64 bits are zero. No dependency required.
     * A <em>linked</em> Bedrock player joins under their real Java UUID, for which this returns {@code false} —
     * use {@link #isFloodgatePlayer(UUID)} to also catch linked players.
     */
    public static boolean isFloodgateId(UUID uuid) {
        return uuid != null && uuid.getMostSignificantBits() == 0L;
    }

    /** Pure: the Java UUID Floodgate assigns a Bedrock player from their XUID — {@code new UUID(0, xuid)}. */
    public static UUID javaIdFromXuid(long xuid) {
        return new UUID(0L, xuid);
    }

    /**
     * Pure: strip a leading Floodgate username prefix (default {@code "."}, so {@code ".Steve" -> "Steve"})
     * before looking a player up in a name-keyed store. Returns the name unchanged if it doesn't start with
     * the prefix, or if the prefix is null/empty.
     */
    public static String stripPrefix(String username, String prefix) {
        if (username == null || prefix == null || prefix.isEmpty()) {
            return username;
        }
        return username.startsWith(prefix) ? username.substring(prefix.length()) : username;
    }

    /** Whether the Floodgate API is on the classpath (Geyser/Floodgate installed on this server). */
    public static boolean available() {
        return PRESENT;
    }

    /** Whether this player joined via Bedrock (includes linked players). {@code false} if Floodgate is absent. */
    public static boolean isFloodgatePlayer(UUID uuid) {
        Object api = api();
        if (api == null || uuid == null) {
            return false;
        }
        try {
            if (isFloodgatePlayerMethod == null) {
                isFloodgatePlayerMethod = api.getClass().getMethod("isFloodgatePlayer", UUID.class);
            }
            return (boolean) isFloodgatePlayerMethod.invoke(api, uuid);
        } catch (Throwable ignored) {
            return false;
        }
    }

    /** The configured Floodgate username prefix (default {@code "."}), or {@code ""} if Floodgate is absent. */
    public static String prefix() {
        Object api = api();
        if (api == null) {
            return "";
        }
        try {
            if (playerPrefixMethod == null) {
                playerPrefixMethod = api.getClass().getMethod("getPlayerPrefix");
            }
            Object prefix = playerPrefixMethod.invoke(api);
            return prefix == null ? "" : prefix.toString();
        } catch (Throwable ignored) {
            return "";
        }
    }

    /** The Bedrock player's XUID, or {@code null} if they aren't a Bedrock player / Floodgate is absent. */
    public static String xuid(UUID uuid) {
        Object player = player(uuid);
        if (player == null) {
            return null;
        }
        try {
            if (getXuidMethod == null) {
                getXuidMethod = player.getClass().getMethod("getXuid");
            }
            Object xuid = getXuidMethod.invoke(player);
            return xuid == null ? null : xuid.toString();
        } catch (Throwable ignored) {
            return null;
        }
    }

    /**
     * The player's effective Java UUID: the linked Java account's UUID if they've linked, otherwise the
     * Floodgate UUID. {@code null} if they aren't a Bedrock player / Floodgate is absent.
     */
    public static UUID javaUuid(UUID uuid) {
        Object player = player(uuid);
        if (player == null) {
            return null;
        }
        try {
            if (correctUniqueIdMethod == null) {
                correctUniqueIdMethod = player.getClass().getMethod("getCorrectUniqueId");
            }
            return (UUID) correctUniqueIdMethod.invoke(player);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Object player(UUID uuid) {
        Object api = api();
        if (api == null || uuid == null) {
            return null;
        }
        try {
            if (getPlayerMethod == null) {
                getPlayerMethod = api.getClass().getMethod("getPlayer", UUID.class);
            }
            return getPlayerMethod.invoke(api, uuid);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Object api() {
        if (!PRESENT) {
            return null;
        }
        Object instance = apiInstance;
        if (instance == null) {
            try {
                instance = Class.forName(FLOODGATE_API).getMethod("getInstance").invoke(null);
                apiInstance = instance;
            } catch (Throwable ignored) {
                return null;
            }
        }
        return instance;
    }

    private static boolean classPresent(String name) {
        try {
            Class.forName(name, false, Bedrock.class.getClassLoader());
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
