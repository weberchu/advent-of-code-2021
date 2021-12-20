import kotlin.math.abs

private interface AlignResult

private class NotAlign : AlignResult

private data class IsAlign(
    val orientationTransform: (Beacon) -> Beacon,
    val scannerLocation: Triple<Int, Int, Int>
) : AlignResult

typealias Beacon = Triple<Int, Int, Int>

private operator fun Triple<Int, Int, Int>.plus(t: Triple<Int, Int, Int>): Triple<Int, Int, Int> {
    return Triple(this.first + t.first, this.second + t.second, this.third + t.third)
}

private operator fun Triple<Int, Int, Int>.minus(t: Triple<Int, Int, Int>): Triple<Int, Int, Int> {
    return Triple(this.first - t.first, this.second - t.second, this.third - t.third)
}

/**
 * group of 4 transformations with
 *  - x-axis does not change
 *  - x-axis is flipped
 *  - y-axis as x-axis
 *  - flipped y-axis as x-axis
 *  - z-axis as x-axis
 *  - flipped z-axis as x-axis
 */
private val orientationTransforms = listOf(
    fun(inBeacon: Beacon): Beacon {
        return Beacon(inBeacon.first, inBeacon.second, inBeacon.third)
    },
    fun(inBeacon: Beacon): Beacon {
        return Beacon(-1 * inBeacon.first, inBeacon.third, inBeacon.second)
    },
    fun(inBeacon: Beacon): Beacon {
        return Beacon(inBeacon.second, inBeacon.third, inBeacon.first)
    },
    fun(inBeacon: Beacon): Beacon {
        return Beacon(-1 * inBeacon.second, inBeacon.first, inBeacon.third)
    },
    fun(inBeacon: Beacon): Beacon {
        return Beacon(inBeacon.third, inBeacon.first, inBeacon.second)
    },
    fun(inBeacon: Beacon): Beacon {
        return Beacon(-1 * inBeacon.third, inBeacon.second, inBeacon.first)
    }
).flatMap { transform ->
    // return itself the 3 more rotating at the x-axis (Triple.first)
    listOf(
        transform,
        fun(inBeacon: Beacon): Beacon {
            val base = transform(inBeacon)
            return Beacon(base.first, -1 * base.third, base.second)
        },
        fun(inBeacon: Beacon): Beacon {
            val base = transform(inBeacon)
            return Beacon(base.first, -1 * base.second, -1 * base.third)
        },
        fun(inBeacon: Beacon): Beacon {
            val base = transform(inBeacon)
            return Beacon(base.first, base.third, -1 * base.second)
        }
    )
}

fun main() {
    fun parseScannersInput(input: List<String>): List<List<Beacon>> {
        val scanners = mutableListOf<List<Beacon>>()
        var scanner = mutableListOf<Beacon>()
        input.forEach { line ->
            if (line.startsWith("---")) {
                scanner = mutableListOf()
            } else if (line.isEmpty()) {
                scanners.add(scanner)
            } else {
                val coordinates = line.split(",")
                scanner.add(Beacon(coordinates[0].toInt(), coordinates[1].toInt(), coordinates[2].toInt()))
            }
        }
        scanners.add(scanner)
        return scanners
    }

    fun withinDetectableRange(beacon: Beacon): Boolean {
        return beacon.first in -1000..1000 &&
            beacon.second in -1000..1000 &&
            beacon.third in -1000..1000
    }

    /**
     * Try to align scanner2 with scanner1.
     *
     * If they can align, the x, y, z direction of scanner 2 that can aligned to scanner 1 is returned.
     */
    fun align(scanner1: List<Beacon>, scanner1Location: Triple<Int, Int, Int>, scanner2: List<Beacon>): AlignResult {
        scanner1.forEach { beacon1 ->
            scanner2.forEach { beacon2 ->
                orientationTransforms.forEach { transform ->
                    // assume beacon2 is beacon1, and scanner 1 is at (0, 0, 0)
                    val transformedBeacon2 = transform(beacon2)
                    val scanner2Location = beacon1 - transformedBeacon2

                    var matchedCount = 1 // start with 1 because beacon 1 is assumed to match beacon2
                    for (beacon2Remaining in scanner2) {
                        if (beacon2Remaining != beacon2) {
                            val transformedBeacon2Remaining = transform(beacon2Remaining)
                            val beacon2RemainingPositionRelativeToScanner1 = scanner2Location + transformedBeacon2Remaining

                            if (withinDetectableRange(beacon2RemainingPositionRelativeToScanner1 - scanner1Location)) {
                                if (scanner1.contains(beacon2RemainingPositionRelativeToScanner1)) {
                                    matchedCount++
                                } else {
                                    break
                                }
                            }
                        }
                    }

                    if (matchedCount >= 12) {
                        // scanner1's beacons perfectly align with scanner2's
                        return IsAlign(transform, scanner2Location)
                    }
                }
            }
        }

        return NotAlign()
    }

    val allBeacons = mutableSetOf<Beacon>()
    val alignResults = mutableMapOf<Int, IsAlign>()

    fun alignScanners(scanners: List<List<Beacon>>) {
        // store all beacons relative to scanner 0
        allBeacons.addAll(scanners[0])
        val notYetIdentifiedScanners = (1 until scanners.size).toMutableList()
        val identifiedScanners = mutableListOf(0)
        val processedScanners = mutableListOf<Int>()
        alignResults[0] = IsAlign(orientationTransforms[0], Triple(0, 0 , 0))

        while (notYetIdentifiedScanners.isNotEmpty()) {
            val scannerBaseId = identifiedScanners.removeFirst()
            val scannerBase = scanners[scannerBaseId]
            val baseAlignResult = alignResults[scannerBaseId]!!
            val scannerBaseRelativeToScanner0 = scannerBase.map {
                baseAlignResult.orientationTransform.invoke(it) + baseAlignResult.scannerLocation
            }

            for (i in notYetIdentifiedScanners.size - 1 downTo 0) {
                val scannerToAlignId = notYetIdentifiedScanners[i]
                val scannerToAlign = scanners[scannerToAlignId]

                val alignResult = align(scannerBaseRelativeToScanner0, baseAlignResult.scannerLocation, scannerToAlign)

                if (alignResult is IsAlign) {
                    alignResults[scannerToAlignId] = alignResult
                    notYetIdentifiedScanners.removeAt(i)
                    identifiedScanners.add(scannerToAlignId)

                    allBeacons.addAll(scannerToAlign.map {
                        alignResult.orientationTransform.invoke(it) + alignResult.scannerLocation
                    })
                }
            }

            processedScanners.add(scannerBaseId)
        }
    }

    fun part1(): Int {
        return allBeacons.size
    }

    fun part2(): Int {
        var largestDistance = -1

        alignResults.forEach { (scanner1Index, scanner1Result) ->
            alignResults.forEach { (scanner2Index, scanner2Result) ->
                if (scanner1Index != scanner2Index) {
                    val difference = scanner1Result.scannerLocation - scanner2Result.scannerLocation
                    val distance = abs(difference.first) + abs(difference.second) + abs(difference.third)
                    if (distance > largestDistance) {
                        largestDistance = distance
                    }
                }
            }
        }

        return largestDistance
    }

    val input = readInput("Day19")
    val scanners = parseScannersInput(input)
    alignScanners(scanners)
    println("Part 1: " + part1())
    println("Part 2: " + part2())
}
