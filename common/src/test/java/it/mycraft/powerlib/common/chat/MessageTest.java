package it.mycraft.powerlib.common.chat;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageTest {

    private static String plain(net.kyori.adventure.text.Component c) {
        return PlainTextComponentSerializer.plainText().serialize(c);
    }

    @Test
    void legacyAmpersandIsParsedAsStyleNotLiteralText() {
        Message msg = new Message("&aHello");
        assertEquals("Hello", plain(msg.getComponent()),
                "legacy '&a' must become a green style, not literal text");
    }

    @Test
    void placeholderIsReplacedInsideStyledComponent() {
        Message msg = new Message("&aHello {name}").addPlaceHolder("{name}", "Bob");
        assertEquals("Hello Bob", plain(msg.getComponent()),
                "placeholder must be replaced even when the text is colored/styled");
    }

    @Test
    void multiLineLegacyIsParsedPerLine() {
        Message msg = new Message("&aLine1", "&bLine2");
        assertEquals("Line1", plain(msg.getComponentList().get(0)));
        assertEquals("Line2", plain(msg.getComponentList().get(1)));
    }

    @Test
    void miniMessageMarkupIsParsed() {
        Message msg = Message.mini("<green>Hi</green>");
        assertEquals("Hi", plain(msg.getComponent()),
                "MiniMessage markup must be parsed into components");
    }
}
