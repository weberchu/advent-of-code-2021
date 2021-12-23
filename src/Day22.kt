import kotlin.math.max
import kotlin.math.min

private data class CubeRange(
    val x: IntRange,
    val y: IntRange,
    val z: IntRange
) {
    fun overlapsWith(anotherCubeRange: CubeRange): Boolean {
        return this.x.overlapsWith(anotherCubeRange.x) &&
            this.y.overlapsWith(anotherCubeRange.y) &&
            this.z.overlapsWith(anotherCubeRange.z)
    }

    fun rangeContaining(anotherCubeRange: CubeRange): CubeRange? {
        return if (this.overlapsWith(anotherCubeRange)) {
            CubeRange(
                max(this.x.first, anotherCubeRange.x.first)..min(this.x.last, anotherCubeRange.x.last),
                max(this.y.first, anotherCubeRange.y.first)..min(this.y.last, anotherCubeRange.y.last),
                max(this.z.first, anotherCubeRange.z.first)..min(this.z.last, anotherCubeRange.z.last)
            )
        } else {
            null
        }
    }

    fun area(): Long {
        return (this.x.last - this.x.first + 1).toLong() *
            (this.y.last - this.y.first + 1) *
            (this.z.last - this.z.first + 1)
    }
}

private fun IntRange.overlapsWith(anotherRange: IntRange): Boolean {
    return this.contains(anotherRange.first) || this.contains(anotherRange.last) ||
        anotherRange.contains(this.first) || anotherRange.contains(this.last)
}

private fun IntRange.partition(valuesToPartition: List<Int>): List<IntRange> {
    val partitionedRanges = mutableListOf<IntRange>()
    var startValue = start

    valuesToPartition.forEach { nextPartition ->
        partitionedRanges.add(startValue until nextPartition)
        startValue = nextPartition
    }
    partitionedRanges.add(startValue..this.last)

    return partitionedRanges
}

fun main() {
    fun part1(input: List<String>): Int {
        val onCubes = mutableSetOf<Triple<Int, Int, Int>>()
        val validRange = -50..50

        input.forEach { line ->
            val split = line.split(" ")
            val onOff = split[0]
            val ranges = split[1].split(",").associate { rangeStr ->
                val rangeSplit = rangeStr.split("=")
                val rangeInts = rangeSplit[1].split("..").map { it.toInt() }
                val range = rangeInts[0]..rangeInts[1]
                rangeSplit[0] to range
            }

            if (onOff == "on") {
                for (x in ranges["x"]!!) {
                    if (x in validRange) {
                        for (y in ranges["y"]!!) {
                            if (y in validRange) {
                                for (z in ranges["z"]!!) {
                                    if (z in validRange) {
                                        onCubes.add(Triple(x, y, z))
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                for (x in ranges["x"]!!) {
                    if (x in validRange) {
                        for (y in ranges["y"]!!) {
                            if (y in validRange) {
                                for (z in ranges["z"]!!) {
                                    if (z in validRange) {
                                        onCubes.remove(Triple(x, y, z))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return onCubes.size
    }

    /**
     * Start from the end of the sequence.
     * We know the effect of the last sequence will definitely stay.
     * And the effect of the second to last sequence will stay of those not overlap with the last, and so on.
     */
    fun part2(input: List<String>): Long {
        var numberOfCubeOn = 0L
        val finalizedCubeRanges = mutableSetOf<CubeRange>()

        input.reversed().forEach { line ->
            val split = line.split(" ")
            val onOff = split[0]
            val ranges = split[1].split(",").associate { rangeStr ->
                val rangeSplit = rangeStr.split("=")
                val rangeInts = rangeSplit[1].split("..").map { it.toInt() }
                val range = rangeInts[0]..rangeInts[1]
                rangeSplit[0] to range
            }
            val newCubeRange = CubeRange(ranges["x"]!!, ranges["y"]!!, ranges["z"]!!)

            if (onOff == "off") {
                // remember this space is finalized as off
                finalizedCubeRanges.add(newCubeRange)
            } else {
                // all finalized cube ranges that overlap with the new cube range
                val overlappedFinalizedCubeRanges = finalizedCubeRanges.mapNotNull { finalizedCubeRange ->
                    newCubeRange.rangeContaining(finalizedCubeRange)
                }

                // get all the start and end edge of the finalized cube range that is within the new cube range
                val xAxisToSplit = overlappedFinalizedCubeRanges
                    .map { it.x }
                    .flatMap { listOf(it.first, it.last + 1) }
                    .filter { it > newCubeRange.x.first && it <= newCubeRange.x.last }
                    .toSortedSet()
                    .toList()
                val yAxisToSplit = overlappedFinalizedCubeRanges
                    .map { it.y }
                    .flatMap { listOf(it.first, it.last + 1) }
                    .filter { it > newCubeRange.y.first && it <= newCubeRange.y.last }
                    .toSortedSet()
                    .toList()
                val zAxisToSplit = overlappedFinalizedCubeRanges
                    .map { it.z }
                    .flatMap { listOf(it.first, it.last + 1) }
                    .filter { it > newCubeRange.z.first && it <= newCubeRange.z.last }
                    .toSortedSet()
                    .toList()

                // partition the new cube range with the split axis
                val partitionedRangeX = newCubeRange.x.partition(xAxisToSplit)
                val partitionedRangeY = newCubeRange.y.partition(yAxisToSplit)
                val partitionedRangeZ = newCubeRange.z.partition(zAxisToSplit)

                // for each partitioned cube range, it is on if it does not overlap with any finalized cube ranges
                for (x in partitionedRangeX) {
                    for (y in partitionedRangeY) {
                        for (z in partitionedRangeZ) {
                            val partitionedCubeRange = CubeRange(x, y, z)
                            val isOverlapWithFinalizedCubeRanges = overlappedFinalizedCubeRanges.any { it.overlapsWith(partitionedCubeRange) }
                            if (!isOverlapWithFinalizedCubeRanges) {
                                numberOfCubeOn += partitionedCubeRange.area()
                            }
                        }
                    }
                }

                finalizedCubeRanges.add(newCubeRange)
            }
        }

        return numberOfCubeOn
    }

    val input = readInput("Day22")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
