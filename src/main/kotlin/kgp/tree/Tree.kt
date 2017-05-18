package main.kotlin.kgp.tree

import java.util.*

/**
 * Represents the different types of tree generation methods.
 *
 * [Grow] mode will generate trees with nodes chosen randomly from
 * the union of the function and terminal sets, allowing trees that are
 * smaller than the max depth to be created. This method generally produces
 * asymmetric trees.
 *
 * [Full] mode will generates tree with nodes chosen from the function set,
 * until the maximum depth is reached when it will start choosing from the
 * terminal set. This tends to grow "bushy" symmetrical trees.
 *
 * [HalfAndHalf] mode will use a 50/50 combination of the full and grow modes,
 * meaning the trees in the initial population will have a mix of shapes.
 */
enum class TreeGenerationMode {
    Grow,
    Full,
    HalfAndHalf
}

/**
 * Options that control tree generation by a [TreeGenerator].
 *
 * @property maxDepth The maximum depth of trees generated.
 * @property numFeatures The number of features available to trees generated.
 * @property mode The method to use for generating tress.
 * @property constants A set of constants available to trees generated.
 */
data class TreeGeneratorOptions(
        val maxDepth: Int,
        val numFeatures: Int,
        val mode: TreeGenerationMode,
        val constants: List<Double>
)

/**
 * Generates [Tree]s using a given function set and options.
 *
 * @property functions A function set made available to the trees generated.
 * @property options Controls the operation of the tree generator.
 */
class TreeGenerator(val functions: List<Function>, val options: TreeGeneratorOptions) {

    private val random = Random()

    /**
     * Generates a random tree using the functions and options of this generator.
     *
     * @returns A tree that represents some program.
     */
    fun generateTree(): Tree {
        // Start the tree with a function to prevent degenerate trees.
        val root = this.random.choice(this.functions)

        val tree = Tree(mutableListOf(root), this)

        // Delegate to the appropriate tree generation mode.
        return when (this.options.mode) {
            TreeGenerationMode.Grow -> this.grow(tree)
            TreeGenerationMode.Full -> this.full(tree)
            TreeGenerationMode.HalfAndHalf -> this.rampedHalfAndHalf(tree)
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

/**
 * Represents a program as a tree of nodes.
 *
 * Internally, we keep a simple list of nodes that is the "program".
 *
 * @property nodes A collection of nodes that make up this tree.
 * @property treeGenerator The tree generator used to create this tree (or its parent tree)
 */
class Tree(var nodes: MutableList<Node>, val treeGenerator: TreeGenerator) {

    /**
     * The fitness of this tree as based on some data set.
     *
     * Initially, the value will be set to a high constant (1e9) to penalise programs
     * that haven't had their fitness evaluated yet.
     */
    var fitness = 1e9

    /**
     * Executes the trees program on a set of inputs.
     *
     * @param case A collection of inputs to make available to the program.
     * @returns The output of the program for the given inputs.
     */
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

    /**
     * Creates a copy of this tree. All nodes are copied too.
     */
    fun copy(): Tree {
        return Tree(this.nodes.map { n -> n }.toMutableList(), this.treeGenerator)
    }

    internal fun getRandomSubtree(program: List<Node> = this.nodes): Pair<Int, Int> {
        val random = Random()

        val probs = program.map { n ->
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
            val node = program[end]

            stack += (node as? Function)?.arity ?: 0
            end += 1
        }

        return Pair(start, end)
    }

    /**
     * Performs the crossover operation on this tree and the tree given.
     *
     * Note that this operation directly modifies the tree that initiates the operation.
     *
     * @param other The tree to perform crossover with.
     */
    fun crossover(other: Tree) {
        // Subtree from ourselves
        val (start, end) = this.getRandomSubtree()

        // Subtree from other
        val (otherStart, otherEnd) = other.getRandomSubtree()

        // Transfer genetic material from other tree.
        this.nodes = (
                this.nodes.subList(0, start) +
                other.nodes.subList(otherStart, otherEnd) +
                this.nodes.subList(end, this.nodes.size)
        ).toMutableList()
    }

    /**
     * Performs the point mutation operation on this tree.
     *
     * Point mutation works by choosing a collection of random nodes in the
     * tree to be replaced and then replacing each with another random node.
     *
     * Note that this operation directly modifies the tree that initiates the operation.
     *
     * @param replacementRate The frequency with which replacements should occur.
     */
    fun pointMutation(replacementRate: Double) {
        val nodes = this.copy().nodes
        val random = Random()
        val options = this.treeGenerator.options

        // Get nodes to modify
        val mutate = (0..nodes.size - 1).map { idx ->
            val replace = random.nextDouble() < replacementRate

            Pair(idx, replace)
        }.filter { (_, replace) ->
            replace
        }.map { (idx, _) ->
            idx
        }

        mutate.map { node ->
            when (this.nodes[node]) {
                is Function -> {
                    // Find another function with the same arity
                    val arity = this.nodes[node].arity

                    val replacements = this.treeGenerator.functions.filter { func ->
                        func.arity == arity
                    }

                    val replacement = random.choice(replacements)

                    this.nodes[node] = replacement
                }
                else -> {
                    // Terminal
                    val idx = random.nextInt(options.numFeatures + 1)

                    val term = if (idx == options.numFeatures) {
                        // Add small amount of noise to a constant
                        Constant(random.choice(options.constants) + random.nextGaussian())
                    } else {
                        Input(idx)
                    }

                    this.nodes[node] = term
                }
            }
        }
    }

    /**
     * Performs the subtree mutation operation on this tree.
     *
     * This is achieved by creating a new randomly generated tree and performing crossover
     * with that new randomly generated tree.
     *
     * Note that this operation directly modifies the tree that initiates the operation.
     */
    fun subtreeMutation() {
        val other = this.treeGenerator.generateTree()

        this.crossover(other)
    }

    /**
     * Performs the hoist mutation operation on this tree.
     *
     * Operates by choosing a random subtree of this tree, and then a random subtree
     * within that subtree and replacing the first subtree by the second. This effectively
     * "hoists" the subtree's subtree further up in the original tree.
     *
     * Note that this operation directly modifies the tree that initiates the operation.
     */
    fun hoistMutation() {
        // Find a subtree to replace
        val (start, end) = this.getRandomSubtree()

        val subtree = this.nodes.subList(start, end)

        // Get a subtree of the subtree to hoist
        val (subStart, subEnd) = this.getRandomSubtree(subtree)
        val hoist = subtree.subList(subStart, subEnd)

        this.nodes = (
            this.nodes.subList(0, start) +
            hoist +
            this.nodes.subList(end, this.nodes.size)
        ).toMutableList()
    }

    /**
     * Prints the tree as an S-expression.
     *
     * @returns A string representation of this tree as an S-expression.
     */
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

/**
 * @suppress
 */
fun <T> Random.choice(list: List<T>): T {
    return list[(this.nextDouble() * list.size).toInt()]
}

/**
 * @suppress
 */
fun List<Double>.cumulativeSum(): List<Double> {
    var total = 0.0

    return this.map { v ->
        total += v

        total
    }
}

/**
 * @suppress
 */
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