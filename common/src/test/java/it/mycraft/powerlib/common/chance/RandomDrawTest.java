package it.mycraft.powerlib.common.chance;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomDrawTest {

    // ---- new API ----

    @Test
    void emptyDrawReturnsNull() {
        assertNull(new RandomDraw<String>().pick());
        assertTrue(new RandomDraw<String>().pick(5).isEmpty());
    }

    @Test
    void allZeroWeightsReturnsNull() {
        assertNull(new RandomDraw<String>().add("a", 0).pick());
    }

    @Test
    void zeroWeightItemIsNeverDrawn() {
        RandomDraw<String> draw = new RandomDraw<String>().add("zero", 0).add("win", 5);
        for (int i = 0; i < 100; i++) {
            assertEquals("win", draw.pick(), "a zero-weight item must never be drawn");
        }
    }

    @Test
    void weightsAreHonouredOverManyDraws() {
        RandomDraw<String> draw = new RandomDraw<String>()
                .add("a", 1).add("b", 3).add("c", 6); // shares: 0.1, 0.3, 0.6
        assertDistribution(draw, Map.of("a", 0.1, "b", 0.3, "c", 0.6));
    }

    @Test
    void decimalWeightsSummingToOneAreHonoured() {
        RandomDraw<String> draw = RandomDraw.of(Map.of("rare", 0.05, "uncommon", 0.25, "common", 0.70));
        assertDistribution(draw, Map.of("rare", 0.05, "uncommon", 0.25, "common", 0.70));
    }

    @Test
    void aliasTableRebuildsAfterMutation() {
        RandomDraw<String> draw = new RandomDraw<String>().add("a", 1);
        draw.pick();              // builds the table with one item
        draw.add("b", 1);         // must invalidate it
        boolean sawB = false;
        for (int i = 0; i < 200 && !sawB; i++) {
            sawB = "b".equals(draw.pick());
        }
        assertTrue(sawB, "an item added after the first draw must become drawable");
    }

    @Test
    void pickKReturnsKItemsWithReplacement() {
        RandomDraw<String> draw = new RandomDraw<String>().add("only", 1);
        List<String> picks = draw.pick(7);
        assertEquals(7, picks.size());
        assertTrue(picks.stream().allMatch("only"::equals));
    }

    @Test
    void choicesMatchesPopulationWeights() {
        List<String> population = List.of("a", "b", "c");
        double[] weights = {0.1, 0.3, 0.6};
        int draws = 300_000;
        HashMap<String, Integer> counts = new HashMap<>();
        for (String s : RandomDraw.choices(population, weights, draws)) {
            counts.merge(s, 1, Integer::sum);
        }
        assertTrue(Math.abs(counts.get("c") / (double) draws - 0.6) < 0.01);
        assertTrue(Math.abs(counts.get("a") / (double) draws - 0.1) < 0.01);
    }

    @Test
    void probabilityOfReturnsNormalisedShare() {
        RandomDraw<String> draw = new RandomDraw<String>().add("a", 1).add("b", 3);
        assertEquals(0.25, draw.probabilityOf("a"), 1e-9);
        assertEquals(0.75, draw.probabilityOf("b"), 1e-9);
        assertEquals(0.0, draw.probabilityOf("missing"), 1e-9);
    }

    // ---- legacy API still works (backward compatibility) ----

    @Test
    @SuppressWarnings("deprecation")
    void deprecatedApiStillDrawsAndDelegates() {
        RandomDraw<String> draw = new RandomDraw<>();
        draw.addItem("zero", 0);
        draw.addItem("win", 5);
        assertEquals(5.0, draw.getTotalChance(false), 1e-9);
        assertEquals(1.0, draw.getProbability("win", false), 1e-9);
        for (int i = 0; i < 50; i++) {
            assertEquals("win", draw.shuffle(false));
        }
    }

    private static void assertDistribution(RandomDraw<String> draw, Map<String, Double> expected) {
        int draws = 500_000;
        HashMap<String, Integer> counts = new HashMap<>();
        for (int i = 0; i < draws; i++) {
            counts.merge(draw.pick(), 1, Integer::sum);
        }
        expected.forEach((key, share) -> {
            double observed = counts.getOrDefault(key, 0) / (double) draws;
            assertTrue(Math.abs(observed - share) < 0.01,
                    "expected ~" + share + " for " + key + " but observed " + observed);
        });
    }
}
