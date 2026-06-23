package it.mycraft.powerlib.common.messaging;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessengerTest {

    /** A loopback transport that immediately echoes whatever is sent back to the listener. */
    private static final class LoopTransport implements MessageTransport {
        private BiConsumer<String, byte[]> handler;

        @Override
        public void send(String channel, byte[] data) {
            if (handler != null) {
                handler.accept(channel, data);
            }
        }

        @Override
        public void listen(BiConsumer<String, byte[]> handler) {
            this.handler = handler;
        }
    }

    @Test
    void publishDeliversTheDecodedPayloadToSubscribersOfThatChannel() {
        Messenger messenger = new Messenger(new LoopTransport());
        Channel<String> chat = Channel.of("chat", Codec.string());
        List<String> received = new ArrayList<>();

        messenger.subscribe(chat, received::add);
        messenger.publish(chat, "hello");

        assertEquals(List.of("hello"), received);
    }

    @Test
    void payloadsForOtherChannelsAreIgnored() {
        Messenger messenger = new Messenger(new LoopTransport());
        Channel<String> listened = Channel.of("listened", Codec.string());
        Channel<String> other = Channel.of("other", Codec.string());
        List<String> received = new ArrayList<>();

        messenger.subscribe(listened, received::add);
        messenger.publish(other, "ignored");

        assertTrue(received.isEmpty());
    }
}
