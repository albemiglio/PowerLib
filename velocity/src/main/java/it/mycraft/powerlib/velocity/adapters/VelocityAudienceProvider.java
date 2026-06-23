package it.mycraft.powerlib.velocity.adapters;

import com.velocitypowered.api.command.CommandSource;
import it.mycraft.powerlib.common.chat.AudienceProvider;
import net.kyori.adventure.audience.Audience;

import java.util.function.Predicate;

public class VelocityAudienceProvider implements AudienceProvider {

    @Override
    public Audience player(Object sender) {
        return AudienceAdapter.audience((CommandSource) sender);
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
