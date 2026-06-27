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

    /** The shared plugin-messaging channel identifier ({@code powerlib:main}). */
    public static final ChannelIdentifier CHANNEL = MinecraftChannelIdentifier.create("powerlib", "main");

    private volatile BiConsumer<String, byte[]> handler;

    /**
     * Registers the {@code powerlib:main} channel and subscribes this transport to plugin-message events.
     */
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

    /**
     * Handles inbound plugin messages on the {@code powerlib:main} channel, unframing them and
     * dispatching to the registered handler. Marks matching events as handled so they are not forwarded.
     *
     * @param event the incoming plugin-message event
     */
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
