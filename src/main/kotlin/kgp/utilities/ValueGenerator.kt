package main.kotlin.kgp.utilities

import kotlin.coroutines.experimental.buildSequence

class IntervalSequenceGenerator {

    fun generate(start: Double, end: Double, step: Double): Sequence<Double> = buildSequence {
        var x = start

        while (x <= end) {
            yield(x)

            x += step
        }
    }
}