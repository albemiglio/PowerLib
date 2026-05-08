package it.mycraft.powerlib.bukkit.utils;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class NexoUtils {

    private static final Logger LOGGER = Logger.getLogger(NexoUtils.class.getName());

    private static Method nexoFurnFromEntity;
    private static Method nexoFurnFromBlock;
    private static Method nexoBlockFromBlock;
    private static Method nexoIdFromItem;

    static {
        nexoFurnFromEntity = safeMethod("com.nexomc.nexo.api.NexoFurniture", "furnitureFromEntity", Entity.class);
        nexoFurnFromBlock  = firstNonNull(
                safeMethod("com.nexomc.nexo.api.NexoFurniture", "furnitureMechanic", Block.class),
                safeMethod("com.nexomc.nexo.api.NexoFurniture", "furnitureFromBlock", Block.class)
        );
        nexoBlockFromBlock = safeMethod("com.nexomc.nexo.api.NexoBlocks", "blockFromBlock", Block.class);
        nexoIdFromItem     = safeMethod("com.nexomc.nexo.api.NexoItems", "idFromItem", ItemStack.class);
    }

    private NexoUtils() {}

    public static String getNexoId(Block block) {
        if (block == null) return null;
        String id = invokeIdFromMechanic(nexoBlockFromBlock, block);
        if (id != null) return id;
        return invokeIdFromMechanic(nexoFurnFromBlock, block);
    }

    public static String getNexoId(Entity entity) {
        if (entity == null) return null;
        return invokeIdFromMechanic(nexoFurnFromEntity, entity);
    }

    public static String getNexoId(ItemStack item) {
        if (nexoIdFromItem == null || item == null || item.getType().isAir()) return null;
        try {
            return (String) nexoIdFromItem.invoke(null, item);
        } catch (ReflectiveOperationException e) {
            LOGGER.log(Level.FINE, "Nexo idFromItem reflection failed", e);
            return null;
        }
    }

    public static boolean isNexoItem(ItemStack item, String nexoId) {
        String id = getNexoId(item);
        return id != null && id.equalsIgnoreCase(nexoId);
    }

    private static String invokeIdFromMechanic(Method staticAccessor, Object arg) {
        if (staticAccessor == null) return null;
        try {
            Object mechanic = staticAccessor.invoke(null, arg);
            if (mechanic == null) return null;
            Method getId = mechanic.getClass().getMethod("getItemID");
            return (String) getId.invoke(mechanic);
        } catch (ReflectiveOperationException e) {
            LOGGER.log(Level.FINE, "Nexo mechanic reflection failed", e);
            return null;
        }
    }

    private static Method safeMethod(String className, String name, Class<?>... params) {
        try {
            return Class.forName(className).getMethod(name, params);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            LOGGER.log(Level.FINE, "Nexo method " + className + "#" + name + " not available", e);
            return null;
        }
    }

    private static Method firstNonNull(Method a, Method b) {
        return a != null ? a : b;
    }
}
