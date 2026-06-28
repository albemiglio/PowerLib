package it.mycraft.powerlib.bukkit.inventory;

import it.mycraft.powerlib.common.utils.ColorAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Fluent builder for chest {@link Inventory}s, with helpers to fill rows, columns, borders, and
 * chess-patterned borders, place items by slot, and open the result to a player.
 */
public class InventoryBuilder {


    private int size;
    private String title;
    private Map<Integer, ItemStack> setItem;
    private List<ItemStack> fillChessBorder;
    private ItemStack fillInventory;
    private ItemStack fillBorder;
    private InventoryHolder inventoryHolder;

    /**
     * Creates an empty inventory builder.
     */
    public InventoryBuilder() {
        setItem = new HashMap<>();
        fillChessBorder = new ArrayList<>();
        inventoryHolder = null;
    }

    /**
     * Sets the inventoryholder
     *
     * @param holder InventoryHolder obj
     * @return The InventoryBuilder
     */
    public InventoryBuilder setHolder(InventoryHolder holder) {
        this.inventoryHolder = holder;
        return this;
    }

    /**
     * Sets the rows' number
     *
     * @param rows Amount of rows
     * @return The InventoryBuilder
     */
    public InventoryBuilder setRows(int rows) {
        this.size = rows * 9;
        return this;
    }

    /**
     * Sets the inventory title
     *
     * @param title The title
     * @return The InventoryBuilder
     */
    public InventoryBuilder setTitle(String title) {
        this.title = ColorAPI.color(title);
        return this;
    }

    /**
     * Gets the current (placeholder-resolved) inventory title.
     *
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets an item in the inventory
     *
     * @param slot      The slot
     * @param itemStack The item to set
     * @return The InventoryBuilder
     */
    public InventoryBuilder setItem(Integer slot, ItemStack itemStack) {
        this.setItem.put(slot, itemStack);
        return this;
    }

    /**
     * Sets an item in an entire row
     *
     * @param row       The row
     * @param itemStack The item to set
     * @return The InventoryBuilder
     */
    public InventoryBuilder fillRow(Integer row, ItemStack itemStack) {
        setRowAndColumnItems(row, 0, itemStack);
        return this;
    }

    /**
     * Sets an item in an entire column
     *
     * @param column    The column
     * @param itemStack The item to set
     * @return The InventoryBuilder
     */
    public InventoryBuilder fillColumn(Integer column, ItemStack itemStack) {
        setRowAndColumnItems(0, column, itemStack);
        return this;
    }

    /**
     * Fills the inventory's borders with an itemstack
     *
     * @param itemStack the item to fill the inventory's borders with
     * @return The InventoryBuilder
     */
    public InventoryBuilder fillBorder(ItemStack itemStack) {
        this.fillBorder = itemStack;
        return this;
    }

    /**
     * Fills the inventory's borders with two itemstacks in a chess-like style
     *
     * @param itemStack1 the one item to fill the inventory's borders with
     * @param itemStack2 the other item to fill the inventory's borders with
     * @return The InventoryBuilder
     */
    public InventoryBuilder fillChessBorder(ItemStack itemStack1, ItemStack itemStack2) {
        fillChessBorder.addAll(Arrays.asList(itemStack1, itemStack2));
        return this;
    }

    /**
     * Sets an item in the entire inventory
     *
     * @param itemStack The item
     * @return The InventoryBuilder
     */
    public InventoryBuilder fillInventory(ItemStack itemStack) {
        this.fillInventory = itemStack;
        return this;
    }

    /**
     * Builds an Inventory with the itemstacks provided previously
     *
     * @return The Inventory
     */
    public Inventory build() {
        Inventory inventory = Bukkit.createInventory(inventoryHolder, size, title);

        if (fillInventory != null) {
            fillInventory(inventory);
        }

        if (fillBorder != null)
            fillBorder(inventory);

        if (!fillChessBorder.isEmpty())
            fillChessBorder(inventory);

        setItems(inventory);

        return inventory;
    }

    /**
     * Opens the built inventory to the player
     *
     * @param player The player
     */
    public void open(Player player) {
        open(player, this);
    }

    /**
     * Opens a built inventory to the player
     *
     * @param player           The player
     * @param inventoryBuilder The InventoryBuilder
     */
    private void open(Player player, InventoryBuilder inventoryBuilder) {
        player.openInventory(inventoryBuilder.build());
    }

    /**
     * Fills an inventory with an item
     *
     * @param inventory The inventory
     */
    private void fillInventory(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, fillInventory);
    }

    /**
     * Fills the inventory's borders with an itemstack
     *
     * @param inventory The inventory
     */
    private void fillBorder(Inventory inventory) {
        for (int i = 1; i <= 2; i++) {
            int max = i == 2 ? (inventory.getSize() - 1) : (i * 9) - 1;
            int min = max - 8;
            for (int ignored; min <= max; min++) {
                inventory.setItem(min, fillBorder);
            }
        }

        for (int i = 1; i < 10; i = i + 8) {
            for (int j = i - 1; j < inventory.getSize(); j = j + 9) {
                inventory.setItem(j, fillBorder);
            }
        }
    }

    /**
     * Fills the inventory's borders with two itemstacks in a chess-like style
     *
     * @param inventory The inventory
     * @return The InventoryBuilder
     */
    private void fillChessBorder(Inventory inventory) {
        int inventorySize = inventory.getSize();
        boolean bool = true;
        ItemStack itemStack = bool ? fillChessBorder.get(0) : fillChessBorder.get(1);
        int slot = 0;

        for (int ignored; slot < 9; slot++) {
            inventory.setItem(slot, itemStack);
            bool = !bool;
            itemStack = bool ? fillChessBorder.get(0) : fillChessBorder.get(1);
        }
        if (inventorySize == 9)
            return;

        int t = ((inventorySize / 9) - 2);
        for (int j = 0; j < t; j++) {
            inventory.setItem(slot, itemStack);
            slot = slot + 8;
            inventory.setItem(slot, itemStack);
            slot++;
            bool = !bool;
            itemStack = bool ? fillChessBorder.get(0) : fillChessBorder.get(1);
        }

        if (t == 5) {
            bool = !bool;
            itemStack = bool ? fillChessBorder.get(0) : fillChessBorder.get(1);
        }

        slot = inventorySize - 9;
        for (int ignored = 0; slot < inventorySize; slot++) {
            inventory.setItem(slot, itemStack);
            bool = !bool;
            itemStack = bool ? fillChessBorder.get(0) : fillChessBorder.get(1);
        }
    }

    private void setRowAndColumnItems(int row, int column, ItemStack itemStack) {
        if (row != 0) {
            int max = (row * 9) - 1;
            int min = max - 8;
            for (int ignored; min <= max; min++) {
                setItem.put(min, itemStack);
            }
        }

        if (column != 0) {
            int max = (row * 9) - 1;
            int min = max - 8;
            for (int i = column - 1; i < size; i = i + 9) {
                setItem.put(i, itemStack);
            }
        }

    }

    /**
     * Sets the provided items in an inventory
     *
     * @param inventory The inventory
     * @return The inventory filled
     */
    private void setItems(Inventory inventory) {
        if (setItem.size() != 0) {
            for (Integer integer : setItem.keySet()) {
                inventory.setItem(integer, setItem.get(integer));
            }
        }
    }

    /**
     * Adds a custom value on a placeholder to the inventory's title
     *
     * @param placeholder The placeholder
     * @param value       The value
     * @return The InventoryBuilder
     */
    public InventoryBuilder addPlaceHolder(String placeholder, Object value) {
        title = title.replace(placeholder, String.valueOf(value));

        return this;
    }
}
