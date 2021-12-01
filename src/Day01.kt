fun main() {
    fun part1(input: List<String>): Int {
        var numberOfIncrement = 0
        for (i in 1 until input.size) {
            if (input[i].toInt() > input[i-1].toInt()) {
                numberOfIncrement++
            }
        }
        return numberOfIncrement
    }

    /**
     * Compare the sliding window of size 3.
     * Consider the first window comparison with the first 4 numbers - A, B, C, D
     * The isIncrement boolean is B+C+D > A+B+C, which is effectively D > A.
     * Therefore we can simplify compare input[i] > input[i-3]
     *
     */
    fun part2(input: List<String>): Int {
        var numberOfIncrement = 0
        for (i in 3 until input.size) {
            if (input[i].toInt() > input[i-3].toInt()) {
                numberOfIncrement++
            }
        }
        return numberOfIncrement
    }

    val input = readInput("Day01")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
