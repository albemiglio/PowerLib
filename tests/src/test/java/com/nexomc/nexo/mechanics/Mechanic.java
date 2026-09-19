package com.nexomc.nexo.mechanics;

/**
 * Stand-in for Nexo's mechanic base class, under Nexo's package name. PowerLib resolves
 * {@code getItemID()} on this base class once, so every Kotlin furniture subclass stays reachable through
 * a single handle; nothing else of a mechanic is ever read.
 *
 * <p>Its presence alone does not switch {@link it.mycraft.powerlib.bukkit.utils.NexoUtils} on: that bind
 * also needs Nexo's three API classes, which stay deliberately absent from the test classpath.
 */
public abstract class Mechanic {

    public abstract String getItemID();
}
