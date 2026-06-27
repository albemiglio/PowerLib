package it.mycraft.powerlib.bungee.adapters;

import it.mycraft.powerlib.bungee.PowerLib;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.function.Predicate;

/**
 * Static helpers that resolve BungeeCord senders into Adventure {@link Audience}s.
 */
public class AudienceAdapter {

    private AudienceAdapter() {

    }

    /**
     * Returns the audience for a single command sender.
     *
     * @param sender the command sender
     * @return the matching audience
     */
    public static Audience audience(CommandSender sender) {
        return PowerLib.adventure().sender(sender);
    }

    /**
     * Returns the audience for a single player.
     *
     * @param player the player
     * @return the matching audience
     */
    public static Audience audience(ProxiedPlayer player) {
        return PowerLib.adventure().player(player);
    }

    /**
     * Returns the audience of all senders holding the given permission.
     *
     * @param permission the permission node
     * @return the matching audience
     */
    public static Audience audience(String permission) {
        return PowerLib.adventure().permission(permission);
    }

    /**
     * Returns the audience of all senders matching the given predicate.
     *
     * @param predicate the sender filter
     * @return the matching audience
     */
    public static Audience audience(Predicate<CommandSender> predicate) { return PowerLib.adventure().filter(predicate); }

    /**
     * Returns the audience of all senders, players and console alike.
     *
     * @return the audience of everyone
     */
    public static Audience all() {
        return PowerLib.adventure().all();
    }

    /**
     * Returns the audience of all online players.
     *
     * @return the audience of every player
     */
    public static Audience players() {
        return PowerLib.adventure().players();
    }

    /**
     * Returns the audience of every player connected to the named backend server.
     *
     * @param serverName the backend server name
     * @return the matching audience
     */
    public static Audience server(String serverName) {
        return PowerLib.adventure().server(serverName);
    }

    /**
     * Returns the audience for the proxy console.
     *
     * @return the console audience
     */
    public static Audience console() {
        return PowerLib.adventure().console();
    }
}
