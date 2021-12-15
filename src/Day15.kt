fun main() {
    val neighbourOffsets = listOf(
        Pair(-1, 0),
        Pair(1, 0),
        Pair(0, -1),
        Pair(0, 1)
    )

    fun findLowestRisk(map: List<String>): Int {
        // visited position in (x, y) pair mapping to the lowest total risk to this position
        val visitedPosition = mutableMapOf(
            Pair(0, 0) to 0
        )

        // list of positions pending to explore. Key is position, value is the risk before reaching this position
        val yetToExploreStack = mutableMapOf<Pair<Int, Int>, Int>()
        yetToExploreStack[Pair(0, 1)] = 0
        yetToExploreStack[Pair(1, 0)] = 0

        while (yetToExploreStack.isNotEmpty()) {
            val (position, riskSoFar) = yetToExploreStack.entries.first()
            yetToExploreStack.remove(position)

            val totalRisk = riskSoFar + map[position.second][position.first].digitToInt()

            if (visitedPosition.contains(position) && visitedPosition[position]!! < totalRisk) {
                // already visited with a lower risk before, no need to explore more
                continue
            }

            visitedPosition[position] = totalRisk

            val height = map.size
            val width = map[0].length
            neighbourOffsets.forEach { offset ->
                val neighbourX = position.first + offset.first
                val neighbourY = position.second + offset.second
                if (neighbourX in 0 until width && neighbourY in 0 until height) {
                    val neighbourPair = Pair(neighbourX, neighbourY)

                    // if this neighbour is not in the pending list, add it to pending
                    if (!yetToExploreStack.contains(neighbourPair) || yetToExploreStack[neighbourPair]!! > totalRisk) {
                        // if this neighbour exists in pending list with a higher risk, replace it with the current lower risk
                        yetToExploreStack[neighbourPair] = totalRisk
                    }
                }
            }
        }

        return visitedPosition[Pair(map[0].length - 1, map.size - 1)]!!
    }

    fun part1(map: List<String>): Int {
        return findLowestRisk(map)
    }

    fun mapRowWithOffset(row: String, offset: Int): String {
        return if (offset == 0) {
            row
        } else {
            row
                .map { ((it.digitToInt() + offset - 1) % 9 + 1).digitToChar() }
                .toCharArray()
                .concatToString()
        }
    }

    fun part2(input: List<String>): Int {
        val map = mutableListOf<String>()

        for (y in 0..4) {
            input.forEach { row ->
                map.add(listOf(0, 1, 2, 3, 4).joinToString("") { x ->
                    mapRowWithOffset(row, y + x)
                })
            }
        }

        return findLowestRisk(map)
    }

    val input = readInput("Day15")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
