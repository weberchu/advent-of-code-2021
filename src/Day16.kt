interface Packet {
    val version: Int
    val typeID: Int
}

fun main() {
    data class LiteralPacket(
        override val version: Int,
        override val typeID: Int,
        val number: Long
    ) : Packet

    data class OperatorPacket(
        override val version: Int,
        override val typeID: Int,
        val subPackets: List<Packet>
    ) : Packet

    fun binaryInput(input: String) =
        input.map { it.digitToInt(16).toString(2).padStart(4, '0') }.joinToString("")

    fun parseNumber(binary: String): Pair<Long, String> {
        var currentPointer = -5
        var literalValue = ""
        do {
            currentPointer += 5
            literalValue += binary.substring(currentPointer + 1, currentPointer + 5)
        } while (binary[currentPointer] == '1')

        return Pair(literalValue.toLong(2), binary.substring(currentPointer + 5))
    }

    fun decodePacket(binaryInput: String): Pair<Packet, String> {
        val version = binaryInput.substring(0, 3).toInt(2)
        val typeID = binaryInput.substring(3, 6).toInt(2)

        return when (typeID) {
            4 -> {
                val (number, remainingInput) = parseNumber(binaryInput.substring(6))
                Pair(LiteralPacket(version, typeID, number), remainingInput)
            }
            else -> {
                val lengthTypeID = binaryInput[6]
                val subPackets = mutableListOf<Packet>()

                if (lengthTypeID == '0') {
                    val bitLength = binaryInput.substring(7, 22).toInt(2)
                    var remainingSubInput = binaryInput.substring(22, 22 + bitLength)

                    while (remainingSubInput.isNotEmpty()) {
                        val (packet, remaining) = decodePacket(remainingSubInput)
                        subPackets.add(packet)
                        remainingSubInput = remaining
                    }

                    Pair(OperatorPacket(version, typeID, subPackets), binaryInput.substring(22 + bitLength))
                } else {
                    val numberOfSubPackets = binaryInput.substring(7, 18).toInt(2)
                    var remainingSubInput = binaryInput.substring(18)

                    for (i in 1..numberOfSubPackets) {
                        val (packet, remaining) = decodePacket(remainingSubInput)
                        subPackets.add(packet)
                        remainingSubInput = remaining
                    }

                    Pair(OperatorPacket(version, typeID, subPackets), remainingSubInput)
                }
            }
        }
    }

    fun getVersionTotal(packet: Packet): Int {
        return if (packet is LiteralPacket) {
            packet.version
        } else {
            packet.version + (packet as OperatorPacket).subPackets.sumOf { getVersionTotal(it) }
        }
    }

    fun part1(input: List<String>): Int {
        val binaryInput = binaryInput(input[0])

        val (packet, _) = decodePacket(binaryInput)
        return getVersionTotal(packet)
    }

    fun evaluate(packet: Packet): Long {
        if (packet is LiteralPacket) {
            return packet.number
        }

        packet as OperatorPacket

        val subPacketValues = packet.subPackets.map { evaluate(it) }

        return when (packet.typeID) {
            0 -> subPacketValues.sum()
            1 -> subPacketValues.reduce { acc, number -> acc * number }
            2 -> subPacketValues.minOrNull()!!
            3 -> subPacketValues.maxOrNull()!!
            5 -> if (subPacketValues[0] > subPacketValues[1]) 1 else 0
            6 -> if (subPacketValues[0] < subPacketValues[1]) 1 else 0
            7 -> if (subPacketValues[0] == subPacketValues[1]) 1 else 0
            else -> throw Exception("Unknown typeID ${packet.typeID}")
        }

    }

    fun part2(input: List<String>): Long {
        val binaryInput = binaryInput(input[0])

        val (packet, _) = decodePacket(binaryInput)

        return evaluate(packet)
    }

    val input = readInput("Day16")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
