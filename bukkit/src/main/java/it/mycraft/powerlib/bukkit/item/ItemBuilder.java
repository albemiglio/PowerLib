package it.mycraft.powerlib.bukkit.item;

import com.google.common.base.Enums;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.lone.itemsadder.api.CustomStack;
import it.mycraft.powerlib.bukkit.config.ConfigurationAdapter;
import it.mycraft.powerlib.bukkit.reflection.ReflectionAPI;
import it.mycraft.powerlib.common.chat.Message;
import it.mycraft.powerlib.common.configuration.Configuration;
import it.mycraft.powerlib.bukkit.utils.NexoUtils;
import it.mycraft.powerlib.common.objects.Pair;
import it.mycraft.powerlib.common.utils.ColorAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Fluent builder for {@link ItemStack}s, supporting names, lore, enchantments, potion effects, skulls,
 * colored leather armor, placeholders, persistent data, and ItemsAdder/Nexo custom items.
 */
public class ItemBuilder implements Cloneable {

    /**
     * Whether the ItemsAdder plugin is currently enabled on the server.
     *
     * @return true if ItemsAdder is enabled
     */
    public static boolean isUsingItemsAdder() { return Bukkit.getPluginManager().isPluginEnabled("ItemsAdder"); }
    @Getter
    private String material;
    @Getter
    private String name;
    @Getter
    private List<String> lore;
    @Getter
    private int amount = 1;
    @Getter
    private short metadata = 0;
    @Getter
    private int customModelData = 0;
    @Getter
    private boolean glowing = false;
    @Getter
    private HashMap<Enchantment, Integer> enchantments;
    @Getter
    private HashMap<PotionEffect, Boolean> potions;
    @Getter
    private HashMap<String, Object> placeholders;
    private SkullMeta skullMeta;
    private Pair<Optional<String>, Optional<String>> itemsAdderData;
    private final Map<NamespacedKey, Pair<PersistentDataType, Object>> persistentData = new HashMap<>();

    /**
     * Creates an item builder with default values (a single STONE with empty name and lore).
     */
    public ItemBuilder() {
        lore = new ArrayList<>();
        enchantments = new HashMap<>();
        potions = new HashMap<>();
        placeholders = new HashMap<>();
        itemsAdderData = new Pair<>(Optional.empty(), Optional.empty());
    }

    /**
     * Sets the item's material
     *
     * @param material The material
     * @return The ItemBuilder
     */
    public ItemBuilder setMaterial(Material material) {
        this.material = material.toString();
        return this;
    }

    /**
     * Sets the item's material getting it from a String
     *
     * @param material The material
     * @return The ItemBuilder
     */
    public ItemBuilder setMaterial(String material) {
        if (material.length() > 11 && !material.startsWith("itemsadder:")) {
            Optional<Material> optMaterial = Enums.getIfPresent(Material.class, material).toJavaUtil();
            if (optMaterial.isPresent())
                this.material = optMaterial.get().toString();
        } else {
            this.material = material;
        }
        return this;
    }

    /**
     * Sets the item's material getting it from an int ID. 1.13+
     *
     * @param id The ID
     * @return The ItemBuilder
     */
    public ItemBuilder setMaterial(int id) {
        this.material = LegacyItemAPI.getMaterial(id).toString();
        return this;
    }

    /**
     * Sets the item's material data
     *
     * @param id   The ID
     * @param data The Data
     * @return The ItemBuilder
     */
    public ItemBuilder setMaterial(int id, int data) {
        this.material = LegacyItemAPI.getMaterial(id, data).toString();
        return this;
    }

    /**
     * Sets the item's custom name
     *
     * @param name The name
     * @return The ItemBuilder
     */
    public ItemBuilder setName(String name) {
        this.name = ColorAPI.color(name);
        return this;
    }

    /**
     * Sets the item's lore Array
     *
     * @param lore The lore array
     * @return The ItemBuilder
     */
    public ItemBuilder setLore(String... lore) {
        setLore(Arrays.asList(lore));
        return this;
    }

    /**
     * Sets the item's lore List
     *
     * @param lore The lore list
     * @return The ItemBuilder
     */
    public ItemBuilder setLore(List<String> lore) {
        this.lore = ColorAPI.color(lore);
        return this;
    }

    /**
     * Sets the item's enchantment
     *
     * @param enchantment The enchantment
     * @param level       The level
     * @return The ItemBuilder
     */
    public ItemBuilder setEnchantment(Enchantment enchantment, Integer level) {
        enchantments.put(enchantment, level);
        return this;
    }


    /**
     * Sets the potion's effect
     *
     * @param type     The effect type
     * @param duration The duration measured in ticks
     * @param level    The amplifier
     * @return The ItemBuilder
     */
    public ItemBuilder setPotionEffect(PotionEffectType type, int duration, int level) {
        return setPotionEffect(type, duration, level, true, true, true);
    }

    /**
     * Sets the potion's effect
     *
     * @param type      The potion effect type to add
     * @param duration  The duration measured in ticks
     * @param level     The amplifier
     * @param overwrite true if any existing effect of the same type should be overwritten
     * @return The ItemBuilder
     */
    public ItemBuilder setPotionEffect(PotionEffectType type, int duration, int level, boolean overwrite) {
        return setPotionEffect(type, duration, level, overwrite, true, true);
    }

    /**
     * Sets the potion's effect
     *
     * @param type      The potion effect type to add
     * @param duration  The duration measured in ticks
     * @param level     The amplifier
     * @param overwrite true if any existing effect of the same type should be overwritten
     * @param ambient   The ambient status
     * @return The ItemBuilder
     */
    public ItemBuilder setPotionEffect(PotionEffectType type, int duration, int level, boolean overwrite, boolean ambient) {
        return setPotionEffect(type, duration, level, overwrite, ambient, true);
    }

    /**
     * Sets the potion's effect
     *
     * @param type      The potion effect type to add
     * @param duration  The duration measured in ticks
     * @param level     The amplifier
     * @param overwrite true if any existing effect of the same type should be overwritten
     * @param ambient   The ambient status
     * @param particles The particle status
     * @return The ItemBuilder
     */
    public ItemBuilder setPotionEffect(PotionEffectType type, int duration, int level, boolean overwrite, boolean ambient, boolean particles) {
        potions.put(new PotionEffect(type, duration, (level - 1), overwrite, ambient), particles);
        return this;
    }

    /**
     * Sets the item's amount
     *
     * @param amount The amount
     * @return The ItemBuilder
     */
    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    /**
     * Dets the item's metadata (for older Minecraft versions)
     *
     * @param metadata The metadata
     * @return The ItemBuilder
     */
    public ItemBuilder setMetaData(short metadata) {
        this.metadata = metadata;
        return this;
    }

    /**
     * Sets the item's itemGlowing
     *
     * @param itemGlowing True to set item itemGlowing
     * @return The ItemBuilder
     */
    public ItemBuilder setGlowing(boolean itemGlowing) {
        this.glowing = itemGlowing;
        return this;
    }

    /**
     * Sets the item's custom model data
     *
     * @param customModelData The custom model data value
     * @return The ItemBuilder
     */
    public ItemBuilder setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    /**
     * Stores a value in the item's {@link org.bukkit.persistence.PersistentDataContainer}, written on {@link #build()}.
     *
     * @param <T>   the primitive type stored in the container
     * @param <Z>   the complex type exposed to callers
     * @param key   the namespaced key to store under
     * @param type  the persistent data type
     * @param value the value to store
     * @return The ItemBuilder
     */
    public <T, Z> ItemBuilder setPersistentData(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        persistentData.put(key, new Pair<>(type, value));
        return this;
    }

    /**
     * Clones another itemstack into the builder and stores its data
     *
     * @param itemStack The ItemStack to clone
     * @return The ItemBuilder
     */
    public ItemBuilder clone(ItemStack itemStack) {
        if(itemStack == null || itemStack.getType() == Material.AIR) {
            return this;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        material = itemStack.getType().toString();
        amount = itemStack.getAmount();
        metadata = itemStack.getDurability();

        if (itemMeta == null) {
            material = "AIR";
            name = null;
            lore.clear();
            enchantments.clear();
            potions.clear();
            placeholders.clear();
            return this;
        }

        name = itemMeta.getDisplayName();
        if (itemMeta.hasLore())
            lore = itemMeta.getLore();

        if (itemMeta.hasEnchants() && itemMeta.getItemFlags().contains(ItemFlag.HIDE_ENCHANTS))
            glowing = true;

        if (itemMeta.hasCustomModelData()) {
            customModelData = itemMeta.getCustomModelData();
        }

        if (itemMeta instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) itemMeta;
            for (PotionEffect potionEffect : potionMeta.getCustomEffects()) {
                potions.put(potionEffect, true);
            }
        }

        NBTItem nbtItem = new NBTItem(itemStack);
        NBTCompound comp = nbtItem.getCompound("itemsadder");
        if(isUsingItemsAdder() && comp != null) {
            itemsAdderData = new Pair<>(Optional.ofNullable(comp.getString("namespace")), Optional.ofNullable(comp.getString("id")));
        }
        return this;
    }

    /**
     * Sets a custom skin to a skull
     *
     * @param itemBuilder The itemBuilder
     * @param url         The minecraft.net url
     * @return The skull with a non-player skin
     */
    private ItemStack setCustomSkin(ItemBuilder itemBuilder, String url) {
        int version = ReflectionAPI.getNumericalVersion();
        String material = version >= 13 ? "PLAYER_HEAD" : "SKULL_ITEM";
        ItemStack skull = itemBuilder.setMaterial(material).setMetaData((byte) 3).setAmount(1).setName(name).setLore(lore).build();
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData;

        encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());

        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
            new Message("Error while creating an head with custom skin!", "Message: " + ex.getMessage()).sendConsole();
            return null;
        }

        skull.setItemMeta(skullMeta);
        return skull;
    }

    /**
     * Sets a player's skin to a skull
     *
     * @param uuid The player's uuid
     * @return The skull with a player skin
     */
    private ItemBuilder setPlayerSkin(UUID uuid) {
        int version = ReflectionAPI.getNumericalVersion();
        material = version >= 13 ? "PLAYER_HEAD" : "SKULL_ITEM";
        amount = 1;
        metadata = (short) 3;

        SkullMeta skullMeta = (SkullMeta) new ItemStack(Material.getMaterial(material)).getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        this.skullMeta = skullMeta;
        return this;
    }

    /**
     * Sets a custom RGB color to a leather armor
     *
     * @param red         The red value
     * @param green       The green value
     * @param blue        The blue value
     * @param itemBuilder The itemBuilder
     * @return The armor with a custom color
     */
    private ItemStack setColorToArmor(int red, int green, int blue, ItemBuilder itemBuilder) {
        ItemStack leatherArmor = itemBuilder.setMaterial(material).setName(name).setLore(lore).build();
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) leatherArmor.getItemMeta();
        try {
            Color color = Color.fromRGB(red, green, blue);
            leatherArmorMeta.setColor(color);
        } catch (Exception ignored) {
        }
        leatherArmor.setItemMeta(leatherArmorMeta);
        return leatherArmor;
    }

    /**
     * Adds a custom value on a placeholder to the item's name and lore
     *
     * @param placeholder The placeholder
     * @param value       The value
     * @return The ItemBuilder
     */
    public ItemBuilder addPlaceHolder(String placeholder, Object value) {
        placeholders.put(placeholder, value);
        return this;
    }

    /**
     * Builds an ItemStack with the data provided previously
     *
     * @return The ItemStack
     */
    public ItemStack build() {
        // Fail‐safe defaults
        if (this.material == null) {
            this.material = "STONE";
        }
        if (this.name == null) {
            this.name = "";
        }
        if (this.lore == null) {
            this.lore = new ArrayList<>();
        }

        for (String placeholder : placeholders.keySet()) {
            Object value = placeholders.getOrDefault(placeholder, "NULL");
            setName(this.name.replace(placeholder, value.toString()));
            setLore(this.lore.stream().map((s) -> s.replace(placeholder, value.toString()))
                    .collect(Collectors.toList()));
        }
        String name = this.name;
        List<String> lore = this.lore == null ? null : new ArrayList<>(this.lore);

        ItemStack itemStack;
        try {
            if (material.startsWith("itemsadder:") && isUsingItemsAdder()) {
                String customItem = material.substring("itemsadder:".length());
                if (CustomStack.isInRegistry(customItem)) {
                    itemStack = CustomStack.getInstance(customItem).getItemStack();
                } else {
                    // fallback if custom stack missing
                    itemStack = new ItemStack(Material.BARRIER, amount, metadata);
                }
            } else if (material.startsWith("nexo:")) {
                ItemStack nexoItem = NexoUtils.itemStackFromId(material.substring("nexo:".length()));
                if (nexoItem != null) {
                    itemStack = nexoItem;
                    itemStack.setAmount(amount);
                } else {
                    // fallback if the Nexo item is missing / Nexo unavailable
                    itemStack = new ItemStack(Material.BARRIER, amount, metadata);
                }
            } else {
                Material m = Material.getMaterial(material);
                if (m == null) {
                    // fallback if material invalid
                    m = Material.STONE;
                }
                itemStack = new ItemStack(m, amount, metadata);
            }
        } catch (Exception e) {
            // on any error, return a simple stone
            Bukkit.getLogger().warning("ItemBuilder.build() failed: " + e.getMessage());
            return new ItemStack(Material.STONE, 1);
        }

        if (itemStack == null || itemStack.getType().isAir()) {
            return itemStack == null ? new ItemStack(Material.STONE, 1) : itemStack;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            if (!name.isEmpty()) {
                itemMeta.setDisplayName(name);
            }
            if (!lore.isEmpty()) {
                itemMeta.setLore(lore);
            }

            if (glowing && enchantments.isEmpty()) {
                itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            if (!enchantments.isEmpty()) {
                for (Enchantment enchantment : enchantments.keySet()) {
                    itemMeta.addEnchant(enchantment, enchantments.get(enchantment), true);
                }
            }

            if (customModelData != 0) {
                itemMeta.setCustomModelData(customModelData);
            }

            for (Map.Entry<NamespacedKey, Pair<PersistentDataType, Object>> entry : persistentData.entrySet()) {
                itemMeta.getPersistentDataContainer().set(entry.getKey(), entry.getValue().getLeft(), entry.getValue().getRight());
            }

            itemStack.setItemMeta(itemMeta);
        }

        if (itemStack.getType() == Material.POTION && !potions.isEmpty()) {
            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            for (PotionEffect potionEffect : potions.keySet()) {
                potionMeta.addCustomEffect(potionEffect, potions.get(potionEffect));
            }
            itemStack.setItemMeta(potionMeta);
        }

        if(itemsAdderData.getLeft().isPresent() && itemsAdderData.getRight().isPresent()) {
            NBTItem nbtItem = new NBTItem(itemStack);
            NBTCompound comp = nbtItem.getOrCreateCompound("itemsadder");
            comp.setString("namespace", itemsAdderData.getLeft().get());
            comp.setString("id", itemsAdderData.getRight().get());
            nbtItem.applyNBT(itemStack);
        }
        return itemStack;
    }

    /**
     * Uses the skin URL to make a custom skull
     *
     * @param skinURL The skin url
     * @return The ItemStack
     */
    public ItemStack customHeadBuild(String skinURL) {
        if (!skinURL.endsWith("="))
            return setCustomSkin(this, "http://textures.minecraft.net/texture/" + skinURL);

        return setCustomSkin(this, base64Decoder(skinURL));
    }

    /**
     * This method is used for extracting the minecraft.net link from the base64 text
     *
     * @param base64 The base64 text
     * @return The minecraft.net link
     */
    private String base64Decoder(String base64) {
        byte[] decoded = Base64.getDecoder().decode(base64);
        String s = new String(decoded);

        return s.replace("\"}}}", "").split(":\"")[1];
    }

    /**
     * Uses the player's name to make a player skull
     *
     * @param playerName The player name
     * @return The ItemStack
     */
    public ItemBuilder setPlayerHead(String playerName) {
        return setPlayerSkin(Bukkit.getOfflinePlayer(playerName).getUniqueId());
    }

    /**
     * Uses the player's uuid to make a player skull
     *
     * @param uuid The player's uuid
     * @return The ItemStack
     */
    public ItemBuilder setPlayerHead(UUID uuid) {
        return setPlayerSkin(uuid);
    }

    /**
     * Uses RGB color values to set a custom color to a leather armor
     *
     * @param red   The red value
     * @param green The green value
     * @param blue  The blue value
     * @return The ItemStack
     */
    public ItemStack coloredArmorBuild(int red, int green, int blue) {
        return setColorToArmor(red, green, blue, this);
    }

    /**
     * Looks for any item's info in the provided FileConfiguration and builds an ItemStack from it
     *
     * @param fileConfiguration The file configuration to get the item's info from
     * @param path              The section where the item's info are stored
     * @return The related ItemBuilder
     */
    public ItemBuilder fromConfig(FileConfiguration fileConfiguration, String path) {
        return fromConfig(ConfigurationAdapter.adapt(fileConfiguration), path);
    }

    /**
     * Looks for any item's info in the provided NATIVE Configuration and builds an ItemStack from it
     *
     * @param configuration NATIVE CONFIGURATION to get the item's info from
     * @param path          The section where the item's info are stored
     * @return The related ItemBuilder
     */
    public ItemBuilder fromConfig(Configuration configuration, String path) {
        boolean itemLegacy = false;
        boolean itemGlowing = false;
        String newPath;
        String itemMaterial = "STONE";
        String itemName = null;
        List<String> itemLore = null;
        int itemAmount = 1;
        int customModelData = 0;
        short itemMetadata = 0;

        for (String s : configuration.getSection(path).getKeys()) {
            switch (s) {
                case "legacy":
                    newPath = path + ".legacy";
                    itemLegacy = configuration.getBoolean(newPath);
                    break;
                case "material":
                    newPath = path + ".material";
                    itemMaterial = itemLegacy ? "LEGACY_" + configuration.getString(newPath) :
                            configuration.getString(newPath);
                    break;
                case "name":
                    newPath = path + ".name";
                    itemName = configuration.getString(newPath);
                    break;
                case "lore":
                    newPath = path + ".lore";
                    itemLore = configuration.getStringList(newPath);
                    break;
                case "amount":
                    newPath = path + ".amount";
                    itemAmount = configuration.getInt(newPath);
                    break;
                case "metadata":
                    newPath = path + ".metadata";
                    itemMetadata = (short) configuration.getInt(newPath);
                    break;
                case "glowing":
                    newPath = path + ".glowing";
                    itemGlowing = configuration.getBoolean(newPath);
                    break;
                case "customModelData":
                    newPath = path + ".customModelData";
                    customModelData = configuration.getInt(newPath);
                default:
                    break;
            }
        }
        return this.setMaterial(itemMaterial)
                .setName(itemName)
                .setLore(itemLore)
                .setAmount(itemAmount).setMetaData(itemMetadata)
                .setCustomModelData(customModelData)
                .setGlowing(itemGlowing);
    }

    /**
     * Translates hex color codes in the item's name and lore
     *
     * @return The ItemBuilder
     */
    public ItemBuilder hex() {
        setName(ColorAPI.hex(getName()));
        setLore(ColorAPI.hex(getLore()));
        return this;
    }

    /**
     * Just puts in the ItemBuilder object its default values
     */
    private void reset() {
        material = "STONE";
        name = "";
        glowing = false;
        lore = new ArrayList<>();
        enchantments = new HashMap<>();
        potions = new HashMap<>();
        placeholders = new HashMap<>();
        amount = 1;
        metadata = 0;
    }

    @Override
    public ItemBuilder clone() {
        try {
            ItemBuilder clone = (ItemBuilder) super.clone();
            clone.enchantments = new HashMap<>(enchantments);
            clone.potions = new HashMap<>(potions);
            clone.placeholders = new HashMap<>(placeholders);
            clone.lore = new ArrayList<>(lore);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
