package it.mycraft.powerlib.commands.bungee;

import it.mycraft.powerlib.commands.Argument;
import it.mycraft.powerlib.commands.CommandBuilder;
import it.mycraft.powerlib.commands.CommandContext;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Collections;
import java.util.List;

public final class BungeeCommandBuilder extends CommandBuilder {

    private BungeeCommandBuilder(String name) { super(name); }

    public static BungeeCommandBuilder create(String name) { return new BungeeCommandBuilder(name); }

    @Override
    protected CommandBuilder newChild(String name) { return new BungeeCommandBuilder(name); }

    @Override
    public void register(Object pluginRaw) {
        if (!(pluginRaw instanceof Plugin)) {
            throw new IllegalArgumentException("Bungee registration expects a net.md_5.bungee.api.plugin.Plugin");
        }
        Plugin plugin = (Plugin) pluginRaw;
        Command cmd = new BungeeAdapter(this);
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, cmd);
    }

    private static class BungeeAdapter extends Command implements TabExecutor {
        private final CommandBuilder builder;
        BungeeAdapter(CommandBuilder builder) {
            super(builder.getName(), builder.getPermission());
            this.builder = builder;
        }

        @Override
        public void execute(net.md_5.bungee.api.CommandSender bungeeSender, String[] args) {
            BungeeCommandSender sender = new BungeeCommandSender(bungeeSender);
            if (builder.getPermission() != null && !sender.hasPermission(builder.getPermission())) {
                sender.sendMessage("§cNo permission.");
                return;
            }
            CommandContext ctx = parseArgs(args);
            if (builder.getExecutor() != null) builder.getExecutor().accept(sender, ctx);
        }

        @Override
        public Iterable<String> onTabComplete(net.md_5.bungee.api.CommandSender bungeeSender, String[] args) {
            if (builder.getTabComplete() == null) return Collections.emptyList();
            return builder.getTabComplete().apply(new BungeeCommandSender(bungeeSender), parseArgs(args));
        }

        private CommandContext parseArgs(String[] raw) {
            CommandContext ctx = new CommandContext();
            List<Argument<?>> args = builder.getArguments();
            for (int i = 0; i < args.size() && i < raw.length; i++) {
                Argument<?> arg = args.get(i);
                arg.parse(raw[i]).ifPresent(v -> ctx.put(arg.getName(), v));
            }
            return ctx;
        }
    }
}
