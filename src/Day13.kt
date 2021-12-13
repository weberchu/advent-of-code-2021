fun main() {
    fun processInput(input: List<String>): Pair<List<Pair<Char, Int>>, List<Pair<Int, Int>>> {
        val foldInstruction = mutableListOf<Pair<Char, Int>>()
        val dotPositions = mutableListOf<Pair<Int, Int>>()

        input.forEach { line ->
            if (line.startsWith("fold along ")) {
                val split = line.substringAfter("fold along ").split("=")
                foldInstruction.add(Pair(split[0].first(), split[1].toInt()))
            } else if (line.isNotBlank()) {
                val split = line.split(",")
                dotPositions.add(Pair(split[0].toInt(), split[1].toInt()))
            }
        }
        return Pair(foldInstruction, dotPositions)
    }

    fun fold(dotPositions: List<Pair<Int, Int>>, foldAxis: Char, foldPosition: Int): List<Pair<Int, Int>> {
        return when (foldAxis) {
            'x' -> {
                dotPositions.map { dot ->
                    if (dot.first > foldPosition) {
                        Pair(foldPosition - dot.first + foldPosition, dot.second)
                    } else {
                        dot
                    }
                }.toSet().toList()
            }
            'y' -> {
                dotPositions.map { dot ->
                    if (dot.second > foldPosition) {
                        Pair(dot.first, foldPosition - dot.second + foldPosition)
                    } else {
                        dot
                    }
                }.toSet().toList()
            }
            else -> {
                throw Exception("Unknown fold axis $foldAxis")
            }
        }
    }

    fun part1(input: List<String>): Int {
        val (foldInstruction, dotPositions) = processInput(input)

        val dotsAfterFirstFold = fold(dotPositions, foldInstruction[0].first, foldInstruction[0].second)

        return dotsAfterFirstFold.size
    }

    fun printDots(dotPositions: List<Pair<Int, Int>>) {
        var width = 0
        var height = 0
        val dotSet = mutableSetOf<Pair<Int, Int>>()
        dotPositions.forEach { dot ->
            if (dot.first > width) {
                width = dot.first
            }
            if (dot.second > height) {
                height = dot.second
            }
            dotSet.add(dot)
        }

        for (y in 0..height) {
            for (x in 0..width) {
                if (dotSet.contains(Pair(x, y))) {
                    print('#')
                } else {
                    print('.')
                }
            }
            println()
        }
    }

    fun part2(input: List<String>): Int {
        val processedInput = processInput(input)
        val foldInstruction = processedInput.first
        var dotPositions = processedInput.second

        foldInstruction.forEach { instruction ->
            dotPositions = fold(dotPositions, instruction.first, instruction.second)
        }

        printDots(dotPositions)

        return 0
    }

    val input = readInput("Day13")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
