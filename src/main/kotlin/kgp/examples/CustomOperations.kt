package kgp.examples

import kgp.tree.Function
import kgp.tree.Operation

object CustomOperations {

    val C_UNDEF = 10e9

    class Inverse : Function(arity = 1, representation = "1 /") {

        override val operation: Operation =  { args ->
            when {
                args[0] != 0.0 -> 1.0 / args[0]
                else -> args[0] + C_UNDEF
            }
        }
    }

    class Negation : Function(arity = 1, representation = "-") {

        override val operation: Operation =  { args ->
            -args[0]
        }
    }

    class SquareRoot : Function(arity = 1, representation = "sqrt") {
        override val operation: Operation = { args ->
            when {
            // Protect from negative values
                args[0] >= 0.0 -> Math.sqrt(args[0])
                else           -> 0.0
            }
        }
    }

    class Square : Function(arity = 1, representation = "square") {
        override val operation: Operation = { args ->
            Math.pow(args[0], 2.0)
        }
    }

    class Cube : Function(arity = 1, representation = "cube") {
        override val operation: Operation = { args ->
            Math.pow(args[0], 3.0)
        }
    }

    class Tangent : Function(arity = 1, representation = "tan") {
        override val operation: Operation = { args ->
            Math.tan(args[0])
        }
    }

    class HyperbolicTangent : Function(arity = 1, representation = "tanh") {
        override val operation: Operation = { args ->
            Math.tanh(args[0])
        }
    }

    class NaturalLog : Function(arity = 1, representation = "ln") {
        override val operation: Operation = { args ->
            when {
                args[0] != 0.0 -> Math.log(Math.abs(args[0]))
                else -> args[0] + C_UNDEF
            }
        }
    }

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