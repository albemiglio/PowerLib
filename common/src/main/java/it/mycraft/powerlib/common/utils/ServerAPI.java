package it.mycraft.powerlib.common.utils;

/**
 * Detects the running server platform by probing for platform-specific classes. The detected
 * {@link ServerType} is resolved once at class-load time and exposed via {@code getType()}.
 */
public class ServerAPI {

    private static ServerType type;

    public static ServerType getType() {
        return type;
    }

    static {
        loadType();
    }

    private static void loadType() {
        if (isUsingBukkit()) {
            type = ServerType.BUKKIT;
        }
        else if (isStrictlyUsingBungee()) {
            type = ServerType.BUNGEECORD;
        }
        else if (isUsingVelocity()) {
            type = ServerType.VELOCITY;
        }
        else type = ServerType.OTHER;
    }

    /** @return {@code true} if the Bukkit API is present on the classpath */
    public static boolean isUsingBukkit() {
        try {
            Class.forName("org.bukkit.Bukkit");
            return true;
        } catch(ClassNotFoundException ex) {
            return false;
        }
    }

    /** @return {@code true} if the BungeeCord API is present (may also be true on Velocity via compatibility shims) */
    public static boolean isUsingBungee() { // might throw wrong server types e.g. when using Snap inside Velocity
        try {
            Class.forName("net.md_5.bungee.api.ProxyServer");
            return true;
        } catch(ClassNotFoundException ex) {
            return false;
        }
    }

    /** @return {@code true} only on a real BungeeCord proxy (the {@code BungeeCord} implementation class) */
    public static boolean isStrictlyUsingBungee() {
        try {
            Class.forName("net.md_5.bungee.BungeeCord");
            return true;
        } catch(ClassNotFoundException ex) {
            return false;
        }
    }

    /** @return {@code true} if the Velocity API is present on the classpath */
    public static boolean isUsingVelocity() {
        try {
            Class.forName("com.velocitypowered.api.proxy.ProxyServer");
            return true;
        } catch(ClassNotFoundException ex) {
            return false;
        }
    }

    /** @return {@code true} only on a real Velocity proxy (the {@code Velocity} implementation class) */
    public static boolean isStrictlyUsingVelocity() {
        try {
            Class.forName("com.velocitypowered.proxy.Velocity");
            return true;
        } catch(ClassNotFoundException ex) {
            return false;
        }
    }
}
