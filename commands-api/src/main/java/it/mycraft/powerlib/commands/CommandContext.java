package it.mycraft.powerlib.commands;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class CommandContext {

    private final Map<String, Object> values = new LinkedHashMap<>();

    public CommandContext put(String name, Object value) {
        values.put(name, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String name, Class<T> type) {
        Object v = values.get(name);
        if (v == null || !type.isInstance(v)) return Optional.empty();
        return Optional.of((T) v);
    }

    public Map<String, Object> all() {
        return java.util.Collections.unmodifiableMap(values);
    }
}
