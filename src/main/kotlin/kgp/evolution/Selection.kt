package main.kotlin.kgp.evolution

import main.kotlin.kgp.tree.Tree
import java.util.*

class TournamentSelection(val tournamentSize: Int) {

    val random = Random()

    fun tournament(population: List<Tree>, negative: Boolean = false): Tree {

        val contenders = (0..tournamentSize).map {
            random.choice(population)
        }

        return when {
            !negative -> contenders.minBy(Tree::fitness)!!
            else      -> contenders.maxBy(Tree::fitness)!!
        }
    }
}

fun <T> Random.choice(list: List<T>): T {
    return list[(this.nextDouble() * list.size).toInt()]
}
