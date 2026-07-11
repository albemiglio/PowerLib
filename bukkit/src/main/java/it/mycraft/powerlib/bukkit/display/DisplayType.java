package it.mycraft.powerlib.bukkit.display;

import java.util.Locale;

public enum DisplayType {
    CHAT,
    ACTION_BAR,
    TITLE,
    BOSS_BAR;

    public static DisplayType parse(String raw, DisplayType fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        String normalized = raw.trim()
                .toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');
        if ("ACTIONBAR".equals(normalized)) {
            normalized = "ACTION_BAR";
        } else if ("BOSSBAR".equals(normalized)) {
            normalized = "BOSS_BAR";
        }
        try {
            return DisplayType.valueOf(normalized);
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }
}
