package it.mycraft.powerlib.bukkit.utils;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class NexoUtils {

    private static Class<?> nexoFurnitureClass;
    private static Class<?> nexoBlocksClass;
    private static Class<?> nexoItemsClass;
    private static Method nexoFurnFromEntity;
    private static Method nexoFurnFromBlock;
    private static Method nexoBlockFromBlock;
    private static Method nexoIdFromItem;

    static {
        loadReflection();
    }

    private static void loadReflection() {
        try {
            nexoFurnitureClass = Class.forName("com.nexomc.nexo.api.NexoFurniture");
            try {
                nexoFurnFromEntity = nexoFurnitureClass.getMethod("furnitureFromEntity", Entity.class);
            } catch (Exception ignored) {}
            try {
                nexoFurnFromBlock = nexoFurnitureClass.getMethod("furnitureMechanic", Block.class);
            } catch (Exception ignored) {}
            if (nexoFurnFromBlock == null) {
                try {
                    nexoFurnFromBlock = nexoFurnitureClass.getMethod("furnitureFromBlock", Block.class);
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}

        try {
            nexoBlocksClass = Class.forName("com.nexomc.nexo.api.NexoBlocks");
            nexoBlockFromBlock = nexoBlocksClass.getMethod("blockFromBlock", Block.class);
        } catch (Exception ignored) {}

        try {
            nexoItemsClass = Class.forName("com.nexomc.nexo.api.NexoItems");
            nexoIdFromItem = nexoItemsClass.getMethod("idFromItem", ItemStack.class);
        } catch (Exception ignored) {}
    }

    public static String getNexoId(Block block) {
        if (nexoBlockFromBlock != null && block != null) {
            try {
                Object mechanic = nexoBlockFromBlock.invoke(null, block);
                if (mechanic != null) {
                    Method getId = mechanic.getClass().getMethod("getItemID");
                    return (String) getId.invoke(mechanic);
                }
            } catch (Exception ignored) {}
        }

        if (nexoFurnFromBlock != null && block != null) {
            try {
                Object mechanic = nexoFurnFromBlock.invoke(null, block);
                if (mechanic != null) {
                    Method getId = mechanic.getClass().getMethod("getItemID");
                    return (String) getId.invoke(mechanic);
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    public static String getNexoId(Entity entity) {
        if (nexoFurnFromEntity != null && entity != null) {
            try {
                Object furniture = nexoFurnFromEntity.invoke(null, entity);
                if (furniture != null) {
                    Method getId = furniture.getClass().getMethod("getItemID");
                    return (String) getId.invoke(furniture);
                }
            } catch (Exception ignored) {}
        }
        return null;
    }
    
    public static String getNexoId(ItemStack item) {
        if (nexoIdFromItem != null && item != null && !item.getType().isAir()) {
            try {
                return (String) nexoIdFromItem.invoke(null, item);
            } catch (Exception ignored) {}
        }
        return null;
    }

    public static boolean isNexoItem(ItemStack item, String nexoId) {
        String id = getNexoId(item);
        return id != null && id.equalsIgnoreCase(nexoId);
    }
}

