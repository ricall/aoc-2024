package org.ricall.dayxx

import org.junit.jupiter.api.Test
import org.ricall.dayxx.Direction.*
import java.io.File
import kotlin.test.assertEquals

private data class Point(val x: Int, val y: Int) {
    operator fun plus(direction: Direction) = Point(x + direction.delta.x, y + direction.delta.y)
}

private enum class Direction(val delta: Point) {
    NORTH(Point(0, -1)),
    EAST(Point(1, 0)),
    SOUTH(Point(0, 1)),
    WEST(Point(-1, 0));
}

private class Memory(input: String, val width: Int, val height: Int) {
    val end = Point(width - 1, height - 1)
    val corruptions = input.lines().map { line ->
        val (x, y) = line.split(",").map(String::toInt)
        Point(x, y)
    }
    var corruptMemory: Set<Point> = emptySet()

    fun setTime(time: Int): Memory {
        corruptions.take(time).forEach { corruptMemory += it }
        return this
    }

    fun minSteps(): Int? {
        var steps = 0
        val visited = mutableSetOf<Point>()
        var edgePoints = listOf(Point(0, 0))
        while (edgePoints.isNotEmpty()) {
            edgePoints = buildList {
                edgePoints.forEach { point ->
                    if (point == end) {
                        return steps
                    }
                    sequenceOf(EAST, NORTH, WEST, SOUTH)
                        .map { point + it }
                        .filter {
                            it.x in 0..<width
                                    && it.y in 0..<height
                                    && !corruptMemory.contains(it)
                                    && !visited.contains(it)
                        }
                        .forEach {
                            add(it)
                            visited.add(it)
                        }
                }

            }
            steps++
        }
        return null
    }

    fun findCorruptionThatBlocksExit(): Point? {
        corruptions.forEach { point ->
            corruptMemory += point
            if (minSteps() == null) {
                return point
            }
        }
        return null
    }
}

class Day18 {
    private val TEST_DATA = """
        |5,4
        |4,2
        |4,5
        |3,0
        |2,1
        |6,3
        |2,4
        |1,5
        |0,6
        |3,3
        |2,6
        |5,1
        |1,2
        |5,5
        |2,5
        |6,5
        |1,4
        |0,4
        |6,4
        |1,1
        |6,1
        |1,0
        |0,5
        |1,6
        |2,0""".trimMargin()

    @Test
    fun `part 1 test data`() {
        val minSteps = Memory(TEST_DATA, 7, 7)
            .setTime(12)
            .minSteps()

        assertEquals(22, minSteps)
    }

    @Test
    fun `part 1`() {
        val minSteps = Memory(File("./inputs/day18.txt").readText(), 71, 71)
            .setTime(1024)
            .minSteps()

        assertEquals(278, minSteps)
    }

    @Test
    fun `part 2 test data`() {
        val point = Memory(TEST_DATA, 7, 7)
            .findCorruptionThatBlocksExit()

        assertEquals(Point(6, 1), point)
    }

    @Test
    fun `part 2`() {
        val point = Memory(File("./inputs/day18.txt").readText(), 71, 71)
            .findCorruptionThatBlocksExit()

        assertEquals(Point(43, 12), point)
    }
}