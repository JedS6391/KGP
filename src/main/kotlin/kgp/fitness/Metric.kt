package kgp.fitness

/**
 * A feature of some case in a data set.
 *
 * A feature is essentially a variable to a GP program.
 *
 * @param value The value of this feature.
 * @param name A name for this feature.
 */
data class Feature(val value: Double, val name: String)

/**
 * A case in a data set.
 *
 * A case is really just a collection of features mapping to an output.
 *
 * @param features A set of features that make up this case.
 * @param output The output expected for this set of features.
 */
data class Case(val features: List<Feature>, val output: Double)

/**
 * A set of outputs given by a program on a set of training cases.
 */
typealias Outputs = List<Double>

/**
 * A set of cases that a program will be trained on.
 */
typealias Cases = List<Case>

/**
 * A function that will evaluate the performance a program based on
 * how close its outputs were to the cases given.
 */
typealias FitnessFunction = (Cases, Outputs) -> Double

/**
 * Provides a way of measuring the fitness of a set of outputs.
 *
 * @param function A fitness function that this metric will use.
 */
class Metric(val function: FitnessFunction) {

    /**
     * Evaluates the fitness based on a set of cases and a set of outputs.
     *
     * @property cases The expected input-output cases.
     * @property outputs The predicted outputs of a program.
     */
    fun fitness(cases: Cases, outputs: Outputs): Double {
        return this.function(cases, outputs)
    }
}

object FitnessFunctions {
    val sse = Metric(function = { cases, outputs ->
        cases.zip(outputs).map { (expected, predicted) ->
            Math.pow((predicted - expected.output), 2.0)
        }.sum()
    })

    val mse = Metric(function = { cases, outputs ->
        val sse = cases.zip(outputs).map { (expected, predicted) ->
            Math.pow((predicted - expected.output), 2.0)
        }.sum()

        ((1.0 / cases.size.toDouble()) * sse)
    })

    val mae = Metric(function = { cases, outputs ->
        val ae = cases.zip(outputs).map { (expected, predicted) ->
            Math.abs(predicted - expected.output)
        }.sum()

        ((1.0 / cases.size.toDouble()) * ae)
    })
}