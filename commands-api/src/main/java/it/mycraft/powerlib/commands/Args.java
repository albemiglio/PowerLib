package it.mycraft.powerlib.commands;

import java.util.Optional;
import java.util.function.Function;

public final class Args {
    private Args() {}

    public static Argument<String> string(String name) {
        return new Argument<>(name, String.class, Optional::ofNullable);
    }

    public static Argument<Integer> integer(String name) {
        return new Argument<>(name, Integer.class, raw -> {
            try { return Optional.of(Integer.parseInt(raw)); }
            catch (NumberFormatException e) { return Optional.empty(); }
        });
    }

    public static Argument<Boolean> bool(String name) {
        return new Argument<>(name, Boolean.class, raw -> {
            if ("true".equalsIgnoreCase(raw)) return Optional.of(Boolean.TRUE);
            if ("false".equalsIgnoreCase(raw)) return Optional.of(Boolean.FALSE);
            return Optional.empty();
        });
    }

    public static <T> Argument<T> custom(String name, Class<T> type, Function<String, Optional<T>> parser) {
        return new Argument<>(name, type, parser);
    }
}
