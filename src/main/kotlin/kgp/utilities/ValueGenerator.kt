package kgp.utilities

import java.util.*
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

class UniformlyDistributedSequenceGenerator {

    /**
     * Generates [n] values that are uniformly distributed between [start] and [end].
     *
     * @param n Number of values to generate.
     * @param start Lower bound on the range of values.
     * @param end Upper bound on the range of values.
     */
    fun generate(n: Int, start: Double, end: Double): Sequence<Double> = buildSequence {
        val random = Random()

        (0..n).map {
            val r = random.nextDouble()

            // Scaled to range
            yield(r * (end - start) + start)
        }
    }
}