package it.mycraft.powerlib.bukkit.utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class NexoUtils {

    private static Class<?> nexoFurnitureClass;
    private static Class<?> nexoBlocksClass;
    private static Class<?> nexoItemsClass;
    private static Method nexoFurnFromEntity;
    private static Method nexoFurnFromBlock;
    private static Method nexoBaseFromEntity;
    private static Method nexoBaseFromBlock;
    private static Method nexoBaseFromLocation;
    private static Method nexoPlaceWithYaw;
    private static Method nexoIsFurnitureString;
    private static Method nexoUpdateFurniture;
    private static Method nexoConvertFurniture;
    private static Method nexoRemoveEntity;
    private static Method nexoRemoveLocation;
    private static Method nexoRemoveEntityPlayer;
    private static Method nexoRemoveLocationPlayer;
    private static Method nexoRemoveEntityOnly;
    private static Method nexoRemoveLocationOnly;
    private static Method nexoBlockFromBlock;
    private static Method nexoIdFromItem;
    private static Method nexoItemFromId;
    private static final ThreadLocal<String> lastFurnitureError = ThreadLocal.withInitial(() -> "");

    static {
        loadReflection();
    }

    private static void loadReflection() {
        try {
            nexoFurnitureClass = Class.forName("com.nexomc.nexo.api.NexoFurniture");
            try {
                nexoFurnFromEntity = nexoFurnitureClass.getMethod("furnitureFromEntity", Entity.class);
            } catch (Exception ignored) {}
            if (nexoFurnFromEntity == null) {
                try {
                    nexoFurnFromEntity = nexoFurnitureClass.getMethod("furnitureMechanic", Entity.class);
                } catch (Exception ignored) {}
            }
            try {
                nexoFurnFromBlock = nexoFurnitureClass.getMethod("furnitureMechanic", Block.class);
            } catch (Exception ignored) {}
            if (nexoFurnFromBlock == null) {
                try {
                    nexoFurnFromBlock = nexoFurnitureClass.getMethod("furnitureFromBlock", Block.class);
                } catch (Exception ignored) {}
            }
            try {
                nexoBaseFromEntity = nexoFurnitureClass.getMethod("baseEntity", Entity.class);
            } catch (Exception ignored) {}
            try {
                nexoBaseFromBlock = nexoFurnitureClass.getMethod("baseEntity", Block.class);
            } catch (Exception ignored) {}
            try {
                nexoBaseFromLocation = nexoFurnitureClass.getMethod("baseEntity", Location.class);
            } catch (Exception ignored) {}
            nexoPlaceWithYaw = findPlaceMethod(nexoFurnitureClass);
            try {
                nexoIsFurnitureString = nexoFurnitureClass.getMethod("isFurniture", String.class);
            } catch (Exception ignored) {}
            try {
                nexoUpdateFurniture = nexoFurnitureClass.getMethod("updateFurniture", ItemDisplay.class);
            } catch (Exception ignored) {}
            try {
                nexoConvertFurniture = nexoFurnitureClass.getMethod("convertFurniture", ItemDisplay.class);
            } catch (Exception ignored) {}
            try {
                nexoRemoveEntity = nexoFurnitureClass.getMethod("remove", Entity.class, Player.class, Class.forName("com.nexomc.nexo.utils.drops.Drop"));
            } catch (Exception ignored) {}
            try {
                nexoRemoveLocation = nexoFurnitureClass.getMethod("remove", Location.class, Player.class, Class.forName("com.nexomc.nexo.utils.drops.Drop"));
            } catch (Exception ignored) {}
            try {
                nexoRemoveEntityPlayer = nexoFurnitureClass.getMethod("remove", Entity.class, Player.class);
            } catch (Exception ignored) {}
            try {
                nexoRemoveLocationPlayer = nexoFurnitureClass.getMethod("remove", Location.class, Player.class);
            } catch (Exception ignored) {}
            try {
                nexoRemoveEntityOnly = nexoFurnitureClass.getMethod("remove", Entity.class);
            } catch (Exception ignored) {}
            try {
                nexoRemoveLocationOnly = nexoFurnitureClass.getMethod("remove", Location.class);
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}

        try {
            nexoBlocksClass = Class.forName("com.nexomc.nexo.api.NexoBlocks");
            nexoBlockFromBlock = nexoBlocksClass.getMethod("blockFromBlock", Block.class);
        } catch (Exception ignored) {}

        try {
            nexoItemsClass = Class.forName("com.nexomc.nexo.api.NexoItems");
            nexoIdFromItem = nexoItemsClass.getMethod("idFromItem", ItemStack.class);
            nexoItemFromId = nexoItemsClass.getMethod("itemFromId", String.class);
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

    public static boolean replaceFurniture(Object furniture, Location location, String newFurnitureId, Player player) {
        lastFurnitureError.set("");
        if (newFurnitureId == null || newFurnitureId.isEmpty() || location == null || location.getWorld() == null) {
            lastFurnitureError.set("invalid target id or location");
            return false;
        }
        if (!isFurniture(newFurnitureId)) {
            lastFurnitureError.set("target id is not registered as a Nexo furniture: " + newFurnitureId);
            return false;
        }

        Object baseEntity = getBaseEntity(furniture, location);
        if (baseEntity instanceof ItemDisplay itemDisplay && updateFurnitureDisplay(itemDisplay, newFurnitureId)) {
            return true;
        }

        Location placeLocation = baseEntity instanceof Entity entity ? entity.getLocation().clone() : location.clone();
        float yaw = placeLocation.getYaw();

        if (!removeFurniture(baseEntity, location, player)) {
            if (lastFurnitureError.get().isEmpty()) {
                lastFurnitureError.set("could not remove old furniture");
            }
            return false;
        }

        return placeFurniture(newFurnitureId, placeLocation, yaw, BlockFace.UP);
    }

    public static boolean updateFurnitureDisplay(ItemDisplay baseEntity, String newFurnitureId) {
        if (baseEntity == null || newFurnitureId == null || newFurnitureId.isEmpty()) {
            lastFurnitureError.set("invalid display entity or target id");
            return false;
        }

        ItemStack newItem = itemStackFromNexoId(newFurnitureId);
        if (newItem == null) {
            lastFurnitureError.set("could not build Nexo item for furniture id: " + newFurnitureId);
            return false;
        }

        try {
            baseEntity.setItemStack(newItem);
            invokeNexoFurnitureUpdater(nexoConvertFurniture, baseEntity);
            invokeNexoFurnitureUpdater(nexoUpdateFurniture, baseEntity);
            return true;
        } catch (Exception ex) {
            lastFurnitureError.set("error while updating furniture display to " + newFurnitureId
                    + ": " + ex.getClass().getSimpleName());
            return false;
        }
    }

    public static String getLastFurnitureError() {
        return lastFurnitureError.get();
    }

    public static boolean isFurniture(String furnitureId) {
        if (nexoIsFurnitureString == null) {
            return true;
        }
        try {
            Object result = nexoIsFurnitureString.invoke(null, furnitureId);
            return Boolean.TRUE.equals(result);
        } catch (Exception ignored) {
            return true;
        }
    }

    public static boolean placeFurniture(String furnitureId, Location location, float yaw, BlockFace blockFace) {
        if (nexoPlaceWithYaw == null || furnitureId == null || furnitureId.isEmpty() || location == null) {
            lastFurnitureError.set("No compatible NexoFurniture.place(String, Location, yaw/Rotation, BlockFace) method is available");
            return false;
        }

        try {
            Object rotationArgument = placeRotationArgument(nexoPlaceWithYaw, yaw);
            Object placed = nexoPlaceWithYaw.invoke(null, furnitureId, location, rotationArgument, blockFace == null ? BlockFace.UP : blockFace);
            if (placed == null) {
                lastFurnitureError.set("Nexo returned null while placing " + furnitureId);
            }
            return placed != null;
        } catch (Exception ex) {
            lastFurnitureError.set("error while placing " + furnitureId + ": " + ex.getClass().getSimpleName());
            return false;
        }
    }

    public static boolean removeFurniture(Object furniture, Location location, Player player) {
        Object emptyDrop = emptyDrop();

        if (furniture instanceof Entity entity) {
            if (emptyDrop != null && invokeRemove(nexoRemoveEntity, entity, player, emptyDrop)) {
                return true;
            }
            if (invokeRemove(nexoRemoveEntityPlayer, entity, player)) {
                return true;
            }
            if (invokeRemove(nexoRemoveEntityOnly, entity)) {
                return true;
            }
        }

        if (location != null) {
            if (emptyDrop != null && invokeRemove(nexoRemoveLocation, location, player, emptyDrop)) {
                return true;
            }
            if (invokeRemove(nexoRemoveLocationPlayer, location, player)) {
                return true;
            }
            if (invokeRemove(nexoRemoveLocationOnly, location)) {
                return true;
            }
        }

        lastFurnitureError.set("Nexo remove returned false for " + describeFurniture(furniture, location));
        return false;
    }

    private static Object getBaseEntity(Object furniture, Location location) {
        Object currentBase = invokeBase(nexoBaseFromLocation, location);
        if (currentBase instanceof Entity) {
            return currentBase;
        }

        if (furniture instanceof Entity entity) {
            Object base = invokeBase(nexoBaseFromEntity, entity);
            return base instanceof Entity ? base : entity;
        }
        if (furniture instanceof Block block) {
            Object base = invokeBase(nexoBaseFromBlock, block);
            if (base instanceof Entity) {
                return base;
            }
        }
        Object base = invokeBase(nexoBaseFromLocation, location);
        return base instanceof Entity ? base : furniture;
    }

    private static Object invokeBase(Method method, Object value) {
        if (method == null || value == null) {
            return null;
        }
        try {
            return method.invoke(null, value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static boolean invokeRemove(Method method, Object... args) {
        if (method == null) {
            return false;
        }
        try {
            Object removed = method.invoke(null, args);
            return Boolean.TRUE.equals(removed);
        } catch (Exception ignored) {
            return false;
        }
    }

    private static String describeFurniture(Object furniture, Location location) {
        if (furniture instanceof Entity entity) {
            return entity.getType() + "@" + entity.getLocation().getWorld().getName() + ","
                    + entity.getLocation().getBlockX() + "," + entity.getLocation().getBlockY() + ","
                    + entity.getLocation().getBlockZ();
        }
        if (location != null && location.getWorld() != null) {
            return location.getWorld().getName() + "," + location.getBlockX() + ","
                    + location.getBlockY() + "," + location.getBlockZ();
        }
        return "unknown location";
    }

    private static Object emptyDrop() {
        try {
            Class<?> dropClass = Class.forName("com.nexomc.nexo.utils.drops.Drop");
            Method emptyDrop = dropClass.getMethod("emptyDrop");
            return emptyDrop.invoke(null);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static ItemStack itemStackFromNexoId(String itemId) {
        if (nexoItemFromId == null) {
            return null;
        }

        try {
            Object itemBuilder = nexoItemFromId.invoke(null, itemId);
            if (itemBuilder == null) {
                return null;
            }
            Method build = itemBuilder.getClass().getMethod("build");
            Object itemStack = build.invoke(itemBuilder);
            return itemStack instanceof ItemStack ? (ItemStack) itemStack : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static void invokeNexoFurnitureUpdater(Method method, ItemDisplay baseEntity) {
        if (method == null || baseEntity == null) {
            return;
        }
        try {
            method.invoke(null, baseEntity);
        } catch (Exception ignored) {
        }
    }

    private static Method findPlaceMethod(Class<?> furnitureClass) {
        for (Method method : furnitureClass.getMethods()) {
            if (!"place".equals(method.getName()) || method.getParameterCount() != 4) {
                continue;
            }

            Class<?>[] parameters = method.getParameterTypes();
            if (parameters[0] != String.class || parameters[1] != Location.class || parameters[3] != BlockFace.class) {
                continue;
            }

            Class<?> rotationType = parameters[2];
            if (rotationType == Float.class || rotationType == Float.TYPE || rotationType == Rotation.class) {
                return method;
            }
        }
        return null;
    }

    private static Object placeRotationArgument(Method placeMethod, float yaw) {
        Class<?> type = placeMethod.getParameterTypes()[2];
        if (type == Rotation.class) {
            return yawToRotation(yaw);
        }
        return yaw;
    }

    private static Rotation yawToRotation(float yaw) {
        float normalized = yaw % 360.0F;
        if (normalized < 0) {
            normalized += 360.0F;
        }

        int index = Math.round(normalized / 45.0F) % 8;
        return switch (index) {
            case 1 -> Rotation.CLOCKWISE_45;
            case 2 -> Rotation.CLOCKWISE;
            case 3 -> Rotation.CLOCKWISE_135;
            case 4 -> Rotation.FLIPPED;
            case 5 -> Rotation.FLIPPED_45;
            case 6 -> Rotation.COUNTER_CLOCKWISE;
            case 7 -> Rotation.COUNTER_CLOCKWISE_45;
            default -> Rotation.NONE;
        };
    }
}

