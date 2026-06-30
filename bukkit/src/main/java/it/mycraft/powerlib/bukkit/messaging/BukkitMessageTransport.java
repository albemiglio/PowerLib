package it.mycraft.powerlib.bukkit.messaging;

import it.mycraft.powerlib.bukkit.PowerLib;
import it.mycraft.powerlib.common.messaging.Framing;
import it.mycraft.powerlib.common.messaging.MessageTransport;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

/**
 * Rides a single Minecraft plugin-messaging channel ({@code powerlib:main}) and multiplexes the
 * logical channels through {@link Framing}. Plugin messages are connection-bound, so outbound frames
 * are carried by the first online player; with no players online the proxy link is unavailable and
 * the frame is dropped.
 */
public class BukkitMessageTransport implements MessageTransport, PluginMessageListener {

    /** The shared plugin-messaging channel name ({@code powerlib:main}). */
    public static final String MC_CHANNEL = "powerlib:main";

    private final AtomicReference<BiConsumer<String, byte[]>> handler = new AtomicReference<>();

    /**
     * Registers the {@code powerlib:main} channel for incoming and outgoing plugin messages.
     */
    public BukkitMessageTransport() {
        Plugin plugin = PowerLib.getPlugin();
        Messenger messenger = Bukkit.getMessenger();
        messenger.registerOutgoingPluginChannel(plugin, MC_CHANNEL);
        messenger.registerIncomingPluginChannel(plugin, MC_CHANNEL, this);
    }

    @Override
    public void send(String channel, byte[] data) {
        Player carrier = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (carrier != null) {
            carrier.sendPluginMessage(PowerLib.getPlugin(), MC_CHANNEL, Framing.frame(channel, data));
        }
    }

    @Override
    public void listen(BiConsumer<String, byte[]> handler) {
        this.handler.set(handler);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!MC_CHANNEL.equals(channel)) {
            return;
        }
        BiConsumer<String, byte[]> current = handler.get();
        if (current != null) {
            Framing.Frame frame = Framing.parse(message);
            current.accept(frame.channel(), frame.data());
        }
    }
}
