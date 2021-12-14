fun main() {
    fun MutableMap<String, Long>.addPairCount(pair: String, count: Long) {
        this.putIfAbsent(pair, 0)
        this.put(pair, this[pair]!! + count)
    }

    fun grow(originalPolymerSequenceCount: Map<String, Long>, mutationRules: Map<String, Pair<String, String>>): MutableMap<String, Long> {
        val newPolymerSequenceCount = mutableMapOf<String, Long>()

        originalPolymerSequenceCount.forEach { (pair, count) ->
            if (mutationRules.containsKey(pair)) {
                val newPair = mutationRules[pair]!!
                newPolymerSequenceCount.addPairCount(newPair.first, count)
                newPolymerSequenceCount.addPairCount(newPair.second, count)
            } else {
                newPolymerSequenceCount.addPairCount(pair, count)
            }
        }

        return newPolymerSequenceCount
    }

    fun runPolymer(input: List<String>, numberOfSteps: Int): Long {
        // store the occurrence of each element pair
        var polymerSequenceCount = mutableMapOf<String, Long>()
        // store the rule how each element pair will mutate into a double pairs
        val mutationRules = mutableMapOf<String, Pair<String, String>>()

        val polymerTemplate = input[0]
        for (i in 0..polymerTemplate.length - 2) {
            val pair = polymerTemplate.substring(i, i + 2)
            polymerSequenceCount.putIfAbsent(pair, 0)
            polymerSequenceCount[pair] = polymerSequenceCount[pair]!! + 1
        }

        for (i in 2 until input.size) {
            val rule = input[i].split(" -> ")

            mutationRules[rule[0]] = Pair(
                rule[0][0] + rule[1],
                rule[1] + rule[0][1]
            )
        }

        for (i in 1..numberOfSteps) {
            polymerSequenceCount = grow(polymerSequenceCount, mutationRules)
        }

        // We are counting the number of pairs above.
        // So all elements are double counted except the first and last element.
        val elementCount = mutableMapOf<Char, Long>()
        polymerSequenceCount.forEach { (pair, count) ->
            elementCount.putIfAbsent(pair[0], 0)
            elementCount.putIfAbsent(pair[1], 0)
            elementCount[pair[0]] = elementCount[pair[0]]!! + count
            elementCount[pair[1]] = elementCount[pair[1]]!! + count
        }

        val actualElementCount = elementCount.map { element ->
            val actualCount = if (element.key == polymerTemplate.first() || element.key == polymerTemplate.last()) {
                (element.value + 1) / 2
            } else {
                element.value / 2
            }
            element.key to actualCount
        }.toMap()

        val sortedElementCount = actualElementCount.entries.sortedBy { it.value }
        return sortedElementCount.last().value - sortedElementCount.first().value
    }

    fun part1(input: List<String>): Long {
        return runPolymer(input, 10)
    }

    fun part2(input: List<String>): Long {
        return runPolymer(input, 40)
    }

    val input = readInput("Day14")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
