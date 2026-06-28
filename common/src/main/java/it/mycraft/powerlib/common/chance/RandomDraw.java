package it.mycraft.powerlib.common.chance;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A weighted random draw: objects are added with integer or decimal weights and one is picked with
 * probability proportional to its weight. Integer and decimal weights are tracked separately, and
 * each method takes a flag selecting which set to operate on.
 *
 * <p>Draws use Vose's <i>alias method</i>: the first {@link #shuffle(boolean)} after a change builds
 * an alias table in {@code O(n)}, and every draw after that is {@code O(1)} with the exact weighted
 * probabilities (no list materialisation, no per-draw scan). The table is rebuilt lazily whenever an
 * item is added or removed.</p>
 */
public class RandomDraw {
    private HashMap<Object, Integer> intMap;
    private HashMap<Object, Double> doubleMap;

    // Lazily built O(1) samplers; nulled out whenever the matching map changes.
    private transient Alias intAlias;
    private transient Alias doubleAlias;

    /**
     * Creates an empty draw.
     */
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
        this.intAlias = null;
    }

    /**
     * Adds an Item with a decimal chance to be extracted
     *
     * @param obj         The object being added to the draw
     * @param probability The object's DECIMAL chance of being drawn
     */
    public void addItem(Object obj, Double probability) {
        this.doubleMap.put(obj, probability);
        this.doubleAlias = null;
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
            this.doubleAlias = null;
        } else {
            this.intMap.remove(obj);
            this.intAlias = null;
        }
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
     * Draws a random item with a probability proportional to its weight, in {@code O(1)}.
     *
     * @param useDoubleValues If the map is using decimal numbers (false for integers e.g. 1.0)
     * @return The random-extracted item, or null if there is nothing to draw
     */
    public Object shuffle(boolean useDoubleValues) {
        Map<Object, ? extends Number> map = useDoubleValues ? this.doubleMap : this.intMap;
        if (map.isEmpty()) {
            return null;
        }
        Alias alias = useDoubleValues ? this.doubleAlias : this.intAlias;
        if (alias == null) {
            alias = Alias.build(map);
            if (alias == null) { // all weights non-positive
                return null;
            }
            if (useDoubleValues) {
                this.doubleAlias = alias;
            } else {
                this.intAlias = alias;
            }
        }
        return alias.draw(ThreadLocalRandom.current());
    }

    private boolean contains(Object obj, boolean useDoubleValues) {
        return (useDoubleValues && this.doubleMap.containsKey(obj)) || ((!useDoubleValues) && this.intMap.containsKey(obj));
    }

    /**
     * Immutable Vose alias table over a fixed set of weighted items. Build is {@code O(n)};
     * {@link #draw(ThreadLocalRandom)} is {@code O(1)}. Items with a zero (or negative) weight are
     * kept in the table but can never be returned.
     */
    private static final class Alias {
        private final Object[] items;
        private final double[] prob;  // prob[i] = chance of keeping items[i] vs falling back to alias[i]
        private final int[] alias;

        private Alias(Object[] items, double[] prob, int[] alias) {
            this.items = items;
            this.prob = prob;
            this.alias = alias;
        }

        private Object draw(ThreadLocalRandom random) {
            int column = random.nextInt(prob.length);
            return random.nextDouble() < prob[column] ? items[column] : items[alias[column]];
        }

        /**
         * Builds the alias table, or returns null if the weights do not sum to a positive total.
         */
        private static Alias build(Map<Object, ? extends Number> weights) {
            int n = weights.size();
            Object[] items = new Object[n];
            double[] scaled = new double[n];
            double total = 0;
            int i = 0;
            for (Map.Entry<Object, ? extends Number> entry : weights.entrySet()) {
                double w = entry.getValue().doubleValue();
                items[i] = entry.getKey();
                scaled[i] = w < 0 ? 0 : w; // clamp negatives so they behave like zero, never drawn
                total += scaled[i];
                i++;
            }
            if (total <= 0) {
                return null;
            }
            // Normalise so the average scaled weight is 1 (each entry becomes its share of n).
            for (int k = 0; k < n; k++) {
                scaled[k] = scaled[k] * n / total;
            }
            double[] prob = new double[n];
            int[] alias = new int[n];
            Deque<Integer> small = new ArrayDeque<>();
            Deque<Integer> large = new ArrayDeque<>();
            for (int k = 0; k < n; k++) {
                (scaled[k] < 1.0 ? small : large).add(k);
            }
            while (!small.isEmpty() && !large.isEmpty()) {
                int s = small.poll();
                int l = large.poll();
                prob[s] = scaled[s];
                alias[s] = l;
                scaled[l] = (scaled[l] + scaled[s]) - 1.0;
                (scaled[l] < 1.0 ? small : large).add(l);
            }
            // Leftovers (floating-point slack) are certainties.
            while (!large.isEmpty()) prob[large.poll()] = 1.0;
            while (!small.isEmpty()) prob[small.poll()] = 1.0;
            return new Alias(items, prob, alias);
        }
    }
}
