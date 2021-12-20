import kotlin.math.pow

private data class Image(
    val lightPixels: Set<Pair<Int, Int>>,
    // indicate the pixel out of range is light or dark
    val infinityPixel: Char,
    val rangeX: IntRange,
    val rangeY: IntRange
)

fun main() {
    val indexOffset = listOf(
        Pair(-1, -1),
        Pair(0, -1),
        Pair(1, -1),
        Pair(-1, 0),
        Pair(0, 0),
        Pair(1, 0),
        Pair(-1, 1),
        Pair(0, 1),
        Pair(1, 1)
    )

    fun enhanceIndex(x: Int, y: Int, image: Image): Int {
        return indexOffset.map { offset ->
            if (x + offset.first in image.rangeX && y + offset.second in image.rangeY) {
                if (image.lightPixels.contains(Pair(x + offset.first, y + offset.second))) {
                    1
                } else {
                    0
                }
            } else {
                // out of range, so use infinity pixel
                if (image.infinityPixel == '#') 1 else 0
            }
        }.mapIndexed { index, i ->
            if (i == 1) {
                2.0.pow(8 - index).toInt()
            } else {
                0
            }
        }.sum()
    }

    fun enhance(image: Image, algorithm: String): Image {
        val newLightPixels = mutableSetOf<Pair<Int, Int>>()
        val newRangeX = image.rangeX.first - 1..image.rangeX.last + 1
        val newRangeY = image.rangeY.first - 1..image.rangeY.last + 1

        for (x in newRangeX) {
            for (y in newRangeY) {
                val enhanceIndex = enhanceIndex(x, y, image)
                if (algorithm[enhanceIndex] == '#') {
                    newLightPixels.add(Pair(x, y))
                }
            }
        }

        val newInfinityPixel = if (image.infinityPixel == '.') {
            algorithm[0]
        } else {
            algorithm[511]
        }

        return Image(newLightPixels, newInfinityPixel, newRangeX, newRangeY)
    }

    fun printImage(image: Image) {
        println("---------- x=${image.rangeX}, y=${image.rangeY}, infinity=${image.infinityPixel}")
        for (y in image.rangeY) {
            for (x in image.rangeX) {
                if (image.lightPixels.contains(Pair(x, y))) {
                    print('#')
                } else {
                    print('.')
                }
            }
            println()
        }
    }

    fun parseInput(input: List<String>): Pair<String, Image> {
        val algorithm = input[0]

        // coordinates of all light pixels
        // coordinates are relative to an arbitrary point
        val lightPixels = mutableSetOf<Pair<Int, Int>>()

        for (y in 2 until input.size) {
            input[y].forEachIndexed { x, pixel ->
                if (pixel == '#') {
                    lightPixels.add(Pair(x, y - 2))
                }
            }
        }

        val image = Image(lightPixels, '.', 0 until input[2].length, 0 until input.size - 2)
        return Pair(algorithm, image)
    }

    fun enhanceTimes(image: Image, algorithm: String, times: Int): Image {
        var enhancedImage = image
        for (i in 1..times) {
            enhancedImage = enhance(enhancedImage, algorithm)
        }
        return enhancedImage
    }

    fun part1(input: List<String>): Int {
        val parseResult = parseInput(input)
        val algorithm = parseResult.first
        var image = parseResult.second

        image = enhanceTimes(image, algorithm, 2)

        return image.lightPixels.size
    }

    fun part2(input: List<String>): Int {
        val parseResult = parseInput(input)
        val algorithm = parseResult.first
        var image = parseResult.second

        image = enhanceTimes(image, algorithm, 50)

        return image.lightPixels.size
    }

    val input = readInput("Day20")

    println("Part 1: " + part1(input))
    println("Part 2: " + part2(input))
}
