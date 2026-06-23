package it.mycraft.powerlib.common.configuration;

/**
 * Helpers to keep user config files in sync with packaged defaults across plugin updates.
 *
 * @author AlbeMiglio
 */
public final class ConfigMigration {

    private ConfigMigration() {
    }

    /**
     * Recursively copies into {@code target} every key present in {@code defaults} but missing
     * in {@code target}, without touching values the user already set.
     *
     * @param target   the user's (possibly outdated) configuration
     * @param defaults the packaged default configuration
     * @return true if at least one key was added
     */
    public static boolean backfill(Configuration target, Configuration defaults) {
        if (target == null || defaults == null) {
            return false;
        }
        boolean changed = false;
        for (String key : defaults.getKeys()) {
            Object defaultValue = defaults.get(key);
            if (defaultValue instanceof Configuration) {
                Object targetValue = target.get(key);
                if (targetValue instanceof Configuration) {
                    changed |= backfill((Configuration) targetValue, (Configuration) defaultValue);
                } else {
                    target.set(key, defaultValue);
                    changed = true;
                }
            } else if (!target.contains(key)) {
                target.set(key, defaultValue);
                changed = true;
            }
        }
        return changed;
    }
}
