package it.mycraft.powerlib.commands.bungee;

import it.mycraft.powerlib.commands.CommandSender;

public final class BungeeCommandSender implements CommandSender {
    private final net.md_5.bungee.api.CommandSender raw;
    public BungeeCommandSender(net.md_5.bungee.api.CommandSender raw) { this.raw = raw; }
    @Override public String getName() { return raw.getName(); }
    @Override public boolean hasPermission(String permission) { return permission == null || raw.hasPermission(permission); }
    @Override public void sendMessage(String message) { raw.sendMessage(net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message)); }
    @Override public Object raw() { return raw; }
}
