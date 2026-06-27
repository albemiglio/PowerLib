package it.mycraft.powerlib.bungee.adapters;

import it.mycraft.powerlib.common.chat.AudienceProvider;
import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.CommandSender;

import java.util.function.Predicate;

/**
 * BungeeCord {@link AudienceProvider} backed by {@link AudienceAdapter}.
 */
public class BungeeAudienceProvider implements AudienceProvider {

    @Override
    public Audience player(Object sender) {
        return AudienceAdapter.audience((CommandSender) sender);
    }

    @Override
    public Audience console() {
        return AudienceAdapter.console();
    }

    @Override
    public Audience players() {
        return AudienceAdapter.players();
    }

    @Override
    public Audience all() {
        return AudienceAdapter.all();
    }

    @Override
    public Audience permission(String node) {
        return AudienceAdapter.audience(node);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Audience filter(Predicate<Object> predicate) {
        return AudienceAdapter.audience((Predicate) predicate);
    }
}
