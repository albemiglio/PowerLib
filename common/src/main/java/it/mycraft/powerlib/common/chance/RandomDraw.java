package it.mycraft.powerlib.common.chance;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A weighted random draw over a fixed, closed set of items: each item is added with a weight and
 * picked with probability proportional to its weight (weights need not sum to 1 — they are
 * normalised internally).
 *
 * <p>Draws use Vose's <i>alias method</i>: the first {@link #pick()} after a change builds an alias
 * table in {@code O(n)} and every pick after that is {@code O(1)} with the exact weighted
 * probabilities. The table is rebuilt lazily whenever an item is added or removed.</p>
 *
 * <pre>{@code
 * RandomDraw<String> loot = new RandomDraw<String>()
 *         .add("common", 70)
 *         .add("rare", 25)
 *         .add("legendary", 5);
 *
 * String one = loot.pick();          // O(1)
 * List<String> ten = loot.pick(10);  // ten independent picks
 *
 * // one-shot, à la Python's random.choices:
 * List<String> drops = RandomDraw.choices(
 *         List.of("a", "b", "c"), new double[]{0.1, 0.3, 0.6}, 5);
 * }</pre>
 *
 * @param <T> the type of the drawn items
 */
public class RandomDraw<T> {
    private final Map<T, Double> weights = new LinkedHashMap<>();
    private transient Alias alias;

    /**
     * Creates an empty draw.
     */
    public RandomDraw() {
    }

    /**
     * Builds a draw from a map of items to weights.
     *
     * @param weights the items and their (positive) weights
     * @param <T>     the item type
     * @return a draw over the given items
     */
    public static <T> RandomDraw<T> of(Map<T, Double> weights) {
        RandomDraw<T> draw = new RandomDraw<>();
        weights.forEach(draw::add);
        return draw;
    }

    /**
     * Draws {@code k} items (with replacement) from a population and matching weights, in a single
     * call — the equivalent of Python's {@code random.choices(population, weights, k=k)}. Repeated
     * items in the population have their weights summed.
     *
     * @param population the items to draw from
     * @param weights    one weight per item (same size as {@code population})
     * @param k          how many items to draw
     * @param <T>        the item type
     * @return {@code k} independently drawn items (empty if there is nothing to draw)
     */
    public static <T> List<T> choices(List<T> population, double[] weights, int k) {
        if (population.size() != weights.length) {
            throw new IllegalArgumentException("population and weights must have the same size");
        }
        RandomDraw<T> draw = new RandomDraw<>();
        for (int i = 0; i < population.size(); i++) {
            draw.weights.merge(population.get(i), weights[i], Double::sum);
        }
        return draw.pick(k);
    }

    /**
     * Adds (or re-weights) an item.
     *
     * @param item   the item
     * @param weight its weight; zero or negative means it is kept but never drawn
     * @return this draw, for chaining
     */
    public RandomDraw<T> add(T item, double weight) {
        this.weights.put(item, weight);
        this.alias = null;
        return this;
    }

    /**
     * Removes an item from the draw.
     *
     * @param item the item to remove
     * @return this draw, for chaining
     */
    public RandomDraw<T> remove(T item) {
        if (this.weights.remove(item) != null) {
            this.alias = null;
        }
        return this;
    }

    /**
     * @return true if no item has been added
     */
    public boolean isEmpty() {
        return this.weights.isEmpty();
    }

    /**
     * @param item the item
     * @return the item's weight, or 0 if it is not in the draw
     */
    public double weightOf(T item) {
        return this.weights.getOrDefault(item, 0.0);
    }

    /**
     * @return the sum of every item's weight
     */
    public double total() {
        double sum = 0;
        for (double w : this.weights.values()) {
            sum += w;
        }
        return sum;
    }

    /**
     * @param item the item
     * @return the item's effective chance of being drawn (weight / total), or 0
     */
    public double probabilityOf(T item) {
        double total = this.total();
        return total <= 0 ? 0 : this.weightOf(item) / total;
    }

    /**
     * Draws one item with probability proportional to its weight, in {@code O(1)}.
     *
     * @return a drawn item, or null if there is nothing to draw
     */
    public T pick() {
        if (this.weights.isEmpty()) {
            return null;
        }
        if (this.alias == null) {
            this.alias = Alias.build(this.weights);
            if (this.alias == null) { // all weights non-positive
                return null;
            }
        }
        return this.cast(this.alias.draw(ThreadLocalRandom.current()));
    }

    /**
     * Draws {@code k} items, with replacement (each pick is independent), in {@code O(k)}.
     *
     * @param k how many items to draw
     * @return a list of drawn items; empty if there is nothing to draw
     */
    public List<T> pick(int k) {
        List<T> out = new ArrayList<>(Math.max(0, k));
        for (int i = 0; i < k; i++) {
            T item = this.pick();
            if (item == null) {
                break;
            }
            out.add(item);
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    private T cast(Object o) {
        return (T) o;
    }

    // ------------------------------------------------------------------------------------------
    // Legacy API — kept for backward compatibility, now backed by the alias method. Prefer the
    // methods above (add / pick / probabilityOf); the integer/decimal split and the boolean flag
    // are no longer needed.
    // ------------------------------------------------------------------------------------------

    /**
     * @param map             A HashMap with the drawing objects as keys and their chances as values
     * @param useDoubleValues If the map is using decimal numbers (false for integers e.g. 1.0)
     * @deprecated use {@link #of(Map)} or {@link #add(Object, double)}
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public RandomDraw(HashMap<Object, Double> map, boolean useDoubleValues) {
        map.forEach((key, value) -> this.add((T) key, useDoubleValues ? value : value.intValue()));
    }

    /**
     * @deprecated use {@link #add(Object, double)}
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public void addItem(Object obj, Integer probability) {
        this.add((T) obj, probability);
    }

    /**
     * @deprecated use {@link #add(Object, double)}
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public void addItem(Object obj, Double probability) {
        this.add((T) obj, probability);
    }

    /**
     * @deprecated use {@link #remove(Object)}
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public void removeItem(Object obj, boolean isDoubleValue) {
        this.remove((T) obj);
    }

    /**
     * @deprecated use {@link #total()}
     */
    @Deprecated
    public Double getTotalChance(boolean useDoubleValues) {
        return this.total();
    }

    /**
     * @deprecated use {@link #probabilityOf(Object)}
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public Double getProbability(Object obj, boolean useDoubleValues) {
        return this.probabilityOf((T) obj);
    }

    /**
     * @deprecated use {@link #pick()}
     */
    @Deprecated
    public Object shuffle(boolean useDoubleValues) {
        return this.pick();
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
        private static Alias build(Map<?, ? extends Number> weights) {
            int n = weights.size();
            Object[] items = new Object[n];
            double[] scaled = new double[n];
            double total = 0;
            int i = 0;
            for (Map.Entry<?, ? extends Number> entry : weights.entrySet()) {
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
