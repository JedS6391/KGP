package main.kotlin.kgp.tree

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
 */
abstract class Function(
        override val arity: Int,
        val representation: String
) : Node {

    abstract val operation: Operation

    override fun evaluate(args: List<Double>): Double {
        if (args.size != this.arity)
            throw ArityException("Expected ${this.arity} arguments but received ${args.size}")

        return this.operation(args)
    }
}

abstract class Terminal : Node {
    // Terminal node has no arguments and no children.
    override val arity = 0

    abstract override fun evaluate(args: List<Double>): Double
}

class Constant(val value: Double) : Terminal() {

    override fun evaluate(args: List<Double>): Double {
        return this.value
    }
}

class Input(val index: Int) : Terminal() {

    override fun evaluate(args: List<Double>): Double {
        return Double.NEGATIVE_INFINITY
    }
}

object Nodes {

    class Addition : Function(arity = 2, representation = "+") {

        override val operation: Operation =  { args ->
            args[0] + args[1]
        }
    }

    class Subtraction : Function(arity = 2, representation = "-") {

        override val operation: Operation =  { args ->
            args[0] - args[1]
        }
    }

    class Multiplication : Function(arity = 2, representation = "*") {

        override val operation: Operation =  { args ->
            args[0] * args[1]
        }
    }

    class Division : Function(arity = 2, representation = "/") {

        override val operation: Operation =  { args ->
            // If we get 0 as the divisor then change it to be 1.
            args[0] / if (args[1] == 0.0) 1.0 else args[1]
        }
    }

    class Sine : Function(arity = 1, representation = "sin") {

        override val operation: Operation =  { args ->
            Math.sin(args[0])
        }
    }

    class Cosine : Function(arity = 1, representation = "cos") {

        override val operation: Operation =  { args ->
            Math.cos(args[0])
        }
    }
}