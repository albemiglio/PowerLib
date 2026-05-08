package it.mycraft.powerlib.commands;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class CommandBuilder {

    protected final String name;
    protected String description = "";
    protected String permission;
    protected final List<Argument<?>> arguments = new ArrayList<>();
    protected BiConsumer<CommandSender, CommandContext> executor;
    protected BiFunction<CommandSender, CommandContext, List<String>> tabComplete;
    protected final Map<String, CommandBuilder> subcommands = new LinkedHashMap<>();

    protected CommandBuilder(String name) {
        this.name = name;
    }

    public CommandBuilder description(String desc) { this.description = desc; return this; }
    public CommandBuilder permission(String perm) { this.permission = perm; return this; }
    public CommandBuilder argument(Argument<?> arg) { this.arguments.add(arg); return this; }
    public CommandBuilder executor(BiConsumer<CommandSender, CommandContext> exec) { this.executor = exec; return this; }
    public CommandBuilder tabComplete(BiFunction<CommandSender, CommandContext, List<String>> tab) { this.tabComplete = tab; return this; }
    public CommandBuilder subcommand(String name, Consumer<CommandBuilder> config) {
        CommandBuilder sub = newChild(name);
        config.accept(sub);
        subcommands.put(name, sub);
        return this;
    }

    /** Subclasses produce same-platform child builders. */
    protected abstract CommandBuilder newChild(String name);

    /** Platform-specific registration entrypoint. Receives a platform plugin/proxy handle. */
    public abstract void register(Object pluginOrProxy);

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPermission() { return permission; }
    public List<Argument<?>> getArguments() { return arguments; }
    public BiConsumer<CommandSender, CommandContext> getExecutor() { return executor; }
    public BiFunction<CommandSender, CommandContext, List<String>> getTabComplete() { return tabComplete; }
    public Map<String, CommandBuilder> getSubcommands() { return subcommands; }
}
