package it.mycraft.powerlib.commands.bukkit;

import it.mycraft.powerlib.commands.Argument;
import it.mycraft.powerlib.commands.CommandBuilder;
import it.mycraft.powerlib.commands.CommandContext;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

final class BukkitCommandFallback {

    private BukkitCommandFallback() {}

    static void register(Plugin plugin, CommandBuilder builder) {
        BukkitCommand cmd = new BukkitCommand(builder.getName()) {
            @Override
            public boolean execute(org.bukkit.command.CommandSender bukkitSender, String label, String[] args) {
                BukkitCommandSender sender = new BukkitCommandSender(bukkitSender);
                if (builder.getPermission() != null && !sender.hasPermission(builder.getPermission())) {
                    sender.sendMessage("§cNo permission.");
                    return true;
                }
                CommandContext ctx = parseArgs(builder, args);
                if (builder.getExecutor() != null) {
                    builder.getExecutor().accept(sender, ctx);
                }
                return true;
            }

            @Override
            public List<String> tabComplete(org.bukkit.command.CommandSender bukkitSender, String alias, String[] args) {
                if (builder.getTabComplete() == null) return Collections.emptyList();
                return builder.getTabComplete().apply(new BukkitCommandSender(bukkitSender), parseArgs(builder, args));
            }
        };
        cmd.setDescription(builder.getDescription());
        if (builder.getPermission() != null) cmd.setPermission(builder.getPermission());

        getCommandMap().register(plugin.getName().toLowerCase(), cmd);
    }

    private static CommandContext parseArgs(CommandBuilder builder, String[] raw) {
        CommandContext ctx = new CommandContext();
        List<Argument<?>> args = builder.getArguments();
        for (int i = 0; i < args.size() && i < raw.length; i++) {
            Argument<?> arg = args.get(i);
            arg.parse(raw[i]).ifPresent(v -> ctx.put(arg.getName(), v));
        }
        return ctx;
    }

    private static CommandMap getCommandMap() {
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            return (CommandMap) field.get(Bukkit.getServer());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot access Bukkit CommandMap", e);
        }
    }
}
