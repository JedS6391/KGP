package kgp.examples

import kgp.tree.TreeGenerationMode
import kgp.tree.TreeGeneratorOptions
import kgp.evolution.BaseModel
import kgp.evolution.EvolutionOptions
import kgp.fitness.*
import kgp.tree.Nodes
import kgp.utilities.IntervalSequenceGenerator

class Keijzer6 {
    object Main {
        @JvmStatic
        fun main(args: Array<String>) {
            val functions = setOf(
                    Nodes.Addition(),
                    Nodes.Multiplication(),
                    CustomOperations.Inverse(),
                    CustomOperations.Negation(),
                    CustomOperations.SquareRoot()
            ).toList()

            val caseLoader = object : CaseLoader {
                val f = { x: Double ->
                    (1..x.toInt()).map { i ->
                        1.0 / i
                    }.sum()
                }
                val range = IntervalSequenceGenerator()

                override fun loadCases(): Cases {
                    val xs = range.generate(1.0, 51.0, 1.0).map { x ->
                        Feature(x, "x")
                    }

                    val cases = xs.map { x ->
                        val y = this.f(x.value)
                        Case(listOf(x), y)
                    }.toList()

                    return cases
                }
            }

            val cases = caseLoader.loadCases()

            val genOptions = TreeGeneratorOptions(
                    maxDepth = 5,
                    numFeatures = 1,
                    constants = listOf(0.0, 1.0),
                    mode = TreeGenerationMode.HalfAndHalf
            )

            val evoOptions = EvolutionOptions(
                    populationSize = 500,
                    generations = 100   ,
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