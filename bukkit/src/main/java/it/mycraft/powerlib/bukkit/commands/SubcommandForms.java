package it.mycraft.powerlib.bukkit.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.BooleanSupplier;

/**
 * The completion logic behind {@link AcfTabCompletions}, kept free of ACF and Bukkit types so it can be
 * tested on its own.
 *
 * <p>ACF registers every form of {@code @Subcommand("resign|dimettiti")} as its own key, but while
 * completing it adds each matching key's <em>preferred</em> form (the first one) instead of the key,
 * without filtering it against what was typed. This recomputes the same walk over the real keys: it adds
 * the forms ACF left out and drops the preferred forms ACF added for keys the typed text does not match.
 * Anything else in the list — argument completions, other plugins' suggestions — is left untouched.
 */
final class SubcommandForms {

    private SubcommandForms() {
    }

    /**
     * One subcommand key as ACF registered it.
     *
     * @param key       the key, possibly several words ({@code "offers crea"})
     * @param preferred the preferred form ACF puts in the tab list for this key
     * @param visible   whether the sender may see it; evaluated only for keys that match, as ACF does
     */
    record Candidate(String key, String preferred, BooleanSupplier visible) {
    }

    /**
     * Fixes ACF's completion list for the argument being typed.
     *
     * @param current    the list ACF produced
     * @param args       the arguments after the root label, split on single spaces keeping empty ones, as
     *                   ACF splits them; the last one is the argument being typed
     * @param candidates every subcommand key of the root command
     * @return {@code current} itself when nothing changes, otherwise a new list
     */
    static List<String> complete(List<String> current, String[] args, Iterable<Candidate> candidates) {
        int index = args.length - 1;
        String typed = args[index].toLowerCase(Locale.ROOT);
        String parent = index == 0 ? "" : String.join(" ", Arrays.copyOf(args, index)).toLowerCase(Locale.ROOT) + " ";

        Set<String> matching = new LinkedHashSet<>();
        Set<String> preferredMismatches = new HashSet<>();
        for (Candidate candidate : candidates) {
            String key = candidate.key();
            // "__default" and "__catchunknown" are ACF's internal keys, not names a player can type.
            if (key.startsWith("__") || !key.startsWith(parent)) {
                continue;
            }
            String[] words = key.split(" ");
            if (words.length <= index || !words[index].startsWith(typed) || !candidate.visible().getAsBoolean()) {
                continue;
            }
            matching.add(words[index]);
            String[] preferred = candidate.preferred().split(" ");
            if (preferred.length > index && !preferred[index].startsWith(typed)) {
                preferredMismatches.add(preferred[index]);
            }
        }
        if (matching.isEmpty()) {
            return current;
        }

        List<String> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (String completion : current) {
            String lower = completion.toLowerCase(Locale.ROOT);
            if (!preferredMismatches.contains(lower)) {
                result.add(completion);
                seen.add(lower);
            }
        }
        // Keys are lower case; a suggestion already present with other casing is not added twice.
        for (String form : matching) {
            if (seen.add(form)) {
                result.add(form);
            }
        }
        return result.equals(current) ? current : result;
    }
}
