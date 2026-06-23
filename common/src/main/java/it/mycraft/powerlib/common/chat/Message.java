package it.mycraft.powerlib.common.chat;

import it.mycraft.powerlib.common.utils.ColorAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;

public class Message {

    private static final AudienceProvider PROVIDER = Audiences.getProvider();

    /** Parses legacy ('&'/'§') and '&#RRGGBB' hex codes into real Adventure components. */
    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.builder().character('§').hexColors().build();
    private static final MiniMessage MINI = MiniMessage.miniMessage();

    /**
     * Converts a legacy-formatted string ('&' codes and '&#RRGGBB' hex) into a real component,
     * instead of leaving section-sign codes as literal text.
     */
    static Component toComponent(String legacy) {
        if (legacy == null) return text("");
        return LEGACY.deserialize(ColorAPI.color(ColorAPI.hex(legacy)));
    }

    private Component singleLineMessage;
    private List<Component> multiLineMessages;
    private HoverEvent<?> hoverEvent;
    private ClickEvent clickEvent;

    public Message() {
        this.singleLineMessage = text("");
        this.multiLineMessages = new ArrayList<>();
    }

    public Message(String singleLineMessage, boolean color) {
        this.singleLineMessage = color ? toComponent(singleLineMessage) : text(singleLineMessage);
        this.multiLineMessages = new ArrayList<>();
    }

    public Message(String singleLineMessage) {
        this(singleLineMessage, true);
    }

    public Message(String... multiLineMessages) {
        this(Arrays.asList(multiLineMessages), true);
    }

    public Message(List<String> multiLineMessages, boolean color) {
        this.singleLineMessage = text("");
        this.multiLineMessages = multiLineMessages.stream()
                .map(s -> color ? toComponent(s) : text(s))
                .collect(Collectors.toList());
    }

    public Message(List<String> multiLineMessages) {
        this(multiLineMessages, true);
    }

    /**
     * Builds a Message from MiniMessage markup, e.g. "&lt;gradient:#ff0000:#0000ff&gt;hi&lt;/gradient&gt;".
     * @param miniMessage the MiniMessage string
     * @return the message
     */
    public static Message mini(String miniMessage) {
        Message m = new Message();
        m.singleLineMessage = MINI.deserialize(miniMessage == null ? "" : miniMessage);
        return m;
    }

    public static Message mini(String... lines) {
        return mini(Arrays.asList(lines));
    }

    public static Message mini(List<String> lines) {
        Message m = new Message();
        m.multiLineMessages = lines.stream()
                .map(s -> MINI.deserialize(s == null ? "" : s))
                .collect(Collectors.toList());
        return m;
    }

    public Message addPlaceHolder(String placeholder, Object value) {
        String replacement = String.valueOf(value);
        this.singleLineMessage = replace(this.singleLineMessage, placeholder, replacement);
        this.multiLineMessages.replaceAll(c -> replace(c, placeholder, replacement));
        return this;
    }

    private static Component replace(Component component, String placeholder, String value) {
        return component.replaceText(builder -> builder.matchLiteral(placeholder).replacement(value));
    }

    public Message set(String message) {
        this.singleLineMessage = toComponent(message);
        return this;
    }

    public Message set(List<String> messages) {
        this.multiLineMessages = messages.stream().map(Message::toComponent).collect(Collectors.toList());
        return this;
    }

    public Message set(String... messages) {
        return set(Arrays.asList(messages));
    }

    /**
     * Adds a hover event in the ENTIRE message!
     * @param event the adding hover event
     * @return the message
     */
    public Message setHoverEvent(HoverEvent<?> event) {
        this.hoverEvent = event;
        return this;
    }

    /**
     * Adds a click event in the ENTIRE message!
     * @param event the adding click event
     * @return the message
     */
    public Message setClickEvent(ClickEvent event) {
        this.clickEvent = event;
        return this;
    }

    /**
     * Appends multiple components to a single-line message (useful for adding interactive component parts)
     * @param components the components to append
     * @return the final message
     */
    public Message append(Component... components) {
        for (Component c : components) {
            this.singleLineMessage = this.singleLineMessage.append(c);
        }
        return this;
    }

    public Message append(Message... messages) {
        Arrays.stream(messages).map(m -> (Component[])
                        (m.getComponentList().isEmpty() ? new Component[]{m.getComponent()} : m.getComponentList().toArray(new Component[0])))
                .forEach(this::append);
        return this;
    }

    /**
     * Appends multiple messages to a Message (converting it in a multi-line message)
     * @param lines an optional array of lines to append
     * @return the final message
     */
    public Message appendLines(Component... lines) {
        if (this.singleLineMessage != null) {
            this.multiLineMessages.add(this.singleLineMessage);
        }
        this.multiLineMessages.addAll(Arrays.asList(lines));
        return this;
    }

    public Message appendLines(Message... messages) {
        Arrays.stream(messages).map(m -> (Component[])
                        (m.getComponentList().isEmpty() ? new Component[]{m.getComponent()} : m.getComponentList().toArray(new Component[0])))
                .forEach(this::appendLines);
        return this;
    }

    /**
     * The message as a legacy section-sign string. Prefer {@link #getComponent()} for modern usage.
     * @return the legacy-serialized text
     */
    public String getText() {
        return LEGACY.serialize(singleLineMessage);
    }

    public List<String> getTextList() {
        return multiLineMessages.stream().map(LEGACY::serialize).collect(Collectors.toList());
    }

    public Component getComponent() {
        return singleLineMessage;
    }

    public List<Component> getComponentList() {
        return new ArrayList<>(multiLineMessages);
    }

    /**
     * Enhanced and recommended version - doesn't use any reflection!
     * @param audience the senders audience
     */
    public void send(Audience audience) {
        if (multiLineMessages.isEmpty()) {
            sendRawMessage(audience, singleLineMessage);
        } else {
            this.multiLineMessages.forEach((msg) -> sendRawMessage(audience, msg));
        }
    }

    /**
     * Sends the message
     * @param commandSender might be console, a Player, or a generic CommandSender
     */
    public void send(Object commandSender) {
        if (PROVIDER != null) {
            send(PROVIDER.player(commandSender));
        }
    }

    /**
     * Authorized players broadcast
     * @param permission the required node
     */
    public void broadcast(String permission) {
        if (PROVIDER != null) {
            send(PROVIDER.permission(permission));
        }
    }

    /**
     * Sends to Audience filtered by some conditions
     * @param filter the filter
     */
    public void send(Predicate<Object> filter) {
        if (PROVIDER != null) {
            send(PROVIDER.filter(filter));
        }
    }

    /**
     * Sends the message to the server Console
     */
    public void sendConsole() {
        if (PROVIDER != null) {
            send(PROVIDER.console());
        }
    }

    /**
     * Broadcast to all players! Console is NOT included in this audience
     */
    public void broadcast() {
        if (PROVIDER != null) {
            send(PROVIDER.players());
        }
    }

    /**
     * Broadcast to everyone. Players and console, everything included.
     */
    public void sendAll() {
        if (PROVIDER != null) {
            send(PROVIDER.all());
        }
    }

    /**
     * @deprecated colors are now applied automatically when the message is created; this is a no-op.
     */
    @Deprecated
    public Message color() {
        return this;
    }

    /**
     * @deprecated hex colors are now applied automatically when the message is created; this is a no-op.
     */
    @Deprecated
    public Message hex(String pre, String post) {
        return this;
    }

    /**
     * @deprecated hex colors are now applied automatically when the message is created; this is a no-op.
     */
    @Deprecated
    public Message hex() {
        return this;
    }

    /**
     * Removes all styling, keeping only the plain text of the message.
     * @return the message
     */
    public Message decolor() {
        this.singleLineMessage = strip(this.singleLineMessage);
        this.multiLineMessages.replaceAll(Message::strip);
        return this;
    }

    private static Component strip(Component component) {
        return text(LEGACY.serialize(component).replaceAll("(?i)§[0-9A-FK-ORX]", ""));
    }

    private void sendRawMessage(Audience audience, Component component) {
        if (this.hoverEvent != null) {
            component = component.hoverEvent(this.hoverEvent);
        }
        if (this.clickEvent != null) {
            component = component.clickEvent(this.clickEvent);
        }
        audience.sendMessage(component);
    }

    private void reset() {
        multiLineMessages = new ArrayList<>();
        singleLineMessage = text("");
    }
}
