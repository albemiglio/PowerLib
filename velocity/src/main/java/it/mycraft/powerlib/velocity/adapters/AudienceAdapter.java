package it.mycraft.powerlib.velocity.adapters;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import it.mycraft.powerlib.velocity.PowerLib;
import net.kyori.adventure.audience.Audience;

import java.util.function.Predicate;

/**
 * Builds Adventure {@link Audience} views over Velocity command sources and players.
 */
public class AudienceAdapter {

    private AudienceAdapter() {

    }

    /**
     * Returns the given command source as an {@link Audience}.
     *
     * @param sender the command source
     * @return the sender as an audience
     */
    public static Audience audience(CommandSource sender) {
        return sender;
    }

    /**
     * Returns the given player as an {@link Audience}.
     *
     * @param player the player
     * @return the player as an audience
     */
    public static Audience audience(Player player) {
        return player;
    }

    /**
     * Returns an audience of every online source holding the given permission.
     *
     * @param permission the permission node to filter by
     * @return the filtered audience
     */
    public static Audience audience(String permission) {
        return PowerLib.getProxy().filterAudience((audience -> ((CommandSource) audience).hasPermission(permission)));
    }

    /**
     * Returns an audience of every online source matching the given predicate.
     *
     * @param predicate the filter to apply
     * @return the filtered audience
     */
    public static Audience audience(Predicate<Audience> predicate) {
        return PowerLib.getProxy().filterAudience(predicate);
    }

    /**
     * Returns an audience containing every online source (players and console).
     *
     * @return an audience of all sources
     */
    public static Audience all() {
        return PowerLib.getProxy().filterAudience(audience -> true);
    }

    /**
     * Returns an audience containing only online players.
     *
     * @return an audience of all players
     */
    public static Audience players() {
        return PowerLib.getProxy().filterAudience(audience -> audience instanceof Player);
    }

    /**
     * Returns an audience of the players currently connected to the named backend server.
     *
     * @param serverName the backend server name, matched case-insensitively
     * @return an audience of players on that server
     */
    public static Audience server(String serverName) {
        return PowerLib.getProxy().filterAudience(a -> a instanceof Player
                && ((Player) a).getCurrentServer().isPresent()
                && ((Player) a).getCurrentServer().get().getServerInfo().getName().equalsIgnoreCase(serverName));
    }

    /**
     * Returns the proxy console as an {@link Audience}.
     *
     * @return the console audience
     */
    public static Audience console() {
        return PowerLib.getProxy().getConsoleCommandSource();
    }
}
