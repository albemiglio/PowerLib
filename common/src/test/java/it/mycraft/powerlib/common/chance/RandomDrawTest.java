package it.mycraft.powerlib.common.chance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
}
