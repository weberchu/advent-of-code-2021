fun main() {
    val neighboursOffset = listOf(
        Pair(-1, -1),
        Pair(-1, 0),
        Pair(-1, 1),
        Pair(0, -1),
        Pair(0, 1),
        Pair(1, -1),
        Pair(1, 0),
        Pair(1, 1)
    )

    fun increaseEnergy(octopusGroup: MutableList<MutableList<Int>>, i: Int, j: Int) {
        octopusGroup[i][j]++

        if (octopusGroup[i][j] == 10) {
            neighboursOffset.forEach { offset ->
                val neighbourI = i + offset.first
                val neighbourJ = j + offset.second
                if (neighbourI in 0 until octopusGroup.size && neighbourJ in 0 until octopusGroup[i].size) {
                    increaseEnergy(octopusGroup, neighbourI, neighbourJ)
                }
            }
        }
    }

    fun executeStep(octopusGroup: MutableList<MutableList<Int>>): Int {
        for (i in octopusGroup.indices) {
            for (j in octopusGroup[i].indices) {
                increaseEnergy(octopusGroup, i, j)
            }
        }

        var flashCount = 0
        for (i in octopusGroup.indices) {
            for (j in octopusGroup[i].indices) {
                if (octopusGroup[i][j] >= 10) {
                    octopusGroup[i][j] = 0
                    flashCount++
                }
            }
        }

        return flashCount
    }

    fun part1(input: List<String>): Int {
        val octopusGroup = input.map { it.map { char -> char.digitToInt() }.toMutableList() }.toMutableList()

        var flashCount = 0
        for (i in 1 .. 100) {
            flashCount += executeStep(octopusGroup)
        }

        return flashCount
    }

    fun part2(input: List<String>): Int {
        val octopusGroup = input.map { it.map { char -> char.digitToInt() }.toMutableList() }.toMutableList()

        var stepCount = 0
        do {
            val flashCount = executeStep(octopusGroup)
            stepCount++
        } while (flashCount != 100)

        return stepCount
    }

    val input = readInput("Day11")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
