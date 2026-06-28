package it.mycraft.powerlib.common.chance;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomDrawTest {

    @Test
    void emptyDrawReturnsNullInsteadOfThrowing() {
        assertNull(new RandomDraw().shuffle(false));
    }

    @Test
    void allZeroWeightsReturnsNull() {
        RandomDraw draw = new RandomDraw();
        draw.addItem("a", 0);
        assertNull(draw.shuffle(false));
    }

    @Test
    void onlyPositiveWeightItemIsEverDrawn() {
        RandomDraw draw = new RandomDraw();
        draw.addItem("zero", 0);
        draw.addItem("win", 5);
        for (int i = 0; i < 100; i++) {
            assertEquals("win", draw.shuffle(false), "a zero-weight item must never be drawn");
        }
    }

    @Test
    void integerWeightsAreHonouredOverManyDraws() {
        RandomDraw draw = new RandomDraw();
        draw.addItem("a", 1);
        draw.addItem("b", 3);
        draw.addItem("c", 6); // expected shares: 0.1, 0.3, 0.6
        assertDistribution(draw, false, Map.of("a", 0.1, "b", 0.3, "c", 0.6));
    }

    @Test
    void decimalWeightsSummingToOneAreHonoured() {
        RandomDraw draw = new RandomDraw();
        draw.addItem("rare", 0.05);
        draw.addItem("uncommon", 0.25);
        draw.addItem("common", 0.70);
        assertDistribution(draw, true, Map.of("rare", 0.05, "uncommon", 0.25, "common", 0.70));
    }

    @Test
    void aliasTableRebuildsAfterMutation() {
        RandomDraw draw = new RandomDraw();
        draw.addItem("a", 1);
        draw.shuffle(false);            // builds the table with one item
        draw.addItem("b", 1);           // must invalidate it
        boolean sawB = false;
        for (int i = 0; i < 200 && !sawB; i++) {
            sawB = "b".equals(draw.shuffle(false));
        }
        assertTrue(sawB, "an item added after the first draw must become drawable");
    }

    private static void assertDistribution(RandomDraw draw, boolean useDouble, Map<String, Double> expected) {
        int draws = 500_000;
        HashMap<Object, Integer> counts = new HashMap<>();
        for (int i = 0; i < draws; i++) {
            counts.merge(draw.shuffle(useDouble), 1, Integer::sum);
        }
        expected.forEach((key, share) -> {
            double observed = counts.getOrDefault(key, 0) / (double) draws;
            assertTrue(Math.abs(observed - share) < 0.01,
                    "expected ~" + share + " for " + key + " but observed " + observed);
        });
    }
}
