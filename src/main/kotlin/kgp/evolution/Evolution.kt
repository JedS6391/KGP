package main.kotlin.kgp.evolution

import main.kotlin.kgp.fitness.Cases
import main.kotlin.kgp.fitness.Feature
import main.kotlin.kgp.fitness.Metric
import main.kotlin.kgp.tree.Function
import main.kotlin.kgp.tree.Tree
import main.kotlin.kgp.tree.TreeGenerator
import main.kotlin.kgp.tree.TreeGeneratorOptions

interface Model {
    var population: MutableList<Tree>
    fun initialise()
    fun evolve(): Tree
}

data class EvolutionOptions(
        val populationSize: Int,
        val generations: Int,
        val functionSet: List<Function>,
        val treeGeneratorOptions: TreeGeneratorOptions,
        val fitnessCases: Cases,
        val metric: Metric
)

class BaseModel(val options: EvolutionOptions) : Model {

    override var population: MutableList<Tree> = mutableListOf()
    private val generator = TreeGenerator(this.options.functionSet, this.options.treeGeneratorOptions)

    override fun initialise() {
        (0..this.options.populationSize).forEach {
            this.population.add(this.generator.generateTree())
        }
    }

    override fun evolve(): Tree {
        // Determine initial fitness values for population
        this.population.forEach { tree ->
            val outputs = this.options.fitnessCases.map { (features) ->
                tree.execute(features.map(Feature::value))
            }

            tree.fitness = this.options.metric.fitness(this.options.fitnessCases, outputs)
        }

        // Sort population so that the fittest individual is first.
        this.population.sortedBy(Tree::fitness)

        return this.population.first()
    }

}