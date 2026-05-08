package it.mycraft.powerlib.bukkit.compat;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Resolves Enchantment and PotionEffectType constants by canonical key,
 * supporting both pre-1.20.5 names (DURABILITY, INCREASE_DAMAGE, ...) and
 * post-1.20.5 names (UNBREAKING, STRENGTH, ...).
 *
 * Lookups are cached after first resolution. Caller passes the *canonical*
 * minecraft key (e.g. "unbreaking", "strength", "slowness", "jump_boost",
 * "instant_damage") and gets back whatever the running server exposes.
 */
public final class RegistryCompat {

    private static final Logger LOGGER = Logger.getLogger(RegistryCompat.class.getName());

    private static final Map<String, Enchantment> ENCHANT_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, PotionEffectType> POTION_CACHE = new ConcurrentHashMap<>();

    /** Mapping from canonical key to legacy field name(s) — first match wins. */
    private static final Map<String, String[]> ENCHANT_LEGACY = Map.of(
            "unbreaking", new String[]{"UNBREAKING", "DURABILITY"}
    );

    private static final Map<String, String[]> POTION_LEGACY = Map.of(
            "strength",        new String[]{"STRENGTH",        "INCREASE_DAMAGE"},
            "slowness",        new String[]{"SLOWNESS",        "SLOW"},
            "jump_boost",      new String[]{"JUMP_BOOST",      "JUMP"},
            "instant_damage",  new String[]{"INSTANT_DAMAGE",  "HARM"},
            "instant_health",  new String[]{"INSTANT_HEALTH",  "HEAL"}
    );

    private RegistryCompat() {}

    public static Enchantment glowEnchant() {
        return enchantment("unbreaking");
    }

    public static Enchantment enchantment(String canonicalKey) {
        return ENCHANT_CACHE.computeIfAbsent(canonicalKey, RegistryCompat::resolveEnchant);
    }

    public static PotionEffectType potionEffect(String canonicalKey) {
        return POTION_CACHE.computeIfAbsent(canonicalKey, RegistryCompat::resolvePotion);
    }

    private static Enchantment resolveEnchant(String key) {
        // 1) Modern Registry API (1.20.x+)
        try {
            Enchantment fromRegistry = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(key));
            if (fromRegistry != null) return fromRegistry;
        } catch (Throwable ignored) {
        }
        // 2) Enchantment.getByKey (1.13+)
        try {
            Enchantment byKey = Enchantment.getByKey(NamespacedKey.minecraft(key));
            if (byKey != null) return byKey;
        } catch (Throwable ignored) {
        }
        // 3) Reflection on legacy field names
        String[] candidates = ENCHANT_LEGACY.getOrDefault(key, new String[]{key.toUpperCase()});
        for (String fieldName : candidates) {
            try {
                Field f = Enchantment.class.getField(fieldName);
                Object value = f.get(null);
                if (value instanceof Enchantment) {
                    return (Enchantment) value;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                LOGGER.log(Level.FINE, "Enchantment field " + fieldName + " not found", e);
            }
        }
        throw new IllegalStateException("Cannot resolve Enchantment for key: " + key);
    }

    private static PotionEffectType resolvePotion(String key) {
        // 1) Modern getByKey (1.20.x+ supports keys for most types)
        try {
            PotionEffectType byKey = PotionEffectType.getByKey(NamespacedKey.minecraft(key));
            if (byKey != null) return byKey;
        } catch (Throwable ignored) {
        }
        // 2) Reflection on legacy + new field names
        String[] candidates = POTION_LEGACY.getOrDefault(key, new String[]{key.toUpperCase()});
        for (String fieldName : candidates) {
            try {
                Field f = PotionEffectType.class.getField(fieldName);
                Object value = f.get(null);
                if (value instanceof PotionEffectType) {
                    return (PotionEffectType) value;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                LOGGER.log(Level.FINE, "PotionEffectType field " + fieldName + " not found", e);
            }
        }
        // 3) getByName fallback (deprecated but surviving)
        for (String fieldName : candidates) {
            PotionEffectType byName = PotionEffectType.getByName(fieldName);
            if (byName != null) return byName;
        }
        throw new IllegalStateException("Cannot resolve PotionEffectType for key: " + key);
    }
}
