package main.kotlin.kgp

import main.kotlin.kgp.fitness.*
import main.kotlin.kgp.tree.*

class Main {

    object Main {

        @JvmStatic
        fun main(args: Array<String>) {
            val functions = setOf(
                    Nodes.Addition(),
                    Nodes.Subtraction(),
                    Nodes.Multiplication()
            ).toList()

            val caseLoaderOptions = CsvCaseLoaderOptions(
                    filename = "/Users/jedsimson/Desktop/simple.csv",
                    numFeatures = 1
            )

            val caseLoader = CsvCaseLoader(caseLoaderOptions)
            val cases = caseLoader.loadCases()

            val genOptions = TreeGeneratorOptions(
                    maxDepth = 5,
                    numFeatures = 1,
                    constants = listOf(0.0, 1.0, 2.0)
            )
            val treeGen = TreeGenerator(functions, genOptions)

            val tree = treeGen.generateTree(TreeGenerationMode.Grow)

            val mse = Metric(function = { cases, outputs ->
                val se = cases.zip(outputs).map { (expected, predicted) ->
                    Math.pow((predicted - expected.output), 2.0)
                }.sum()

                ((1.0 / cases.size.toDouble()) * se)
            })

            println(tree)

            if (tree.isValid()) {
                val outputs = cases.map { (features) -> tree.execute(features.map(Feature::value)) }
                val fitness = mse.fitness(cases, outputs)

                println("Fitness (MSE): $fitness")
            } else {
                println("Invalid tree...")
            }

        }
    }
}
