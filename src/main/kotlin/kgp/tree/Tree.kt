package main.kotlin.kgp.tree

import java.util.*

enum class TreeGenerationMode {
    Grow,
    Full,
    RampedHalfAndHalf
}

data class TreeGeneratorOptions(val maxDepth: Int, val numFeatures: Int, val constants: List<Double>)

class TreeGenerator(val functions: List<Function>, val options: TreeGeneratorOptions) {

    val random = Random()

    fun generateTree(mode: TreeGenerationMode): Tree {
        // Start the tree with a function to prevent degenerate trees.
        val root = this.random.choice(this.functions)

        val tree = Tree(mutableListOf(root))

        return when (mode) {
            TreeGenerationMode.Grow -> this.grow(tree)
            TreeGenerationMode.Full -> this.full(tree)
            TreeGenerationMode.RampedHalfAndHalf -> this.rampedHalfAndHalf(tree)
        }
    }

    private fun grow(tree: Tree): Tree {
        val terminals = mutableListOf(tree.nodes.first().arity)

        while (terminals.isNotEmpty()) {
            val depth = terminals.size

            // Pick a random node from the union of the function and terminal set.
            val choice = this.random.nextInt(this.options.numFeatures + this.functions.size)

            if (depth < this.options.maxDepth && choice <= this.functions.size) {
                // Adding a function node.
                val node = this.random.choice(this.functions)

                tree.nodes.add(node)
                terminals.add(node.arity)
            } else {
                // Adding a terminal node either because we've reached maximum
                // depth or that was the randomly chosen node.
                // Add 1 to number of features to determine when to use constant.
                val idx = this.random.nextInt(this.options.numFeatures + 1)

                val node = if (idx == this.options.numFeatures) {
                    Constant(this.random.choice(this.options.constants))
                } else {
                    Input(idx)
                }

                tree.nodes.add(node)
                terminals[terminals.lastIndex] -= 1

                while (terminals.last() == 0) {
                    terminals.removeAt(terminals.lastIndex)

                    if (terminals.isEmpty()) {
                        return tree
                    }

                    terminals[terminals.lastIndex] -= 1
                }
            }
        }

        // Shouldn't get here
        throw Exception("Error during tree construction.")
    }

    private fun full(tree: Tree): Tree {
        val terminals = mutableListOf(tree.nodes.first().arity)

        while (terminals.isNotEmpty()) {
            val depth = terminals.size

            if (depth < this.options.maxDepth) {
                // Adding a function node.
                val node = this.random.choice(this.functions)

                tree.nodes.add(node)
                terminals.add(node.arity)
            } else {
                // Adding a terminal node either because we've reached maximum
                // depth or that was the randomly chosen node.
                // Add 1 to number of features to determine when to use constant.
                val idx = this.random.nextInt(this.options.numFeatures + 1)

                val node = if (idx == this.options.numFeatures) {
                    Input(idx)
                } else {
                    Constant(this.random.choice(this.options.constants))
                }

                tree.nodes.add(node)
                terminals[terminals.lastIndex] -= 1

                while (terminals.last() == 0) {
                    terminals.removeAt(terminals.lastIndex)

                    if (terminals.isEmpty()) {
                        return tree
                    }

                    terminals[terminals.lastIndex] -= 1
                }
            }
        }

        // Shouldn't get here
        throw Exception("Error during tree construction.")
    }

    private fun rampedHalfAndHalf(tree: Tree): Tree {
        if (this.random.nextDouble() < 0.5) {
            return this.grow(tree)
        } else {
            return this.full(tree)
        }
    }
}

class Tree(val nodes: MutableList<Node>) {

    fun isValid(): Boolean {
        val terminals = mutableListOf(0)

        this.nodes.forEach { node ->
            if (node is Function) {
                terminals.add(node.arity)
            } else {
                terminals[terminals.lastIndex] -= 1

                while (terminals.last() == 0) {
                    terminals.removeAt(terminals.lastIndex)
                    terminals[terminals.lastIndex] -= 1
                }
            }
        }

        return terminals == listOf(-1)
    }

    fun execute(case: List<Double>): Double {
        val stack = mutableListOf<MutableList<Node>>()

        for (node in this.nodes) {
            if (node is Function) {
                stack.add(mutableListOf(node))
            } else {
                stack[stack.lastIndex].add(node)
            }

            while (stack.last().size == stack.last()[0].arity + 1) {
                val function = stack.last()[0]
                val range = 1..stack.last().lastIndex
                val terminals = stack.last().slice(range).map { term ->
                    when (term) {
                        is Constant -> term.value
                        is Input    -> case[term.index]
                        else        -> throw Exception("Unexpected terminal type.")
                    }
                }

                val result = function.evaluate(terminals)

                if (stack.size != 1) {
                    stack.removeAt(stack.lastIndex)
                    stack.last().add(Constant(result))
                } else {
                    return result
                }
            }
        }

        throw Exception("Failed to execute tree.")
    }

    override fun toString(): String {
        val terminals = mutableListOf(0)
        val sb = StringBuilder()

        this.nodes.forEachIndexed { idx, node ->
            if (node is Function) {
                terminals.add(node.arity)
                sb.append("(")
                sb.append(node.representation)
                sb.append(" ")
            } else {
                when (node) {
                    is Constant -> sb.append(node.value)
                    is Input    -> {
                        sb.append("x[")
                        sb.append(node.index)
                        sb.append("]")
                    }
                }

                terminals[terminals.lastIndex] -= 1

                while (terminals[terminals.lastIndex] == 0) {
                    terminals.removeAt(terminals.lastIndex)
                    terminals[terminals.lastIndex] -= 1
                    sb.append(")")
                }

                if (idx != this.nodes.size - 1) {
                    sb.append(" ")
                }
            }
        }

        return sb.toString()
    }
}

fun <T> Random.choice(list: List<T>): T {
    return list[(this.nextDouble() * list.size).toInt()]
}