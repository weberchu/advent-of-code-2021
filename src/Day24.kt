private val validRegistryValue = setOf("w", "x", "y", "z")

private data class Registry(
    val w: Int,
    val x: Int,
    val y: Int,
    val z: Int
)

/**
 * Observation from the commands, and applied as assumption to optimize
 * - the 14 inp is separated to each other, so treating each inp until the command before next inp (or the end) as a
 *   block to process
 * - w is always at w, so the leftover value of w does not matter for next block
 * - x and y are always reset to 0 at each block before any use, so the leftover value of x and y do not matter for next block
 * - so the only registry value that matters to next block is z
 */
fun main() {
    fun runALU(commands: List<String>, input: List<Int>, initialState: Registry = Registry(0, 0, 0, 0)): Registry {
        val registry = mutableMapOf(
            "w" to initialState.w,
            "x" to initialState.x,
            "y" to initialState.y,
            "z" to initialState.z
        )

        val inputIterator = input.iterator()

        commands.forEach { command ->
            val cmd = command.split(" ")
            when (cmd[0]) {
                "inp" -> registry[cmd[1]] = inputIterator.next()
                "add", "mul", "div", "mod", "eql" -> {
                    val secondValue = if (validRegistryValue.contains(cmd[2])) {
                        registry[cmd[2]]!!
                    } else {
                        cmd[2].toInt()
                    }

                    when (cmd[0]) {
                        "add" -> registry[cmd[1]] = registry[cmd[1]]!! + secondValue
                        "mul" -> registry[cmd[1]] = registry[cmd[1]]!! * secondValue
                        "div" -> registry[cmd[1]] = registry[cmd[1]]!! / secondValue
                        "mod" -> registry[cmd[1]] = registry[cmd[1]]!! % secondValue
                        "eql" -> registry[cmd[1]] = if (registry[cmd[1]]!! == secondValue) 1 else 0
                    }
                }
                else -> println("Invalid command ${command}")
            }
        }

        return Registry(registry["w"]!!, registry["x"]!!, registry["y"]!!, registry["z"]!!)
    }

    fun monadBlock(initialZ: Int, input: Int, varLine3: Int, varLine4: Int, varLine14: Int): Int {
        var x = initialZ % 26 + varLine4
        var z = initialZ / varLine3

        x = if (x == input) {
            0
        } else {
            1
        }

        var y = 25 * x + 1
        z *= y

        y = (input + varLine14) * x

        z += y

        return z
    }

    /**
     * block variables in line 3, 4, and 14
     */
    val blockVariations = listOf(
        listOf(1, 14, 12), // impossible to make if statement true, must x26
        listOf(1, 10, 9), // impossible to make if statement true, must x26
        listOf(1, 13, 8), // impossible to make if statement true, must x26
        listOf(26, -8, 3),
        listOf(1, 11, 0), // impossible to make if statement true, must x26
        listOf(1, 11, 11), // impossible to make if statement true, must x26
        listOf(1, 14, 10), // impossible to make if statement true, must x26
        listOf(26, -11, 13),
        listOf(1, 14, 3), // impossible to make if statement true, must x26
        listOf(26, -1, 10),
        listOf(26, -8, 10),
        listOf(26, -5, 14),
        listOf(26, -16, 6),
        listOf(26, -6, 5)
    )

    /**
     * Each block is effectively doing this:
     * if (initialZ%26 + var@line4 == input)
     *   z = z / (1 or 26, var@line3)
     * else
     *   z = z / (1 or 26) * 26 + input + var@line14
     *
     * Key points:
     * 1. z will be divided by 26 when var@line4 = 26
     * 2. if we cannot get the if statement to true, z will be multiplied by 26 + some positive value. This is making
     *    z bigger, or cancel out the division in (1)
     *
     * So the strategy is to make as much if statement to true if possible, especially those near the end.
     *
     * Given the blockVariations above, I decided to let it inflate at the first 7 rounds and give it all input 9. After
     * 7 blocks z = 9919487.
     *
     */
    fun findModelNumber(commands: List<String>, isFindMax: Boolean = true): Long {
        val inputBlocks = mutableListOf<List<String>>()
        var inputBlock = mutableListOf<String>()

        commands.forEach { command ->
            if (command.startsWith("inp")) {
                if (inputBlock.isNotEmpty()) {
                    inputBlocks.add(inputBlock)
                    inputBlock = mutableListOf()
                }
            }
            inputBlock.add(command)
        }
        inputBlocks.add(inputBlock)

        val searchSequence = if (isFindMax) 9 downTo 1 else 1..9

        for (i1 in searchSequence) {
            val result1 = monadBlock(0, i1, blockVariations[0][0], blockVariations[0][1], blockVariations[0][2])
            for (i2 in searchSequence) {
                val result2 = monadBlock(result1, i2, blockVariations[1][0], blockVariations[1][1], blockVariations[1][2])
                for (i3 in searchSequence) {
                    val result3 = monadBlock(result2, i3, blockVariations[2][0], blockVariations[2][1], blockVariations[2][2])
                    for (i4 in searchSequence) {
                        val result4 = monadBlock(result3, i4, blockVariations[3][0], blockVariations[3][1], blockVariations[3][2])
                        for (i5 in searchSequence) {
                            val result5 = monadBlock(result4, i5, blockVariations[4][0], blockVariations[4][1], blockVariations[4][2])
                            for (i6 in searchSequence) {
                                val result6 = monadBlock(result5, i6, blockVariations[5][0], blockVariations[5][1], blockVariations[5][2])
                                for (i7 in searchSequence) {
                                    // block 1 to 7
                                    val result7 = monadBlock(result6, i7, blockVariations[6][0], blockVariations[6][1], blockVariations[6][2])

                                    // block 8, try to get if statements to true
                                    val i8 = result7 % 26 + blockVariations[7][1]
                                    if (i8 < 1 || i8 > 9) {
                                        continue
                                    }
                                    val result8 = monadBlock(result7, i8, blockVariations[7][0], blockVariations[7][1], blockVariations[7][2])

                                    for (i9 in searchSequence) {
                                        // block 9 must inflate. try as big as possible
                                        val result9 = monadBlock(result8, i9, blockVariations[8][0], blockVariations[8][1], blockVariations[8][2])

                                        // block 10 to 14, try to get if statements to true
                                        var prevResult = result9
                                        val resultMap = mutableMapOf<Int, Int>()
                                        var isResultFound = true
                                        for (blockNum in 9..13) {
                                            val inputRequired = prevResult % 26 + blockVariations[blockNum][1]
                                            resultMap[blockNum] = inputRequired
                                            if (inputRequired < 1 || inputRequired > 9) {
                                                isResultFound = false
                                                continue
                                            }
                                            val result = monadBlock(prevResult, inputRequired, blockVariations[blockNum][0], blockVariations[blockNum][1], blockVariations[blockNum][2])

                                            prevResult = result
                                        }

                                        if (isResultFound) {
                                            // max found
                                            val inputList = listOf(
                                                i1, i2, i3, i4, i5, i6, i7, i8, i9,
                                                resultMap[9]!!, resultMap[10]!!, resultMap[11]!!, resultMap[12]!!, resultMap[13]!!
                                            )

                                            // validate
                                            val validateResult = runALU(commands, inputList)
                                            println("validateResult = ${validateResult}")

                                            if (validateResult.z != 0) {
                                                throw Exception("Validate result is not 0")
                                            }

                                            return inputList.joinToString("") { it.toString() }.toLong()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return -1
    }

    fun part1(commands: List<String>): Long {
        return findModelNumber(commands)
    }

    fun part2(commands: List<String>): Long {
        return findModelNumber(commands, false)
    }

    val input = readInput("Day24")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
