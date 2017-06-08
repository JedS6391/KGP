package kgp.examples

import kgp.tree.TreeGenerationMode
import kgp.tree.TreeGeneratorOptions
import kgp.evolution.BaseModel
import kgp.evolution.EvolutionOptions
import kgp.fitness.*
import kgp.tree.Nodes
import kgp.utilities.IntervalSequenceGenerator


class Pagie1 {

    companion object Main {
        @JvmStatic
        fun main(args: Array<String>) {
            val functions = setOf(
                    Nodes.Addition(),
                    Nodes.Subtraction(),
                    Nodes.Multiplication(),
                    Nodes.Division(),
                    CustomOperations.Exponent(),
                    CustomOperations.Inverse(),
                    CustomOperations.Negation()
            ).toList()

            val caseLoader = object : CaseLoader {
                val f = { x: Double, y: Double ->
                    (1.0 / (1 + Math.pow(x, -4.0))) + (1.0 / (1 + Math.pow(y, -4.0)))
                }

                val range = IntervalSequenceGenerator()

                override fun loadCases(): Cases {
                    val seq = range.generate(start = -5.0, end = 5.0, step = 0.4)

                    val samples = seq.map { v ->
                        val x = Feature(name = "x", value = v)
                        val y = Feature(name = "y", value = v)


                        listOf(x, y)
                    }

                    val cases = samples.map { sample ->
                        val target = this.f(sample[0].value, sample[1].value)

                        Case(sample, target)
                    }.toList()

                    return cases
                }
            }

            val cases = caseLoader.loadCases()

            val genOptions = TreeGeneratorOptions(
                    maxDepth = 5,
                    numFeatures = 2,
                    constants = listOf(1.0, 4.0),
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