package it.mycraft.powerlib.common.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of a config default merge operation.
 */
public final class ConfigUpdateResult {

    private final String file;
    private final boolean changed;
    private final boolean written;
    private final boolean dryRun;
    private final String backupPath;
    private final List<String> addedPaths;
    private final List<String> warnings;

    private ConfigUpdateResult(Builder builder) {
        this.file = builder.file;
        this.changed = builder.changed;
        this.written = builder.written;
        this.dryRun = builder.dryRun;
        this.backupPath = builder.backupPath;
        this.addedPaths = Collections.unmodifiableList(new ArrayList<>(builder.addedPaths));
        this.warnings = Collections.unmodifiableList(new ArrayList<>(builder.warnings));
    }

    public static Builder builder(String file) {
        return new Builder(file);
    }

    public String file() {
        return file;
    }

    public boolean changed() {
        return changed;
    }

    public boolean written() {
        return written;
    }

    public boolean dryRun() {
        return dryRun;
    }

    public String backupPath() {
        return backupPath;
    }

    public List<String> addedPaths() {
        return addedPaths;
    }

    public List<String> warnings() {
        return warnings;
    }

    public static final class Builder {
        private final String file;
        private boolean changed;
        private boolean written;
        private boolean dryRun;
        private String backupPath;
        private final List<String> addedPaths = new ArrayList<>();
        private final List<String> warnings = new ArrayList<>();

        private Builder(String file) {
            this.file = file;
        }

        public Builder changed(boolean changed) {
            this.changed = changed;
            return this;
        }

        public Builder written(boolean written) {
            this.written = written;
            return this;
        }

        public Builder dryRun(boolean dryRun) {
            this.dryRun = dryRun;
            return this;
        }

        public Builder backupPath(String backupPath) {
            this.backupPath = backupPath;
            return this;
        }

        public Builder addPath(String path) {
            if (path != null && !path.isBlank()) {
                this.addedPaths.add(path);
            }
            return this;
        }

        public Builder addedPaths(List<String> paths) {
            if (paths != null) {
                paths.forEach(this::addPath);
            }
            return this;
        }

        public Builder warning(String warning) {
            if (warning != null && !warning.isBlank()) {
                this.warnings.add(warning);
            }
            return this;
        }

        public Builder warnings(List<String> warnings) {
            if (warnings != null) {
                warnings.forEach(this::warning);
            }
            return this;
        }

        public ConfigUpdateResult build() {
            return new ConfigUpdateResult(this);
        }
    }
}
