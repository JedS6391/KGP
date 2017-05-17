package main.kotlin.kgp.tree

import java.util.*

enum class TreeGenerationMode {
    Grow,
    Full,
    RampedHalfAndHalf
}

data class TreeGeneratorOptions(
        val maxDepth: Int,
        val numFeatures: Int,
        val mode: TreeGenerationMode,
        val constants: List<Double>
)

class TreeGenerator(val functions: List<Function>, val options: TreeGeneratorOptions) {

    val random = Random()

    fun generateTree(): Tree {
        // Start the tree with a function to prevent degenerate trees.
        val root = this.random.choice(this.functions)

        val tree = Tree(mutableListOf(root), this)

        return when (this.options.mode) {
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

    private fun rampedHalfAndHalf(tree: Tree): Tree {
        if (this.random.nextDouble() < 0.5) {
            return this.grow(tree)
        } else {
            return this.full(tree)
        }
    }
}

class Tree(var nodes: MutableList<Node>, val treeGenerator: TreeGenerator) {

    var fitness = 1e9

    fun execute(case: List<Double>): Double {
        val node = this.nodes.first()

        when (node) {
            is Constant -> return node.value
            is Input    -> return case[node.index]
        }

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

    fun copy(): Tree {
        return Tree(this.nodes.map { n -> n }.toMutableList(), this.treeGenerator)
    }

    internal fun getRandomSubtree(): Pair<Int, Int> {
        val random = Random()

        val probs = this.nodes.map { n ->
            when (n) {
                is Function -> 0.9
                else        -> 0.1
            }
        }

        val normalised = probs.map { p ->
            p / probs.sum()
        }.cumulativeSum()

        var stack = 1
        val start = normalised.insertionPoint(random.nextDouble())
        var end = start

        while (stack > (end - start)) {
            val node = this.nodes[end]

            stack += (node as? Function)?.arity ?: 0
            end += 1
        }

        return Pair(start, end)
    }

    fun crossover(other: Tree) {
        // Subtree from ourselves
        val (start, end) = this.getRandomSubtree()
        val removed = start..end

        // Subtree from other
        val (otherStart, otherEnd) = other.getRandomSubtree()
        val otherRemoved = ((0..other.nodes.size).toSet() - (otherStart..otherEnd).toSet()).toList()

        // Transfer genetic material from other tree.
        this.nodes = (this.nodes.subList(0, start) +
                     other.nodes.subList(otherStart, otherEnd) +
                     this.nodes.subList(end, this.nodes.size)).toMutableList()
    }

    fun pointMutation() {

    }

    fun subtreeMutation() {
        val other = this.treeGenerator.generateTree()

        this.crossover(other)
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

fun List<Double>.cumulativeSum(): List<Double> {
    var total = 0.0

    return this.map { v ->
        total += v

        total
    }
}

fun List<Double>.insertionPoint(value: Double): Int {
    var low = 0
    var high = this.size

    while (low < high) {
        // Use bit-shift instead of divide by 2.
        val middle = (low + high) ushr 1

        when {
            value <= this[middle] -> high = middle
            else                  -> low = middle + 1
        }
    }

    return low
}