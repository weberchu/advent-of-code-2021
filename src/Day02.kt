fun main() {
    fun part1(input: List<String>): Int {
        var horizontal = 0
        var depth = 0

        input.forEach {
            val parsedTokens = it.split(" ")

            if (parsedTokens[0] == "forward") {
                horizontal += parsedTokens[1].toInt()
            } else if (parsedTokens[0] == "down") {
                depth += parsedTokens[1].toInt()
            } else if (parsedTokens[0] == "up") {
                depth -= parsedTokens[1].toInt()
            } else {
                throw Exception("unknown instruction: $it")
            }
        }

        return horizontal * depth
    }

    fun part2(input: List<String>): Int {
        var horizontal = 0
        var depth = 0
        var aim = 0

        input.forEach {
            val parsedTokens = it.split(" ")
            val instruction = parsedTokens[0]
            val value = parsedTokens[1].toInt()

            when (instruction) {
                "forward" -> {
                    horizontal += value
                    depth += aim * value
                }
                "down" -> {
                    aim += value
                }
                "up" -> {
                    aim -= value
                }
                else -> {
                    throw Exception("unknown instruction: $it")
                }
            }
        }

        return horizontal * depth
    }

    val input = readInput("Day02")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
