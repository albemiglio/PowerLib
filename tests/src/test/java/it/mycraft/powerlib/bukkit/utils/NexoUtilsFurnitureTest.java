package it.mycraft.powerlib.bukkit.utils;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.entity.SimpleEntityMock;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests the Nexo furniture-replacement bridge in {@link NexoUtils}.
 *
 * <p>Two layers:
 * <ul>
 *   <li><b>Nexo absent</b> — the public API must never throw, must return {@code false}, and must leave a
 *   reason in {@link NexoUtils#getLastFurnitureError()}.</li>
 *   <li><b>Nexo present (stand-in)</b> — the reflection handles are bound to a fake {@code NexoFurniture}
 *   ({@link StubNexoFurniture}) via the package-private {@code bindFurnitureHandles} seam, then the full
 *   remove/place/rotation logic is driven end-to-end. This mirrors the white-box reflection idiom used by
 *   {@code NexoListenerTest}. Every handle is reset after each test so global state stays "Nexo absent".</li>
 * </ul>
 */
class NexoUtilsFurnitureTest {

    private static final String[] FURNITURE_FIELDS = {
            "furnBaseFromEntity", "furnBaseFromBlock", "furnBaseFromLocation", "furnIsFurniture",
            "furnUpdate", "furnConvert", "furnPlace", "furnRemoveEntityDrop", "furnRemoveLocationDrop",
            "furnRemoveEntityPlayer", "furnRemoveLocationPlayer", "furnRemoveEntity", "furnRemoveLocation"
    };

    private ServerMock server;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        StubNexoFurniture.reset();
    }

    @AfterEach
    void tearDown() throws ReflectiveOperationException {
        for (String field : FURNITURE_FIELDS) {
            setStaticField(field, null); // restore "Nexo absent" for the rest of the suite
        }
        MockBukkit.unmock();
    }

    private Location worldLocation() {
        World world = server.addSimpleWorld("world");
        return new Location(world, 3, 65, 7);
    }

    // --- Public behaviour, Nexo absent ---------------------------------------------------------------

    @Test
    void exposesTheExactSignaturesConfiguredGameplayModuleCallsAgainst() throws NoSuchMethodException {
        Method replace = NexoUtils.class.getMethod(
                "replaceFurniture", Object.class, Location.class, String.class, Player.class);
        Method lastError = NexoUtils.class.getMethod("getLastFurnitureError");

        assertThat(Modifier.isStatic(replace.getModifiers())).isTrue();
        assertThat(replace.getReturnType()).isEqualTo(boolean.class);
        assertThat(Modifier.isStatic(lastError.getModifiers())).isTrue();
        assertThat(lastError.getReturnType()).isEqualTo(String.class);
    }

    @Test
    void rejectsNullEmptyOrWorldlessTargets() {
        Location valid = worldLocation();

        assertThat(NexoUtils.replaceFurniture(null, valid, null, null)).isFalse();
        assertThat(NexoUtils.getLastFurnitureError()).isEqualTo("invalid target id or location");

        assertThat(NexoUtils.replaceFurniture(null, valid, "", null)).isFalse();
        assertThat(NexoUtils.getLastFurnitureError()).isEqualTo("invalid target id or location");

        assertThat(NexoUtils.replaceFurniture(null, null, "chair", null)).isFalse();
        assertThat(NexoUtils.getLastFurnitureError()).isEqualTo("invalid target id or location");

        assertThat(NexoUtils.replaceFurniture(null, new Location(null, 0, 0, 0), "chair", null)).isFalse();
        assertThat(NexoUtils.getLastFurnitureError()).isEqualTo("invalid target id or location");
    }

    @Test
    void withoutNexoInstalledFailsGracefullyAndReportsAReason() {
        Location location = worldLocation();
        PlayerMock player = server.addPlayer();

        boolean[] result = new boolean[1];
        assertThatCode(() -> result[0] = NexoUtils.replaceFurniture(null, location, "chair", player))
                .doesNotThrowAnyException();

        assertThat(result[0]).isFalse();
        assertThat(NexoUtils.getLastFurnitureError()).isNotBlank();
    }

    @Test
    void handlesAnEntityFurnitureHintWithoutNexoWithoutThrowing() {
        Location location = worldLocation();
        Entity furniture = new SimpleEntityMock(server);

        boolean[] result = new boolean[1];
        assertThatCode(() -> result[0] = NexoUtils.replaceFurniture(furniture, location, "chair", server.addPlayer()))
                .doesNotThrowAnyException();

        assertThat(result[0]).isFalse();
        assertThat(NexoUtils.getLastFurnitureError()).isNotBlank();
    }

    // --- Nexo present (stand-in): end-to-end replace ------------------------------------------------

    @Test
    void bindFurnitureHandlesResolvesEveryHandleFromAProvider() throws ReflectiveOperationException {
        NexoUtils.bindFurnitureHandles(StubNexoFurniture.class);

        assertThat(getStaticField("furnBaseFromLocation")).isNotNull();
        assertThat(getStaticField("furnIsFurniture")).isNotNull();
        assertThat(getStaticField("furnPlace")).isNotNull();
        assertThat(getStaticField("furnRemoveEntityPlayer")).isNotNull();
        assertThat(getStaticField("furnRemoveLocation")).isNotNull();
    }

    @Test
    void replacesByLocationWhenNexoRemovesAndPlaces() {
        NexoUtils.bindFurnitureHandles(StubNexoFurniture.class);
        StubNexoFurniture.removeResult = true;
        StubNexoFurniture.placeResult = new Object();

        assertThat(NexoUtils.replaceFurniture(null, worldLocation(), "chair", server.addPlayer())).isTrue();
    }

    @Test
    void replacesUsingAnEntityBaseReportedByNexo() {
        StubNexoFurniture.baseFromLocation = new SimpleEntityMock(server); // Nexo resolves a base entity
        NexoUtils.bindFurnitureHandles(StubNexoFurniture.class);

        Entity furniture = new SimpleEntityMock(server);
        assertThat(NexoUtils.replaceFurniture(furniture, worldLocation(), "chair", server.addPlayer())).isTrue();
    }

    @Test
    void replacesUsingABlockBaseReportedByNexo() {
        StubNexoFurniture.baseFromBlock = new SimpleEntityMock(server); // base found from the block, not the location
        NexoUtils.bindFurnitureHandles(StubNexoFurniture.class);

        Block block = server.addSimpleWorld("blocks").getBlockAt(0, 64, 0);
        assertThat(NexoUtils.replaceFurniture(block, worldLocation(), "chair", server.addPlayer())).isTrue();
    }

    @Test
    void reportsWhenTargetIsNotRegisteredAsFurniture() {
        StubNexoFurniture.furnitureFlag = false;
        NexoUtils.bindFurnitureHandles(StubNexoFurniture.class);

        assertThat(NexoUtils.replaceFurniture(null, worldLocation(), "not_a_chair", null)).isFalse();
        assertThat(NexoUtils.getLastFurnitureError())
                .contains("target id is not registered as a Nexo furniture: not_a_chair");
    }

    @Test
    void isKnownFurnitureAnswersFalseWhenNexoCannotConfirmIt() {
        // The Nexo bridge stands the Bukkit fallback down only for ids it can confirm as furniture:
        // answering true on an unverifiable id would silently drop custom block interactions, which
        // the native furniture events do not cover.
        StubNexoFurniture.furnitureFlag = true;
        NexoUtils.bindFurnitureHandles(StubNexoFurniture.class);
        assertThat(NexoUtils.isKnownFurniture("chair")).isTrue();

        StubNexoFurniture.furnitureFlag = false;
        assertThat(NexoUtils.isKnownFurniture("a_custom_block")).isFalse();

        StubNexoFurniture.isFurnitureThrows = true;
        assertThat(NexoUtils.isKnownFurniture("chair")).isFalse();
    }

    @Test
    void reportsWhenNexoCannotRemoveTheOldFurniture() {
        StubNexoFurniture.removeResult = false;
        NexoUtils.bindFurnitureHandles(StubNexoFurniture.class);

        assertThat(NexoUtils.replaceFurniture(null, worldLocation(), "chair", server.addPlayer())).isFalse();
        assertThat(NexoUtils.getLastFurnitureError()).contains("Nexo remove returned false");
    }

    @Test
    void fallsBackThroughTheEntityRemoveOverloads() {
        StubNexoFurniture.removeEntityPlayerResult = false; // first overload fails
        StubNexoFurniture.removeEntityResult = true;        // entity-only overload succeeds
        NexoUtils.bindFurnitureHandles(StubNexoFurniture.class);

        Entity furniture = new SimpleEntityMock(server);
        assertThat(NexoUtils.replaceFurniture(furniture, worldLocation(), "chair", server.addPlayer())).isTrue();
    }

    @Test
    void fallsBackThroughTheLocationRemoveOverloads() {
        StubNexoFurniture.removeLocationPlayerResult = false; // first location overload fails
        StubNexoFurniture.removeLocationResult = true;        // location-only overload succeeds
        NexoUtils.bindFurnitureHandles(StubNexoFurniture.class);

        assertThat(NexoUtils.replaceFurniture(null, worldLocation(), "chair", server.addPlayer())).isTrue();
    }

    @Test
    void baseEntityLookupFailureIsSwallowedAndReplacementStillProceeds() {
        StubNexoFurniture.baseFromLocationThrows = true; // invokeBase catch path
        NexoUtils.bindFurnitureHandles(StubNexoFurniture.class);

        // getBaseEntity swallows the exception; with a null furniture hint it falls through to remove+place.
        assertThat(NexoUtils.replaceFurniture(null, worldLocation(), "chair", server.addPlayer())).isTrue();
    }

    @Test
    void isFurnitureAssumesTrueWhenTheNexoCheckThrows() throws ReflectiveOperationException {
        StubNexoFurniture.isFurnitureThrows = true;
        NexoUtils.bindFurnitureHandles(StubNexoFurniture.class);
        StubNexoFurniture.removeResult = true;

        // isFurniture swallows the exception and returns true, so replacement proceeds to remove+place.
        assertThat(NexoUtils.replaceFurniture(null, worldLocation(), "chair", server.addPlayer())).isTrue();
    }

    // --- Internal helpers driven directly ----------------------------------------------------------

    @Test
    void yawToRotationMapsEachOctant() throws ReflectiveOperationException {
        assertThat(yawToRotation(0f)).isEqualTo(Rotation.NONE);
        assertThat(yawToRotation(45f)).isEqualTo(Rotation.CLOCKWISE_45);
        assertThat(yawToRotation(90f)).isEqualTo(Rotation.CLOCKWISE);
        assertThat(yawToRotation(135f)).isEqualTo(Rotation.CLOCKWISE_135);
        assertThat(yawToRotation(180f)).isEqualTo(Rotation.FLIPPED);
        assertThat(yawToRotation(225f)).isEqualTo(Rotation.FLIPPED_45);
        assertThat(yawToRotation(270f)).isEqualTo(Rotation.COUNTER_CLOCKWISE);
        assertThat(yawToRotation(315f)).isEqualTo(Rotation.COUNTER_CLOCKWISE_45);
        assertThat(yawToRotation(360f)).isEqualTo(Rotation.NONE);
        assertThat(yawToRotation(-45f)).isEqualTo(Rotation.COUNTER_CLOCKWISE_45); // negatives normalise to 315
    }

    @Test
    void findPlaceMethodMatchesFloatAndRotationSignaturesOnly() throws ReflectiveOperationException {
        assertThat(findPlaceMethod(StubNexoFurniture.class)).isNotNull();       // float place(...)
        assertThat(findPlaceMethod(RotationPlaceApi.class)).isNotNull();        // Rotation place(...)
        assertThat(findPlaceMethod(WrongArityPlaceApi.class)).isNull();         // place(...) with bad arity -> skipped
        assertThat(findPlaceMethod(NoPlaceApi.class)).isNull();
    }

    @Test
    void placeRotationArgumentPicksFloatOrRotationPerSignature() throws ReflectiveOperationException {
        Object floatArg = placeRotationArgument(StubNexoFurniture.class.getMethod(
                "place", String.class, Location.class, float.class, BlockFace.class), 90f);
        Object rotationArg = placeRotationArgument(RotationPlaceApi.class.getMethod(
                "place", String.class, Location.class, Rotation.class, BlockFace.class), 90f);

        assertThat(floatArg).isInstanceOf(Float.class);
        assertThat(rotationArg).isInstanceOf(Rotation.class);
    }

    @Test
    void placeFurnitureReportsWhenNoPlaceMethodIsBound() throws ReflectiveOperationException {
        setStaticField("furnPlace", null);
        assertThat(placeFurniture("chair", worldLocation(), 0f, BlockFace.UP)).isFalse();
        assertThat(NexoUtils.getLastFurnitureError()).contains("No compatible NexoFurniture.place");
    }

    @Test
    void placeFurnitureReportsWhenNexoReturnsNull() throws ReflectiveOperationException {
        StubNexoFurniture.placeResult = null;
        setStaticField("furnPlace", StubNexoFurniture.class.getMethod(
                "place", String.class, Location.class, float.class, BlockFace.class));

        assertThat(placeFurniture("chair", worldLocation(), 0f, BlockFace.UP)).isFalse();
        assertThat(NexoUtils.getLastFurnitureError()).contains("Nexo returned null while placing chair");
    }

    @Test
    void placeFurnitureReportsWhenNexoThrows() throws ReflectiveOperationException {
        StubNexoFurniture.placeThrows = true;
        setStaticField("furnPlace", StubNexoFurniture.class.getMethod(
                "place", String.class, Location.class, float.class, BlockFace.class));

        assertThat(placeFurniture("chair", worldLocation(), 0f, BlockFace.UP)).isFalse();
        assertThat(NexoUtils.getLastFurnitureError()).contains("error while placing chair");
    }

    @Test
    void updateFurnitureDisplayReportsWhenItemCannotBeBuilt() throws ReflectiveOperationException {
        // Nexo core absent -> itemStackFromId returns null, so the swap is rejected before dereferencing.
        boolean result = updateFurnitureDisplay(null, "chair");

        assertThat(result).isFalse();
        assertThat(NexoUtils.getLastFurnitureError()).contains("could not build Nexo item");
    }

    @Test
    void invokeFurnitureUpdaterInvokesWhenBoundAndSwallowsFailures() throws ReflectiveOperationException {
        Method updater = StubNexoFurniture.class.getMethod("updateFurniture", ItemDisplay.class);
        Method thrower = StubNexoFurniture.class.getMethod("throwingUpdate", ItemDisplay.class);

        assertThatCode(() -> {
            invokeFurnitureUpdater(null, null);      // no handle -> no-op
            invokeFurnitureUpdater(updater, null);   // invoked, ignores its argument
            invokeFurnitureUpdater(thrower, null);   // throws internally, swallowed
        }).doesNotThrowAnyException();
    }

    @Test
    void describeFurnitureFallsBackToUnknownLocation() throws ReflectiveOperationException {
        assertThat(describeFurniture(null, null)).isEqualTo("unknown location");
    }

    @Test
    void optionalMethodAndOptionalClassResolveLeniently() throws ReflectiveOperationException {
        assertThat(optionalClass("java.lang.String")).isEqualTo(String.class);
        assertThat(optionalClass("no.such.Class")).isNull();
        assertThat(optionalMethod(String.class, "length")).isNotNull();
        assertThat(optionalMethod(String.class, "definitelyMissingMethod")).isNull();
    }

    // --- Reflection plumbing -----------------------------------------------------------------------

    private static Rotation yawToRotation(float yaw) throws ReflectiveOperationException {
        return (Rotation) callPrivate("yawToRotation", new Class<?>[]{float.class}, yaw);
    }

    private static Method findPlaceMethod(Class<?> owner) throws ReflectiveOperationException {
        return (Method) callPrivate("findPlaceMethod", new Class<?>[]{Class.class}, owner);
    }

    private static Object placeRotationArgument(Method place, float yaw) throws ReflectiveOperationException {
        return callPrivate("placeRotationArgument", new Class<?>[]{Method.class, float.class}, place, yaw);
    }

    private static boolean placeFurniture(String id, Location loc, float yaw, BlockFace face)
            throws ReflectiveOperationException {
        return (boolean) callPrivate("placeFurniture",
                new Class<?>[]{String.class, Location.class, float.class, BlockFace.class}, id, loc, yaw, face);
    }

    private static boolean updateFurnitureDisplay(ItemDisplay display, String id)
            throws ReflectiveOperationException {
        return (boolean) callPrivate("updateFurnitureDisplay",
                new Class<?>[]{ItemDisplay.class, String.class}, display, id);
    }

    private static void invokeFurnitureUpdater(Method method, ItemDisplay display)
            throws ReflectiveOperationException {
        callPrivate("invokeFurnitureUpdater", new Class<?>[]{Method.class, ItemDisplay.class}, method, display);
    }

    private static String describeFurniture(Object furniture, Location location)
            throws ReflectiveOperationException {
        return (String) callPrivate("describeFurniture",
                new Class<?>[]{Object.class, Location.class}, furniture, location);
    }

    private static Method optionalMethod(Class<?> owner, String name) throws ReflectiveOperationException {
        return (Method) callPrivate("optionalMethod",
                new Class<?>[]{Class.class, String.class, Class[].class}, owner, name, new Class<?>[0]);
    }

    private static Class<?> optionalClass(String name) throws ReflectiveOperationException {
        return (Class<?>) callPrivate("optionalClass", new Class<?>[]{String.class}, name);
    }

    private static Object callPrivate(String name, Class<?>[] types, Object... args)
            throws ReflectiveOperationException {
        Method method = NexoUtils.class.getDeclaredMethod(name, types);
        method.setAccessible(true);
        return method.invoke(null, args);
    }

    private static void setStaticField(String field, Object value) throws ReflectiveOperationException {
        Field f = NexoUtils.class.getDeclaredField(field);
        f.setAccessible(true);
        f.set(null, value);
    }

    private static Object getStaticField(String field) throws ReflectiveOperationException {
        Field f = NexoUtils.class.getDeclaredField(field);
        f.setAccessible(true);
        return f.get(null);
    }

    // --- Stand-in Nexo APIs ------------------------------------------------------------------------

    /**
     * Stand-in for {@code com.nexomc.nexo.api.NexoFurniture}: same public static method surface that
     * {@link NexoUtils#bindFurnitureHandles} resolves, with configurable results per test.
     */
    public static final class StubNexoFurniture {
        static Object baseFromLocation;
        static Object baseFromEntity;
        static Object baseFromBlock;
        static boolean furnitureFlag;
        static boolean isFurnitureThrows;
        static boolean removeResult;
        static Boolean removeEntityPlayerResult; // null -> fall back to removeResult
        static Boolean removeEntityResult;
        static Boolean removeLocationPlayerResult;
        static Boolean removeLocationResult;
        static boolean baseFromLocationThrows;
        static Object placeResult;
        static boolean placeThrows;

        static void reset() {
            baseFromLocation = null;
            baseFromEntity = null;
            baseFromBlock = null;
            furnitureFlag = true;
            isFurnitureThrows = false;
            removeResult = true;
            removeEntityPlayerResult = null;
            removeEntityResult = null;
            removeLocationPlayerResult = null;
            removeLocationResult = null;
            baseFromLocationThrows = false;
            placeResult = new Object();
            placeThrows = false;
        }

        public static Object baseEntity(Location location) {
            if (baseFromLocationThrows) {
                throw new IllegalStateException("boom");
            }
            return baseFromLocation;
        }

        public static Object baseEntity(Entity entity) {
            return baseFromEntity;
        }

        public static Object baseEntity(Block block) {
            return baseFromBlock;
        }

        public static boolean isFurniture(String id) {
            if (isFurnitureThrows) {
                throw new IllegalStateException("boom");
            }
            return furnitureFlag;
        }

        public static void updateFurniture(ItemDisplay display) {
            // no-op re-render
        }

        public static void convertFurniture(ItemDisplay display) {
            // no-op conversion
        }

        public static void throwingUpdate(ItemDisplay display) {
            throw new IllegalStateException("boom");
        }

        public static Object place(String id, Location location, float yaw, BlockFace face) {
            if (placeThrows) {
                throw new IllegalStateException("boom");
            }
            return placeResult;
        }

        public static boolean remove(Entity entity, Player player) {
            return removeEntityPlayerResult != null ? removeEntityPlayerResult : removeResult;
        }

        public static boolean remove(Location location, Player player) {
            return removeLocationPlayerResult != null ? removeLocationPlayerResult : removeResult;
        }

        public static boolean remove(Entity entity) {
            return removeEntityResult != null ? removeEntityResult : removeResult;
        }

        public static boolean remove(Location location) {
            return removeLocationResult != null ? removeLocationResult : removeResult;
        }
    }

    public static final class RotationPlaceApi {
        public static Object place(String id, Location location, Rotation rotation, BlockFace face) {
            return new Object();
        }
    }

    public static final class WrongArityPlaceApi {
        public static Object place(String id, Location location) {
            return new Object();
        }
    }

    public static final class NoPlaceApi {
        public static void unrelated() {
            // no place(...) at all
        }
    }
}
