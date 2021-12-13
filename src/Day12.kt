fun main() {
    fun MutableMap<String, MutableList<String>>.putCaveEdge(from: String, to:String) {
        // do not put edges that starts with "end" or ends with "start"
        if (from != "end" && to != "start") {
            if (this[from] != null) {
                this[from]?.add(to)
            } else {
                this[from] = mutableListOf(to)
            }
        }
    }

    fun createCaveGraph(input: List<String>): MutableMap<String, MutableList<String>> {
        val caveGraph = mutableMapOf<String, MutableList<String>>()
        input.forEach { edge ->
            val caves = edge.split("-")
            caveGraph.putCaveEdge(caves[0], caves[1])
            caveGraph.putCaveEdge(caves[1], caves[0])
        }
        return caveGraph
    }

    fun isBigCave(cave: String): Boolean {
        return cave.matches(Regex("[A-Z]*"))
    }

    fun traverseCave(caveGraph: Map<String, List<String>>, visitedCaves: List<String>, nextCave: String, canRepeatSmallCaveOnce: Boolean): List<List<String>> {
        val newVisitedCaves = visitedCaves + nextCave

        if (nextCave == "end") {
            return listOf(newVisitedCaves)
        }

        return caveGraph[nextCave]?.mapNotNull { cave ->
            if (isBigCave(cave)) {
                // big cave, don't care if it has been visited before
                traverseCave(caveGraph, newVisitedCaves, cave, canRepeatSmallCaveOnce)
            } else {
                if (!visitedCaves.contains(cave)) {
                    // small cave never been before, retain the canRepeatSmallCaveOnce flag
                    traverseCave(caveGraph, newVisitedCaves, cave, canRepeatSmallCaveOnce)
                } else if (canRepeatSmallCaveOnce) {
                    // small cave visited before but have a quota left to visit again
                    traverseCave(caveGraph, newVisitedCaves, cave, false)
                } else {
                    // small cave visited before and no more quote to visit again
                    null
                }
            }
        }?.flatten() ?: emptyList()
    }

    fun part1(input: List<String>): Int {
        val visitedCaves = listOf<String>()

        val caveGraph = createCaveGraph(input)

        val possiblePaths = traverseCave(caveGraph, visitedCaves, "start", false)

        return possiblePaths.size
    }

    fun part2(input: List<String>): Int {
        val visitedCaves = listOf<String>()

        val caveGraph = createCaveGraph(input)

        val possiblePaths = traverseCave(caveGraph, visitedCaves, "start", true)

        return possiblePaths.size
    }

    val input = readInput("Day12")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
