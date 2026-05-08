package it.mycraft.powerlib.bukkit.item.applier;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public final class PersistentDataApplier {

    private PersistentDataApplier() {}

    public static void apply(ItemMeta meta, Map<NamespacedKey, PersistentEntry<?, ?>> data) {
        if (meta == null || data == null || data.isEmpty()) return;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        for (Map.Entry<NamespacedKey, PersistentEntry<?, ?>> e : data.entrySet()) {
            applyOne(container, e.getKey(), e.getValue());
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void applyOne(PersistentDataContainer container, NamespacedKey key, PersistentEntry<?, ?> entry) {
        // Erasure: the value's runtime type matches the type token by construction.
        PersistentDataType type = entry.type();
        container.set(key, type, entry.value());
    }
}
