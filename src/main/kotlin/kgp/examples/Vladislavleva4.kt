package kgp.examples

import kgp.tree.TreeGenerationMode
import kgp.tree.TreeGeneratorOptions
import kgp.evolution.BaseModel
import kgp.evolution.EvolutionOptions
import kgp.fitness.*
import kgp.tree.Nodes
import kgp.utilities.UniformlyDistributedSequenceGenerator
import java.util.*


class Vladislavleva4 {

    object Main {
        @JvmStatic
        fun main(args: Array<String>) {
            val functions = setOf(
                    Nodes.Addition(),
                    Nodes.Subtraction(),
                    Nodes.Multiplication(),
                    Nodes.Division(),
                    CustomOperations.Square()
            ).toList()

            val caseLoader = object : CaseLoader {
                val f = { x0: Double, x1: Double, x2: Double, x3: Double, x4: Double ->
                    val xs = listOf(x0, x1, x2, x3, x4)

                    10 / (5 + xs.sumByDouble { x -> Math.pow(x - 3.0, 2.0) })
                }

                val range = UniformlyDistributedSequenceGenerator()

                override fun loadCases(): Cases {
                    val seq = range.generate(5120, 0.05, 6.05)

                    val samples = (0..1024).map {
                        val features = seq.take(5).mapIndexed { idx, f ->
                            Feature(name = "x$idx", value = f)
                        }

                        features.toList()
                    }

                    val cases = samples.map { sample ->
                        val x0 = sample[0].value
                        val x1 = sample[1].value
                        val x2 = sample[2].value
                        val x3 = sample[3].value
                        val x4 = sample[4].value

                        val target = this.f(x0, x1, x2, x3, x4)

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

            val mse = Metric(function = { cases, outputs ->
                val se = cases.zip(outputs).map { (expected, predicted) ->
                    Math.pow((predicted - expected.output), 2.0)
                }.sum()

                ((1.0 / cases.size.toDouble()) * se)
            })

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
                    metric = mse,
                    stoppingThreshold = 0.01
            )

            val model = BaseModel(evoOptions)

            model.train(cases)

            println(model.best)
        }
    }
}