package kgp.examples

import kgp.tree.TreeGenerationMode
import kgp.tree.TreeGeneratorOptions
import kgp.evolution.BaseModel
import kgp.evolution.EvolutionOptions
import kgp.fitness.*
import kgp.tree.Nodes
import kgp.utilities.IntervalSequenceGenerator
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class QuarticPolynomial {

    object Main {
        @JvmStatic
        fun main(args: Array<String>) {
            val functions = setOf(
                    Nodes.Addition(),
                    Nodes.Subtraction(),
                    Nodes.Multiplication(),
                    Nodes.Division(),
                    CustomOperations.Negation(),
                    Nodes.Sine(),
                    Nodes.Cosine()
            ).toList()

            val caseLoader = object : CaseLoader {
                val f = { x: Double ->
                    Math.pow(x, 4.0) + Math.pow(x, 3.0) + Math.pow(x, 2.0) + x
                }

                val range = IntervalSequenceGenerator()

                override fun loadCases(): Cases {
                    val seq = range.generate(start = -1.0, end = 1.0, step = 0.1)

                    val samples = seq.map { x ->
                        val feature = Feature(
                                BigDecimal(x).setScale(2, RoundingMode.HALF_UP).toDouble(),
                                "x")

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
                    populationSize = 300,
                    generations = 100,
                    tournamentSize = 3,
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