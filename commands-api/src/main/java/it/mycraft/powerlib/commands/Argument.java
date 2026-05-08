package it.mycraft.powerlib.commands;

import java.util.Optional;

public final class Argument<T> {

    private final String name;
    private final java.util.function.Function<String, Optional<T>> parser;
    private final Class<T> type;
    private boolean optional;

    public Argument(String name, Class<T> type, java.util.function.Function<String, Optional<T>> parser) {
        this.name = name;
        this.type = type;
        this.parser = parser;
    }

    public Argument<T> optional() { this.optional = true; return this; }

    public String getName() { return name; }
    public Class<T> getType() { return type; }
    public boolean isOptional() { return optional; }
    public Optional<T> parse(String raw) { return parser.apply(raw); }
}
