package kgp.examples

import kgp.tree.TreeGenerationMode
import kgp.tree.TreeGeneratorOptions
import kgp.evolution.BaseModel
import kgp.evolution.EvolutionOptions
import kgp.fitness.*
import kgp.tree.Nodes
import kgp.utilities.UniformlyDistributedSequenceGenerator
import java.util.*


class Korns12 {

    object Main {
        @JvmStatic
        fun main(args: Array<String>) {
            val functions = setOf(
                    Nodes.Addition(),
                    Nodes.Subtraction(),
                    Nodes.Multiplication(),
                    Nodes.Division(),
                    CustomOperations.SquareRoot(),
                    CustomOperations.Square(),
                    CustomOperations.Cube(),
                    Nodes.Cosine(),
                    Nodes.Sine(),
                    CustomOperations.Tangent(),
                    CustomOperations.HyperbolicTangent(),
                    CustomOperations.NaturalLog(),
                    CustomOperations.Exponent()
            ).toList()

            val caseLoader = object : CaseLoader {
                val f = { x0: Double, x4: Double ->
                    2.0 - (2.1 * (Math.cos(9.8 * x0) * Math.sin(1.3 * x4)))
                }

                val range = UniformlyDistributedSequenceGenerator()

                override fun loadCases(): Cases {
                    val seq = range.generate(50000, -50.0, 50.0)

                    val samples = (0..10000).map {
                        val features = seq.take(5).mapIndexed { idx, f ->
                            Feature(name = "x$idx", value = f)
                        }

                        features.toList()
                    }

                    val cases = samples.map { sample ->
                        val target = this.f(sample[0].value, sample[4].value)
                        Case(sample, target)
                    }.toList()

                    return cases
                }
            }

            val cases = caseLoader.loadCases()

            val genOptions = TreeGeneratorOptions(
                    maxDepth = 5,
                    numFeatures = 5,
                    constants = listOf(Random().nextDouble()),
                    mode = TreeGenerationMode.HalfAndHalf
            )

            val evoOptions = EvolutionOptions(
                    populationSize = 100,
                    generations = 50,
                    tournamentSize = 20,
                    crossoverRate = 0.7,
                    subtreeMutationRate = 0.1,
                    hoistMutationRate = 0.3,
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