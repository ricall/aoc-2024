package org.ricall.day12

import org.junit.jupiter.api.Test
import org.ricall.day12.Day12.Direction.*
import java.io.File
import kotlin.test.assertEquals

class Day12 {
    private val TEST_DATA = """
        |RRRRIICCFF
        |RRRRIICCCF
        |VVRRRCCFFF
        |VVRCCCJFFF
        |VVVVCJJCFE
        |VVIVCCJJEE
        |VVIIICJJEE
        |MIIIIIJJEE
        |MIIISIJEEE
        |MMMISSJEEE""".trimMargin()

    data class Point(val x: Int, val y: Int) {
        operator fun plus(direction: Direction) = Point(x + direction.delta.x, y + direction.delta.y)
    }

    enum class Direction(val delta: Point) {
        NORTH(Point(0, -1)),
        EAST(Point(1, 0)),
        SOUTH(Point(0, 1)),
        WEST(Point(-1, 0));

        operator fun plus(direction: Direction) = Point(this.delta.x + direction.delta.x, this.delta.y + direction.delta.y)

        fun rotateRight() = when(this) {
            NORTH -> WEST
            EAST -> NORTH
            SOUTH -> EAST
            WEST -> SOUTH
        }
    }

    class Garden(input: String) {
        private val points = parseInput(input)

        private fun getRegion(point: Point, visited: MutableSet<Point>): Set<Point> = buildSet {
            val ch = points[point]
            visited.add(point)
            add(point)
            addAll(listOf(NORTH, SOUTH, WEST, EAST)
                .map { point + it }
                .filter { points[it] == ch }
                .filter { !visited.contains(it) }
                .flatMap { point -> getRegion(point, visited) })
        }

        private fun getRegions() = sequence {
            val remaining = points.keys.toMutableSet()
            val visited = mutableSetOf<Point>()

            while (remaining.isNotEmpty()) {
                val point = remaining.first()
                val region = getRegion(point, visited)
                yield(region)
                remaining.removeAll(region)
            }
        }

        private fun calculatePerimeter(region: Collection<Point>) = region.sumOf { point ->
            listOf(NORTH, SOUTH, WEST, EAST)
                .map { direction -> point + direction }
                .filter { p -> !region.contains(p) }
                .size
        }

        private fun countCorners(point: Point, direction: Direction): Int {
            val ch = points[point]
            var corners = 0

            val right = direction.rotateRight()
            if (points[point + direction] != ch && points[point + right] != ch) {
                // Counts an edge surrounding this point (direction & right)
                corners++
            }

            if (points[point + direction + right] != ch && points[point + direction] == ch && points[point + right] == ch) {
                // Counts an edge between point -> diagonal (direction + right)
                corners++
            }
            return corners
        }

        private fun calculateEdges(region: Collection<Point>) = region.sumOf { point ->
            listOf(NORTH, EAST, SOUTH, WEST).sumOf { direction -> countCorners(point, direction) }
        }

        fun calculatePricePartOne() = getRegions().sumOf { region ->
            val area = region.size
            val perimeter = calculatePerimeter(region)

            area * perimeter
        }

        fun calculatePricePartTwo() = getRegions().sumOf { region ->
            val area = region.size
            val edges = calculateEdges(region)

            area * edges
        }

        private fun parseInput(input: String): Map<Point, Char> = buildMap {
            input.lines().mapIndexed { y, line ->
                line.mapIndexed { x, ch ->
                    put(Point(x, y), ch)
                }
            }
        }
    }

    @Test
    fun `part 1 test data`() {
        val price = Garden(TEST_DATA).calculatePricePartOne()
        assertEquals(1930, price)
    }

    @Test
    fun `part 1`() {
        val price = Garden(File("./inputs/day12.txt").readText()).calculatePricePartOne()
        assertEquals(1370100, price)
    }

    @Test
    fun `part 2 test data`() {
        val price = Garden(TEST_DATA).calculatePricePartTwo()
        assertEquals(1206, price)
    }

    @Test
    fun `part 2`() {
        val price = Garden(File("./inputs/day12.txt").readText()).calculatePricePartTwo()
        assertEquals(818286, price)
    }
}