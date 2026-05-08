package it.mycraft.powerlib.commands;

/** Platform-agnostic abstraction over a command issuer. */
public interface CommandSender {
    String getName();
    boolean hasPermission(String permission);
    void sendMessage(String message);
    Object raw(); // platform-native sender for advanced use
}
