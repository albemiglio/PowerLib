package it.mycraft.powerlib.common.utils;

/**
 * The kind of server platform the library is running on.
 */
public enum ServerType {

    /** A Bukkit/Spigot/Paper game server. */
    BUKKIT,
    /** A BungeeCord proxy. */
    BUNGEECORD,
    /** A Velocity proxy. */
    VELOCITY,
    /** An unrecognized platform. */
    OTHER
}
