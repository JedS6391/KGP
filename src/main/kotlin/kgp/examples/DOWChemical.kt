package kgp.examples

import kgp.tree.TreeGenerationMode
import kgp.tree.TreeGeneratorOptions
import kgp.evolution.BaseModel
import kgp.evolution.EvolutionOptions
import kgp.fitness.*
import kgp.tree.Nodes
import kgp.utilities.IntervalSequenceGenerator
import java.util.*

class DOWChemical {

    object Main {

        @JvmStatic
        fun main(args: Array<String>) {
            val functions = setOf(
                    Nodes.Addition(),
                    Nodes.Subtraction(),
                    Nodes.Multiplication(),
                    Nodes.Division(),
                    Nodes.Sine(),
                    Nodes.Cosine(),
                    CustomOperations.SquareRoot(),
                    CustomOperations.Exponent()
            ).toList()

            val caseLoader = CsvCaseLoader(
                    options = CsvCaseLoaderOptions(
                            filename = "/Users/jedsimson/Desktop/DOW-Chemical-Datasets/TrainingData.csv",
                            numFeatures = 57
                    )
            )

            val cases = caseLoader.loadCases()

            val genOptions = TreeGeneratorOptions(
                    maxDepth = 8,
                    numFeatures = 57,
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