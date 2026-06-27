package it.mycraft.powerlib.bukkit.adapters;

import it.mycraft.powerlib.bukkit.PowerLib;
import lombok.NonNull;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

/**
 * Builds Adventure {@link Audience} views over Bukkit command senders, players, worlds, and permissions.
 */
public class AudienceAdapter {

    private AudienceAdapter() {

    }

    /**
     * Returns the given command sender as an {@link Audience}.
     *
     * @param sender the command sender
     * @return the sender as an audience
     */
    public static Audience audience(CommandSender sender) {
        return PowerLib.adventure().sender(sender);
    }

    /**
     * Returns the given player as an {@link Audience}.
     *
     * @param player the player
     * @return the player as an audience
     */
    public static Audience audience(Player player) {
        return PowerLib.adventure().player(player);
    }

    /**
     * Returns an audience of every online sender holding the given permission.
     *
     * @param permission the permission node to filter by
     * @return the filtered audience
     */
    public static Audience audience(String permission) {
        return PowerLib.adventure().permission(permission);
    }

    /**
     * Returns an audience of every command sender matching the given predicate.
     *
     * @param predicate the filter to apply
     * @return the filtered audience
     */
    public static Audience audience(Predicate<CommandSender> predicate) { return PowerLib.adventure().filter(predicate); }

    /**
     * Returns an audience of the players currently in the given world.
     *
     * @param world the world
     * @return an audience of players in that world
     */
    public static Audience audience(World world) {
        return PowerLib.adventure().world(Key.key(world.getName()));
    }

    /**
     * Returns an audience containing every online sender (players and console).
     *
     * @return an audience of all senders
     */
    public static Audience all() {
        return PowerLib.adventure().all();
    }

    /**
     * Returns an audience containing only online players.
     *
     * @return an audience of all players
     */
    public static Audience players() {
        return PowerLib.adventure().players();
    }

    /**
     * Returns the server console as an {@link Audience}.
     *
     * @return the console audience
     */
    public static Audience console() {
        return PowerLib.adventure().console();
    }
}
