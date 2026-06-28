package it.mycraft.powerlib.commands;

import org.junit.jupiter.api.Test;

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
}
