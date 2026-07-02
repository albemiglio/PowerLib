package it.mycraft.powerlib.common.configuration;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Adds missing default keys to a user's YAML file <b>as text</b>, so comments, blank lines and
 * existing values survive byte-for-byte. This is the comment-preserving alternative to
 * load-&gt;mutate-&gt;dump, which nukes every comment.
 *
 * <p>Placement: a missing top-level key is appended at the end of the file; a missing key whose
 * parent section already exists is inserted at the end of that section's block; a missing key whose
 * parent section is itself absent is appended as a new section. Only the top level and one level of
 * nesting are placed surgically in-line — deeper additions fall back to appending their whole
 * top-level branch, which is correct (values + comments preserved) if not minimal.
 *
 * @author AlbeMiglio
 */
final class ConfigTextMerge {

    private static final String INDENT = "  ";

    private ConfigTextMerge() {
    }

    /**
     * @param userText the user's current file contents
     * @param defaults the packaged default configuration
     * @return {@code userText} with every genuinely-missing default key inserted; the original text
     * is otherwise left untouched. Returns {@code userText} unchanged when nothing is missing.
     */
    static String merge(String userText, Configuration defaults) {
        if (defaults == null) {
            return userText;
        }
        Configuration user = ConfigurationProvider.getProvider(YamlConfiguration.class).load(userText);

        List<String> lines = new ArrayList<>(splitKeepStructure(userText));

        // Pass 1: fill missing keys inside sections that already exist in the user file.
        for (String section : defaults.getKeys()) {
            Object defVal = defaults.get(section);
            if (!(defVal instanceof Configuration) || !(user.get(section) instanceof Configuration)) {
                continue;
            }
            Configuration defSection = (Configuration) defVal;
            Configuration userSection = (Configuration) user.get(section);
            for (String child : defSection.getKeys()) {
                if (userSection.contains(child)) {
                    continue;
                }
                String rendered = renderEntry(child, defSection.get(child), 1);
                insertIntoSection(lines, section, rendered);
            }
        }

        // Pass 2: append missing top-level keys (scalars, lists, or whole absent sections).
        StringBuilder appended = new StringBuilder();
        for (String key : defaults.getKeys()) {
            if (user.contains(key)) {
                continue;
            }
            appended.append(renderEntry(key, defaults.get(key), 0));
        }

        boolean trailingNewline = userText.endsWith("\n");
        StringBuilder out = new StringBuilder(String.join("\n", lines));
        if (trailingNewline && out.length() > 0 && out.charAt(out.length() - 1) != '\n') {
            out.append('\n');
        }
        if (appended.length() > 0) {
            if (out.length() > 0 && out.charAt(out.length() - 1) != '\n') {
                out.append('\n');
            }
            out.append(appended);
        }
        return out.toString();
    }

    /**
     * Splits into lines, dropping the single trailing empty element produced by a final newline so
     * that re-joining with "\n" and re-appending keeps the file structure identical.
     */
    private static List<String> splitKeepStructure(String text) {
        List<String> lines = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                lines.add(text.substring(start, i));
                start = i + 1;
            }
        }
        // trailing content after the last '\n' (or the whole string if no newline)
        if (start < text.length()) {
            lines.add(text.substring(start));
        } else if (text.isEmpty()) {
            // keep an empty file empty
            return lines;
        }
        return lines;
    }

    /**
     * Inserts {@code rendered} (already indented, newline-terminated) at the end of the block that
     * belongs to top-level section {@code section}. The block ends at the first later line whose
     * indentation is at column 0 (a sibling/next top-level key or comment attached to it). Trailing
     * blank lines inside the block are kept below the insertion so spacing is preserved.
     */
    private static void insertIntoSection(List<String> lines, String section, String rendered) {
        int header = indexOfTopLevelKey(lines, section);
        if (header < 0) {
            return; // section not literally present as a header; pass 2 handles it if fully absent
        }
        int insertAt = lines.size();
        for (int i = header + 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (isTopLevelContent(line)) {
                insertAt = i;
                break;
            }
        }
        // back up over trailing blank lines so the new key sits against the last real child
        while (insertAt - 1 > header && lines.get(insertAt - 1).trim().isEmpty()) {
            insertAt--;
        }
        for (String l : stripTrailingNewline(rendered).split("\n", -1)) {
            lines.add(insertAt++, l);
        }
    }

    private static int indexOfTopLevelKey(List<String> lines, String key) {
        String prefix = key + ":";
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!line.isEmpty() && line.charAt(0) != ' ' && line.charAt(0) != '\t' && line.charAt(0) != '#'
                    && (line.equals(key + ":") || line.startsWith(prefix + " ") || line.startsWith(prefix))) {
                // guard against "namexyz:" matching "name"
                if (line.equals(key + ":") || line.startsWith(prefix)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /** A line that starts a new top-level entry (key or its leading comment), i.e. column-0 non-blank. */
    private static boolean isTopLevelContent(String line) {
        return !line.isEmpty() && line.charAt(0) != ' ' && line.charAt(0) != '\t';
    }

    /**
     * Renders a single YAML entry (key + value) at the given indent depth using snakeyaml, so
     * scalars, lists and nested maps all serialise correctly. Returns newline-terminated text.
     */
    private static String renderEntry(String key, Object value, int depth) {
        Object plain = toPlain(value);
        Map<String, Object> single = new LinkedHashMap<>();
        single.put(key, plain);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        String dumped = new Yaml(options).dump(single); // e.g. "version: 2\n" or "db:\n  host: localhost\n"

        if (depth == 0) {
            return dumped;
        }
        String pad = repeat(INDENT, depth);
        StringBuilder sb = new StringBuilder();
        for (String l : stripTrailingNewline(dumped).split("\n", -1)) {
            sb.append(l.isEmpty() ? "" : pad + l).append('\n');
        }
        return sb.toString();
    }

    /** Unwraps our {@link Configuration} back into plain maps so snakeyaml can dump it. */
    private static Object toPlain(Object value) {
        if (value instanceof Configuration) {
            Configuration c = (Configuration) value;
            Map<String, Object> map = new LinkedHashMap<>();
            for (String k : c.getKeys()) {
                map.put(k, toPlain(c.get(k)));
            }
            return map;
        }
        return value;
    }

    private static String stripTrailingNewline(String s) {
        return s.endsWith("\n") ? s.substring(0, s.length() - 1) : s;
    }

    private static String repeat(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }
}
