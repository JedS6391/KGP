package kgp.examples

import kgp.tree.TreeGenerationMode
import kgp.tree.TreeGeneratorOptions
import kgp.evolution.BaseModel
import kgp.evolution.EvolutionOptions
import kgp.fitness.*
import kgp.tree.Nodes
import kgp.utilities.UniformlyDistributedSequenceGenerator

/**
 * A basic regression problem for KGP to solve.
 *
 * The target function is `x0^2 - x1^2 + x1 - 1`.
 *
 * **See Also**
 *
 * - [Basic Regression Problem](http://kgp.readthedocs.io/en/latest/examples.html#basic-regression-problem)
 */
class BasicRegressionProblem {

    companion object Main {

        @JvmStatic
        fun main(args: Array<String>) {
            val functions = setOf(
                    Nodes.Addition(),
                    Nodes.Subtraction(),
                    Nodes.Multiplication(),
                    Nodes.Division()
            ).toList()

            val caseLoader = object : CaseLoader {
                val f: (Double, Double) -> Double = { x0, x1 ->
                    Math.pow(x0, 2.0) - Math.pow(x1, 2.0) + x1 - 1.0
                }
                val range = UniformlyDistributedSequenceGenerator()

                override fun loadCases(): Cases {
                    val seq = range.generate(200, -1.0, 1.0)

                    val x0s = seq.take(100).map { x0 ->
                        Feature(x0, "x0")
                    }
                    val x1s = seq.take(100).map { x1 ->
                        Feature(x1, "x1")
                    }

                    val cases = x0s.zip(x1s).map { (x0, x1) ->
                        val y = this.f(x0.value, x1.value)
                        Case(listOf(x0, x1), y)
                    }.toList()

                    return cases
                }
            }

            val cases = caseLoader.loadCases()

            val genOptions = TreeGeneratorOptions(
                    maxDepth = 5,
                    numFeatures = 2,
                    constants = listOf(-1.0, 0.0, 1.0),
                    mode = TreeGenerationMode.HalfAndHalf
            )

            val evoOptions = EvolutionOptions(
                    populationSize = 1000,
                    generations = 500,
                    tournamentSize = 20,
                    crossoverRate = 0.7,
                    subtreeMutationRate = 0.1,
                    hoistMutationRate = 0.05,
                    pointMutationRate = 0.1,
                    pointReplacementRate = 0.05,
                    numOffspring = 10,
                    functionSet = functions,
                    treeGeneratorOptions = genOptions,
                    metric = FitnessFunctions.mae,
                    stoppingThreshold = 0.01
            )

            val model = BaseModel(evoOptions)

            model.train(cases)

            println(model.best)
            println(model.best.fitness)
        }
    }
}
