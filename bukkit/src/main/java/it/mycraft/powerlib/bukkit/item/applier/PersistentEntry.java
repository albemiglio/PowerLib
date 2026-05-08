package it.mycraft.powerlib.bukkit.item.applier;

import org.bukkit.persistence.PersistentDataType;

/**
 * Type-safe pairing of a PersistentDataType and its value.
 * The wildcard erasure on the map side is unavoidable; this record keeps
 * the generics tight at construction site.
 */
public record PersistentEntry<T, Z>(PersistentDataType<T, Z> type, Z value) {
}
