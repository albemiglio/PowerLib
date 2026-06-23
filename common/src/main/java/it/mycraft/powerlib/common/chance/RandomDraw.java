package it.mycraft.powerlib.common.chance;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RandomDraw {
    private HashMap<Object, Integer> intMap;
    private HashMap<Object, Double> doubleMap;

    public RandomDraw() {
        this.intMap = new HashMap<>();
        this.doubleMap = new HashMap<>();
    }

    /**
     * @param map             A HashMap with the drawing objects as keys and their chances as values
     * @param useDoubleValues If the map is using decimal numbers (false for integers e.g. 1.0)
     */
    public RandomDraw(HashMap<Object, Double> map, boolean useDoubleValues) {
        if (useDoubleValues) {
            this.doubleMap = map;
            this.intMap = new HashMap<>();
        } else {
            this.intMap = new HashMap<>();
            this.doubleMap = new HashMap<>();
            map.keySet().forEach((key) -> this.intMap.put(key, map.get(key).intValue()));
        }
    }

    /**
     * Adds an Item with an integer chance to be extracted
     *
     * @param obj         The object being added to the draw
     * @param probability The object's INTEGER chance of being drawn
     */
    public void addItem(Object obj, Integer probability) {
        this.intMap.put(obj, probability);
    }

    /**
     * Adds an Item with a decimal chance to be extracted
     *
     * @param obj         The object being added to the draw
     * @param probability The object's DECIMAL chance of being drawn
     */
    public void addItem(Object obj, Double probability) {
        this.doubleMap.put(obj, probability);
    }

    /**
     * Removes an Item, if present, from the extraction
     *
     * @param obj           The object being removed from the draw
     * @param isDoubleValue If the map is using decimal numbers (false for integers e.g. 1.0)
     */
    public void removeItem(Object obj, boolean isDoubleValue) {
        if (isDoubleValue) {
            this.doubleMap.remove(obj);
        } else this.intMap.remove(obj);
    }

    /**
     * Sums the chances of all the items of the extraction
     *
     * @param useDoubleValues If the map is using decimal numbers (false for integers e.g. 1.0)
     * @return The total probability of the draw
     */
    public Double getTotalChance(boolean useDoubleValues) {
        double d;
        if (useDoubleValues) {
            d = this.doubleMap.values().stream().mapToDouble(n -> n).sum();
        } else {
            d = this.intMap.values().stream().mapToInt(n -> n).sum();
        }
        return d;
    }

    /**
     * Divides the object's partial probability by the total probability to get the effective chance
     *
     * @param obj             The object whose chance we don't know about
     * @param useDoubleValues If the map is using decimal numbers (false for integers e.g. 1.0)
     * @return The related item's chance to be extracted
     */
    public Double getProbability(Object obj, boolean useDoubleValues) {
        double d;
        double total = this.getTotalChance(useDoubleValues);
        if (!contains(obj, useDoubleValues)) {
            return 0.0;
        }
        if (useDoubleValues) {
            d = this.doubleMap.get(obj) / total;
        } else {
            d = this.intMap.get(obj) / total;
        }
        return d;
    }

    /**
     * Draws a random item with a probability proportional to its weight.
     * Uses a cumulative-weight scan (no list materialization, exact distribution).
     *
     * @param useDoubleValues If the map is using decimal numbers (false for integers e.g. 1.0)
     * @return The random-extracted item, or null if there is nothing to draw
     */
    public Object shuffle(boolean useDoubleValues) {
        Map<Object, ? extends Number> map = useDoubleValues ? this.doubleMap : this.intMap;
        double total = this.getTotalChance(useDoubleValues);
        if (map.isEmpty() || total <= 0) {
            return null;
        }
        double target = ThreadLocalRandom.current().nextDouble(total);
        double cumulative = 0;
        for (Map.Entry<Object, ? extends Number> entry : map.entrySet()) {
            cumulative += entry.getValue().doubleValue();
            if (target < cumulative) {
                return entry.getKey();
            }
        }
        return null;
    }

    private boolean contains(Object obj, boolean useDoubleValues) {
        return (useDoubleValues && this.doubleMap.containsKey(obj)) || ((!useDoubleValues) && this.intMap.containsKey(obj));
    }
}
