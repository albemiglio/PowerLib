package it.mycraft.powerlib.bukkit.item;

import it.mycraft.powerlib.bukkit.compat.RegistryCompat;
import it.mycraft.powerlib.bukkit.reflection.ReflectionAPI;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public enum LegacyPotionAPI {
    REGENERATION_0("373:8193", 373, 8193, false, 0, false, "regeneration", 540, "Regeneration"),
    REGENERATION_1("373:8257", 373, 8257, false, 0, true, "regeneration", 2400, "Regeneration"),
    REGENERATION_2("373:8225", 373, 8225, false, 1, false, "regeneration", 264, "Regeneration"),
    REGENERATION_3("373:8289", 373, 8289, false, 1, true, "regeneration", 1200, "Regeneration"),
    REGENERATION_4("373:16385", 373, 16385, true, 0, false, "regeneration", 396, "Regeneration"),
    REGENERATION_5("373:16449", 373, 16449, true, 0, true, "regeneration", 1560, "Regeneration"),
    REGENERATION_6("373:16417", 373, 16417, true, 1, false, "regeneration", 192, "Regeneration"),
    REGENERATION_7("373:16481", 373, 16481, true, 1, true, "regeneration", 540, "Regeneration"),
    SPEED_0("373:8194", 373, 8194, false, 0, false, "speed", 3600, "Swiftness"),
    SPEED_1("373:8258", 373, 8258, false, 0, true, "speed", 9600, "Swiftness"),
    SPEED_2("373:8226", 373, 8226, false, 1, false, "speed", 1560, "Swiftness"),
    SPEED_3("373:8290", 373, 8290, false, 1, true, "speed", 4800, "Swiftness"),
    SPEED_4("373:16386", 373, 16386, true, 0, false, "speed", 2580, "Swiftness"),
    SPEED_5("373:16450", 373, 16450, true, 0, true, "speed", 624, "Swiftness"),
    SPEED_6("373:16418", 373, 16418, true, 1, false, "speed", 1284, "Swiftness"),
    SPEED_7("373:16482", 373, 16482, true, 1, true, "speed", 3600, "Swiftness"),
    FIRE_RESISTANCE_0("373:8195", 373, 8195, false, 0, false, "fire_resistance", 3600, "Fire Resistance"),
    FIRE_RESISTANCE_1("373:8259", 373, 8259, false, 0, true, "fire_resistance", 9600, "Fire Resistance"),
    FIRE_RESISTANCE_2("373:16387", 373, 16387, true, 0, false, "fire_resistance", 2580, "Fire Resistance"),
    FIRE_RESISTANCE_3("373:16451", 373, 16451, true, 0, true, "fire_resistance", 624, "Fire Resistance"),
    POISON_0("373:8196", 373, 8196, false, 0, false, "poison", 540, "Poison"),
    POISON_1("373:8260", 373, 8260, false, 0, true, "poison", 2400, "Poison"),
    POISON_2("373:8228", 373, 8228, false, 1, false, "poison", 264, "Poison"),
    POISON_3("373:8292", 373, 8292, false, 1, true, "poison", 1200, "Poison"),
    POISON_4("373:16388", 373, 16388, true, 0, false, "poison", 396, "Poison"),
    POISON_5("373:16452", 373, 16452, true, 0, true, "poison", 1560, "Poison"),
    POISON_6("373:16420", 373, 16420, true, 1, false, "poison", 192, "Poison"),
    POISON_7("373:16484", 373, 16484, true, 1, true, "poison", 540, "Poison"),
    HEALTH_BOOST_0("373:8197", 373, 8197, false, 0, false, "health_boost", 2400, "Healing"),
    HEALTH_BOOST_1("373:8229", 373, 8229, false, 1, false, "health_boost", 2400, "Healing"),
    HEALTH_BOOST_2("373:16389", 373, 16389, true, 0, false, "health_boost", 2400, "Healing"),
    HEALTH_BOOST_3("373:16421", 373, 16421, true, 1, false, "health_boost", 2400, "Healing"),
    NIGHT_VISION_0("373:8198", 373, 8198, false, 0, false, "night_vision", 3600, "Night Vision"),
    NIGHT_VISION_1("373:8262", 373, 8262, false, 0, true, "night_vision", 9600, "Night Vision"),
    NIGHT_VISION_2("373:16390", 373, 16390, true, 0, false, "night_vision", 2580, "Night Vision"),
    NIGHT_VISION_3("373:16454", 373, 16454, true, 0, true, "night_vision", 624, "Night Vision"),
    WEAKNESS_0("373:8200", 373, 8200, false, 0, false, "weakness", 1560, "Weakness"),
    WEAKNESS_1("373:8264", 373, 8264, false, 0, true, "weakness", 4800, "Weakness"),
    WEAKNESS_2("373:16392", 373, 16392, true, 0, false, "weakness", 1284, "Weakness"),
    WEAKNESS_3("373:16456", 373, 16456, true, 0, true, "weakness", 3600, "Weakness"),
    INCREASE_DAMAGE_0("373:8201", 373, 8201, false, 0, false, "strength", 3600, "Strength"),
    INCREASE_DAMAGE_1("373:8265", 373, 8265, false, 0, true, "strength", 9600, "Strength"),
    INCREASE_DAMAGE_2("373:8233", 373, 8233, false, 1, false, "strength", 1560, "Strength"),
    INCREASE_DAMAGE_3("373:8297", 373, 8297, false, 1, true, "strength", 4800, "Strength"),
    INCREASE_DAMAGE_4("373:16393", 373, 16393, true, 0, false, "strength", 2580, "Strength"),
    INCREASE_DAMAGE_5("373:16457", 373, 16457, true, 0, true, "strength", 624, "Strength"),
    INCREASE_DAMAGE_6("373:16425", 373, 16425, true, 1, false, "strength", 1284, "Strength"),
    INCREASE_DAMAGE_7("373:16489", 373, 16489, true, 1, true, "strength", 3600, "Strength"),
    SLOW_0("373:8202", 373, 8202, false, 0, false, "slowness", 1560, "Slowness"),
    SLOW_1("373:8266", 373, 8266, false, 0, true, "slowness", 4800, "Slowness"),
    SLOW_2("373:16394", 373, 16394, true, 0, false, "slowness", 1284, "Slowness"),
    SLOW_3("373:16458", 373, 16458, true, 0, true, "slowness", 3600, "Slowness"),
    JUMP_0("373:8203", 373, 8203, false, 0, false, "jump_boost", 3600, "Leaping"),
    JUMP_1("373:8267", 373, 8267, false, 0, true, "jump_boost", 9600, "Leaping"),
    JUMP_2("373:8235", 373, 8235, false, 1, false, "jump_boost", 1560, "Leaping"),
    JUMP_3("373:8299", 373, 8299, false, 1, true, "jump_boost", 4800, "Leaping"),
    JUMP_4("373:16395", 373, 16395, true, 0, false, "jump_boost", 2580, "Leaping"),
    JUMP_5("373:16459", 373, 16459, true, 0, true, "jump_boost", 624, "Leaping"),
    JUMP_6("373:16427", 373, 16427, true, 1, false, "jump_boost", 1284, "Leaping"),
    JUMP_7("373:16491", 373, 16491, true, 1, true, "jump_boost", 3600, "Leaping"),
    HARM_0("373:8204", 373, 8204, false, 0, false, "instant_damage", 2400, "Harming"),
    HARM_1("373:8236", 373, 8236, false, 1, false, "instant_damage", 2400, "Harming"),
    HARM_2("373:16396", 373, 16396, true, 0, false, "instant_damage", 2400, "Harming"),
    HARM_3("373:16428", 373, 16428, true, 1, false, "instant_damage", 2400, "Harming"),
    WATER_BREATHING_0("373:8205", 373, 8205, false, 0, false, "water_breathing", 3600, "Water Breathing"),
    WATER_BREATHING_1("373:8269", 373, 8269, false, 0, true, "water_breathing", 9600, "Water Breathing"),
    WATER_BREATHING_2("373:16397", 373, 16397, true, 0, false, "water_breathing", 2580, "Water Breathing"),
    WATER_BREATHING_3("373:16461", 373, 16461, true, 0, true, "water_breathing", 624, "Water Breathing"),
    INVISIBILITY_0("373:8206", 373, 8206, false, 0, false, "invisibility", 3600, "Invisibility"),
    INVISIBILITY_1("373:8270", 373, 8270, false, 0, true, "invisibility", 9600, "Invisibility"),
    INVISIBILITY_2("373:16398", 373, 16398, true, 0, false, "invisibility", 2580, "Invisibility"),
    INVISIBILITY_3("373:16462", 373, 16462, true, 0, true, "invisibility", 624, "Invisibility"),
    ;

    @Getter
    private String totalID, minecraftName;

    @Getter
    private int tier, ID, metadata, duration;

    @Getter
    private boolean extended, isSplash;

    @Getter(AccessLevel.NONE)
    private final String canonicalKey;

    LegacyPotionAPI(String totalID, int ID, int metadata, boolean isSplash, int tier, boolean extended, String canonicalKey, int duration, String minecraftName) {
        this.totalID = totalID;
        this.ID = ID;
        this.metadata = metadata;
        this.isSplash = isSplash;
        this.tier = tier;
        this.extended = extended;
        this.canonicalKey = canonicalKey;
        this.duration = duration;
        this.minecraftName = minecraftName;
    }

    public PotionEffectType getPotionEffectType() {
        return RegistryCompat.potionEffect(canonicalKey);
    }

    /**
     * Create potion from id or metadata
     * @param id The id
     * @return The potion
     */
    public static ItemStack getPotion(String id){
        if(id.contains(":"))
            return getPotion(Integer.parseInt(id.split(":")[1]));

        return getPotion(Integer.parseInt(id));
    }

    /**
     * Create potion from metadata
     * @param metadata The metadata
     * @return The potion
     */
    public static ItemStack getPotion(int metadata) {
        LegacyPotionAPI lp = getLegacyPotion(metadata);

        String name = lp.isSplash ? "&fSplash potion of " : "&fPotion of ";
        String lore;

        long milliseconds = (lp.getDuration() / 20) * 1000L;

        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;

        if (String.valueOf(seconds).toCharArray().length < 2) {
            String s = "0" + String.valueOf(seconds);
            lore = minutes + ":" + s;
        } else {
            lore = minutes + ":" + seconds;
        }

        if (ReflectionAPI.getNumericalVersion() <= 12)
            return new ItemBuilder()
                    .setMaterial(373)
                    .setMetaData((short) lp.getMetadata())
                    .setPotionEffect(lp.getPotionEffectType(), lp.getDuration(), lp.getDuration(), true)
                    .build();

        return new ItemBuilder()
                .setMaterial(373)
                .setName(name + lp.getMinecraftName())
                .setLore("&1" + lp.getMinecraftName() + " (" + lore + ")")
                .setPotionEffect(lp.getPotionEffectType(), lp.getDuration(), lp.getDuration(), true)
                .build();
    }

    /**
     * Find potion from metadata
     * @param metadata The metadata
     * @return The LegacyPotionAPI object
     */
    private static LegacyPotionAPI getLegacyPotion(int metadata) {
        for (LegacyPotionAPI lpa : LegacyPotionAPI.values()) {
            if (lpa.getMetadata() == metadata)
                return lpa;
        }
        return null;
    }
}
