fun main() {
    fun parseNumbersToDraw(input: List<String>): List<Int> = input[0].split(",").map { it.toInt() }

    fun parseBoards(input: List<String>): MutableList<List<List<Int>>> {
        val boards = mutableListOf<List<List<Int>>>()

        for (i in 2 until input.size step 6) {
            val board = mutableListOf<List<Int>>()
            for (row in 0..4) {
                board.add(input[i + row].split(" ").filter { it.isNotEmpty() }.map { it.toInt() })
            }
            boards.add(board)
        }

        return boards
    }

    fun createBoardMarkers(boards: MutableList<List<List<Int>>>): List<List<MutableList<Int>>> =
        boards.map {
            listOf(
                mutableListOf(0, 0, 0, 0, 0),
                mutableListOf(0, 0, 0, 0, 0),
                mutableListOf(0, 0, 0, 0, 0),
                mutableListOf(0, 0, 0, 0, 0),
                mutableListOf(0, 0, 0, 0, 0)
            )
        }

    fun isWinner(board: List<List<Int>>, rowIndex: Int, colIndex: Int): Boolean {
        if (board[rowIndex].all { it == 1 }) {
            return true
        } else if (board.all { it[colIndex] == 1 }) {
            return true
        }
        return false
    }

    fun score(board: List<List<Int>>, markers: List<List<Int>>, numberCalled: Int): Int {
        var uncalledSum = 0
        markers.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, marker ->
                if (marker == 0) {
                    uncalledSum += board[rowIndex][colIndex]
                }
            }
        }

        return uncalledSum * numberCalled
    }

    /**
     * Call a number for the Bingo game
     *
     * @param boards all the bingo boards
     * @param boardMarkers markers correspond to all bingo boards. 0 is unmarked. 1 is marked (called)
     * @param numberCalled the next number to call
     * @return list of winning board indices
     */
    fun callNumber(
        boards: List<List<List<Int>>>,
        boardMarkers: List<List<MutableList<Int>>>,
        numberCalled: Int
    ): List<Int> {
        val winnerBoards = mutableListOf<Int>()
        boards.forEachIndexed { boardIndex, board ->
            board.forEachIndexed { rowIndex, row ->
                row.forEachIndexed { colIndex, boardNumber ->
                    if (boardNumber == numberCalled) {
                        boardMarkers[boardIndex][rowIndex][colIndex] = 1
                        if (isWinner(boardMarkers[boardIndex], rowIndex, colIndex)) {
                            winnerBoards.add(boardIndex)
                        }
                    }
                }
            }
        }

        return winnerBoards
    }

    fun part1(input: List<String>): Int {
        val numbersToDraw = parseNumbersToDraw(input)
        val boards = parseBoards(input)

        // 0 is unmarked, 1 is marked by the drawn numbers
        val boardMarkers = createBoardMarkers(boards)

        numbersToDraw.forEach { numberCalled ->
            val winningBoardIndices = callNumber(boards, boardMarkers, numberCalled)
            if (winningBoardIndices.size == 1) {
                return score(boards[winningBoardIndices[0]], boardMarkers[winningBoardIndices[0]], numberCalled)
            } else if (winningBoardIndices.size > 1) {
                throw Exception("Multiple winners: $winningBoardIndices")
            }
        }

        throw Exception("No winner")
    }

    fun part2(input: List<String>): Int {
        val numbersToDraw = parseNumbersToDraw(input)
        val boards = parseBoards(input)

        // 0 is unmarked, 1 is marked by the drawn numbers
        val boardMarkers = createBoardMarkers(boards).toMutableList()

        numbersToDraw.forEach { numberCalled ->
            val winningBoardIndices = callNumber(boards, boardMarkers, numberCalled)

            if (boards.size == 1 && winningBoardIndices.size == 1) {
                return score(boards[0], boardMarkers[0], numberCalled)
            }

            winningBoardIndices.reversed().forEach { winningBoardIndex ->
                boards.removeAt(winningBoardIndex)
                boardMarkers.removeAt(winningBoardIndex)
            }

            if (boards.size == 0) {
                throw Exception("Multiple boards win at the end: $winningBoardIndices")
            }
        }

        throw Exception("There are board remains after all numbers called")
    }

    val input = readInput("Day04")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
