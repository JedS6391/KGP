package main.kotlin.kgp.evolution

import main.kotlin.kgp.fitness.Cases
import main.kotlin.kgp.fitness.Feature
import main.kotlin.kgp.fitness.Metric
import main.kotlin.kgp.tree.Function
import main.kotlin.kgp.tree.Tree
import main.kotlin.kgp.tree.TreeGenerator
import main.kotlin.kgp.tree.TreeGeneratorOptions
import java.util.*

interface Model {
    var population: MutableList<Tree>
    fun initialise()
    fun evolve(): Tree
}

data class EvolutionOptions(
        val populationSize: Int,
        val generations: Int,
        val tournamentSize: Int,
        val crossoverRate: Double,
        val numOffspring: Int,
        val functionSet: List<Function>,
        val treeGeneratorOptions: TreeGeneratorOptions,
        val fitnessCases: Cases,
        val metric: Metric
)

class BaseModel(val options: EvolutionOptions) : Model {

    override var population: MutableList<Tree> = mutableListOf()
    private val generator = TreeGenerator(this.options.functionSet, this.options.treeGeneratorOptions)
    private val selector = TournamentSelection(this.options.tournamentSize)
    private val random = Random()

    override fun initialise() {
        (0..this.options.populationSize).forEach {
            this.population.add(this.generator.generateTree())
        }
    }

    override fun evolve(): Tree {
        // Determine initial fitness values for population.
        // Assumes that the population has already been initialised.
        this.population.forEach { tree ->
            val outputs = this.options.fitnessCases.map { (features) ->
                tree.execute(features.map(Feature::value))
            }

            tree.fitness = this.options.metric.fitness(this.options.fitnessCases, outputs)
        }

        // Sort population so that the fittest individual is first.
        this.population = this.population.sortedBy(Tree::fitness).toMutableList()

        (0..this.options.generations).forEach { gen ->
            // Choose some individuals for the next population
            (0..this.options.numOffspring).forEach {
                val individual = this.selector.tournament(this.population).copy()

                if (this.random.nextDouble() < this.options.crossoverRate) {

                    val other = this.selector.tournament(this.population).copy()

                    individual.crossover(other)
                }

                individual.subtreeMutation()

                // Evaluate this individual
                val outputs = this.options.fitnessCases.map { (features) ->
                    individual.execute(features.map(Feature::value))
                }

                individual.fitness = this.options.metric.fitness(this.options.fitnessCases, outputs)

                // Choose a member of the existing population to replace with this new individual.
                // TODO: Make selector return index of tournament winner/loser.
                val loser = this.selector.tournament(this.population, negative = true)
                val loserIdx = this.population.indexOf(loser)

                this.population[loserIdx] = individual
            }

            this.population = this.population.sortedBy(Tree::fitness).toMutableList()

            println("Generation #$gen\t Best Fitness: ${this.population.first().fitness}")
        }

        this.population = this.population.sortedBy(Tree::fitness).toMutableList()

        return this.population.first()
    }

}