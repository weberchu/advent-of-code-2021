import kotlin.math.pow

fun main() {

    val digitToSegmentsMap = mapOf(
        0 to listOf('a', 'b', 'c', 'e', 'f', 'g'),
        1 to listOf('c', 'f'),
        2 to listOf('a', 'c', 'd', 'e', 'g'),
        3 to listOf('a', 'c', 'd', 'f', 'g'),
        4 to listOf('b', 'c', 'd', 'f'),
        5 to listOf('a', 'b', 'd', 'f', 'g'),
        6 to listOf('a', 'b', 'd', 'e', 'f', 'g'),
        7 to listOf('a', 'c', 'f'),
        8 to listOf('a', 'b', 'c', 'd', 'e', 'f', 'g'),
        9 to listOf('a', 'b', 'c', 'd', 'f', 'g')
    )

    fun part1(input: List<String>): Int {
        val segmentSizeOf_1_4_7_8 = setOf(
            digitToSegmentsMap[1]!!.size,
            digitToSegmentsMap[4]!!.size,
            digitToSegmentsMap[7]!!.size,
            digitToSegmentsMap[8]!!.size
        )

        return input.map { line ->
            line.split("|")[1].split(" ")
        }.sumOf { outputDigits ->
            outputDigits.count { segmentSizeOf_1_4_7_8.contains(it.length) }
        }
    }

    /**
     * With the given 10 signal patterns, find the corresponding numbers.
     * @param signalPatterns list of all 10 signal patterns
     * @return a map with key=set of segments in Char, value=corresponding number value
     */
    fun findSegmentsToNumber(signalPatterns: List<String>): Map<Set<Char>, Int> {
        val numberMap = mutableMapOf<Int, Set<Char>>()
        val remainingSignalPatterns = mutableListOf<Set<Char>>()

        // Step 1: "1", "4", "7", "8" are unique in segment length of 2, 4, 3, 7 respectively
        signalPatterns.forEach { pattern ->
            when (pattern.length) {
                2 -> numberMap[1] = pattern.toSet()
                4 -> numberMap[4] = pattern.toSet()
                3 -> numberMap[7] = pattern.toSet()
                7 -> numberMap[8] = pattern.toSet()
                else -> remainingSignalPatterns.add(pattern.toSet())
            }
        }

        // Step 2: In remaining numbers, only "9" fully contains the segments from "4"
        val pattern9 = remainingSignalPatterns.find { it.containsAll(numberMap[4]!!) }!!
        numberMap[9] = pattern9
        remainingSignalPatterns.remove(pattern9)

        // Step 3: In remaining numbers, only "0" fully contains the segments from "7" with 6 segments.
        // Only "3" fully contains the segments from "7" with 5 segments.
        val pattern0 = remainingSignalPatterns.find { it.containsAll(numberMap[7]!!) && it.size == 6 }!!
        val pattern3 = remainingSignalPatterns.find { it.containsAll(numberMap[7]!!) && it.size == 5 }!!
        numberMap[0] = pattern0
        numberMap[3] = pattern3
        remainingSignalPatterns.remove(pattern0)
        remainingSignalPatterns.remove(pattern3)

        // Step 4: Remaining number with 6 segments must be "6"
        val pattern6 = remainingSignalPatterns.find {it.size == 6 }!!
        numberMap[6] = pattern6
        remainingSignalPatterns.remove(pattern6)

        // Step 5: Remaining number with a subset of "6" segments must be "5"
        val pattern5 = remainingSignalPatterns.find { numberMap[6]!!.containsAll(it) }!!
        numberMap[5] = pattern5
        remainingSignalPatterns.remove(pattern5)

        // Step 6: Last one is "2"
        numberMap[2] = remainingSignalPatterns.first()

        return numberMap.map { (num, segments) ->
            segments to num
        }.toMap()
    }

    /**
     * With the given number to segments mapping, find the number value of the given outputDigits
     * @param numberToSegments a map with key=set of segments in Char, value=corresponding number value
     * @param outputDigits always 4 digits in encoded segments
     * @return the number value of outputDigits
     */
    fun decodeValue(numberToSegments: Map<Set<Char>, Int>, outputDigits: List<String>): Int {
        return outputDigits.mapIndexed { index, digit ->
            numberToSegments[digit.toSet()]!! * 10.0.pow(outputDigits.size - index - 1).toInt()
        }.sum()
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line ->
            val lineSplit = line.split(" | ")
            val segmentsToNumber = findSegmentsToNumber(lineSplit[0].split(" "))

            val outputDigits = lineSplit[1].split(" ")
            val decodedValue = decodeValue(segmentsToNumber, outputDigits)

            decodedValue
        }
    }

    val input = readInput("Day08")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
