package it.mycraft.powerlib.bukkit.utils;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import it.mycraft.powerlib.bukkit.PowerLib;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests the GPS bridge in {@link GpsUtils}.
 *
 * <p>Two layers, mirroring {@code NexoUtilsFurnitureTest}:
 * <ul>
 *   <li><b>GPS absent</b> — the public API must never throw and must return {@code false}, so plugins can
 *   call it unconditionally on servers that do not own the (paid) plugin.</li>
 *   <li><b>GPS present (stand-in)</b> — the handles are bound to {@link StubGpsApi} through the
 *   package-private {@code bindGpsHandles} seam and a mock plugin named {@code GPS} is registered, so the
 *   enabled-gate is exercised for real. Every handle is reset after each test so global state goes back to
 *   "GPS absent" for the rest of the suite.</li>
 * </ul>
 */
class GpsUtilsTest {

    private static final String[] HANDLE_FIELDS = {
            "apiConstructor", "startCompass", "gpsIsActive", "stopGPS",
            "addPoint", "removePoint", "getAllPoints", "pointGetName", "api"
    };

    private ServerMock server;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        StubGpsApi.reset();
    }

    @AfterEach
    void tearDown() throws ReflectiveOperationException {
        for (String field : HANDLE_FIELDS) {
            setStaticField(field, null); // restore "GPS absent" for the rest of the suite
        }
        setStaticField("apiAttempted", false);
        setStaticField("injectWarned", false);
        setPowerLibPlugin(null);
        MockBukkit.unmock();
    }

    private Location worldLocation() {
        World world = server.addSimpleWorld("world");
        return new Location(world, 10, 64, -20);
    }

    /** Binds the stand-in API and registers an enabled plugin named GPS, then injects the stub instance. */
    private void withGpsInstalled() throws ReflectiveOperationException {
        MockBukkit.createMockPlugin("GPS");
        GpsUtils.bindGpsHandles(StubGpsApi.class, StubPoint.class);
        setStaticField("api", new StubGpsApi(MockBukkit.createMockPlugin("owner")));
        setStaticField("apiAttempted", true);
    }

    // --- GPS absent ---------------------------------------------------------------------------------

    @Test
    void withoutGpsEveryAccessorIsInertAndSilent() {
        Location location = worldLocation();
        PlayerMock player = server.addPlayer();

        assertThatCode(() -> {
            assertThat(GpsUtils.isAvailable()).isFalse();
            assertThat(GpsUtils.isActive(player)).isFalse();
            assertThat(GpsUtils.startCompass(player, location)).isFalse();
            assertThat(GpsUtils.stop(player)).isFalse();
            assertThat(GpsUtils.addPoint("home", location)).isFalse();
            assertThat(GpsUtils.removePoint("home")).isFalse();
            assertThat(GpsUtils.hasPoint("home")).isFalse();
        }).doesNotThrowAnyException();
    }

    @Test
    void bindingIsANoOpWhenEitherGpsClassIsMissing() throws ReflectiveOperationException {
        GpsUtils.bindGpsHandles(null, StubPoint.class);
        assertThat(getStaticField("apiConstructor")).isNull();

        GpsUtils.bindGpsHandles(StubGpsApi.class, null);
        assertThat(getStaticField("apiConstructor")).isNull();
    }

    @Test
    void anApiThatDoesNotMatchDisablesTheBridgeInsteadOfBindingPartially() throws ReflectiveOperationException {
        GpsUtils.bindGpsHandles(DriftedGpsApi.class, StubPoint.class);

        // Drifted API: the constructor resolves but startCompass does not, so nothing stays bound.
        assertThat(getStaticField("apiConstructor")).isNull();
        assertThat(GpsUtils.isAvailable()).isFalse();
    }

    @Test
    void boundHandlesStayInertWhileTheGpsPluginIsNotEnabled() {
        // Classes on the classpath are not enough: GPS must also be running.
        GpsUtils.bindGpsHandles(StubGpsApi.class, StubPoint.class);

        assertThat(GpsUtils.isAvailable()).isFalse();
        assertThat(GpsUtils.startCompass(server.addPlayer(), worldLocation())).isFalse();
    }

    // --- GPS present (stand-in) ---------------------------------------------------------------------

    @Test
    void startsTheCompassTowardsTheTarget() throws ReflectiveOperationException {
        withGpsInstalled();
        Location target = worldLocation();
        PlayerMock player = server.addPlayer();

        assertThat(GpsUtils.isAvailable()).isTrue();
        assertThat(GpsUtils.startCompass(player, target)).isTrue();
        assertThat(StubGpsApi.compassTarget).isEqualTo(target);
        assertThat(StubGpsApi.compassPlayer).isEqualTo(player);
    }

    @Test
    void neverHijacksAnAlreadyRunningRoute() throws ReflectiveOperationException {
        withGpsInstalled();
        StubGpsApi.active = true;

        assertThat(GpsUtils.startCompass(server.addPlayer(), worldLocation())).isFalse();
        assertThat(StubGpsApi.compassTarget).isNull(); // the existing route was left alone
    }

    @Test
    void rejectsUnusableArgumentsBeforeTouchingGps() throws ReflectiveOperationException {
        withGpsInstalled();
        PlayerMock player = server.addPlayer();

        assertThat(GpsUtils.startCompass(null, worldLocation())).isFalse();
        assertThat(GpsUtils.startCompass(player, null)).isFalse();
        assertThat(GpsUtils.startCompass(player, new Location(null, 0, 0, 0))).isFalse(); // world-less
        assertThat(GpsUtils.addPoint(null, worldLocation())).isFalse();
        assertThat(GpsUtils.addPoint("home", null)).isFalse();
        assertThat(GpsUtils.addPoint("home", new Location(null, 0, 0, 0))).isFalse();
        assertThat(GpsUtils.removePoint(null)).isFalse();
        assertThat(GpsUtils.hasPoint(null)).isFalse();
        assertThat(GpsUtils.isActive(null)).isFalse();
        assertThat(GpsUtils.stop(null)).isFalse();

        assertThat(StubGpsApi.compassTarget).isNull();
        assertThat(StubGpsApi.points).isEmpty();
    }

    @Test
    void reportsWhetherARouteIsRunning() throws ReflectiveOperationException {
        withGpsInstalled();
        PlayerMock player = server.addPlayer();

        assertThat(GpsUtils.isActive(player)).isFalse();
        StubGpsApi.active = true;
        assertThat(GpsUtils.isActive(player)).isTrue();
    }

    @Test
    void stopsOnlyWhenARouteIsActuallyRunning() throws ReflectiveOperationException {
        withGpsInstalled();
        PlayerMock player = server.addPlayer();

        assertThat(GpsUtils.stop(player)).isFalse();     // nothing running -> nothing to stop
        assertThat(StubGpsApi.stopped).isNull();

        StubGpsApi.active = true;
        assertThat(GpsUtils.stop(player)).isTrue();
        assertThat(StubGpsApi.stopped).isEqualTo(player);
    }

    @Test
    void addsRemovesAndLooksUpNamedPoints() throws ReflectiveOperationException {
        withGpsInstalled();
        Location location = worldLocation();

        assertThat(GpsUtils.hasPoint("bank")).isFalse();
        assertThat(GpsUtils.addPoint("bank", location)).isTrue();
        assertThat(GpsUtils.hasPoint("bank")).isTrue();
        assertThat(GpsUtils.hasPoint("BANK")).isFalse(); // exact match only, as GPS names them
        assertThat(GpsUtils.removePoint("bank")).isTrue();
        assertThat(GpsUtils.hasPoint("bank")).isFalse();
    }

    @Test
    void aThrowingGpsIsReportedAsFailureRatherThanPropagated() throws ReflectiveOperationException {
        withGpsInstalled();
        StubGpsApi.throwOnCall = true;

        assertThatCode(() -> {
            assertThat(GpsUtils.startCompass(server.addPlayer(), worldLocation())).isFalse();
            assertThat(GpsUtils.addPoint("bank", worldLocation())).isFalse();
            assertThat(GpsUtils.removePoint("bank")).isFalse();
            assertThat(GpsUtils.hasPoint("bank")).isFalse();
        }).doesNotThrowAnyException();
    }

    // --- Lazy construction --------------------------------------------------------------------------

    @Test
    void bindsTheApiOnceInjectHasRunInsteadOfStayingInertForever() throws ReflectiveOperationException {
        MockBukkit.createMockPlugin("GPS");
        GpsUtils.bindGpsHandles(StubGpsApi.class, StubPoint.class);

        // Used before PowerLib.inject(...): unusable now, but the attempt must stay open.
        assertThat(GpsUtils.isAvailable()).isFalse();
        assertThat(getStaticField("apiAttempted")).isEqualTo(false);

        PowerLib.inject(MockBukkit.createMockPlugin("owner"));

        // A later call binds for real, rather than staying dead until the next reload.
        assertThat(GpsUtils.isAvailable()).isTrue();
        assertThat(GpsUtils.startCompass(server.addPlayer(), worldLocation())).isTrue();
    }

    // --- Reflection plumbing ------------------------------------------------------------------------

    private static void setPowerLibPlugin(Plugin plugin) throws ReflectiveOperationException {
        Field f = PowerLib.class.getDeclaredField("plugin");
        f.setAccessible(true);
        f.set(null, plugin);
    }

    private static void setStaticField(String field, Object value) throws ReflectiveOperationException {
        Field f = GpsUtils.class.getDeclaredField(field);
        f.setAccessible(true);
        f.set(null, value);
    }

    private static Object getStaticField(String field) throws ReflectiveOperationException {
        Field f = GpsUtils.class.getDeclaredField(field);
        f.setAccessible(true);
        return f.get(null);
    }

    // --- Stand-in GPS API ---------------------------------------------------------------------------

    /**
     * Stand-in for {@code com.live.bemmamin.gps.api.GPSAPI}: same public surface that
     * {@link GpsUtils#bindGpsHandles} resolves, with configurable results per test.
     */
    public static final class StubGpsApi {

        static Player compassPlayer;
        static Location compassTarget;
        static Player stopped;
        static boolean active;
        static boolean throwOnCall;
        static final List<StubPoint> points = new ArrayList<>();

        static void reset() {
            compassPlayer = null;
            compassTarget = null;
            stopped = null;
            active = false;
            throwOnCall = false;
            points.clear();
        }

        public StubGpsApi(Plugin plugin) {
            // GPS binds the owning plugin here; the stand-in only needs the signature to exist.
        }

        public void startCompass(Player player, Location location) {
            fuse();
            compassPlayer = player;
            compassTarget = location;
        }

        public boolean gpsIsActive(Player player) {
            return active;
        }

        public void stopGPS(Player player) {
            fuse();
            stopped = player;
            active = false;
        }

        public void addPoint(String name, Location location) {
            fuse();
            points.add(new StubPoint(name));
        }

        public void removePoint(String name) {
            fuse();
            points.removeIf(point -> point.getName().equals(name));
        }

        public List<StubPoint> getAllPoints() {
            fuse();
            return points;
        }

        private static void fuse() {
            if (throwOnCall) {
                throw new IllegalStateException("boom");
            }
        }
    }

    /** Stand-in for {@code com.live.bemmamin.gps.logic.Point}. */
    public static final class StubPoint {

        private final String name;

        StubPoint(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /** A GPS whose API drifted: the constructor still matches, {@code startCompass} does not. */
    public static final class DriftedGpsApi {

        public DriftedGpsApi(Plugin plugin) {
        }

        public void startCompass(Player player) { // wrong arity
        }
    }
}
