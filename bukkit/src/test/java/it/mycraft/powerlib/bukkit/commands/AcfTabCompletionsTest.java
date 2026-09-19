package it.mycraft.powerlib.bukkit.commands;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Private;
import co.aikar.commands.annotation.Subcommand;
import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-end test of {@link AcfTabCompletions} against a real ACF manager.
 *
 * <p>The event goes through Bukkit, so ACF's own completion handler runs first and produces exactly what a
 * player would see today: that is the behaviour being corrected, and asserting against a hand-written list
 * instead would only prove the fix agrees with itself. What arrives at the assertions is therefore ACF's
 * output after this listener has had its say.
 *
 * <p>The event is fired off the main thread because it is an asynchronous one — which is also where it
 * runs in production.
 */
class AcfTabCompletionsTest {

    private ServerMock server;
    private Plugin plugin;
    private PaperCommandManager manager;
    private PlayerMock player;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        server.addSimpleWorld("world");
        plugin = MockBukkit.createMockPlugin("PowerLibTest");
        manager = new PaperCommandManager(plugin);
        manager.registerCommand(new OfficeCommand());
        AcfTabCompletions.register(plugin, manager);
        player = server.addPlayer();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void offersEveryFormAndNotOnlyTheFirstOne() {
        // What ACF alone answers here is "resign, create": the second form of each subcommand is missing.
        List<String> completions = complete("/office ");

        assertTrue(completions.containsAll(List.of("resign", "dimettiti", "create", "crea")),
                () -> "every registered form should be offered, got " + completions);
    }

    @Test
    void answersTheFormThePlayerIsActuallyTyping() {
        // ACF matches the key "dimettiti" but suggests "resign", which is not what completion means.
        assertEquals(List.of("dimettiti"), complete("/office dim"));
    }

    @Test
    void keepsBothFormsWhenBothMatchWhatWasTyped() {
        assertEquals(List.of("create", "crea"), complete("/office cr"));
    }

    @Test
    void completesTheSecondWordOfAClassLevelSubcommand() {
        manager.registerCommand(new OffersCommand());

        assertEquals(List.of("create", "crea"), complete("/offers offerte c"));
    }

    @Test
    void hidesAFormTheSenderMayNotSee() {
        assertEquals(List.of(), complete("/office seg"));

        player.addAttachment(plugin, "office.secret", true);

        assertEquals(List.of("segreto"), complete("/office seg"));
    }

    @Test
    void neverOffersAPrivateSubcommand() {
        assertEquals(List.of(), complete("/office hid"));
    }

    @Test
    void leavesChatAndUnknownCommandsUntouched() {
        // Not handled by ACF: a completion nobody asked for must not be invented.
        assertEquals(List.of("something"), complete("/nosuchcommand dim", List.of("something"), true, false));
        // Handled, but for a root command this manager does not own.
        assertEquals(List.of("something"), complete("/nosuchcommand dim", List.of("something"), true, true));
        // Plain chat, which Paper also routes through this event.
        assertEquals(List.of("something"), complete("hello dim", List.of("something"), false, true));
    }

    @Test
    void leavesALineWithNoArgumentYetUntouched() {
        // "/office" without a space is still completing the command name, not a subcommand.
        assertEquals(List.of("office"), complete("/office", List.of("office"), true, true));
    }

    @Test
    void registerRefusesToBeCalledWithoutAPluginOrAManager() {
        assertThrows(NullPointerException.class, () -> AcfTabCompletions.register(null, manager));
        assertThrows(NullPointerException.class, () -> AcfTabCompletions.register(plugin, null));
    }

    private List<String> complete(String buffer) {
        return complete(buffer, List.of(), true, false);
    }

    private List<String> complete(String buffer, List<String> acfAlreadyFound, boolean command, boolean handled) {
        AsyncTabCompleteEvent event = new AsyncTabCompleteEvent(
                player, new ArrayList<>(acfAlreadyFound), buffer, command, player.getLocation());
        event.setHandled(handled);
        try {
            server.getScheduler().executeAsyncEvent(event).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e.getCause());
        }
        return event.getCompletions();
    }

    @CommandAlias("office")
    public static class OfficeCommand extends BaseCommand {

        @Subcommand("resign|dimettiti")
        public void resign(CommandSender sender) {
        }

        @Subcommand("create|crea")
        public void create(CommandSender sender) {
        }

        @Subcommand("secret|segreto")
        @CommandPermission("office.secret")
        public void secret(CommandSender sender) {
        }

        @Subcommand("hidden|nascosto")
        @Private
        public void hidden(CommandSender sender) {
        }
    }

    @CommandAlias("offers")
    @Subcommand("offers|offerte")
    public static class OffersCommand extends BaseCommand {

        @Subcommand("create|crea")
        public void create(CommandSender sender) {
        }
    }
}
