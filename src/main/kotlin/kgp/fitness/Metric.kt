package main.kotlin.kgp.fitness


data class Feature(val value: Double, val name: String)

data class Case(val features: List<Feature>, val output: Double)

typealias Outputs = List<Double>
typealias Cases = List<Case>
typealias FitnessFunction = (Cases, Outputs) -> Double

class Metric(val function: FitnessFunction) {

    fun fitness(cases: Cases, outputs: Outputs): Double {
        return this.function(cases, outputs)
    }
}
