package main.kotlin.kgp.tree

typealias Operation = (args: List<Double>) -> Double

class ArityException(message: String) : Exception(message)