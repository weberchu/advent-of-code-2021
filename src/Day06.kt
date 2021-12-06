fun main() {
    fun spawnFish(input: List<String>, days: Int): Long {
        val fish = input[0].split(",").map { it.toInt() }

        // a map storing the number of fish having the same timer
        // key: timer value
        // value: number of fish having that timer value
        var fishTimerCount = fish.fold(
            mutableMapOf(
                0 to 0L,
                1 to 0L,
                2 to 0L,
                3 to 0L,
                4 to 0L,
                5 to 0L,
                6 to 0L,
                7 to 0L,
                8 to 0L
            )
        ) { map, thisFish ->
            map[thisFish] = map[thisFish]!! + 1
            map
        }

        for (i in 1..days) {
            fishTimerCount = mutableMapOf(
                0 to fishTimerCount[1]!!,
                1 to fishTimerCount[2]!!,
                2 to fishTimerCount[3]!!,
                3 to fishTimerCount[4]!!,
                4 to fishTimerCount[5]!!,
                5 to fishTimerCount[6]!!,
                6 to fishTimerCount[7]!! + fishTimerCount[0]!!,
                7 to fishTimerCount[8]!!,
                8 to fishTimerCount[0]!!
            )
        }

        return fishTimerCount.values.sum()
    }

    fun part1(input: List<String>): Long {
        return spawnFish(input, 80)
    }

    fun part2(input: List<String>): Long {
        return spawnFish(input, 256)
    }

    val input = readInput("Day06")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
