package it.mycraft.powerlib.common.configuration;

/**
 * Lightweight options for safe config default merging.
 */
public final class ConfigUpdateOptions {

    private final boolean dryRun;
    private final boolean backup;
    private final boolean timestampedBackup;
    private final boolean validateAfterWrite;
    private final boolean atomicWrite;
    private final boolean logChanges;
    private final int maxBackups;

    private ConfigUpdateOptions(Builder builder) {
        this.dryRun = builder.dryRun;
        this.backup = builder.backup;
        this.timestampedBackup = builder.timestampedBackup;
        this.validateAfterWrite = builder.validateAfterWrite;
        this.atomicWrite = builder.atomicWrite;
        this.logChanges = builder.logChanges;
        this.maxBackups = Math.max(0, builder.maxBackups);
    }

    public static ConfigUpdateOptions defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean dryRun() {
        return dryRun;
    }

    public boolean backup() {
        return backup;
    }

    public boolean timestampedBackup() {
        return timestampedBackup;
    }

    public boolean validateAfterWrite() {
        return validateAfterWrite;
    }

    public boolean atomicWrite() {
        return atomicWrite;
    }

    public boolean logChanges() {
        return logChanges;
    }

    public int maxBackups() {
        return maxBackups;
    }

    public static final class Builder {
        private boolean dryRun;
        private boolean backup = true;
        private boolean timestampedBackup = true;
        private boolean validateAfterWrite = true;
        private boolean atomicWrite = true;
        private boolean logChanges = true;
        private int maxBackups = 5;

        private Builder() {
        }

        public Builder dryRun(boolean dryRun) {
            this.dryRun = dryRun;
            return this;
        }

        public Builder backup(boolean backup) {
            this.backup = backup;
            return this;
        }

        public Builder timestampedBackup(boolean timestampedBackup) {
            this.timestampedBackup = timestampedBackup;
            return this;
        }

        public Builder validateAfterWrite(boolean validateAfterWrite) {
            this.validateAfterWrite = validateAfterWrite;
            return this;
        }

        public Builder atomicWrite(boolean atomicWrite) {
            this.atomicWrite = atomicWrite;
            return this;
        }

        public Builder logChanges(boolean logChanges) {
            this.logChanges = logChanges;
            return this;
        }

        public Builder maxBackups(int maxBackups) {
            this.maxBackups = maxBackups;
            return this;
        }

        public ConfigUpdateOptions build() {
            return new ConfigUpdateOptions(this);
        }
    }
}
