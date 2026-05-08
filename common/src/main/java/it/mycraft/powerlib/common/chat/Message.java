package it.mycraft.powerlib.common.chat;

import it.mycraft.powerlib.common.utils.ColorAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;

public class Message {


    private static final PlatformAudience platformAudience = Audiences.getPlatformAudience();
    private TextComponent singleLineMessage;
    private List<TextComponent> multiLineMessages;
    private HoverEvent<?> hoverEvent;
    private ClickEvent clickEvent;

    public Message() {
        this.singleLineMessage = text("");
        this.multiLineMessages = new ArrayList<>();
    }
    public Message(String singleLineMessage, boolean color) {
        this.singleLineMessage = text(color ? ColorAPI.color(singleLineMessage) : singleLineMessage);
        this.multiLineMessages = new ArrayList<>();

    }

    public Message(String singleLineMessage) {
        this(singleLineMessage, true);
    }

    public Message(String... multiLineMessages) {
        this.singleLineMessage = text("");
        this.multiLineMessages = new ArrayList<>(ColorAPI.color(Arrays.asList(multiLineMessages))).stream().map(Component::text).collect(Collectors.toList());
    }

    public Message(List<String> multiLineMessages, boolean color) {
        this.singleLineMessage = text("");
        this.multiLineMessages = (color ? new ArrayList<>(ColorAPI.color(multiLineMessages)) : multiLineMessages).stream().map(Component::text).collect(Collectors.toList());
    }

    public Message(List<String> multiLineMessages) {
        this(multiLineMessages, true);
    }

    /**
     * Builds a Message whose content is a MiniMessage tag-set, parsed and
     * rendered as legacy section codes. Requires `powerlib-minimessage` on
     * the runtime classpath; otherwise falls back to the literal text.
     */
    public static Message miniMessage(String input) {
        return new Message(invokeMini(input), false);
    }

    public static Message miniMessage(String... lines) {
        java.util.List<String> rendered = new java.util.ArrayList<>(lines.length);
        for (String line : lines) rendered.add(invokeMini(line));
        return new Message(rendered, false);
    }

    private static String invokeMini(String input) {
        try {
            String packageName = Message.class.getPackage().getName();
            String mmPackage = packageName.replace(".common.chat", ".minimessage");
            Class<?> clazz = Class.forName(mmPackage + ".MiniMessageSupport");
            return (String) clazz.getMethod("parseLegacy", String.class).invoke(null, input);
        } catch (ReflectiveOperationException e) {
            return input == null ? "" : input;
        }
    }

    public Message addPlaceHolder(String placeholder, Object value) {
        singleLineMessage = singleLineMessage.content(singleLineMessage.content().replace(placeholder, value.toString()));
        multiLineMessages.replaceAll(s -> s.content(s.content().replace(placeholder, value.toString())));
        return this;
    }

    public Message set(String message) {
        this.singleLineMessage = text(ColorAPI.color(message));
        return this;
    }

    public Message set(List<String> messages) {
        this.multiLineMessages = ColorAPI.color(messages).stream().map(Component::text).collect(Collectors.toList());
        return this;
    }

    public Message set(String... messages) {
        this.multiLineMessages = ColorAPI.color(Arrays.asList(messages)).stream().map(Component::text).collect(Collectors.toList());
        return this;
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
        for(Component c : components) {
            this.singleLineMessage = this.singleLineMessage.append(c);
        }
        return this;
    }

    public Message append(Message... messages) {
        Arrays.stream(messages).map(m -> (Component[])
                        (m.getComponentList().isEmpty() ? new Component[]{m.getComponent()} : m.getComponentList().toArray()))
                .forEach(this::append);
        return this;
    }

    /**
     * Appends multiple messages to a Message (converting it in a multi-line message)
     * @param lines an optional array of lines to append
     * @return
     */
    public Message appendLines(Component... lines) {
        if(this.singleLineMessage != null) {
            this.multiLineMessages.add(this.singleLineMessage);
        }
        this.multiLineMessages.addAll(Arrays.stream(lines).map(l -> (TextComponent) l).collect(Collectors.toList()));
        return this;
    }

    public Message appendLines(Message... messages) {
        Arrays.stream(messages).map(m -> (Component[])
                        (m.getComponentList().isEmpty() ? new Component[]{m.getComponent()} : m.getComponentList().toArray()))
                .forEach(this::appendLines);
        return this;
    }

    public String getText() {
        return singleLineMessage.content();
    }

    public List<String> getTextList() {
        return multiLineMessages.stream().map(TextComponent::content).collect(Collectors.toList());
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
        if(multiLineMessages.isEmpty()) {
            sendRawMessage(audience, singleLineMessage);
        }
        else {
            this.multiLineMessages.forEach((msg) -> sendRawMessage(audience, msg));
        }
    }

    /**
     * Sends the message
     * @param commandSender might be console, a Player, or a generic CommandSender
     */
    public void send(Object commandSender) {
        Audience audience;
        try {
            audience = (Audience) platformAudience.getPlayerAudience().invoke(null, commandSender);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            platformAudience.sendError();
            return;
        }
        send(audience);
    }

    /**
     * Authorized players broadcast
     * @param permission the required node
     */
    public void broadcast(String permission) {
        Audience audience;
        try {
            audience = (Audience) platformAudience.getPermissionAudience().invoke(null, permission);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            platformAudience.sendError();
            return;
        }
        send(audience);
    }

    /**
     * Sends to Audience filtered by some conditions
     * @param filter the filter
     */
    public void send(Predicate<Object> filter) {
        Audience audience;
        try {
            audience = (Audience) platformAudience.getFilterAudience().invoke(null, filter);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            platformAudience.sendError();
            return;
        }
        send(audience);
    }

    /**
     * Sends the message to the server Console
     */
    public void sendConsole() {
        Audience console;
        try {
            console = (Audience) platformAudience.getConsoleAudience().invoke(null);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            platformAudience.sendError();
            return;
        }
        send(console);
    }

    /**
     * Broadcast to all players! Console is NOT included in this audience
     */
    public void broadcast() {
        Audience audience;
        try {
            audience = (Audience) platformAudience.getAllPlayersAudience().invoke(null);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            platformAudience.sendError();
            return;
        }
        send(audience);
    }

    /**
     * Broadcast to everyone. Players and console, everything included.
     */
    public void sendAll() {
        Audience audience;
        try {
            audience = (Audience) platformAudience.getAllAudience().invoke(null);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            platformAudience.sendError();
            return;
        }
        send(audience);
    }

    public Message color() { // no need by default
        if (multiLineMessages.isEmpty()) {
            this.singleLineMessage = (TextComponent) ColorAPI.color(singleLineMessage);
        } else this.multiLineMessages = ColorAPI.color(getTextList()).stream().map(Component::text).collect(Collectors.toList());
        return this;
    }

    public Message decolor() {
        if (multiLineMessages.isEmpty()) {
            this.singleLineMessage = (TextComponent) ColorAPI.decolor(singleLineMessage);
        } else this.multiLineMessages = ColorAPI.decolor(getTextList()).stream().map(Component::text).collect(Collectors.toList());
        return this;
    }

    public Message hex(String pre, String post) {
        if (multiLineMessages.isEmpty()) {
            this.singleLineMessage = (TextComponent) ColorAPI.hex(singleLineMessage, pre, post);
        } else this.multiLineMessages = ColorAPI.hex(getTextList(), pre, post)
                .stream().map(Component::text).collect(Collectors.toList());
        return this;
    }

    public Message hex() {
        this.hex("&#", "");
        return this;
    }

    private void sendRawMessage(Audience audience, Component component) {
        if(this.hoverEvent != null) {
            component = component.hoverEvent(this.hoverEvent);
        }
        if(this.clickEvent != null) {
            component = component.clickEvent(this.clickEvent);
        }
        audience.sendMessage(component);
    }

    private void reset() {
        multiLineMessages = new ArrayList<>();
        singleLineMessage = null;
    }
}
