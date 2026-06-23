package it.mycraft.powerlib.velocity.messaging;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import it.mycraft.powerlib.common.messaging.Framing;
import it.mycraft.powerlib.common.messaging.MessageTransport;
import it.mycraft.powerlib.velocity.PowerLib;

import java.util.function.BiConsumer;

/**
 * Velocity side of the {@code powerlib:main} link. Inbound frames arrive as {@link PluginMessageEvent}s;
 * outbound frames are broadcast to every registered server.
 */
public class VelocityMessageTransport implements MessageTransport {

    public static final ChannelIdentifier CHANNEL = MinecraftChannelIdentifier.create("powerlib", "main");

    private volatile BiConsumer<String, byte[]> handler;

    public VelocityMessageTransport() {
        PowerLib.getProxy().getChannelRegistrar().register(CHANNEL);
        PowerLib.getProxy().getEventManager().register(PowerLib.getPlugin(), this);
    }

    @Override
    public void send(String channel, byte[] data) {
        byte[] frame = Framing.frame(channel, data);
        for (RegisteredServer server : PowerLib.getProxy().getAllServers()) {
            server.sendPluginMessage(CHANNEL, frame);
        }
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (!CHANNEL.equals(event.getIdentifier())) {
            return;
        }
        event.setResult(PluginMessageEvent.ForwardResult.handled());
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
