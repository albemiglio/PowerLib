package it.mycraft.powerlib.bukkit.display;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class DisplayApiTest {

    private ServerMock server;
    private MockPlugin plugin;
    private PlayerMock player;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin();
        player = server.addPlayer();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    // --- DisplayType.parse ---

    @Test
    void parseHandlesAliasesAndFallback() {
        assertThat(DisplayType.parse("action-bar", DisplayType.CHAT)).isEqualTo(DisplayType.ACTION_BAR);
        assertThat(DisplayType.parse("actionbar", DisplayType.CHAT)).isEqualTo(DisplayType.ACTION_BAR);
        assertThat(DisplayType.parse("boss-bar", DisplayType.CHAT)).isEqualTo(DisplayType.BOSS_BAR);
        assertThat(DisplayType.parse("bossbar", DisplayType.CHAT)).isEqualTo(DisplayType.BOSS_BAR);
        assertThat(DisplayType.parse("TiTlE", DisplayType.CHAT)).isEqualTo(DisplayType.TITLE);
        assertThat(DisplayType.parse(null, DisplayType.TITLE)).isEqualTo(DisplayType.TITLE);
        assertThat(DisplayType.parse("   ", DisplayType.TITLE)).isEqualTo(DisplayType.TITLE);
        assertThat(DisplayType.parse("nonsense", DisplayType.CHAT)).isEqualTo(DisplayType.CHAT);
    }

    // --- DisplayMessageSpec.from ---

    @Test
    void fromNullSectionIsDisabledWithFallbackType() {
        DisplayMessageSpec spec = DisplayMessageSpec.from(null, DisplayType.BOSS_BAR);
        assertThat(spec.enabled()).isFalse();
        assertThat(spec.type()).isEqualTo(DisplayType.BOSS_BAR);
    }

    @Test
    void fromParsesTypeAliasAndMessageTextFallback() {
        MemoryConfiguration section = new MemoryConfiguration();
        section.set("type", "action-bar");
        section.set("text", "hello");

        DisplayMessageSpec spec = DisplayMessageSpec.from(section, DisplayType.CHAT);
        assertThat(spec.type()).isEqualTo(DisplayType.ACTION_BAR);
        assertThat(spec.message()).isEqualTo("hello");
    }

    @Test
    void fromReadsFadeInAliasAndDurationConversions() {
        MemoryConfiguration camelCase = new MemoryConfiguration();
        camelCase.set("fadeIn", 7);
        camelCase.set("duration", 5); // seconds -> 100 ticks
        DisplayMessageSpec spec = DisplayMessageSpec.from(camelCase, DisplayType.TITLE);
        assertThat(spec.fadeInTicks()).isEqualTo(7);
        assertThat(spec.durationTicks()).isEqualTo(100L);

        MemoryConfiguration seconds = new MemoryConfiguration();
        seconds.set("duration-seconds", 3);
        assertThat(DisplayMessageSpec.from(seconds, DisplayType.TITLE).durationTicks()).isEqualTo(60L);

        MemoryConfiguration ticks = new MemoryConfiguration();
        ticks.set("duration-ticks", 42);
        assertThat(DisplayMessageSpec.from(ticks, DisplayType.TITLE).durationTicks()).isEqualTo(42L);
    }

    @Test
    void fromClampsProgressAndFallsBackOnInvalidEnums() {
        MemoryConfiguration high = new MemoryConfiguration();
        high.set("progress", 2.5D);
        high.set("color", "NOT_A_COLOR");
        high.set("style", "NOPE");
        DisplayMessageSpec clampedHigh = DisplayMessageSpec.from(high, DisplayType.BOSS_BAR);
        assertThat(clampedHigh.progress()).isEqualTo(1.0D);
        assertThat(clampedHigh.barColor()).isEqualTo(BarColor.WHITE);
        assertThat(clampedHigh.barStyle()).isEqualTo(BarStyle.SOLID);

        MemoryConfiguration low = new MemoryConfiguration();
        low.set("progress", -3.0D);
        low.set("color", "red");
        low.set("style", "segmented_10");
        DisplayMessageSpec clampedLow = DisplayMessageSpec.from(low, DisplayType.BOSS_BAR);
        assertThat(clampedLow.progress()).isEqualTo(0.0D);
        assertThat(clampedLow.barColor()).isEqualTo(BarColor.RED);
        assertThat(clampedLow.barStyle()).isEqualTo(BarStyle.SEGMENTED_10);
    }

    // --- DisplayPlaceholders.apply ---

    @Test
    void placeholdersApplyCoversTokenFormsAndGuards() {
        assertThat(DisplayPlaceholders.apply(null, Map.of("a", "b"))).isEmpty();
        assertThat(DisplayPlaceholders.apply("", Map.of("a", "b"))).isEmpty();
        assertThat(DisplayPlaceholders.apply("x", null)).isEqualTo("x");
        assertThat(DisplayPlaceholders.apply("x", Map.of())).isEqualTo("x");
        assertThat(DisplayPlaceholders.apply("hi %name%", Map.of("name", "Bob"))).isEqualTo("hi Bob");
        assertThat(DisplayPlaceholders.apply("hi %name%", Map.of("%name%", "Bob"))).isEqualTo("hi Bob");

        Map<String, Object> mixed = new HashMap<>();
        mixed.put(null, "skip");
        mixed.put("", "skip");
        mixed.put("name", null);
        assertThat(DisplayPlaceholders.apply("hi %name%", mixed)).isEqualTo("hi null");
    }

    // --- DisplayRenderer ---

    @Test
    void rendererSendsEachDisplayType() {
        DisplayRenderer renderer = new DisplayRenderer(plugin);

        renderer.send(player, DisplayMessageSpec.builder().type(DisplayType.CHAT).message("hello").build());
        assertThat(player.nextMessage()).isEqualTo("hello");

        assertThatCode(() -> {
            renderer.send(player, DisplayMessageSpec.builder().type(DisplayType.ACTION_BAR).message("act").build());
            renderer.send(player, DisplayMessageSpec.builder().type(DisplayType.TITLE).title("T").subtitle("S").build());
            renderer.send(player, DisplayMessageSpec.builder().type(DisplayType.BOSS_BAR).message("B").build());
            renderer.send(List.of(player), DisplayMessageSpec.builder().type(DisplayType.CHAT).message("group").build(), Map.of());
            server.getScheduler().performTicks(210L); // run the temporary boss-bar removal task
        }).doesNotThrowAnyException();
    }

    @Test
    void rendererIgnoresDisabledNullAndEmpty() {
        DisplayRenderer renderer = new DisplayRenderer(plugin);
        renderer.send(null, DisplayMessageSpec.builder().message("x").build());
        renderer.send(player, DisplayMessageSpec.builder().enabled(false).message("x").build());
        renderer.send(player, DisplayMessageSpec.builder().type(DisplayType.CHAT).message("").build());
        renderer.send(List.of(), DisplayMessageSpec.builder().message("x").build(), Map.of());
        assertThat(player.nextMessage()).isNull();
    }

    // --- DisplayActionExecutor ---

    @Test
    void executorRoutesAllConfiguredActions() {
        MemoryConfiguration actions = new MemoryConfiguration();
        actions.set("message", "chat-%who%");
        actions.set("action-bar", "act-%who%");
        ConfigurationSection title = actions.createSection("title");
        title.set("title", "Title");
        title.set("subtitle", "Sub");
        ConfigurationSection boss = actions.createSection("boss-bar");
        boss.set("message", "Boss");
        boss.set("duration", 2);

        DisplayActionExecutor executor = new DisplayActionExecutor(new DisplayRenderer(plugin));
        executor.execute(player, actions, Map.of("who", "Bob"));

        assertThat(player.nextMessage()).isEqualTo("chat-Bob");
        assertThatCode(() -> server.getScheduler().performTicks(60L)).doesNotThrowAnyException();
    }

    @Test
    void executorHonoursAliasesAndGuards() {
        DisplayActionExecutor executor = new DisplayActionExecutor(new DisplayRenderer(plugin));

        MemoryConfiguration aliased = new MemoryConfiguration();
        aliased.set("actionbar", "viaAlias");
        ConfigurationSection boss = aliased.createSection("bossbar");
        boss.set("message", "AliasBoss");

        assertThatCode(() -> {
            executor.execute(player, aliased, Map.of());
            executor.execute(null, aliased);       // null player -> no-op
            executor.execute(player, (ConfigurationSection) null); // null actions -> no-op
            server.getScheduler().performTicks(210L);
        }).doesNotThrowAnyException();
    }

    // --- ManagedBossBarService ---

    @Test
    void managedBossBarServiceShowHideClearDoesNotThrow() {
        DisplayMessageSpec spec = DisplayMessageSpec.builder().message("Hi").build();

        assertThatCode(() -> {
            ManagedBossBarService service = new ManagedBossBarService(plugin);
            service.show("greeting", player, spec, Map.of(), 0.5D);
            service.hide("greeting");
            service.show("greeting", player, spec, Map.of(), 0.5D);
            service.clear();
            service.close();
        }).doesNotThrowAnyException();
    }

    @Test
    void managedBossBarServiceHandlesAudienceExpiryAndGuards() {
        DisplayMessageSpec timed = DisplayMessageSpec.builder()
                .message("Timed").durationTicks(40L).build();

        assertThatCode(() -> {
            ManagedBossBarService service = new ManagedBossBarService(plugin);
            service.show("t", List.of(player), timed, Map.of(), 0.7D);
            server.getScheduler().performTicks(45L);          // fires scheduled expiry
            service.show("t", List.of(), timed, Map.of(), 0.5D); // empty audience -> hide
            service.show("n", (PlayerMock) null, timed, Map.of(), 0.5D); // null player -> hide
            service.show("d", List.of(player),
                    DisplayMessageSpec.builder().enabled(false).message("x").build(), Map.of(), 0.5D); // disabled -> ignored
            service.close();
        }).doesNotThrowAnyException();
    }
}
