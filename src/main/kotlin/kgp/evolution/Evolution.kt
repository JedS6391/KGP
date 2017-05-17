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
    fun train(cases: Cases)
    fun test(cases: Cases): List<Double>
}

data class EvolutionOptions(
        val populationSize: Int,
        val generations: Int,
        val tournamentSize: Int,
        val crossoverRate: Double,
        val subtreeMutationRate: Double,
        val hoistMutationRate: Double,
        val pointMutationRate: Double,
        val pointReplacementRate: Double,
        val numOffspring: Int,
        val functionSet: List<Function>,
        val treeGeneratorOptions: TreeGeneratorOptions,
        val metric: Metric,
        val stoppingCriterion: Double
)

class BaseModel(val options: EvolutionOptions) : Model {

    override var population = mutableListOf<Tree>()
    private val generator = TreeGenerator(this.options.functionSet, this.options.treeGeneratorOptions)
    private val selector = TournamentSelection(this.options.tournamentSize)
    private val random = Random()

    lateinit var best: Tree

    private fun initialise() {
        (0..this.options.populationSize).forEach {
            this.population.add(this.generator.generateTree())
        }
    }

    override fun train(cases: Cases) {
        this.initialise()

        // Determine initial fitness values for population.
        // Assumes that the population has already been initialised.
        this.population.forEach { tree ->
            val outputs = cases.map { (features) ->
                tree.execute(features.map(Feature::value))
            }

            tree.fitness = this.options.metric.fitness(cases, outputs)
        }

        // Sort population so that the fittest individual is first.
        this.population = this.population.sortedBy(Tree::fitness).toMutableList()
        this.best = this.population.first()

        (0..this.options.generations).forEach { gen ->
            /*
            if (this.best.fitness < this.options.stoppingCriterion) {
                return
            }
            */

            // Choose some individuals for the next population
            (0..this.options.numOffspring).forEach {
                val individual = this.selector.tournament(this.population).copy()

                // Dispatch mutation
                when {
                    this.random.nextDouble() < this.options.crossoverRate -> {
                        val other = this.selector.tournament(this.population).copy()

                        individual.crossover(other)
                    }
                    this.random.nextDouble() < this.options.subtreeMutationRate -> individual.subtreeMutation()
                    this.random.nextDouble() < this.options.hoistMutationRate   -> individual.hoistMutation()
                    this.random.nextDouble() < this.options.pointMutationRate   -> individual.pointMutation(this.options.pointReplacementRate)
                }

                // Evaluate this individual
                val outputs = cases.map { (features) ->
                    individual.execute(features.map(Feature::value))
                }

                individual.fitness = this.options.metric.fitness(cases, outputs)

                // Choose a member of the existing population to replace with this new individual.
                // TODO: Make selector return index of tournament winner/loser.
                val loser = this.selector.tournament(this.population, negative = true)
                val loserIdx = this.population.indexOf(loser)

                this.population[loserIdx] = individual
            }

            this.population = this.population.sortedBy(Tree::fitness).toMutableList()
            this.best = this.population.first()

            println("Generation #$gen\t Best Fitness: ${this.best.fitness}\t Best Length: ${this.best.nodes.size}")
        }

        this.population = this.population.sortedBy(Tree::fitness).toMutableList()
        this.best = this.population.first()
    }

    override fun test(cases: Cases): List<Double> {
        return cases.map { (features) ->
            this.best.execute(features.map(Feature::value))
        }
    }
}