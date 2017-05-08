package main.kotlin.kgp

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

            val cases = listOf(
                    listOf(-10.0, 82.0),
                    listOf(-9.5, 73.25)
            )

            val options = TreeGeneratorOptions(
                    maxDepth = 5,
                    numFeatures = 1,
                    constants = listOf(0.0, 1.0, 2.0)
            )
            val treeGen = TreeGenerator(functions, options)

            val tree = treeGen.generateTree(TreeGenerationMode.Grow)

            println(tree)

            if (tree.isValid()) {
                cases.map { case ->
                    val result = tree.execute(case)

                    println(result)
                }
            } else {
                println("Invalid tree...")
            }

        }
    }
}
