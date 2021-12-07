import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

fun main() {
    fun part1(input: List<String>): Long {
        val positions = input[0].split(",").map { it.toInt() }.sorted()
        val median = positions[positions.size / 2].toLong()

        return positions.sumOf { abs(it - median) }
    }

    fun part2Cost(positions: List<Int>, targetPosition: Int): Long =
        positions.sumOf { position ->
            val distanceToMove = abs(position - targetPosition).toLong()
            val fuel = (1 + distanceToMove) * distanceToMove / 2
            fuel
        }

    fun part2(input: List<String>): Long {
        val positions = input[0].split(",").map { it.toInt() }
        val average = positions.average()
        val averageCeiling = ceil(average).toInt()
        val averageFloor = floor(average).toInt()

        val costToAverageCeiling = part2Cost(positions, averageCeiling)
        val costToAverageFloor = part2Cost(positions, averageFloor)

        return min(costToAverageCeiling, costToAverageFloor)
    }

    val input = readInput("Day07")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
