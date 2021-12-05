fun main() {
    fun registerVent(ventCoordinates: MutableMap<Pair<Int, Int>, Int>, x: Int, y: Int) {
        ventCoordinates.compute(Pair(x, y)) { _, originalValue ->
            originalValue?.inc() ?: 1
        }
    }

    fun findMoreThanTwo(input: List<String>, countDiagonal: Boolean): Int {
        // a map with key=coordinates and value=number of vents (overlapped lines)
        val ventCoordinates = mutableMapOf<Pair<Int, Int>, Int>()

        input.forEach { line ->
            val fromTo = line.split(" -> ")
            val from = fromTo[0].split(",").map { it.toInt() }
            val to = fromTo[1].split(",").map { it.toInt() }

            if (from[0] == to[0]) {
                // vertical
                if (from[1] < to[1]) {
                    for (i in from[1]..to[1]) {
                        registerVent(ventCoordinates, from[0], i)
                    }
                } else {
                    for (i in to[1]..from[1]) {
                        registerVent(ventCoordinates, from[0], i)
                    }
                }
            } else if (from[1] == to[1]) {
                // horizontal
                if (from[0] < to[0]) {
                    for (i in from[0]..to[0]) {
                        registerVent(ventCoordinates, i, from[1])
                    }
                } else {
                    for (i in to[0]..from[0]) {
                        registerVent(ventCoordinates, i, from[1])
                    }
                }
            } else if (countDiagonal && (from[0] - to[0] == from[1] - to[1])) {
                // top left to bottom right
                if (from[0] < to[0]) {
                    for (i in 0..to[0]-from[0]) {
                        registerVent(ventCoordinates, from[0]+i, from[1]+i)
                    }
                } else {
                    for (i in 0..from[0]-to[0]) {
                        registerVent(ventCoordinates, to[0]+i, to[1]+i)
                    }
                }
            } else if (countDiagonal && (from[0] - to[0] == to[1] - from[1])) {
                // top right to bottom left
                if (from[0] < to[0]) {
                    for (i in 0..to[0]-from[0]) {
                        registerVent(ventCoordinates, from[0]+i, from[1]-i)
                    }
                } else {
                    for (i in 0..from[0]-to[0]) {
                        registerVent(ventCoordinates, to[0]+i, to[1]-i)
                    }
                }
//            } else {
//                    println("ignored line $line")
            }
        }

        return ventCoordinates.filterValues { value -> value >= 2 }.count()
    }

    fun part1(input: List<String>): Int {
        return findMoreThanTwo(input, false)
    }

    fun part2(input: List<String>): Int {
        return findMoreThanTwo(input, true)
    }

    val input = readInput("Day05")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
