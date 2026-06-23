package it.mycraft.powerlib.common.chat;

import net.kyori.adventure.audience.Audience;

import java.util.function.Predicate;

/**
 * Platform-specific source of Adventure {@link Audience}s. Each platform module provides an
 * implementation registered via {@code META-INF/services}, so {@code common} resolves it through
 * {@link java.util.ServiceLoader} instead of reflection.
 */
public interface AudienceProvider {

    /** @param sender the platform command sender (CommandSender / CommandSource) */
    Audience player(Object sender);

    Audience console();

    Audience players();

    Audience all();

    Audience permission(String node);

    Audience filter(Predicate<Object> predicate);
}
