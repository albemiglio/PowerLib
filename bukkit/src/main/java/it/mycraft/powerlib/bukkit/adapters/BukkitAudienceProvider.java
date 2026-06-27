package it.mycraft.powerlib.bukkit.adapters;

import it.mycraft.powerlib.common.chat.AudienceProvider;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;

import java.util.function.Predicate;

/**
 * Bukkit implementation of {@link AudienceProvider}, delegating to {@link AudienceAdapter}.
 */
public class BukkitAudienceProvider implements AudienceProvider {

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
