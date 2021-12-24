import java.lang.Exception
import java.util.*

private enum class Room {
    A, B, C, D
}

private data class BurrowState(
    // 4 rooms. Index 0 is the inner space
    val roomA: List<Char>,
    val roomB: List<Char>,
    val roomC: List<Char>,
    val roomD: List<Char>,
    // 0-1 is the spaces left of room A
    // 2 is between room A and B
    // 3 is between room B and C
    // 4 is between room C and D
    // 5-6 is the spaces right of room D
    val hallway: List<Char>
) {
    override fun toString(): String {
        var output = "\n#############\n" +
            "#${hallway[0]}${hallway[1]}.${hallway[2]}.${hallway[3]}.${hallway[4]}.${hallway[5]}${hallway[6]}#\n" +
            "###${roomA.last()}#${roomB.last()}#${roomC.last()}#${roomD.last()}###\n"
        for (i in roomA.size - 2 downTo 0) {
            output += "  #${roomA[i]}#${roomB[i]}#${roomC[i]}#${roomD[i]}#\n"
        }
        output += "  #########"
        return output
    }
}

// number of move needs to move from room door to hallway destination
private val hallwayMoveCount = mapOf(
    Room.A to mapOf(
        0 to 2,
        1 to 1,
        2 to 1,
        3 to 3,
        4 to 5,
        5 to 7,
        6 to 8
    ),
    Room.B to mapOf(
        0 to 4,
        1 to 3,
        2 to 1,
        3 to 1,
        4 to 3,
        5 to 5,
        6 to 6
    ),
    Room.C to mapOf(
        0 to 6,
        1 to 5,
        2 to 3,
        3 to 1,
        4 to 1,
        5 to 3,
        6 to 4
    ),
    Room.D to mapOf(
        0 to 8,
        1 to 7,
        2 to 5,
        3 to 3,
        4 to 1,
        5 to 1,
        6 to 2
    )
)

// key = start hallway position
// value = map with key = target room, value = Pair(hallway position needs to pass through, moves needed)
private val hallwayToRoomDoor = mapOf(
    0 to mapOf(
        'A' to Pair(listOf(1), 2),
        'B' to Pair(listOf(1, 2), 4),
        'C' to Pair(listOf(1, 2, 3), 6),
        'D' to Pair(listOf(1, 2, 3, 4), 8)
    ),
    1 to mapOf(
        'A' to Pair(listOf(), 1),
        'B' to Pair(listOf(2), 3),
        'C' to Pair(listOf(2, 3), 5),
        'D' to Pair(listOf(2, 3, 4), 7)
    ),
    2 to mapOf(
        'A' to Pair(listOf(), 1),
        'B' to Pair(listOf(), 1),
        'C' to Pair(listOf(3), 3),
        'D' to Pair(listOf(3, 4), 5)
    ),
    3 to mapOf(
        'A' to Pair(listOf(2), 3),
        'B' to Pair(listOf(), 1),
        'C' to Pair(listOf(), 1),
        'D' to Pair(listOf(4), 3)
    ),
    4 to mapOf(
        'A' to Pair(listOf(3, 2), 5),
        'B' to Pair(listOf(3), 3),
        'C' to Pair(listOf(), 1),
        'D' to Pair(listOf(), 1)
    ),
    5 to mapOf(
        'A' to Pair(listOf(4, 3, 2), 7),
        'B' to Pair(listOf(4, 3), 5),
        'C' to Pair(listOf(4), 3),
        'D' to Pair(listOf(), 1)
    ),
    6 to mapOf(
        'A' to Pair(listOf(5, 4, 3, 2), 8),
        'B' to Pair(listOf(5, 4, 3), 6),
        'C' to Pair(listOf(5, 4), 4),
        'D' to Pair(listOf(5), 2)
    )
)

private fun energyPerMove(amphipod: Char): Int {
    return when (amphipod) {
        'A' -> 1
        'B' -> 10
        'C' -> 100
        'D' -> 1000
        else -> throw Exception("Unknown amphipod $amphipod")
    }
}

private val roomToHallwayRange = mapOf(
    Room.A to Pair(1 downTo 0, 2..6),
    Room.B to Pair(2 downTo 0, 3..6),
    Room.C to Pair(3 downTo 0, 4..6),
    Room.D to Pair(4 downTo 0, 5..6)
)

private val roomToAmphipod = mapOf(
    Room.A to 'A',
    Room.B to 'B',
    Room.C to 'C',
    Room.D to 'D'
)

private class UnexploredState {
    private val sortedState = TreeMap<Int, MutableList<BurrowState>>()
    private var size = 0

    fun add(state: BurrowState, energy: Int) {
        if (sortedState.contains(energy)) {
            sortedState[energy]!!.add(state)
        } else {
            sortedState[energy] = mutableListOf(state)
        }
        size++
    }

    fun remove(state: BurrowState, energy: Int) {
        val states = sortedState[energy] ?: throw Exception("Energy $energy not found")
        if (!states.remove(state)) {
            throw Exception("State not found. $state")
        }
        if (states.isEmpty()) {
            sortedState.remove(energy)
        }
        size--
    }

    fun stateWithLowestEnergy(): Pair<BurrowState, Int>? {
        if (sortedState.isEmpty()) {
            return null
        }
        val entry = sortedState.firstEntry()
        return Pair(entry.value[0], entry.key)
    }

    fun size(): Int {
        return size
    }
}

fun main() {
    fun possibleHallwayLocation(hallway: List<Char>, from: Room): List<Int> {
        val possibleHallwayLocation = mutableListOf<Int>()

        val (downRange, upRange) = roomToHallwayRange[from]!!

        for (i in downRange) {
            if (hallway[i] == '.') {
                possibleHallwayLocation.add(i)
            } else {
                break
            }
        }
        for (i in upRange) {
            if (hallway[i] == '.') {
                possibleHallwayLocation.add(i)
            } else {
                break
            }
        }

        return possibleHallwayLocation
    }

    fun isRoomPositionStable(room: List<Char>, roomType: Room, position: Int): Boolean {
        val correctAmphipod = roomToAmphipod[roomType]
        return (position downTo 0).all { room[it] == correctAmphipod }
    }

    fun nextPossibleStatesFromRoom(
        burrow: BurrowState,
        fromRoom: Room,
    ): List<Pair<BurrowState, Int>> {
        val nextPossibleStates = mutableListOf<Pair<BurrowState, Int>>()

        val room = when (fromRoom) {
            Room.A -> burrow.roomA
            Room.B -> burrow.roomB
            Room.C -> burrow.roomC
            Room.D -> burrow.roomD
        }

        for (i in room.size - 1 downTo 0) {
            if (room[i] != '.') {
                val amphipod = room[i]

                // do not move amphipods that are in the right room
                if (!isRoomPositionStable(room, fromRoom, i)) {
                    possibleHallwayLocation(burrow.hallway, fromRoom).forEach { hallwayLocation ->
                        val newRoom = room.toMutableList()
                        newRoom[i] = '.'
                        val newHallway = burrow.hallway.toMutableList()
                        newHallway[hallwayLocation] = amphipod
                        // energy to move out of room
                        var move = room.size - i
                        // energy to move along hallway
                        move += hallwayMoveCount[fromRoom]!![hallwayLocation]!!

                        nextPossibleStates.add(
                            Pair(
                                when (fromRoom) {
                                    Room.A -> BurrowState(newRoom, burrow.roomB, burrow.roomC, burrow.roomD, newHallway)
                                    Room.B -> BurrowState(burrow.roomA, newRoom, burrow.roomC, burrow.roomD, newHallway)
                                    Room.C -> BurrowState(burrow.roomA, burrow.roomB, newRoom, burrow.roomD, newHallway)
                                    Room.D -> BurrowState(burrow.roomA, burrow.roomB, burrow.roomC, newRoom, newHallway)
                                },
                                move * energyPerMove(amphipod)
                            )
                        )
                    }
                }
                break
            }
        }

        return nextPossibleStates
    }

    fun nextPossibleStatesFromHallway(burrow: BurrowState): List<Pair<BurrowState, Int>> {
        val nextPossibleStates = mutableListOf<Pair<BurrowState, Int>>()

        val hallway = burrow.hallway
        for (i in hallway.indices) {
            if (hallway[i] != '.') {
                val amphipod = hallway[i]
                val (roomType, room) = when (amphipod) {
                    'A' -> Pair(Room.A, burrow.roomA)
                    'B' -> Pair(Room.B, burrow.roomB)
                    'C' -> Pair(Room.C, burrow.roomC)
                    'D' -> Pair(Room.D, burrow.roomD)
                    else -> throw Exception("unknown amphipod in hallway ${hallway[i]}")
                }

                if (room.last() == '.' && room.all { it == '.' || it == amphipod }) {
                    val (hallwayPassThroughPositions, hallwayMoves) = hallwayToRoomDoor[i]!![amphipod]!!
                    if (hallwayPassThroughPositions.all { hallway[it] == '.' }) {
                        // nothing is blocking the hallway to travel
                        val targetRoomPosition = room.indexOfFirst { it == '.' }
                        val numberOfMoves = hallwayMoves + room.size - targetRoomPosition

                        val newHallway = hallway.toMutableList()
                        newHallway[i] = '.'
                        val newRoom = room.toMutableList()
                        newRoom[targetRoomPosition] = hallway[i]

                        nextPossibleStates.add(
                            Pair(
                                when (roomType) {
                                    Room.A -> BurrowState(newRoom, burrow.roomB, burrow.roomC, burrow.roomD, newHallway)
                                    Room.B -> BurrowState(burrow.roomA, newRoom, burrow.roomC, burrow.roomD, newHallway)
                                    Room.C -> BurrowState(burrow.roomA, burrow.roomB, newRoom, burrow.roomD, newHallway)
                                    Room.D -> BurrowState(burrow.roomA, burrow.roomB, burrow.roomC, newRoom, newHallway)
                                },
                                numberOfMoves * energyPerMove(amphipod)
                            )
                        )
                    }
                }
            }
        }

        return nextPossibleStates
    }

    /**
     * All next possible states and their required energy
     */
    fun nextPossibleStates(burrow: BurrowState): List<Pair<BurrowState, Int>> {
        val nextPossibleStates = mutableListOf<Pair<BurrowState, Int>>()

        // move the outermost in room A to hallway
        nextPossibleStates.addAll(nextPossibleStatesFromRoom(burrow, Room.A))
        nextPossibleStates.addAll(nextPossibleStatesFromRoom(burrow, Room.B))
        nextPossibleStates.addAll(nextPossibleStatesFromRoom(burrow, Room.C))
        nextPossibleStates.addAll(nextPossibleStatesFromRoom(burrow, Room.D))

        // move from hallway into a room
        nextPossibleStates.addAll(nextPossibleStatesFromHallway(burrow))

        return nextPossibleStates
    }

    fun findMinEnergy(initialState: BurrowState, targetState: BurrowState): Int {
        val startTime = System.currentTimeMillis()
        println("initialState = ${initialState}")

        val minEnergyToState = mutableMapOf<BurrowState, Int>()
        val unexploredStates = UnexploredState()
        val previousState = mutableMapOf<BurrowState, BurrowState>()

        minEnergyToState[initialState] = 0
        unexploredStates.add(initialState, 0)

        val measure = mutableMapOf(
            1 to 0L,
            2 to 0L,
            3 to 0L
        )

        var nextState = unexploredStates.stateWithLowestEnergy()

        while (nextState != null) {
            val time1 = System.currentTimeMillis()
            val stateToExplore = nextState.first
            val time2 = System.currentTimeMillis()
            measure[1] = measure[1]!! + time2 - time1

            val energyToState = minEnergyToState[stateToExplore]!!
            if (stateToExplore == targetState) {
                val backwardPathToInitialState = mutableListOf<BurrowState>()
                var prev: BurrowState? = stateToExplore
                while (prev != null) {
                    backwardPathToInitialState.add(prev)
                    prev = previousState[prev]
                }

                println("\n\nPath from initial to target:")
                var prevEnergy = 0
                backwardPathToInitialState.reversed().forEach {
                    println(it)
                    println("--@${minEnergyToState[it]} (+${minEnergyToState[it]!! - prevEnergy})------------------")
                    prevEnergy = minEnergyToState[it]!!
                }

                println("Time taken = ${System.currentTimeMillis() - startTime}")
                return energyToState
            }

            unexploredStates.remove(stateToExplore, nextState.second)

//            if (energyToState % 500 == 0) {
//                println("nextStateToExplore = ${stateToExplore} @ $energyToState")
//                println("unexploredStates.size = ${unexploredStates.size()}")
//                println("measure = ${measure}")
//            }

            val time3 = System.currentTimeMillis()
            measure[2] = measure[2]!! + time3 - time2

            val nextPossibleStates = nextPossibleStates(stateToExplore)
            nextPossibleStates.forEach { (nextPossibleState, nextEnergy) ->
                val totalEnergyToNextPossibleState = energyToState + nextEnergy
                if (!minEnergyToState.containsKey(nextPossibleState)) {
                    unexploredStates.add(nextPossibleState, totalEnergyToNextPossibleState)
                } else if (totalEnergyToNextPossibleState < minEnergyToState[nextPossibleState]!!) {
                    unexploredStates.remove(nextPossibleState, minEnergyToState[nextPossibleState]!!)
                    unexploredStates.add(nextPossibleState, totalEnergyToNextPossibleState)
                }

                if (!minEnergyToState.containsKey(nextPossibleState) ||
                    totalEnergyToNextPossibleState < minEnergyToState[nextPossibleState]!!
                ) {
                    minEnergyToState[nextPossibleState] = totalEnergyToNextPossibleState
                    previousState[nextPossibleState] = stateToExplore
                }
            }

            val time4 = System.currentTimeMillis()
            measure[3] = measure[3]!! + time4 - time3

            nextState = unexploredStates.stateWithLowestEnergy()
        }

        return -1
    }

    fun part1(input: List<String>): Int {
        val hallway =
            listOf(input[1][1], input[1][2], input[1][4], input[1][6], input[1][8], input[1][10], input[1][11])
        val outerRoomRow = input[2].split("#").filter { it.isNotBlank() }.map { it.first() }
        val innerRoomRow = input[3].split("#").filter { it.isNotBlank() }.map { it.first() }

        val initialState = BurrowState(
            listOf(innerRoomRow[0], outerRoomRow[0]),
            listOf(innerRoomRow[1], outerRoomRow[1]),
            listOf(innerRoomRow[2], outerRoomRow[2]),
            listOf(innerRoomRow[3], outerRoomRow[3]),
            hallway
        )

        val targetState = BurrowState(
            listOf('A', 'A'),
            listOf('B', 'B'),
            listOf('C', 'C'),
            listOf('D', 'D'),
            listOf('.', '.', '.', '.', '.', '.', '.')
        )

        return findMinEnergy(initialState, targetState)
    }

    fun part2(input: List<String>): Int {
        val hallway =
            listOf(input[1][1], input[1][2], input[1][4], input[1][6], input[1][8], input[1][10], input[1][11])
        val outerRoomRow = input[2].split("#").filter { it.isNotBlank() }.map { it.first() }
        val innerRoomRow = input[3].split("#").filter { it.isNotBlank() }.map { it.first() }

        val initialState = BurrowState(
            listOf(innerRoomRow[0], 'D', 'D', outerRoomRow[0]),
            listOf(innerRoomRow[1], 'B', 'C', outerRoomRow[1]),
            listOf(innerRoomRow[2], 'A', 'B', outerRoomRow[2]),
            listOf(innerRoomRow[3], 'C', 'A', outerRoomRow[3]),
            hallway
        )

        val targetState = BurrowState(
            listOf('A', 'A', 'A', 'A'),
            listOf('B', 'B', 'B', 'B'),
            listOf('C', 'C', 'C', 'C'),
            listOf('D', 'D', 'D', 'D'),
            listOf('.', '.', '.', '.', '.', '.', '.')
        )

        return findMinEnergy(initialState, targetState)
    }

    val input = readInput("Day23")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
