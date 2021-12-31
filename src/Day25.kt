fun main() {
    fun printMap(map: List<List<Char>>) {
        map.forEach { row ->
            println(row.joinToString(""))
        }
    }

    /**
     * Move a step
     *
     * @return pair.first: new map. pair.second: whether any sea cucumber has moved
     */
    fun move(map: List<List<Char>>): Pair<List<List<Char>>, Boolean> {
        // initialize new map with same size and all '.'
        val newMapAfterMoveEast = map.map { row ->
            row.map { '.' }.toMutableList()
        }

        var hasMoved = false

        // move east
        map.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { slotIndex, slot ->
                if (slot == '>') {
                    val nextSlotIndex = (slotIndex + 1) % row.size
                    if (map[rowIndex][nextSlotIndex] == '.') {
                        newMapAfterMoveEast[rowIndex][slotIndex] = '.'
                        newMapAfterMoveEast[rowIndex][nextSlotIndex] = '>'
                        hasMoved = true
                    } else {
                        newMapAfterMoveEast[rowIndex][slotIndex] = '>'
                    }
                } else if (slot == 'v') {
                    newMapAfterMoveEast[rowIndex][slotIndex] = map[rowIndex][slotIndex]
                }
            }
        }

        val newMapAfterMoveSouth = map.map { row ->
            row.map { '.' }.toMutableList()
        }

        // move south
        newMapAfterMoveEast.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { slotIndex, slot ->
                if (slot == 'v') {
                    val nextRowIndex = (rowIndex + 1) % newMapAfterMoveEast.size
                    if (newMapAfterMoveEast[nextRowIndex][slotIndex] == '.') {
                        newMapAfterMoveSouth[rowIndex][slotIndex] = '.'
                        newMapAfterMoveSouth[nextRowIndex][slotIndex] = 'v'
                        hasMoved = true
                    } else {
                        newMapAfterMoveSouth[rowIndex][slotIndex] = 'v'
                    }
                } else if (slot == '>') {
                    newMapAfterMoveSouth[rowIndex][slotIndex] = newMapAfterMoveEast[rowIndex][slotIndex]
                }
            }
        }

        return Pair(newMapAfterMoveSouth, hasMoved)
    }

    fun part1(input: List<String>): Int {
        var map = input.map {
            it.toList()
        }

        var hasMoved: Boolean
        var numOfMove = 0
        do {
            val result = move(map)
            map = result.first
            hasMoved = result.second
            numOfMove++
        } while (hasMoved)

        printMap(map)

        return numOfMove
    }

    val input = readInput("Day25")

    println("Part 1: " + part1(input))
}
