package it.mycraft.powerlib.common.chat;

import net.kyori.adventure.audience.Audience;

import java.util.function.Predicate;

/**
 * Platform-specific source of Adventure {@link Audience}s. Each platform module provides an
 * implementation registered via {@code META-INF/services}, so {@code common} resolves it through
 * {@link java.util.ServiceLoader} instead of reflection.
 */
public interface AudienceProvider {

    /**
     * Wraps a single platform sender as an audience.
     *
     * @param sender the platform command sender (CommandSender / CommandSource)
     * @return an audience targeting that sender
     */
    Audience player(Object sender);

    /** @return an audience targeting the server console */
    Audience console();

    /** @return an audience targeting every online player */
    Audience players();

    /** @return an audience targeting every player and the console */
    Audience all();

    /**
     * @param node the permission node to test
     * @return an audience targeting everyone holding the given permission
     */
    Audience permission(String node);

    /**
     * @param predicate test applied to each platform sender
     * @return an audience targeting the senders that match the predicate
     */
    Audience filter(Predicate<Object> predicate);
}
