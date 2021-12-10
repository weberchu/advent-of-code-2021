fun main() {
    val legalPairs = mapOf(
        '(' to ')',
        '[' to ']',
        '{' to '}',
        '<' to '>',
    )
    val illegalCharPoint = mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137,
    )
    val completionCharPoint = mapOf(
        '(' to 1,
        '[' to 2,
        '{' to 3,
        '<' to 4,
    )

    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val syntaxStack = ArrayDeque<Char>()
            var score = 0
            for (char in line) {
                when(char) {
                    '(', '[', '{', '<' -> syntaxStack.addLast(char)
                    ')', ']', '}', '>' -> {
                        val lastChar = syntaxStack.removeLast()
                        val expectedChar = legalPairs[lastChar]
                        if (char != expectedChar) {
//                            println("Expected $expectedChar, but found $char instead.")
                            score = illegalCharPoint[char]!!
                            break
                        }
                    }
                    else -> throw Exception("Unknown character $char")
                }
            }

            score
        }
    }

    fun part2(input: List<String>): Long {
        val lineScores = mutableListOf<Long>()
        input.forEach { line ->
            val syntaxStack = ArrayDeque<Char>()
            var isCorrupted = false
            for (char in line) {
                when(char) {
                    '(', '[', '{', '<' -> syntaxStack.addLast(char)
                    ')', ']', '}', '>' -> {
                        val lastChar = syntaxStack.removeLast()
                        val expectedChar = legalPairs[lastChar]
                        if (char != expectedChar) {
                            isCorrupted = true
                            break
                        }
                    }
                    else -> throw Exception("Unknown character $char")
                }
            }

            if (!isCorrupted) {
                var score = 0L
                while (syntaxStack.isNotEmpty()) {
                    val nextCharToComplete = syntaxStack.removeLast()
                    score = score * 5 + completionCharPoint[nextCharToComplete]!!
                }
                lineScores.add(score)
            }
        }

        return lineScores.sorted()[lineScores.size / 2]
    }

    val input = readInput("Day10")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
