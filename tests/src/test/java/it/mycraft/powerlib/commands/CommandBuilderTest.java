package it.mycraft.powerlib.commands;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CommandBuilderTest {

    @Test
    void argsIntegerParsesValid() {
        Argument<Integer> arg = Args.integer("count");
        assertThat(arg.parse("42")).contains(42);
    }

    @Test
    void argsIntegerRejectsInvalid() {
        Argument<Integer> arg = Args.integer("count");
        assertThat(arg.parse("not-a-number")).isEmpty();
    }

    @Test
    void argsBoolHandlesBothCases() {
        assertThat(Args.bool("flag").parse("true")).contains(true);
        assertThat(Args.bool("flag").parse("FALSE")).contains(false);
    }

    @Test
    void contextStoresAndRetrievesValuesByType() {
        CommandContext ctx = new CommandContext().put("count", 10).put("name", "alice");
        assertThat(ctx.get("count", Integer.class)).contains(10);
        assertThat(ctx.get("name", String.class)).contains("alice");
        assertThat(ctx.get("count", String.class)).isEmpty();
    }

    @Test
    void argsStringPassesRawThroughAndRejectsNull() {
        Argument<String> arg = Args.string("name");
        assertThat(arg.getName()).isEqualTo("name");
        assertThat(arg.getType()).isEqualTo(String.class);
        assertThat(arg.parse("hello")).contains("hello");
        assertThat(arg.parse(null)).isEmpty();
    }

    @Test
    void argsBoolRejectsNonBoolean() {
        assertThat(Args.bool("flag").parse("yes")).isEmpty();
    }

    @Test
    void argsCustomUsesSuppliedParser() {
        Argument<Integer> hex = Args.custom("hex", Integer.class,
                raw -> Optional.of(Integer.parseInt(raw, 16)));
        assertThat(hex.parse("ff")).contains(255);
    }

    @Test
    void argumentOptionalFlagIsOffByDefaultAndCanBeEnabled() {
        Argument<String> arg = Args.string("name");
        assertThat(arg.isOptional()).isFalse();
        assertThat(arg.optional().isOptional()).isTrue();
    }
}
