package it.mycraft.powerlib.minimessage;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MiniMessageSupportTest {

    @Test
    void parsePlainTextProducesSameText() {
        assertThat(MiniMessageSupport.parse("hello").toString()).contains("hello");
    }

    @Test
    void parseLegacyHandlesGradient() {
        String legacy = MiniMessageSupport.parseLegacy("<gradient:red:gold>Ciao</gradient>");
        // Gradient splits each character with its own color code, so plain "Ciao" won't appear
        // contiguously. Instead verify the legacy section symbol is present and all characters are.
        assertThat(legacy).contains("§");
        assertThat(legacy).contains("C");
        assertThat(legacy).contains("i");
        assertThat(legacy).contains("a");
        assertThat(legacy).contains("o");
    }

    @Test
    void parseLegacyOnNullReturnsEmpty() {
        assertThat(MiniMessageSupport.parseLegacy(null)).isEmpty();
    }
}
