package it.mycraft.powerlib.minimessage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class MiniMessageSupport {

    private static final MiniMessage MINI = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.legacySection();

    private MiniMessageSupport() {}

    public static Component parse(String input) {
        if (input == null) return Component.empty();
        return MINI.deserialize(input);
    }

    public static String parseLegacy(String input) {
        if (input == null) return "";
        return LEGACY.serialize(parse(input));
    }
}
