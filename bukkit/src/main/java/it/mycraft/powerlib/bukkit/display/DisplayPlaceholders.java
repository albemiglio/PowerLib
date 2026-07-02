package it.mycraft.powerlib.bukkit.display;

import java.util.Map;

public final class DisplayPlaceholders {

    private DisplayPlaceholders() {
    }

    public static String apply(String input, Map<String, ?> placeholders) {
        if (input == null || input.isEmpty() || placeholders == null || placeholders.isEmpty()) {
            return input == null ? "" : input;
        }
        String result = input;
        for (Map.Entry<String, ?> entry : placeholders.entrySet()) {
            String key = entry.getKey();
            if (key == null || key.isEmpty()) {
                continue;
            }
            String value = String.valueOf(entry.getValue());
            result = result.replace(token(key), value);
        }
        return result;
    }

    private static String token(String key) {
        if (key.startsWith("%") && key.endsWith("%")) {
            return key;
        }
        return "%" + key + "%";
    }
}
