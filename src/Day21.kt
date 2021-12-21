import kotlin.math.max
import kotlin.math.min

private class DeterministicDice {
    var nextNumber = 0
    var rollCount = 0

    fun roll(): Int {
        rollCount++
        nextNumber++
        if (nextNumber > 100) {
            nextNumber -= 100
        }
        return nextNumber
    }
}

private data class GameResult(
    val player1Score: Int,
    val player2Score: Int,
    val rollCount: Int
)

fun main() {
    fun playGame(player1StartPosition: Int, player2StartPosition: Int, dice: DeterministicDice): GameResult {
        var player1Position = player1StartPosition
        var player2Position = player2StartPosition
        var player1Score = 0
        var player2Score = 0
        var isPlayer1Turn = true

        while (player1Score < 1000 && player2Score < 1000) {
            val move = dice.roll() + dice.roll() + dice.roll()

            if (isPlayer1Turn) {
                player1Position = (player1Position + move - 1) % 10 + 1
                player1Score += player1Position
            } else {
                player2Position = (player2Position + move - 1) % 10 + 1
                player2Score += player2Position
            }

            isPlayer1Turn = !isPlayer1Turn
        }

        return GameResult(player1Score, player2Score, dice.rollCount)
    }

    fun part1(input: List<String>): Int {
        val player1Position = input[0].substringAfter("starting position: ").toInt()
        val player2Position = input[1].substringAfter("starting position: ").toInt()

        val result = playGame(player1Position, player2Position, DeterministicDice())

        return min(result.player1Score, result.player2Score) * result.rollCount
    }

    val winningScore = 21
    // map of "number of move" to "number of outcome"
    val diracMovePossibility = mutableMapOf<Int, Int>()

    /**
     * Find all possible number of turns to win and their distribution
     *
     * @return a map with key = number of turns, value = a pair for number of possible outcome to win and not win
     */
    fun findTurnDistributionToWin(startPosition: Int): Map<Int, Pair<Int, Int>> {
        var turn = 1
        // key = pair of position and score, value = number of outcome
        var scoreDistribution = mutableMapOf<Pair<Int, Int>, Int>()
        scoreDistribution[Pair(startPosition, 0)] = 1

        val winningTurnDistribution = mutableMapOf<Int, Pair<Int, Int>>()

        do {
            val newScoreDistribution = mutableMapOf<Pair<Int, Int>, Int>()
            diracMovePossibility.forEach { (move, moveOutcomeCount) ->
                scoreDistribution.forEach { (positionScore, scoreOutcomeCount) ->
                    val newPosition = (positionScore.first + move - 1) % 10 + 1
                    val newScore = positionScore.second + newPosition
                    val newPositionScore = Pair(newPosition, newScore)

                    newScoreDistribution.putIfAbsent(newPositionScore, 0)
                    newScoreDistribution[newPositionScore] =
                        newScoreDistribution[newPositionScore]!! + moveOutcomeCount * scoreOutcomeCount
                }
            }

            val numberOfWins = newScoreDistribution.filterKeys { it.second >= winningScore }
                .map { (positionScore, outcomeCount) ->
                    newScoreDistribution.remove(positionScore)
                    outcomeCount
                }.sum()
            val numberOfNotWins = newScoreDistribution.map { (_, outcomeCount) ->
                outcomeCount
            }.sum()

            winningTurnDistribution[turn] = Pair(numberOfWins, numberOfNotWins)

            scoreDistribution = newScoreDistribution.filterKeys { it.second < winningScore }.toMutableMap()

            turn++
        } while (scoreDistribution.isNotEmpty())

        return winningTurnDistribution
    }

    /**
     * Rolling 3 times the Dirac dice will have a move range from 3 to 9,
     * each with a different number of universes for that outcome.
     *
     * So what we do is to find the number of universes that will pass 1000 scores in different number of steps,
     * for both player. Then we can compare both players possible steps to win to find out the number of wins.
     */
    fun part2(input: List<String>): Long {
        val player1Position = input[0].substringAfter("starting position: ").toInt()
        val player2Position = input[1].substringAfter("starting position: ").toInt()

        for (i in 1..3) {
            for (j in 1..3) {
                for (k in 1..3) {
                    val sum = i + j + k
                    diracMovePossibility.putIfAbsent(sum, 0)
                    diracMovePossibility[sum] = diracMovePossibility[sum]!! + 1
                }
            }
        }

        val player1TurnDistribution = findTurnDistributionToWin(player1Position)
        val player2TurnDistribution = findTurnDistributionToWin(player2Position)

        val player1Wins = player1TurnDistribution.map { (player1Turn, player1Outcome) ->
            if (player1Outcome.first > 0) {
                // when player 1 win and player 2 did not win in the previous turn
                val player2Outcome = player2TurnDistribution[player1Turn - 1]!!
                player1Outcome.first.toLong() * player2Outcome.second
            } else {
                0
            }
        }.sum()

        val player2Wins = player2TurnDistribution.map { (player2Turn, player2Outcome) ->
            if (player2Outcome.first > 0) {
                // when player 2 win and player 1 did not win in the same turn
                val player1Outcome = player1TurnDistribution[player2Turn]!!
                player2Outcome.first.toLong() * player1Outcome.second
            } else {
                0
            }
        }.sum()

        return max(player1Wins, player2Wins)
    }

    val input = readInput("Day21")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
