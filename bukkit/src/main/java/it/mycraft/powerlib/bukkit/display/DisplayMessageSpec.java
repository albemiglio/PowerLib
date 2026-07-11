package it.mycraft.powerlib.bukkit.display;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Locale;

public final class DisplayMessageSpec {

    private final boolean enabled;
    private final DisplayType type;
    private final String message;
    private final String title;
    private final String subtitle;
    private final int fadeInTicks;
    private final int stayTicks;
    private final int fadeOutTicks;
    private final BarColor barColor;
    private final BarStyle barStyle;
    private final long durationTicks;
    private final double progress;

    private DisplayMessageSpec(Builder builder) {
        this.enabled = builder.enabled;
        this.type = builder.type;
        this.message = builder.message;
        this.title = builder.title;
        this.subtitle = builder.subtitle;
        this.fadeInTicks = builder.fadeInTicks;
        this.stayTicks = builder.stayTicks;
        this.fadeOutTicks = builder.fadeOutTicks;
        this.barColor = builder.barColor;
        this.barStyle = builder.barStyle;
        this.durationTicks = builder.durationTicks;
        this.progress = clampProgress(builder.progress);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static DisplayMessageSpec from(ConfigurationSection section) {
        return from(section, DisplayType.CHAT);
    }

    public static DisplayMessageSpec from(ConfigurationSection section, DisplayType fallbackType) {
        Builder builder = builder();
        if (section == null) {
            return builder.enabled(false).type(fallbackType).build();
        }

        builder.enabled(section.getBoolean("enabled", true));
        builder.type(DisplayType.parse(section.getString("type"), fallbackType));
        builder.message(section.getString("message", section.getString("text", "")));
        builder.title(section.getString("title", ""));
        builder.subtitle(section.getString("subtitle", ""));
        builder.fadeInTicks(Math.max(0, section.getInt("fade-in", section.getInt("fadeIn", 10))));
        builder.stayTicks(Math.max(0, section.getInt("stay", 40)));
        builder.fadeOutTicks(Math.max(0, section.getInt("fade-out", section.getInt("fadeOut", 10))));
        builder.barColor(enumValue(BarColor.class, section.getString("color"), BarColor.WHITE));
        builder.barStyle(enumValue(BarStyle.class, section.getString("style"), BarStyle.SOLID));
        builder.durationTicks(readDurationTicks(section));
        builder.progress(section.getDouble("progress", 1.0D));
        return builder.build();
    }

    public boolean enabled() {
        return enabled;
    }

    public DisplayType type() {
        return type;
    }

    public String message() {
        return message;
    }

    public String title() {
        return title;
    }

    public String subtitle() {
        return subtitle;
    }

    public int fadeInTicks() {
        return fadeInTicks;
    }

    public int stayTicks() {
        return stayTicks;
    }

    public int fadeOutTicks() {
        return fadeOutTicks;
    }

    public BarColor barColor() {
        return barColor;
    }

    public BarStyle barStyle() {
        return barStyle;
    }

    public long durationTicks() {
        return durationTicks;
    }

    public double progress() {
        return progress;
    }

    private static long readDurationTicks(ConfigurationSection section) {
        if (section.contains("duration-ticks")) {
            return Math.max(0L, section.getLong("duration-ticks"));
        }
        if (section.contains("durationTicks")) {
            return Math.max(0L, section.getLong("durationTicks"));
        }
        if (section.contains("duration")) {
            return Math.max(0L, section.getLong("duration")) * 20L;
        }
        if (section.contains("duration-seconds")) {
            return Math.max(0L, section.getLong("duration-seconds")) * 20L;
        }
        return 0L;
    }

    private static double clampProgress(double progress) {
        return Math.max(0.0D, Math.min(1.0D, progress));
    }

    private static <T extends Enum<T>> T enumValue(Class<T> type, String raw, T fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return Enum.valueOf(type, raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }

    public static final class Builder {
        private boolean enabled = true;
        private DisplayType type = DisplayType.CHAT;
        private String message = "";
        private String title = "";
        private String subtitle = "";
        private int fadeInTicks = 10;
        private int stayTicks = 40;
        private int fadeOutTicks = 10;
        private BarColor barColor = BarColor.WHITE;
        private BarStyle barStyle = BarStyle.SOLID;
        private long durationTicks;
        private double progress = 1.0D;

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder type(DisplayType type) {
            this.type = type == null ? DisplayType.CHAT : type;
            return this;
        }

        public Builder message(String message) {
            this.message = message == null ? "" : message;
            return this;
        }

        public Builder title(String title) {
            this.title = title == null ? "" : title;
            return this;
        }

        public Builder subtitle(String subtitle) {
            this.subtitle = subtitle == null ? "" : subtitle;
            return this;
        }

        public Builder fadeInTicks(int fadeInTicks) {
            this.fadeInTicks = Math.max(0, fadeInTicks);
            return this;
        }

        public Builder stayTicks(int stayTicks) {
            this.stayTicks = Math.max(0, stayTicks);
            return this;
        }

        public Builder fadeOutTicks(int fadeOutTicks) {
            this.fadeOutTicks = Math.max(0, fadeOutTicks);
            return this;
        }

        public Builder barColor(BarColor barColor) {
            this.barColor = barColor == null ? BarColor.WHITE : barColor;
            return this;
        }

        public Builder barStyle(BarStyle barStyle) {
            this.barStyle = barStyle == null ? BarStyle.SOLID : barStyle;
            return this;
        }

        public Builder durationTicks(long durationTicks) {
            this.durationTicks = Math.max(0L, durationTicks);
            return this;
        }

        public Builder progress(double progress) {
            this.progress = progress;
            return this;
        }

        public DisplayMessageSpec build() {
            return new DisplayMessageSpec(this);
        }
    }
}
