package it.mycraft.powerlib.commands.bukkit;

import it.mycraft.powerlib.commands.CommandSender;

public final class BukkitCommandSender implements CommandSender {

    private final org.bukkit.command.CommandSender raw;

    public BukkitCommandSender(org.bukkit.command.CommandSender raw) {
        this.raw = raw;
    }

    @Override public String getName() { return raw.getName(); }
    @Override public boolean hasPermission(String permission) {
        return permission == null || raw.hasPermission(permission);
    }
    @Override public void sendMessage(String message) { raw.sendMessage(message); }
    @Override public Object raw() { return raw; }
}
