package main.kotlin.kgp.utilities

import kotlin.coroutines.experimental.buildSequence

/**
 * A component that can generate sequences of doubles with a specified interval.
 */
class IntervalSequenceGenerator {

    /**
     * Generates a sequence of doubles between [start] and [end] exclusive, in intervals of [step].
     *
     * @param start The starting point of the sequence.
     * @param end The end point of the sequence (exclusive).
     * @param step The interval to space values with in the sequence.
     */
    fun generate(start: Double, end: Double, step: Double): Sequence<Double> = buildSequence {
        var x = start

        while (x <= end) {
            yield(x)

            x += step
        }
    }
}