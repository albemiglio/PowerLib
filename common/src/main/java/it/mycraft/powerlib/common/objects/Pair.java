package it.mycraft.powerlib.common.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A simple mutable holder for two related values.
 *
 * @param <L> the left value type
 * @param <R> the right value type
 */
@Data
@AllArgsConstructor
public class Pair<L, R> {

    private L left;
    private R right;

}
