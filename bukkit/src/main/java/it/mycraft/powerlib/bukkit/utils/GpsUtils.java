package it.mycraft.powerlib.bukkit.utils;

import it.mycraft.powerlib.bukkit.PowerLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Reflection bridge to the <a href="https://www.spigotmc.org/resources/53672/">GPS</a> navigation plugin,
 * used to point a player's compass at a destination.
 *
 * <p><b>Why reflection and not a normal compile-time dependency?</b> GPS is a <b>paid</b> plugin: it is not
 * published on Maven Central, on CodeMC, or on any other public repository, and it ships no separate API
 * artifact — compiling against it requires the full plugin jar. Declaring it as a {@code provided}
 * dependency would therefore break every build that cannot reach a private mirror of that jar, PowerLib's
 * own public CI included. Reflection keeps PowerLib buildable from a clean {@code .m2} while still binding
 * to the GPS API <em>at runtime</em>, on the servers that actually own the plugin. As a side effect this
 * bridge carries no GPS code at all, only the names of its classes.
 *
 * <p>On servers without GPS the bridge stays inert: every method returns {@code false} and nothing is
 * logged. "GPS not installed" is silent; "GPS installed but its API does not match" is logged once, so
 * version drift surfaces instead of being swallowed.
 *
 * <p>Unlike Nexo's static API, {@code GPSAPI} is instance-based and takes the owning {@link Plugin}, so the
 * instance is built lazily on first use from {@code PowerLib.getPlugin()} — {@link PowerLib#inject} must
 * have run first. PowerLib is shaded per plugin, so every consumer gets its own instance and GPS attributes
 * points to the right plugin.
 *
 * <p>Plugins using this must {@code softdepend} on {@code GPS} in their {@code plugin.yml} so its classes
 * are reachable from the plugin classloader.
 *
 * <p>Call this from the server's main thread: it drives Bukkit and GPS state, which is not thread-safe.
 * The handles themselves are resolved once during class initialisation and only read afterwards.
 */
public final class GpsUtils {

    private static final String PLUGIN_NAME = "GPS";

    private static Constructor<?> apiConstructor; // GPSAPI(Plugin)
    private static Method startCompass;           // GPSAPI.startCompass(Player, Location)
    private static Method gpsIsActive;            // GPSAPI.gpsIsActive(Player) -> boolean
    private static Method stopGPS;                // GPSAPI.stopGPS(Player)
    private static Method addPoint;               // GPSAPI.addPoint(String, Location)
    private static Method removePoint;            // GPSAPI.removePoint(String)
    private static Method getAllPoints;           // GPSAPI.getAllPoints() -> List<Point>
    private static Method pointGetName;           // Point.getName() -> String

    private static Object api;                    // GPSAPI instance, built on first use
    private static boolean apiAttempted;          // a failed construction is not retried, nor re-logged
    private static boolean injectWarned;          // the "not injected yet" warning is logged once

    static {
        bindGps();
    }

    private GpsUtils() {
    }

    private static void bindGps() {
        // Split from the handle resolution below so the resolution is unit-testable against a stand-in
        // API (see GpsUtilsTest) without a live GPS on the classpath. When GPS is absent optionalClass
        // returns null and bindGpsHandles is a no-op — every accessor stays inert.
        bindGpsHandles(optionalClass("com.live.bemmamin.gps.api.GPSAPI"),
                optionalClass("com.live.bemmamin.gps.logic.Point"));
    }

    static void bindGpsHandles(Class<?> gpsApi, Class<?> point) {
        if (gpsApi == null || point == null) {
            return;
        }
        try {
            // All handles come from one stable class, so binding is all-or-nothing: a partial bind would
            // leave the bridge half-working and fail later, at the call the server actually depends on.
            apiConstructor = gpsApi.getConstructor(Plugin.class);
            startCompass = gpsApi.getMethod("startCompass", Player.class, Location.class);
            gpsIsActive = gpsApi.getMethod("gpsIsActive", Player.class);
            stopGPS = gpsApi.getMethod("stopGPS", Player.class);
            addPoint = gpsApi.getMethod("addPoint", String.class, Location.class);
            removePoint = gpsApi.getMethod("removePoint", String.class);
            getAllPoints = gpsApi.getMethod("getAllPoints");
            pointGetName = point.getMethod("getName");
        } catch (NoSuchMethodException | LinkageError drift) {
            apiConstructor = null;
            Bukkit.getLogger().warning("[PowerLib] GPS is installed but its API does not match ("
                    + drift.getMessage() + "); GPS integration disabled. Update PowerLib or GPS.");
        }
    }

    /**
     * Whether GPS is installed, bound, and enabled right now.
     *
     * @return {@code true} if GPS can be used
     */
    public static boolean isAvailable() {
        return ready() != null;
    }

    /**
     * Whether the player currently has a GPS route or compass running.
     *
     * @param player the player to test
     * @return {@code true} if a GPS session is active for that player
     */
    public static boolean isActive(Player player) {
        Object gps = ready();
        if (gps == null || player == null) return false;
        return Boolean.TRUE.equals(query(gpsIsActive, gps, player));
    }

    /**
     * Points the player's compass at the given location.
     *
     * <p>Does nothing if the player is already navigating: an existing route is never hijacked.
     *
     * @param player the player to navigate
     * @param target the destination
     * @return {@code true} if navigation started, {@code false} if GPS is unavailable, the arguments are
     * unusable, or the player was already navigating
     */
    public static boolean startCompass(Player player, Location target) {
        Object gps = ready();
        if (gps == null || player == null || target == null || target.getWorld() == null) return false;
        if (Boolean.TRUE.equals(query(gpsIsActive, gps, player))) return false;
        return call(startCompass, gps, player, target);
    }

    /**
     * Stops the player's GPS session, if any. Safe to call on quit without checking first.
     *
     * @param player the player to stop navigating
     * @return {@code true} if a session was running and was stopped
     */
    public static boolean stop(Player player) {
        Object gps = ready();
        if (gps == null || player == null) return false;
        if (!Boolean.TRUE.equals(query(gpsIsActive, gps, player))) return false;
        return call(stopGPS, gps, player);
    }

    /**
     * Registers a named GPS point.
     *
     * <p>Points added this way live in GPS for as long as it runs: whoever adds a temporary point is
     * responsible for {@link #removePoint(String) removing} it once the route is over, or the point list
     * grows for the whole uptime.
     *
     * @param name     the point name
     * @param location the point location
     * @return {@code true} if the point was registered
     */
    public static boolean addPoint(String name, Location location) {
        Object gps = ready();
        if (gps == null || name == null || location == null || location.getWorld() == null) return false;
        return call(addPoint, gps, name, location);
    }

    /**
     * Removes the named GPS point.
     *
     * @param name the point name
     * @return {@code true} if the removal call went through
     */
    public static boolean removePoint(String name) {
        Object gps = ready();
        if (gps == null || name == null) return false;
        return call(removePoint, gps, name);
    }

    /**
     * Whether a GPS point with this exact name exists.
     *
     * @param name the point name to look for
     * @return {@code true} if such a point is registered
     */
    public static boolean hasPoint(String name) {
        Object gps = ready();
        if (gps == null || name == null) return false;
        if (!(query(getAllPoints, gps) instanceof List<?> points)) return false;
        for (Object point : points) {
            if (name.equals(query(pointGetName, point))) return true;
        }
        return false;
    }

    /**
     * @return the live {@code GPSAPI} instance, or {@code null} if GPS cannot be used right now. The
     * enabled check is repeated on every call because GPS can be disabled while PowerLib stays loaded.
     */
    private static synchronized Object ready() {
        if (apiConstructor == null || !Bukkit.getPluginManager().isPluginEnabled(PLUGIN_NAME)) return null;
        if (apiAttempted) return api;
        Plugin owner = PowerLib.getPlugin();
        if (owner == null) {
            // Not a permanent failure: inject() may just not have run yet, so leave the attempt open and
            // bind on a later call instead of staying inert until the next reload. Warn only once.
            if (!injectWarned) {
                injectWarned = true;
                Bukkit.getLogger().warning("[PowerLib] GPS integration used before PowerLib.inject(plugin);"
                        + " call it at the top of onEnable.");
            }
            return null;
        }
        apiAttempted = true;
        try {
            api = apiConstructor.newInstance(owner);
        } catch (ReflectiveOperationException | LinkageError e) {
            Bukkit.getLogger().warning("[PowerLib] GPS is installed but its API could not be created ("
                    + e.getMessage() + "); GPS integration disabled.");
        }
        return api;
    }

    private static Class<?> optionalClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException | LinkageError absent) {
            return null; // GPS not installed — stay inert, quietly
        }
    }

    private static boolean call(Method method, Object target, Object... args) {
        try {
            method.invoke(target, args);
            return true;
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }

    private static Object query(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}
