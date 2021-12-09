fun main() {
    val neighboursOffset = listOf(
        Pair(-1, 0),
        Pair(0, -1),
        Pair(0, 1),
        Pair(1, 0)
    )

    fun part1(input: List<String>): Int {
        val width = input[0].length
        val height = input.size
        var lowPointHeightSum = 0

        input.forEachIndexed { y, row ->
            row.forEachIndexed { x, currentHeight ->
                val isLowPoint = neighboursOffset.all { offset ->
                    val neighbourX = x + offset.first
                    val neighbourY = y + offset.second

                    if (neighbourX in 0 until width && neighbourY in 0 until height) {
                        input[neighbourY][neighbourX] > currentHeight
                    } else {
                        true
                    }
                }

                if (isLowPoint) {
                    lowPointHeightSum += currentHeight.digitToInt() + 1
                }
            }
        }

        return lowPointHeightSum
    }

    fun floodMarkBasin(
        input: List<String>,
        visitedMarkers: MutableSet<Pair<Int, Int>>,
        coordinate: Pair<Int, Int>
    ): Int {
        visitedMarkers.add(coordinate)

        val width = input[0].length
        val height = input.size

        return 1 + neighboursOffset.sumOf { offset ->
            val neighbourX = coordinate.first + offset.first
            val neighbourY = coordinate.second + offset.second
            if (neighbourX in 0 until width && neighbourY in 0 until height
                && input[neighbourY][neighbourX] != '9' && !visitedMarkers.contains(Pair(neighbourX, neighbourY))
            ) {
                floodMarkBasin(input, visitedMarkers, Pair(neighbourX, neighbourY))
            } else {
                0
            }
        }
    }

    fun part2(input: List<String>): Int {
        val visitedMarkers = mutableSetOf<Pair<Int, Int>>()
        val basinSizes = mutableListOf<Int>()

        input.forEachIndexed { y, row ->
            for (x in row.indices) {
                if (!visitedMarkers.contains(Pair(x, y)) && input[y][x] != '9') {
                    visitedMarkers.add(Pair(x, y))
                    val basinSize = floodMarkBasin(input, visitedMarkers, Pair(x, y))
                    basinSizes.add(basinSize)
                }
            }
        }

        val sortedBasinSizes = basinSizes.sortedDescending()

        return sortedBasinSizes[0] * sortedBasinSizes[1] * sortedBasinSizes[2]
    }

    val input = readInput("Day09")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
