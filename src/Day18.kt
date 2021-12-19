import java.lang.Exception
import kotlin.math.ceil

/**
 * During calculation, all numbers inside a snailfish numbers are represented in a single Char.
 *
 * Value 0-9 or under is represented with Char '0' to '9'
 * Value 10 or above is represented with 'a', 'b', 'c' etc... ascending in the ascii table
 */
fun main() {
    fun Char.isSnailfishDigit(): Boolean {
        return this != '[' && this != ']' && this != ','
    }

    fun Char.regularNumber(): Int {
        return if (this in '0'..'9') {
            this.digitToInt()
        } else {
            this - 'a' + 10
        }
    }

    fun Int.toSnailfishValue(): Char {
        return if (this <= 9) {
            this.digitToChar()
        } else {
            'a'.plus(this - 10)
        }
    }

    fun Char.addSnailfishValue(valueToAdd: Char): Char {
        val regularValue = this.regularNumber() + valueToAdd.regularNumber()
        return regularValue.toSnailfishValue()
    }

    fun explode(snailfishNumber: String, indexToExplode: Int): String {
//        println("explode = ${snailfishNumber} @$indexToExplode")
        // the pair to explode is always in "[X,Y]" format
        val left = snailfishNumber[indexToExplode + 1]
        val right = snailfishNumber[indexToExplode + 3]
        if (snailfishNumber[indexToExplode + 4] != ']') {
            throw Exception("snailfishNumber[indexToExplode+4] is not ']'")
        }

        var resultLeft = snailfishNumber.substring(0, indexToExplode)
        var resultRight = snailfishNumber.substring(indexToExplode + 5)

        for (i in resultLeft.length - 1 downTo 0) {
            if (resultLeft[i].isSnailfishDigit()) {
                resultLeft = resultLeft.replaceRange(i, i + 1, resultLeft[i].addSnailfishValue(left).toString())
                break
            }
        }

        for (i in resultRight.indices) {
            if (resultRight[i].isSnailfishDigit()) {
                resultRight = resultRight.replaceRange(i, i + 1, resultRight[i].addSnailfishValue(right).toString())
                break
            }
        }

        return resultLeft + "0" + resultRight
    }

    fun split(snailfishNumber: String, indexToSplit: Int): String {
//        println("split = ${snailfishNumber} @$indexToSplit")
        val valueToSplit = snailfishNumber[indexToSplit].regularNumber()
        val left = (valueToSplit / 2).toSnailfishValue()
        val right = (ceil(valueToSplit / 2.0).toInt()).toSnailfishValue()
        return snailfishNumber.substring(
            0,
            indexToSplit
        ) + "[$left,$right]" + snailfishNumber.substring(indexToSplit + 1)
    }

    fun reduce(snailfishNumber: String): String {
        var nestedPair = 0
        // explode rule
        var firstIndexGreaterThan10 = -1
        snailfishNumber.forEachIndexed { index, char ->
            if (char == '[') {
                nestedPair++
            } else if (char == ']') {
                nestedPair--
            } else if (firstIndexGreaterThan10 == -1 && char != ',' && char >= 'a') {
                firstIndexGreaterThan10 = index
            }

            if (nestedPair > 4) {
                return reduce(explode(snailfishNumber, index))
            }
        }

        if (firstIndexGreaterThan10 != -1) {
            return reduce(split(snailfishNumber, firstIndexGreaterThan10))
        }

        return snailfishNumber
    }

    fun magnitude(snailfishNumber: String): Int {
        if (snailfishNumber.length == 1) {
            return snailfishNumber[0].digitToInt()
        }

        var openBracketCount = 0
        snailfishNumber.forEachIndexed { index, char ->
            if (char == '[') {
                openBracketCount++
            } else if (char == ']') {
                openBracketCount--
            } else if (char == ',' && openBracketCount == 1) {
                // the ',' for the outermost snailfish number is found
                return 3 * magnitude(snailfishNumber.substring(1, index)) + 2 * magnitude(
                    snailfishNumber.substring(
                        index + 1,
                        snailfishNumber.length - 1
                    )
                )
            }
        }

        throw Exception("Malformed snailfish number $snailfishNumber")
    }

    fun part1(input: List<String>): Int {
        var sum = ""
        input.forEach { line ->
            sum = if (sum.isEmpty()) {
                line
            } else {
                reduce("[$sum,$line]")
            }
        }

        return magnitude(sum)
    }

    fun part2(input: List<String>): Int {
        var greatestMagnitude = -1
        input.forEach { left ->
            input.forEach { right ->
                val sum = magnitude(reduce("[$left,$right]"))
                if (sum > greatestMagnitude) {
                    greatestMagnitude = sum
                }
            }
        }

        return greatestMagnitude
    }

    val input = readInput("Day18")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
