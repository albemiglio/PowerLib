package it.mycraft.powerlib.bukkit.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.RootCommand;
import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

/**
 * Makes ACF offer every form of a subcommand in tab completion, not only the first one.
 *
 * <p>With {@code @Subcommand("resign|dimettiti")} ACF runs both {@code /cmd resign} and
 * {@code /cmd dimettiti}, but tab completion only ever shows {@code resign}, and {@code /cmd dim<tab>}
 * gets {@code resign} back. A plugin whose subcommands exist in two languages therefore looks, to its
 * players, as if half of its commands were missing. After
 *
 * <pre>{@code
 * PaperCommandManager manager = new PaperCommandManager(plugin);
 * // register commands...
 * AcfTabCompletions.register(plugin, manager);
 * }</pre>
 *
 * <p>every form shows up, with the same visibility rules ACF applies: private commands stay hidden, and a
 * form only appears for a sender holding its class and method permissions. See {@code SubcommandForms} for
 * how the list is fixed.
 *
 * <p><b>Requirements.</b> Paper, since it hooks {@link AsyncTabCompleteEvent}, and ACF on the consumer's
 * classpath: this library only compiles against it ({@code acf-paper} 0.5.1, provided and optional). A
 * consumer that relocates {@code co.aikar.commands} must shade PowerLib into the same jar, so the shade
 * plugin rewrites the references in this class too; it does not work against the standalone PowerLib
 * plugin. The behaviour it compensates for was read from ACF 0.5.1's
 * {@code BaseCommand.getCommandsForCompletion}. If a later ACF changes a method used here, the error is
 * caught on every completion, logged once, and ACF's own completion is left as it is; if it renames one of
 * the classes used here, {@link #register} already fails and Bukkit logs the failed listener registration.
 */
public final class AcfTabCompletions {

    private AcfTabCompletions() {
    }

    /**
     * Registers the completion fix for every root command of {@code manager}.
     *
     * <p>Call it once, after creating the manager, from the main thread. The listener is registered for
     * {@code plugin} and goes away with it when the plugin disables.
     *
     * @param plugin  the plugin owning the commands
     * @param manager the ACF manager holding them
     * @return the registered listener, for plugins that unregister listeners themselves
     */
    public static Listener register(Plugin plugin, PaperCommandManager manager) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(manager, "manager");
        Listener listener = new FormsListener(plugin, manager);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        return listener;
    }

    private static final class FormsListener implements Listener {

        private final Plugin plugin;
        private final PaperCommandManager manager;
        private final AtomicBoolean warned = new AtomicBoolean();

        private FormsListener(Plugin plugin, PaperCommandManager manager) {
            this.plugin = plugin;
            this.manager = manager;
        }

        /**
         * Runs after ACF's own handler ({@code NORMAL}) on the async completion thread, where ACF itself
         * already does the same reads and the same permission checks; Paper needs the result inside the
         * event, so there is no hopping to the main thread. Only an event ACF handled is touched: an
         * unhandled one falls back to synchronous completion and Paper ignores these completions.
         */
        @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
        public void onAsyncTabComplete(AsyncTabCompleteEvent event) {
            String buffer = event.getBuffer();
            // Same entry conditions as ACF's PaperAsyncTabCompleteHandler.
            if (!event.isHandled() || (!event.isCommand() && !buffer.startsWith("/"))) {
                return;
            }
            String line = buffer.startsWith("/") ? buffer.substring(1) : buffer;
            int space = line.indexOf(' ');
            if (space < 0) {
                return;
            }
            try {
                // Split exactly as ACF does, so double and trailing spaces yield the same arguments.
                complete(event, line.substring(0, space), line.substring(space + 1).split(" ", -1));
            } catch (RuntimeException | LinkageError e) {
                if (warned.compareAndSet(false, true)) {
                    plugin.getLogger().log(Level.WARNING,
                            "[PowerLib] Could not add the alternative subcommand forms to tab completion, ACF's own "
                                    + "completion is kept (logged once): ACF internals may have changed.", e);
                }
            }
        }

        private void complete(AsyncTabCompleteEvent event, String label, String[] args) {
            RootCommand root = manager.getRootCommand(label);
            if (root == null) {
                return;
            }
            CommandIssuer issuer = manager.getCommandIssuer(event.getSender());
            List<SubcommandForms.Candidate> candidates = new ArrayList<>();
            for (Map.Entry<String, RegisteredCommand> entry : root.getSubCommands().entries()) {
                RegisteredCommand command = entry.getValue();
                candidates.add(new SubcommandForms.Candidate(entry.getKey(), command.getPrefSubCommand(),
                        () -> !command.isPrivate() && manager.hasPermission(issuer, command.getRequiredPermissions())));
            }
            List<String> current = event.getCompletions();
            List<String> result = SubcommandForms.complete(current, args, candidates);
            if (result != current) {
                event.setCompletions(result);
            }
        }
    }
}
