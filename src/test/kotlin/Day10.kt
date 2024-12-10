package org.ricall.day10

import org.junit.jupiter.api.Test
import org.ricall.day10.Direction.*
import java.io.File
import kotlin.test.assertEquals

private val TEST_DATA = """
    |89010123
    |78121874
    |87430965
    |96549874
    |45678903
    |32019012
    |01329801
    |10456732""".trimMargin()

private data class Point(val x: Int, val y: Int) {
    operator fun plus(direction: Direction) = Point(x + direction.delta.x, y + direction.delta.y)
}

private enum class Direction(val delta: Point) {
    NORTH(Point(0, -1)),
    EAST(Point(1, 0)),
    SOUTH(Point(0, 1)),
    WEST(Point(-1, 0)),
}

private class Topography(input: String) {
    val map: List<List<Int>>
    val trailheads: List<Point>
    val width: Int
    val height: Int

    init {
        trailheads = mutableListOf<Point>()
        map = input.lines().mapIndexed { y, line ->
            line.mapIndexed { x, c ->
                val height = c.digitToInt()
                if (height == 0) {
                    trailheads.add(Point(x, y))
                }
                height
            }
        }
        width = map[0].size
        height = map.size
    }

    private fun isValid(p: Point) = p.x in 0..<width && p.y in 0..<height

    private fun uniqueEndpoints(start: Point): Set<Point> {
        val height = map[start.y][start.x]
        if (height == 9) {
            return setOf(start)
        }
        return sequenceOf(NORTH, SOUTH, EAST, WEST)
            .map { start + it }
            .filter(::isValid)
            .filter { map[it.y][it.x] == height + 1 }
            .flatMap(::uniqueEndpoints)
            .toSet()
    }
    fun countUniqueEndpoints(start: Point) = uniqueEndpoints(start).size

    fun countPaths(start: Point): Int {
        val height = map[start.y][start.x]
        if (height == 9) {
            return 1
        }
        return sequenceOf(NORTH, SOUTH, EAST, WEST)
            .map { start + it }
            .filter(::isValid)
            .filter { map[it.y][it.x] == height + 1 }
            .map(::countPaths)
            .sum()
    }

    fun sumTrailheadScores(algorithm: (Point) -> Int) = trailheads.map(algorithm).sum()
}

class Day10 {
    @Test
    fun `part 1 test data`() {
        val model = Topography(TEST_DATA)
        val result = model.sumTrailheadScores(model::countUniqueEndpoints)

        assertEquals(36, result)
    }

    @Test
    fun `part 1`() {
        val model = Topography(File("./inputs/day10.txt").readText())
        val result = model.sumTrailheadScores(model::countUniqueEndpoints)

        assertEquals(796, result)
    }

    @Test
    fun `part 2 test data`() {
        val model = Topography(TEST_DATA)
        val result = model.sumTrailheadScores(model::countPaths)

        assertEquals(81, result)
    }

    @Test
    fun `part 2`() {
        val model = Topography(File("./inputs/day10.txt").readText())
        val result = model.sumTrailheadScores(model::countPaths)

        assertEquals(1942, result)
    }
}