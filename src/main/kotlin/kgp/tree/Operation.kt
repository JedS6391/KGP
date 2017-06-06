package kgp.tree

/**
 * An operation is really just a mapping of arguments to a value.
 */
typealias Operation = (args: List<Double>) -> Double

/**
 * Exception given when the number of arguments given to a function does not match its arity.
 *
 * @param message A message describing what went wrong.
 */
class ArityException(message: String) : Exception(message)