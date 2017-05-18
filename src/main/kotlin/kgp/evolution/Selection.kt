package main.kotlin.kgp.evolution

import main.kotlin.kgp.tree.Tree
import java.util.*

/**
 * A component that is able to perform tournament selection on a collection of individuals.
 *
 * The ability to perform "negative" tournaments is also provided, where losers from a population
 * are chosen instead of winners.
 *
 * @param tournamentSize The size of the tournaments that should be held.
 */
class TournamentSelection(val tournamentSize: Int) {

    private val random = Random()

    /**
     * Performs a tournament on [population].
     *
     * If the [negative] flag is set, then the tournament will choose losers instead of winners.
     *
     * @param population A collection of program trees.
     * @param negative Flag to control whether winners or losers are selected from the population.
     */
    fun tournament(population: List<Tree>, negative: Boolean = false): Tree {
        // Choose some individuals to participate in the tournament.
        val contenders = (0..tournamentSize).map {
            random.choice(population)
        }

        return when {
            // Determine the winners or losers depending on what mode.
            // Annoyingly, `minBy` can potentially return null, but we know
            // that there will always be contenders from the population so we
            // can safely assert that the value is non-null.
            !negative -> contenders.minBy(Tree::fitness)!!
            else      -> contenders.maxBy(Tree::fitness)!!
        }
    }
}

/**
 * @suppress
 */
fun <T> Random.choice(list: List<T>): T {
    return list[(this.nextDouble() * list.size).toInt()]
}
