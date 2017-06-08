package kgp.examples

import kgp.tree.TreeGenerationMode
import kgp.tree.TreeGeneratorOptions
import kgp.evolution.BaseModel
import kgp.evolution.EvolutionOptions
import kgp.fitness.*
import kgp.tree.Nodes
import kgp.utilities.UniformlyDistributedSequenceGenerator

/**
 * The Nguyen-7 problem as given by White et al. (2013).
 *
 * **See Also**
 *
 * - [White et al.  (2013)](http://link.springer.com/10.1007/s10710-012-9177-2)
 * - [Nguyen 7](http://kgp.readthedocs.io/en/latest/examples.html#nguyen-7)
 */
class Nguyen7 {

    companion object Main {
        @JvmStatic
        fun main(args: Array<String>) {
            val functions = setOf(
                    Nodes.Addition(),
                    Nodes.Subtraction(),
                    Nodes.Multiplication(),
                    Nodes.Division(),
                    CustomOperations.Exponent(),
                    CustomOperations.NaturalLog()
            ).toList()

            val caseLoader = object : CaseLoader {
                val f = { x: Double ->
                    // ln(x + 1) + ln(x^2 + 1)
                    Math.log(x + 1) + Math.log(Math.pow(x, 2.0) + 1)
                }

                val range = UniformlyDistributedSequenceGenerator()

                override fun loadCases(): Cases {
                    val seq = range.generate(20, 0.0, 2.0)

                    val samples = seq.map { x ->
                        val feature = Feature(name = "x", value = x)

                        listOf(feature)
                    }

                    val cases = samples.map { sample ->
                        val target = this.f(sample[0].value)

                        Case(sample, target)
                    }.toList()

                    return cases
                }
            }

            val cases = caseLoader.loadCases()

            val genOptions = TreeGeneratorOptions(
                    maxDepth = 5,
                    numFeatures = 1,
                    constants = listOf(1.0, 2.0),
                    mode = TreeGenerationMode.HalfAndHalf
            )

            val evoOptions = EvolutionOptions(
                    populationSize = 500,
                    generations = 100,
                    tournamentSize = 20,
                    crossoverRate = 0.7,
                    subtreeMutationRate = 0.1,
                    hoistMutationRate = 0.05,
                    pointMutationRate = 0.1,
                    pointReplacementRate = 0.05,
                    numOffspring = 10,
                    functionSet = functions,
                    treeGeneratorOptions = genOptions,
                    metric = FitnessFunctions.mse,
                    stoppingThreshold = 0.01
            )

            val model = BaseModel(evoOptions)

            model.train(cases)

            println(model.best)
        }
    }
}