package seamcarving

import java.util.*
import java.util.concurrent.ConcurrentHashMap

sealed class Seam(val totalEnergy: Double) {
    abstract fun path(): List<Pair<Int, Int>>
    fun addNode(position: Pair<Int, Int>, energy: Double): Seam {
        return SeamNode(position, energy, this)
    }
}

object SeamEnd : Seam(0.0) {
    override fun path() = emptyList<Pair<Int, Int>>()
}

data class SeamNode(val currentPosition: Pair<Int, Int>, val currentEnergy: Double, val nextNode: Seam) :
    Seam(currentEnergy + nextNode.totalEnergy) {
    override fun path(): List<Pair<Int, Int>> {
        return listOf(currentPosition) + nextNode.path()
    }
}

class SeamCarving(private val imageEnergies: ImageEnergies) {

    fun verticalSeam(): Seam {
        return (0 until width).toList().parallelStream().map {
            buildRecursiveSeamFrom(Pair(it, 0), verticalEndReached, nextVerticalPositions)
        }.min(Comparator.comparing { it.totalEnergy }).get()
    }

    fun horizontalSeam(): Seam {
        return (0 until height).toList().parallelStream().map {
            buildRecursiveSeamFrom(Pair(0, it), horizontalEndReached, nextHorizontalPositions)
        }.min(Comparator.comparing { it.totalEnergy }).get()
    }

    private val width = imageEnergies.width
    private val height = imageEnergies.height
    private var bestSeams = ConcurrentHashMap<Pair<Int, Int>, Seam>()
    private val verticalEndReached = { pos: Pair<Int, Int> -> pos.second == height }
    private val horizontalEndReached = { pos: Pair<Int, Int> -> pos.first == width }
    private val nextVerticalPositions = { pos: Pair<Int, Int> ->
        val (x, y) = pos
        listOf(x - 1, x, x + 1).filter { it in 0 until width }.map { Pair(it, y + 1) }
    }
    private val nextHorizontalPositions = { pos: Pair<Int, Int> ->
        val (x, y) = pos
        listOf(y - 1, y, y + 1).filter { it in 0 until height }.map { Pair(x + 1, it) }
    }

    /* optimisation which doesn't really improve the perf and decrease readability
    private val nextVerticalPositions = { pos: Pair<Int, Int> ->
        val (x, y) = pos
        if (x > 0 && x < width - 1) {
            listOf(Pair(x - 1, y + 1), Pair(x, y + 1), Pair(x + 1, y + 1))
        } else if (x > 0) {
            listOf(Pair(x - 1, y + 1), Pair(x, y + 1))
        } else {
            listOf(Pair(x, y + 1), Pair(x + 1, y + 1))
        }
    }
    private val nextHorizontalPositions = { pos: Pair<Int, Int> ->
        val (x, y) = pos
        if (y > 0 && y < height - 1) {
            listOf(Pair(x + 1, y - 1), Pair(x + 1, y), Pair(x + 1, y + 1))
        } else if (y > 0) {
            listOf(Pair(x + 1, y - 1), Pair(x + 1, y))
        } else {
            listOf(Pair(x + 1, y), Pair(x + 1, y + 1))
        }
    }
    */

    private fun buildIterativeSeamFrom(
        position: Pair<Int, Int>,
        endReached: (Pair<Int, Int>) -> Boolean,
        nextPositions: (Pair<Int, Int>) -> List<Pair<Int, Int>>
    ): Seam {
        val positionToExplore = ArrayDeque(listOf(position))
        while (positionToExplore.isNotEmpty()) {
            val currentPosition = positionToExplore.element()
            val nextPossiblePositions = nextPositions(currentPosition)
            if (nextPossiblePositions.any { endReached(it) }) {
                bestSeams[currentPosition] = SeamEnd.addNode(currentPosition, energyAt(currentPosition))
                positionToExplore.remove()
            } else {
                val unknownPaths = nextPossiblePositions.filter { !bestSeams.containsKey(it) }
                if (unknownPaths.isNotEmpty()) {
                    unknownPaths.forEach { positionToExplore.push(it) }
                } else {
                    val t: List<Seam> = nextPossiblePositions.map { bestSeams.getValue(it) }
                    val bestSeam: Seam = t.minByOrNull { it.totalEnergy }!!
                    bestSeams[currentPosition] = bestSeam.addNode(currentPosition, energyAt(currentPosition))
                    positionToExplore.remove()
                }

            }
        }
        return bestSeams[position]!!
    }

    private fun buildRecursiveSeamFrom(
        position: Pair<Int, Int>,
        endReached: (Pair<Int, Int>) -> Boolean,
        nextPositions: (Pair<Int, Int>) -> List<Pair<Int, Int>>
    ): Seam {
        return bestSeams.getOrElse(position) {
            if (endReached(position)) {
                return SeamEnd
            }
            nextPositions(position)
                .map {
                    buildRecursiveSeamFrom(it, endReached, nextPositions)
                }
                .minByOrNull { it.totalEnergy }!!
                .addNode(position, energyAt(position))
                .also {
                    bestSeams[position] = it
                }
        }
    }

    private fun energyAt(position: Pair<Int, Int>): Double {
        val (x, y) = position
        return imageEnergies.getEnergy(x, y)
    }

}