package it.mycraft.powerlib.bukkit.commands;

import it.mycraft.powerlib.bukkit.commands.SubcommandForms.Candidate;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class SubcommandFormsTest {

    private static Candidate visible(String key, String preferred) {
        return new Candidate(key, preferred, () -> true);
    }

    private static Candidate hidden(String key, String preferred) {
        return new Candidate(key, preferred, () -> false);
    }

    /** Keys as ACF registers {@code @Subcommand("resign|dimettiti")} and {@code @Subcommand("create|crea")}. */
    private static final List<Candidate> ROOT = List.of(
            visible("resign", "resign"),
            visible("dimettiti", "resign"),
            visible("create", "create"),
            visible("crea", "create"));

    @Test
    void emptyArgumentOffersEveryForm() {
        List<String> acf = List.of("resign", "create");
        assertEquals(List.of("resign", "create", "dimettiti", "crea"),
                SubcommandForms.complete(acf, new String[]{""}, ROOT));
    }

    @Test
    void secondFormPrefixDropsThePreferredOneAcfAdded() {
        // ACF matches the key "dimettiti" but adds "resign", which the player did not type.
        List<String> acf = List.of("resign");
        assertEquals(List.of("dimettiti"), SubcommandForms.complete(acf, new String[]{"dim"}, ROOT));
    }

    @Test
    void sharedPrefixKeepsBothForms() {
        List<String> acf = List.of("create");
        assertEquals(List.of("create", "crea"), SubcommandForms.complete(acf, new String[]{"cr"}, ROOT));
    }

    @Test
    void classLevelSubcommandsCompleteTheSecondWord() {
        // @Subcommand("offers|offerte") on the class, @Subcommand("create|crea") on the method.
        List<Candidate> nested = List.of(
                visible("offers create", "offers create"),
                visible("offers crea", "offers create"),
                visible("offerte create", "offers create"),
                visible("offerte crea", "offers create"));
        List<String> acf = List.of("create");
        assertEquals(List.of("create", "crea"),
                SubcommandForms.complete(acf, new String[]{"offerte", "c"}, nested));
    }

    @Test
    void hiddenFormsAreNeitherAddedNorUsedToDrop() {
        List<Candidate> keys = List.of(visible("reload", "reload"), hidden("ricarica", "reload"));
        List<String> acf = List.of("reload");
        assertSame(acf, SubcommandForms.complete(acf, new String[]{""}, keys),
                "a private or unpermitted form must not show up, nor change the list");
    }

    @Test
    void argumentCompletionsAreLeftAlone() {
        // "/cmd hire St<tab>": no subcommand key lives under "hire ", so the player names stay as they are.
        List<String> acf = List.of("Steve", "Stella");
        assertSame(acf, SubcommandForms.complete(acf, new String[]{"hire", "St"}, ROOT));
    }

    @Test
    void internalKeysAreIgnored() {
        List<Candidate> keys = List.of(visible("__default", "__default"), visible("__catchunknown", "__catchunknown"));
        List<String> acf = List.of("1", "2");
        assertSame(acf, SubcommandForms.complete(acf, new String[]{""}, keys));
    }

    @Test
    void typedTextIsMatchedCaseInsensitively() {
        List<String> acf = List.of("resign");
        assertEquals(List.of("dimettiti"), SubcommandForms.complete(acf, new String[]{"DIM"}, ROOT));
    }

    @Test
    void preferredFormShorterThanTheArgumentIsNotDropped() {
        List<Candidate> keys = List.of(visible("offers create", "offers create"), visible("offers crea", "offers"));
        List<String> acf = List.of("create");
        assertEquals(List.of("create", "crea"), SubcommandForms.complete(acf, new String[]{"offers", "c"}, keys));
    }

    @Test
    void doubleSpaceMatchesNoKey() {
        // "/cmd offers  c": split like ACF, the empty middle argument means no key "offers crea" matches.
        List<Candidate> keys = List.of(visible("offers create", "offers create"), visible("offers crea", "offers create"));
        List<String> acf = List.of("x");
        assertSame(acf, SubcommandForms.complete(acf, new String[]{"offers", "", "c"}, keys));
    }

    @Test
    void anAlreadyFixedListIsReturnedAsIs() {
        // Registering the fix twice, or next to a plugin's own copy, must not change the list again.
        List<String> fixed = SubcommandForms.complete(List.of("resign", "create"), new String[]{""}, ROOT);
        assertSame(fixed, SubcommandForms.complete(fixed, new String[]{""}, ROOT));
    }

    @Test
    void existingSuggestionWithOtherCasingIsNotDuplicated() {
        List<String> acf = List.of("Resign", "create");
        assertEquals(List.of("Resign", "create", "dimettiti", "crea"),
                SubcommandForms.complete(acf, new String[]{""}, ROOT));
    }

    @Test
    void visibilityIsOnlyEvaluatedForMatchingKeys() {
        List<Candidate> keys = List.of(
                visible("resign", "resign"),
                new Candidate("dimettiti", "resign", () -> {
                    throw new AssertionError("permission checked for a key that does not match");
                }));
        List<String> acf = List.of("resign");
        assertSame(acf, SubcommandForms.complete(acf, new String[]{"res"}, keys));
    }
}
