import kotlin.math.pow

enum class RateType {
    OxygenRating, CO2Rating
}

fun main() {
    fun part1(input: List<String>): Int {
        val numberOfBits = input[0].length
        val oneCount = IntArray(numberOfBits)

        // count the number of ones in each bit position
        input.forEach {
            it.toCharArray().forEachIndexed { index, char ->
                if (char == '1') {
                    oneCount[index]++
                }
            }
        }

        var gammaRateBinaryString = ""
        val halfInputSize = input.size / 2
        oneCount.forEach { count ->
            gammaRateBinaryString += if (count > halfInputSize) {
                "1"
            } else {
                "0"
            }
        }

        val gammaRate = gammaRateBinaryString.toInt(2)
        // epsilon has exact opposite bits of gamma so gamma + epsilon = 2 ^ numOfBits
        val epsilonRate = (2.0.pow(numberOfBits) - 1 - gammaRate).toInt()

        return gammaRate * epsilonRate
    }

    fun part2Rate(input: List<String>, rateType: RateType): Int {
        val numberOfBits = input[0].length
        var currentBit = 0
        var remainingInput = input

        while (remainingInput.size > 1) {
            if (currentBit >= numberOfBits) {
                throw Exception("Run out of bits to determine answer. Remaining input size is ${remainingInput.size}")
            }

            val oneList = mutableListOf<String>()
            val zeroList = mutableListOf<String>()
            remainingInput.forEach {
                if (it[currentBit] == '1') {
                    oneList.add(it)
                } else {
                    zeroList.add(it)
                }
            }

            remainingInput = if (rateType === RateType.OxygenRating) {
                if (oneList.size >= zeroList.size) oneList else zeroList
            } else {
                if (oneList.size < zeroList.size) oneList else zeroList
            }

            currentBit++
        }

        return remainingInput[0].toInt(2)
    }

    fun part2(input: List<String>): Int {
        val oxygenRating = part2Rate(input, RateType.OxygenRating)
        val co2Rating = part2Rate(input, RateType.CO2Rating)

        return oxygenRating * co2Rating
    }

    val input = readInput("Day03")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
