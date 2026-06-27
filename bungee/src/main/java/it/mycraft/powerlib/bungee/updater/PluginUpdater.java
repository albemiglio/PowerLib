package it.mycraft.powerlib.bungee.updater;

import it.mycraft.powerlib.common.objects.enums.SiteType;
import it.mycraft.powerlib.common.utils.JSONUtils;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import javax.json.JsonObject;

/**
 * Checks whether a newer plugin version is available from GitHub, SpigotMC, or a custom JSON endpoint.
 */
public class PluginUpdater {

    @Getter
    private String url;
    private Plugin plugin;
    private String field;

    @Getter
    private long spigotVersionId;

    @Getter
    private SiteType type;

    @Getter
    private String latestVersion;

    /**
     * Creates an updater for the given plugin; configure a source with one of the {@code set*URL} methods.
     *
     * @param plugin the plugin whose version is checked
     */
    public PluginUpdater(Plugin plugin) {
        this.plugin = plugin;
        this.url = "";
        this.latestVersion = "";
    }

    /**
     * Configures this updater to read the latest release tag from a GitHub repository.
     *
     * @param user the repository owner
     * @param repo the repository name
     * @return this updater
     */
    public PluginUpdater setGitHubURL(String user, String repo) {
        this.url = "https://api.github.com/repos/{user}/{repo}/releases/latest"
                .replace("{user}", user)
                .replace("{repo}", repo);
        this.type = SiteType.GITHUB;
        this.field = "tag_name";
        return this;
    }

    /**
     * Configures this updater to read the latest version from a SpigotMC resource via the Spiget API.
     *
     * @param resourceId the SpigotMC resource id
     * @return this updater
     */
    public PluginUpdater setSpigotURL(String resourceId) {
        this.url = "https://api.spiget.org/v2/resources/{resourceId}/versions/latest"
                .replace("{resourceId}", resourceId);
        this.type = SiteType.SPIGOTMC;
        this.field = "name";
        return this;
    }

    /**
     * Configures this updater to read the latest version from a SpigotMC resource via the Spiget API.
     *
     * @param resourceId the SpigotMC resource id
     * @return this updater
     */
    public PluginUpdater setSpigotURL(int resourceId) {
        return this.setSpigotURL(String.valueOf(resourceId));
    }

    /**
     * Configures this updater to read the latest version from a custom JSON endpoint.
     *
     * @param url   the endpoint URL returning JSON
     * @param field the JSON field holding the version string
     * @return this updater
     */
    public PluginUpdater setCustomURL(String url, String field) {
        this.url = url;
        this.type = SiteType.OTHER;
        this.field = field;
        return this;
    }

    /**
     * Fetches the configured endpoint and compares the remote version with the plugin's current one.
     *
     * @return {@code true} if a different (newer) version is available, {@code false} otherwise or on error
     */
    public boolean needsUpdate() {
        String version = this.plugin.getDescription().getVersion();
        if (JSONUtils.isValidJSON(this.url)) {
            JsonObject obj = JSONUtils.getJSON(url);
            try {
                this.latestVersion = obj.getString(this.field);
            } catch(NullPointerException ex) {
                return false;
            }
            if (type == SiteType.SPIGOTMC) {
                this.spigotVersionId = obj.getInt("id");
            }
            return !version.equals(latestVersion);
        }
        return false;
    }
}

