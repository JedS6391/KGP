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
 * @property name A name for this metric.
 * @property function A fitness function this metric encapsulates.
 */
interface Metric {
    val name: String
    val function: FitnessFunction

    /**
     * Evaluates the fitness using this metrics fitness function.
     *
     * @property cases The expected input-output cases.
     * @property outputs The predicted outputs of a program.
     * @property programLength The length of the program being evaluated.
     */
    fun fitness(cases: Cases, outputs: Outputs, programLength: Int): Double
}

/**
 * A base implementation for measuring the fitness of solutions.
 */
class BaseMetric(override val name: String, override val function: FitnessFunction) : Metric {

    /**
     * Evaluates the fitness based on a set of cases and a set of outputs.
     *
     * @property cases The expected input-output cases.
     * @property outputs The predicted outputs of a program.
     * @property programLength The length of the program being evaluated (unused).
     */
    override fun fitness(cases: Cases, outputs: Outputs, programLength: Int): Double {
        return this.function(cases, outputs)
    }
}

/**
 * Provides a way of measuring the fitness of a set of outputs with a penalty for parsimony pressure.
 *
 * @property parsimonyCoefficient A coefficient that is used to penalise solutions based on their length.
 */
class ParsimonyAwareMetric(
        override val name: String,
        override val function: FitnessFunction,
        val parsimonyCoefficient: Double) : Metric {

    /**
     * Evaluates the fitness based on a set of cases and a set of outputs.
     *
     * The raw fitness will be penalised with by a small factor based on the program's length:
     *
     * fitness = [function] - ([parsimonyCoefficient] * [programLength])
     *
     * @property cases The expected input-output cases.
     * @property outputs The predicted outputs of a program.
     * @property programLength The length of the program being evaluated.
     */
    override fun fitness(cases: Cases, outputs: Outputs, programLength: Int): Double {
        val fitness = this.function(cases, outputs)

        return fitness - (this.parsimonyCoefficient * programLength.toDouble())
    }
}

/**
 * A collection of standard fitness functions as metrics.
 */
object FitnessFunctions {

    /**
     * Sum of squared errors.
     */
    val sse = BaseMetric(
        name = "SSE",
        function = { cases, outputs ->
            cases.zip(outputs).map { (expected, predicted) ->
                Math.pow((predicted - expected.output), 2.0)
            }.sum()
        }
    )

    /**
     * Mean squared error.
     */
    val mse = BaseMetric(
        name = "MSE",
        function = { cases, outputs ->
            val sse = cases.zip(outputs).map { (expected, predicted) ->
                Math.pow((predicted - expected.output), 2.0)
            }.sum()

            ((1.0 / cases.size.toDouble()) * sse)
        }
    )

    /**
     * Mean absolute error.
     */
    val mae = BaseMetric(
        name = "MAE",
        function = { cases, outputs ->
            val ae = cases.zip(outputs).map { (expected, predicted) ->
                Math.abs(predicted - expected.output)
            }.sum()

            ((1.0 / cases.size.toDouble()) * ae)
        }
    )

    /**
     * Mean squared error with parsimony pressure.
     */
    val parsimonyAwareMse = ParsimonyAwareMetric(
        name = "MSE (parsimony aware)",
        parsimonyCoefficient = 0.001,
        function = { cases, outputs ->
            val sse = cases.zip(outputs).map { (expected, predicted) ->
                Math.pow((predicted - expected.output), 2.0)
            }.sum()

            ((1.0 / cases.size.toDouble()) * sse)
        }
    )
}