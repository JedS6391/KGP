package kgp.examples

import kgp.tree.Function
import kgp.tree.Operation

/**
 * A set of custom operations used by the example problems.
 *
 * These can also be used by other problems from outside KGP core.
 */
object CustomOperations {

    /**
     * Large constant for undefined inputs.
     */
    val C_UNDEF = 10e9

    /**
     * Performs the inverse function on a single argument (1 / x).
     *
     * If x is zero then [C_UNDEF] will be given as output.
     */
    class Inverse : Function(arity = 1, representation = "1 /") {

        override val operation: Operation =  { args ->
            when {
                args[0] != 0.0 -> 1.0 / args[0]
                else -> args[0] + C_UNDEF
            }
        }
    }

    /**
     * Performs the negation function on a single argument (-x).
     */
    class Negation : Function(arity = 1, representation = "-") {

        override val operation: Operation =  { args ->
            -args[0]
        }
    }

    /**
     * Performs the square root function on a single argument (sqrt(x)).
     *
     * If x is less than zero then the function will give zero as output.
     */
    class SquareRoot : Function(arity = 1, representation = "sqrt") {
        override val operation: Operation = { args ->
            when {
            // Protect from negative values
                args[0] >= 0.0 -> Math.sqrt(args[0])
                else           -> 0.0
            }
        }
    }

    /**
     * Squares a single argument (x^2).
     */
    class Square : Function(arity = 1, representation = "square") {
        override val operation: Operation = { args ->
            Math.pow(args[0], 2.0)
        }
    }

    /**
     * Cubes a single argument (x^3).
     */
    class Cube : Function(arity = 1, representation = "cube") {
        override val operation: Operation = { args ->
            Math.pow(args[0], 3.0)
        }
    }

    /**
     * Performs the tangent function on a single argument (tan(x)).
     */
    class Tangent : Function(arity = 1, representation = "tan") {
        override val operation: Operation = { args ->
            Math.tan(args[0])
        }
    }

    /**
     * Performs the hyperbolic tangent function on a single argument (tanh(x)).
     */
    class HyperbolicTangent : Function(arity = 1, representation = "tanh") {
        override val operation: Operation = { args ->
            Math.tanh(args[0])
        }
    }

    /**
     * Performs the natural log function on a single argument (ln(x)).
     *
     * If x is zero then [C_UNDEF] will be given as output.
     */
    class NaturalLog : Function(arity = 1, representation = "ln") {
        override val operation: Operation = { args ->
            when {
                args[0] != 0.0 -> Math.log(Math.abs(args[0]))
                else -> args[0] + C_UNDEF
            }
        }
    }

    /**
     * Performs the exponent function on two arguments (|x|^y).
     *
     * If y is greater than 10, then x + y + [C_UNDEF] will be given as output.
     */
    class Exponent : Function(arity = 2, representation = "^") {
        override val operation: Operation = { args ->
            when {
                // Protect against overflowing exponents
                Math.abs(args[1]) <= 10 -> Math.pow(Math.abs(args[0]), args[1])
                else -> args[0] + args[1] + C_UNDEF
            }
        }
    }
}