package kgp.tree

import kgp.tree.Operation

/**
 * A node that makes up a program tree.
 */
interface Node {
    /**
     * The number of arguments that this node expects.
     */
    val arity: Int

    /**
     * Evaluates this node on a set of arguments.
     */
    fun evaluate(args: List<Double>): Double
}

/**
 * A node that can perform some function on a set of arguments.
 *
 * Function nodes make up the inner nodes of a program tree.
 *
 * @property arity The number of arguments this function operates on.
 * @property representation A representation of this function that can be output.
 */
abstract class Function(
        override val arity: Int,
        val representation: String
) : Node {

    /**
     * An operation that this function performs when evaluated.
     */
    abstract val operation: Operation

    /**
     * Evaluates this function on the given arguments.
     *
     * @param args A collection of arguments. The number of arguments should match the functions arity.
     * @returns The result of applying this function to the arguments given.
     */
    override fun evaluate(args: List<Double>): Double {
        if (args.size != this.arity)
            throw ArityException("Expected ${this.arity} arguments but received ${args.size}")

        return this.operation(args)
    }
}

/**
 * A node that does not have any child nodes.
 *
 * Terminals are the leaf nodes of a program tree and must have an arity of 0.
 */
abstract class Terminal : Node {
    // Terminal node has no arguments and no children.
    override final val arity = 0

    abstract override fun evaluate(args: List<Double>): Double
}

/**
 *  A terminal node that represents a constant value.
 *
 *  Evaluating a constant node will simply result in the value the node encapsulates.
 *
 *  @property value The constant value of this node.
 */
class Constant(val value: Double) : Terminal() {

    /**
     * Gives the value stored at this node.
     */
    override fun evaluate(args: List<Double>): Double {
        return this.value
    }
}

/**
 * A terminal node that represents a feature value.
 *
 * The features a referenced by their index into the feature set.
 *
 * NOTE: Input nodes should not be directly evaluated, as evaluation results in
 * an infinite value. Instead, the index should be used to find the appropriate
 * feature from a feature set.
 *
 * @property index The index of the feature this node represents in a flat feature set.
 */
class Input(val index: Int) : Terminal() {

    /**
     * @suppress
     */
    override fun evaluate(args: List<Double>): Double {
        return Double.NEGATIVE_INFINITY
    }
}

/**
 * A collection of function nodes that are available by default.
 */
object Nodes {

    /**
     * Adds two arguments together (x + y).
     */
    class Addition : Function(arity = 2, representation = "+") {

        override val operation: Operation =  { args ->
            args[0] + args[1]
        }
    }

    /**
     * Subtracts one argument from the other (x - y).
     */
    class Subtraction : Function(arity = 2, representation = "-") {

        override val operation: Operation =  { args ->
            args[0] - args[1]
        }
    }

    /**
     * Multiplies two arguments together (x * y).
     */
    class Multiplication : Function(arity = 2, representation = "*") {

        override val operation: Operation =  { args ->
            args[0] * args[1]
        }
    }

    /**
     * Divides one argument by the other in a protected manner (x / if (x == 0) 1.0 else y).
     *
     * The division is protected to prevent divide by zero errors.
     */
    class Division : Function(arity = 2, representation = "/") {

        override val operation: Operation =  { args ->
            // If we get 0 as the divisor then we need to protect the division.
            args[0] / if (args[1] == 0.0) 1.0 else args[1]
        }
    }

    /**
     * Performs the sine function on a single argument (sin(x))
     */
    class Sine : Function(arity = 1, representation = "sin") {

        override val operation: Operation =  { args ->
            Math.sin(args[0])
        }
    }

    /**
     * Performs the cosine function on a single argument (cos(x))
     */
    class Cosine : Function(arity = 1, representation = "cos") {

        override val operation: Operation =  { args ->
            Math.cos(args[0])
        }
    }
}