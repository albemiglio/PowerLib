package it.mycraft.powerlib.bungee.messaging;

import it.mycraft.powerlib.bungee.PowerLib;
import it.mycraft.powerlib.common.messaging.Framing;
import it.mycraft.powerlib.common.messaging.MessageTransport;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.function.BiConsumer;

/**
 * BungeeCord side of the {@code powerlib:main} link. Inbound frames arrive as {@link PluginMessageEvent}s;
 * outbound frames are broadcast to every backend server.
 */
public class BungeeMessageTransport implements MessageTransport, Listener {

    public static final String MC_CHANNEL = "powerlib:main";

    private volatile BiConsumer<String, byte[]> handler;

    public BungeeMessageTransport() {
        ProxyServer proxy = ProxyServer.getInstance();
        proxy.registerChannel(MC_CHANNEL);
        proxy.getPluginManager().registerListener(PowerLib.getPlugin(), this);
    }

    @Override
    public void send(String channel, byte[] data) {
        byte[] frame = Framing.frame(channel, data);
        for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
            server.sendData(MC_CHANNEL, frame, false);
        }
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!MC_CHANNEL.equals(event.getTag())) {
            return;
        }
        event.setCancelled(true);
        BiConsumer<String, byte[]> current = handler;
        if (current != null) {
            Framing.Frame frame = Framing.parse(event.getData());
            current.accept(frame.channel(), frame.data());
        }
    }

    @Override
    public void listen(BiConsumer<String, byte[]> handler) {
        this.handler = handler;
    }
}
