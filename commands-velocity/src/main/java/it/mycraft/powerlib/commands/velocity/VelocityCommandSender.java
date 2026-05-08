package it.mycraft.powerlib.commands.velocity;

import com.velocitypowered.api.command.CommandSource;
import it.mycraft.powerlib.commands.CommandSender;
import net.kyori.adventure.text.Component;

public final class VelocityCommandSender implements CommandSender {
    private final CommandSource raw;
    public VelocityCommandSender(CommandSource raw) { this.raw = raw; }
    @Override public String getName() {
        return raw instanceof com.velocitypowered.api.proxy.Player
                ? ((com.velocitypowered.api.proxy.Player) raw).getUsername()
                : "CONSOLE";
    }
    @Override public boolean hasPermission(String permission) { return permission == null || raw.hasPermission(permission); }
    @Override public void sendMessage(String message) { raw.sendMessage(Component.text(message)); }
    @Override public Object raw() { return raw; }
}
