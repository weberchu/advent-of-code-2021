fun main() {
    data class TargetArea(
        val xRange: Pair<Int, Int>,
        val yRange: Pair<Int, Int>
    )

    data class Probe(
        val position: Pair<Int, Int>,
        val velocity: Pair<Int, Int>
    )

    fun runStep(
        probe: Probe
    ): Probe {
        val newPosition = Pair(
            probe.position.first + probe.velocity.first,
            probe.position.second + probe.velocity.second
        )
        val newVelocity = Pair(
            if (probe.velocity.first > 0) probe.velocity.first - 1 else if (probe.velocity.first < 0) probe.velocity.first + 1 else 0,
            probe.velocity.second - 1
        )
        return Probe(newPosition, newVelocity)
    }

    fun parseTargetArea(input:List<String>): TargetArea {
        val target = input[0].substringAfter("target area: ").split(", ")
        val targetX = target[0].substringAfter("x=").split("..").map { it.toInt() }
        val targetY = target[1].substringAfter("y=").split("..").map { it.toInt() }
        return TargetArea(
            Pair(targetX[0], targetX[1]),
            Pair(targetY[0], targetY[1]),
        )
    }

    /**
     * Assuming the target y area is always negative, we can solve it in a more mathematical way.
     *
     * When the probe is launched with y-velocity Y, it will always come back down to y coordinate 0 with velocity -Y-1.
     * For instance, when launch with y-velocity 3, its y-coordinate and y-velocity will be:
     *  - (0, 3), (3, 2), (5, 1), (6, 0), (6, -1), (5, -2), (3, -3), (0, -4)
     * Knowing that, the highest t-position will be achieved when the probe make only one but the biggest step
     * when it comes back down and reaches the target t area.
     * In other words, if the deepest target y coordinate is -Y, we will achieve the highest position if we launch it
     * with y-velocity Y-1.
     *
     * Target x-position does not matter because X and Y position and velocity are completely independent.
     * So this assumes it is possible to reach the target x-position as well. Otherwise, there is always no solution.
     */
    fun part1(input: List<String>): Int {
        val targetArea = parseTargetArea(input)

        val lowestTargetY = targetArea.yRange.first
        val yVelocityToLaunch = lowestTargetY * -1 - 1

        // n + (n-1) + (n-2) + ... + 1 = n*(1+n)/2
        return yVelocityToLaunch * (1 + yVelocityToLaunch) / 2
    }

    fun isPossible(initialVelocity: Pair<Int, Int>, targetArea: TargetArea): Boolean {
        var probe = Probe(Pair(0, 0), initialVelocity)
        do {
            probe = runStep(probe)

            if (probe.position.first in targetArea.xRange.first..targetArea.xRange.second &&
                probe.position.second in targetArea.yRange.first..targetArea.yRange.second)
                return true
        } while (
            probe.position.first < targetArea.xRange.second &&
            probe.position.second > targetArea.yRange.first
        )

        return false
    }

    fun part2(input: List<String>): Int {
        val targetArea = parseTargetArea(input)

        /*
         * Range of possible initial x-velocity.
         * Max x-velocity is the max x in target area. Any higher will immediately go out of range in step 1.
         * Min x-velocity is a value that can reach the min x of target area before it is reduces to 0.
         */
        val maxXInitialVelocity = targetArea.xRange.second
        var minXInitialVelocity = 0
        var canReachTargetArea = false
        while (!canReachTargetArea) {
            minXInitialVelocity++

            canReachTargetArea = minXInitialVelocity * (1 + minXInitialVelocity) / 2 >= targetArea.xRange.first
        }

        /*
         * Range of possible initial y-velocity.
         * Min y-velocity is the min y in target area. Any lower will immediately go too low.
         * Max y-velocity is the same theory from part 1.
         */
        val minYInitialVelocity = targetArea.yRange.first
        val maxYInitialVelocity = targetArea.yRange.first * -1 - 1

        var possibleInitialVelocity = 0
        for (x in minXInitialVelocity..maxXInitialVelocity) {
            for (y in minYInitialVelocity..maxYInitialVelocity) {
                if (isPossible(Pair(x, y), targetArea)) {
                    possibleInitialVelocity++
                }
            }
        }

        return possibleInitialVelocity
    }

    val input = readInput("Day17")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
