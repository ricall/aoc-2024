package org.ricall.day08

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private val TEST_DATA = """
    |............
    |........0...
    |.....0......
    |.......0....
    |....0.......
    |......A.....
    |............
    |............
    |........A...
    |.........A..
    |............
    |............""".trimMargin()

private data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    operator fun minus(other: Point) = Point(x - other.x, y - other.y)
}

private fun parseInput(text: String): Triple<Int, Int, Map<Char, List<Point>>> {
    val antennas = mutableMapOf<Char, MutableList<Point>>()
    val lines = text.lines()
    val width = lines[0].length
    val height = lines.size

    lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            if (c != '.') {
                antennas.getOrPut(c) { mutableListOf() }.add(Point(x, y))
            }
        }
    }
    return Triple(width, height, antennas)
}

private fun interface Strategy { fun accept(p1: Point, p2: Point, add: (Point) -> Boolean) }

private val singleLocation = Strategy { p1, p2, add ->
    val delta = p2 - p1

    add(p1 - delta)
    add(p2 + delta)
}

private val resonantLocations = Strategy { p1, p2, add ->
    val delta = p2 - p1

    var candidate = p1 + delta
    while (add(candidate)) { candidate += delta }

    candidate = p2 - delta
    while (add(candidate)) { candidate -= delta }
}

private fun countUniqueLocations(text: String, strategy: Strategy): Int {
    val locations = mutableSetOf<Point>()
    val (width, height, antennas) = parseInput(text)

    val isValid = { point: Point -> point.x in 0 until width && point.y in 0 until height }
    val add = { point: Point ->
        if (isValid(point)) {
            locations.add(point)
            true
        } else {
            false
        }
    }

    antennas.values.forEach { antennaList ->
        val antennaCount = antennaList.size
        (0 until antennaCount-1).forEach { index1 ->
            val p1 = antennaList[index1]
            (index1 + 1 until antennaCount).forEach { index2 ->
                val p2 = antennaList[index2]
                strategy.accept(p1, p2, add)
            }
        }
    }
    return locations.size
}

class Day08 {
    @Test
    fun `part 1 test data`() {
        val count = countUniqueLocations(TEST_DATA, singleLocation)
        assertEquals(14, count)
    }

    @Test
    fun `part 1`() {
        val count = countUniqueLocations(File("./inputs/day8.txt").readText(), singleLocation)
        assertEquals(269, count)
    }

    @Test
    fun `part 2 test data`() {
        val count = countUniqueLocations(TEST_DATA, resonantLocations)
        assertEquals(34, count)
    }

    @Test
    fun `part 2`() {
        val count = countUniqueLocations(File("./inputs/day8.txt").readText(), resonantLocations)
        assertEquals(949, count)
    }
}