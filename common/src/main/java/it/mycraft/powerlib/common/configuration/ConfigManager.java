package it.mycraft.powerlib.common.configuration;

import it.mycraft.powerlib.common.chat.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Platform-independent YAML config manager. Each platform supplies its data folder and plugin jar;
 * all loading/creating/saving logic lives here so the per-platform managers stay thin.
 *
 * @author AlbeMiglio
 */
public class ConfigManager {

    private final Map<String, Configuration> configs = new HashMap<>();
    private final File folder;
    private final File pluginJar;

    /**
     * @param dataFolder the plugin's data folder (created if missing)
     * @param pluginJar  the plugin jar to read packaged default resources from
     */
    protected ConfigManager(File dataFolder, File pluginJar) {
        this.folder = dataFolder;
        this.pluginJar = pluginJar;
        if (folder != null && !folder.exists()) {
            folder.mkdirs();
        }
    }

    /**
     * Gets a loaded config file from the local map, or null if not loaded.
     *
     * @param file the config file name
     * @return the related Configuration, or null
     */
    public Configuration get(String file) {
        return this.configs.getOrDefault(file, null);
    }

    /**
     * Creates the config from a packaged default (if missing), loads it and caches it.
     * Keys added to the packaged default since the file was written are backfilled into it.
     *
     * @param file   the config file name
     * @param source the packaged resource name to copy from
     * @return the loaded config
     */
    public Configuration create(String file, String source) {
        return create(file, source, true);
    }

    /**
     * Same as {@link #create(String, String)} but the source name equals the new one.
     *
     * @param file the config file name
     * @return the loaded config
     */
    public Configuration create(String file) {
        return create(file, file);
    }

    /**
     * Creates the config from a packaged default (if missing), loads it and caches it, optionally
     * backfilling the keys that the packaged default has and the file on disk lacks.
     * <p>
     * Pass {@code false} for files whose entries the user is meant to be able to <i>remove</i> (a
     * layout, a list of modules): with the backfill on, a deleted key is restored from the packaged
     * default on the next load, so removing an entry by hand is impossible.
     *
     * @param file     the config file name
     * @param source   the packaged resource name to copy from
     * @param backfill whether missing default keys are added back to the file on disk
     * @return the loaded config
     */
    public Configuration create(String file, String source, boolean backfill) {
        File resourcePath = new File(folder + File.separator + file);
        if (!resourcePath.exists()) {
            createYAML(file, source, false);
        }
        reload(file);
        if (backfill) {
            backfillFromDefault(file, source);
        }
        return this.configs.get(file);
    }

    /**
     * Same as {@link #create(String, String, boolean)} but the source name equals the new one.
     *
     * @param file     the config file name
     * @param backfill whether missing default keys are added back to the file on disk
     * @return the loaded config
     */
    public Configuration create(String file, boolean backfill) {
        return create(file, file, backfill);
    }

    /**
     * Saves the config file changes and updates it in the local map.
     *
     * @param file the config file name
     */
    public void save(String file) {
        Configuration config = get(file);
        if (config == null) {
            throw new IllegalArgumentException("The specified configuration file doesn't exist!");
        }
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .save(config, new File(folder + File.separator + file));
        } catch (IOException ex) {
            logError("saving", ex);
        }
        this.put(file, config);
    }

    /**
     * Reloads a config file.
     *
     * @param file the config file name
     */
    public void reload(String file) {
        if (!this.configs.containsKey(file)) {
            createYAML(file, false);
        }
        Configuration conf = this.load(file);
        this.put(file, conf);
    }

    /**
     * Reloads all cached config files.
     */
    public void reloadAll() {
        this.configs.keySet().forEach(this::reload);
    }

    private Configuration load(String file) {
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(new File(folder + File.separator + file));
        } catch (IOException ex) {
            logError("loading", ex);
            return null;
        }
    }

    private void put(String file, Configuration config) {
        this.configs.put(file, config);
    }

    /**
     * Adds any key present in the packaged default but missing from the user's file (e.g. after an
     * update that introduced new options), then persists the change. Existing values are untouched.
     *
     * <p>The change is applied to the file <b>text</b> (see {@link ConfigTextMerge}) rather than by
     * re-dumping the in-memory model, so the user's comments, blank lines and formatting survive the
     * upgrade. The in-memory config is then reloaded to reflect the added keys.
     */
    private void backfillFromDefault(String file, String source) {
        Configuration current = this.configs.get(file);
        if (current == null) {
            return;
        }
        Configuration defaults = loadDefault(source);
        if (defaults == null || !ConfigMigration.hasMissingKeys(current, defaults)) {
            return;
        }
        File onDisk = new File(folder + File.separator + file);
        try {
            String userText = new String(Files.readAllBytes(onDisk.toPath()), java.nio.charset.StandardCharsets.UTF_8);
            String merged = ConfigTextMerge.merge(userText, defaults);
            if (!merged.equals(userText)) {
                Files.write(onDisk.toPath(), merged.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                reload(file);
            }
        } catch (IOException ex) {
            logError("backfilling", ex);
        }
    }

    private Configuration loadDefault(String source) {
        try (InputStream in = getResourceAsStream(source)) {
            return (in == null) ? null
                    : ConfigurationProvider.getProvider(YamlConfiguration.class).load(in);
        } catch (IOException ex) {
            logError("reading defaults for", ex);
            return null;
        }
    }

    private void createYAML(String resourcePath, String source, boolean replace) {
        try {
            File file = new File(folder + File.separator + resourcePath);
            if (!file.getParentFile().exists() || !file.exists()) {
                file.getParentFile().mkdirs();
                if (file.createNewFile()) {
                    replace = true;
                }
                if (file.length() == 0) {
                    replace = true;
                }
                if (replace) {
                    Files.copy(getResourceAsStream(source), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    Files.copy(getResourceAsStream(source), file.toPath());
                }
            }
        } catch (IOException ex) {
            logError("creating", ex);
        }
    }

    private void createYAML(String resourcePath, boolean replace) {
        this.createYAML(resourcePath, resourcePath, replace);
    }

    private InputStream getResourceAsStream(String name) throws IOException {
        try (ZipFile file = new ZipFile(pluginJar);
             ZipInputStream zip = new ZipInputStream(pluginJar.toURI().toURL().openStream())) {
            ZipEntry e;
            while ((e = zip.getNextEntry()) != null) {
                if (e.getName().equals(name)) {
                    return cloneInputStream(file.getInputStream(e));
                }
            }
        }
        return null;
    }

    private static InputStream cloneInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private static void logError(String action, Exception ex) {
        new Message("&cException encountered while " + action + " a yaml file! Please contact an administrator!",
                "&cError message: %msg")
                .addPlaceHolder("%msg", String.valueOf(ex.getMessage()))
                .sendConsole();
    }
}
