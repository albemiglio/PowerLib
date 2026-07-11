package it.mycraft.powerlib.bukkit.item;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class ItemUtils {

    public static boolean compare(ItemStack i1, ItemStack i2, boolean ignoreAmount) {
        if (i1 == null || i2 == null || i1.getType() == Material.AIR || i2.getType() == Material.AIR) return false;
        if (i1 == i2) return true;
        if (!ignoreAmount && i1.getAmount() != i2.getAmount()) return false;
        return i1.isSimilar(i2);
    }

    public static boolean compare(ItemStack i1, ItemStack i2) {
        return compare(i1, i2, true);
    }

    public static ItemStack playerHead(UUID owner) {
        ItemStack item = new ItemBuilder().setMaterial(Material.PLAYER_HEAD).build();
        return applyPlayerHeadOwner(item, owner);
    }

    public static ItemStack playerHead(OfflinePlayer owner) {
        ItemStack item = new ItemBuilder().setMaterial(Material.PLAYER_HEAD).build();
        return applyPlayerHeadOwner(item, owner);
    }

    public static ItemStack applyPlayerHeadOwner(ItemStack item, UUID owner) {
        return applyPlayerHeadOwner(item, owner == null ? null : Bukkit.getPlayer(owner));
    }

    public static ItemStack applyPlayerHeadOwner(ItemStack item, OfflinePlayer owner) {
        if (item == null || item.getType() != Material.PLAYER_HEAD || owner == null) {
            return item;
        }

        Player onlinePlayer = owner instanceof Player player ? player : owner.getPlayer();
        if (onlinePlayer == null || !onlinePlayer.isOnline()) {
            return item;
        }

        ItemStack copy = item.clone();
        ItemMeta meta = copy.getItemMeta();
        if (meta instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(onlinePlayer);
            copy.setItemMeta(skullMeta);
        }
        return copy;
    }
}
