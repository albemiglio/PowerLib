package it.mycraft.powerlib.bukkit.reflection;

import it.mycraft.powerlib.bukkit.PowerLib;
import it.mycraft.powerlib.common.chat.Message;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

public class ReflectionAPI {

    /**
     * The legacy CraftBukkit package version segment (e.g. "v1_16_R3"), or null on
     * 1.17+/1.20.5+ where the server package is no longer relocated/versioned.
     */
    @Getter
    private static final String version = resolveLegacyPackageVersion();

    /**
     * The server's numerical (minor) Minecraft version, e.g. 21 for 1.21.x.
     */
    @Getter
    private static final int numericalVersion = resolveNumericalVersion();

    private static String resolveLegacyPackageVersion() {
        String[] parts = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        if (parts.length >= 4 && parts[3].startsWith("v")) {
            return parts[3];
        }
        return null;
    }

    private static int resolveNumericalVersion() {
        try {
            String[] parts = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
            return parts.length >= 2 ? Integer.parseInt(parts[1]) : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @param name The path of a 'net.minecraft.server.v1_X_RX.' class
     * @return The NMS class object
     */
    public static Class<?> getNMSClass(String name) {
        try {
            String path = (version != null)
                    ? "net.minecraft.server." + version + "." + name
                    : "net.minecraft." + name;
            return Class.forName(path);
        } catch (ClassNotFoundException ex) {
            new Message("Error while finding a " + name + " NMS Class!", ex.getMessage()).sendConsole();
            return null;
        }
    }

    /**
     * @param name The path of a generic class
     * @return The class object
     */
    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException ex) {
            new Message("Error while finding a " + name + " Class!", ex.getMessage()).sendConsole();
            return null;
        }
    }

    /**
     * @param name The path of a 'org.bukkit.craftbukkit.v1_X_RX.' class
     * @return The OBC class object
     */
    public static Class<?> getOBCClass(String name) {
        try {
            String base = (version != null)
                    ? "org.bukkit.craftbukkit." + version + "."
                    : "org.bukkit.craftbukkit.";
            return Class.forName(base + name);
        } catch (ClassNotFoundException ex) {
            new Message("Error while finding a " + name + " OBC Class!", ex.getMessage()).sendConsole();
            return null;
        }
    }

    /**
     * @param name The path of a 'org.bukkit.' class
     * @return The Bukkit class object
     */
    public static Class<?> getBukkitClass(String name) {
        try {
            return Class.forName("org.bukkit." + name);
        } catch (ClassNotFoundException ex) {
            new Message("Error while finding a " + name + " Bukkit Class!", ex.getMessage()).sendConsole();
            return null;
        }
    }

    /**
     * Sends a custom Title and Subtitle to a player
     *
     * @param player   The player to display the title to
     * @param title    The title being sent
     * @param subtitle The subtitle being sent
     * @param fadeIn   The "fade-in" time
     * @param stay     The "stay" time
     * @param fadeOut  The "fade-out" time
     */
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if(getNumericalVersion() > 16) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
        else try {
            Class<?> PacketPlayOutTitleClass = ReflectionAPI.getNMSClass("PacketPlayOutTitle");
            Class<?> IChatBaseComponentClass = ReflectionAPI.getNMSClass("IChatBaseComponent");

            Constructor<?> constructor = PacketPlayOutTitleClass.getConstructor(PacketPlayOutTitleClass
                    .getDeclaredClasses()[0], IChatBaseComponentClass, int.class, int.class, int.class);

            Object titleComponent = IChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\":\"" + title + "\"}");
            Object subTitleComponent = IChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\":\"" + subtitle + "\"}");

            Object titlePacket = constructor.newInstance(PacketPlayOutTitleClass
                    .getDeclaredClasses()[0].getField("TITLE").get(null), titleComponent, fadeIn, stay, fadeOut);
            Object subTitlePacket = constructor.newInstance(PacketPlayOutTitleClass
                    .getDeclaredClasses()[0].getField("SUBTITLE")
                    .get(null), subTitleComponent, fadeIn, stay, fadeOut);

            sendPacket(player, titlePacket);
            sendPacket(player, subTitlePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a packet to a certain player
     *
     * @param player The player to send the packet to
     * @param packet The packet being sent
     */
    public static void sendPacket(Player player, Object packet) {
        try {
            Object playerHandle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player);
            Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
            playerConnection.getClass().getMethod("sendPacket", new Class[]{ReflectionAPI.getNMSClass("Packet")})
                    .invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
