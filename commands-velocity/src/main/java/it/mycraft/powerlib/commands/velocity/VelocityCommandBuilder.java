package it.mycraft.powerlib.commands.velocity;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import it.mycraft.powerlib.commands.Argument;
import it.mycraft.powerlib.commands.CommandBuilder;
import it.mycraft.powerlib.commands.CommandContext;

import java.util.Collections;
import java.util.List;

public final class VelocityCommandBuilder extends CommandBuilder {

    private VelocityCommandBuilder(String name) { super(name); }
    public static VelocityCommandBuilder create(String name) { return new VelocityCommandBuilder(name); }

    @Override
    protected CommandBuilder newChild(String name) { return new VelocityCommandBuilder(name); }

    @Override
    public void register(Object proxyRaw) {
        if (!(proxyRaw instanceof ProxyServer)) {
            throw new IllegalArgumentException("Velocity registration expects a ProxyServer");
        }
        ProxyServer proxy = (ProxyServer) proxyRaw;
        CommandManager manager = proxy.getCommandManager();
        CommandMeta meta = manager.metaBuilder(getName()).plugin(this).build();
        manager.register(meta, new VelocityAdapter(this));
    }

    private static class VelocityAdapter implements SimpleCommand {
        private final CommandBuilder builder;
        VelocityAdapter(CommandBuilder builder) { this.builder = builder; }

        @Override
        public void execute(Invocation invocation) {
            VelocityCommandSender sender = new VelocityCommandSender(invocation.source());
            if (builder.getPermission() != null && !sender.hasPermission(builder.getPermission())) {
                sender.sendMessage("§cNo permission.");
                return;
            }
            CommandContext ctx = parseArgs(invocation.arguments());
            if (builder.getExecutor() != null) builder.getExecutor().accept(sender, ctx);
        }

        @Override
        public List<String> suggest(Invocation invocation) {
            if (builder.getTabComplete() == null) return Collections.emptyList();
            return builder.getTabComplete().apply(new VelocityCommandSender(invocation.source()), parseArgs(invocation.arguments()));
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
