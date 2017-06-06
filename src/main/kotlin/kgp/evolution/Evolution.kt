package kgp.evolution

import kgp.fitness.Cases
import kgp.fitness.Feature
import kgp.fitness.Metric
import kgp.tree.Function
import kgp.tree.Tree
import kgp.tree.TreeGenerator
import kgp.tree.TreeGeneratorOptions
import java.util.*
import java.util.stream.Collectors

/**
 * A model that trains a population of solutions.
 *
 * @property population A set of solutions to a problem.
 */
interface Model {
    var population: MutableList<Tree>

    /**
     * Trains the model on the cases given.
     *
     * @param cases A set of input-output cases.
     */
    fun train(cases: Cases)

    /**
     * Tests the model on a set of features.
     *
     * @param features A set of input features.
     * @returns A mapping of the input features to an output.
     */
    fun test(features: List<Feature>): Double
}

/**
 * A collection of options available to configure evolution by the [BaseModel].
 */
data class EvolutionOptions(
        /**
         * The number of solutions the model can have at any time during evolution.
         */
        val populationSize: Int,

        /**
         * The number of generations to perform evolution for.
         */
        val generations: Int,

        /**
         * The size of the tournaments performed by the [TournamentSelection] operator..
         */
        val tournamentSize: Int,

        /**
         * The probability that crossover will occur between two individuals in the population.
         */
        val crossoverRate: Double,

        /**
         * The probability that an individual will undergo a subtree mutation.
         */
        val subtreeMutationRate: Double,

        /**
         * The probability that an individual will undergo a hoist mutation.
         */
        val hoistMutationRate: Double,

        /**
         * The probability that an individual will undergo a point mutation.
         */
        val pointMutationRate: Double,

        /**
         * The frequency with which points should be chosen for point mutation.
         */
        val pointReplacementRate: Double,

        /**
         * Number of individuals taken at each generation to have genetic operators applied to them.
         */
        val numOffspring: Int,

        /**
         * The functions that are available to programs.
         */
        val functionSet: List<Function>,

        /**
         * Options that configure tree generation.
         */
        val treeGeneratorOptions: TreeGeneratorOptions,

        /**
         * A measure that can be used to evaluate individuals in the population.
         */
        val metric: Metric,

        /**
         * A threshold that configures how good solutions should be before stopping.
         *
         * If the threshold is never met then the evolution process will stop when the number
         * of generations given has been reached.
         */
        val stoppingThreshold: Double
)

/**
 * A base model for evolving trees.
 *
 * The best solution will be maintained by the model and will be made available
 * when testing (i.e. making a prediction) using the model.
 *
 * @property options A set of options for configuring the model.
 */
class BaseModel(val options: EvolutionOptions) : Model {

    /**
     * A collection of solutions this model has found.
     */
    override var population = mutableListOf<Tree>()
    private val generator = TreeGenerator(this.options.functionSet, this.options.treeGeneratorOptions)
    private val selector = TournamentSelection(this.options.tournamentSize)
    private val random = Random()

    /**
     * The best solution this model has found.
     */
    lateinit var best: Tree

    private fun initialise() {
        (0..this.options.populationSize).forEach {
            this.population.add(this.generator.generateTree())
        }
    }

    /**
     * Trains the model on a set of input-output cases.
     *
     * The best solution found will be kept and defines the model found for the program being searched for.
     *
     * @param cases A set of input-output cases.
     */
    override fun train(cases: Cases) {
        // Make sure we've got an initial population.
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
            if (this.best.fitness < this.options.stoppingThreshold) {
                return
            }

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
                val outputs = cases.parallelStream().map { (features) ->
                    individual.execute(features.map(Feature::value))
                }.collect(Collectors.toList())

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

    /**
     * Tests the model by evaluating the model on a set of features.
     *
     * Internally, the best program found during training will be used to satisfy the evaluation.
     *
     * @param features A set of features that the output is sought for.
     * @returns The output of evaluating the model using the given feature set.
     */
    override fun test(features: List<Feature>): Double {
        return this.best.execute(features.map(Feature::value))
    }
}