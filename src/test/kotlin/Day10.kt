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

private enum class Direction(val delta: Point) {
    NORTH(Point(0, -1)),
    EAST(Point(1, 0)),
    SOUTH(Point(0, 1)),
    WEST(Point(-1, 0)),
}

private data class Point(val x: Int, val y: Int) {
    operator fun plus(direction: Direction) = Point(x + direction.delta.x, y + direction.delta.y)
}

private class Topography(input: String) {
    val map: List<List<Int>>
    val trailheads: List<Point>
    val width: Int
    val height: Int

    init {
        trailheads = mutableListOf()
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

    private fun heightAt(p: Point) = when {
        p.x in 0..<width && p.y in 0..<height -> map[p.y][p.x]
        else -> Integer.MAX_VALUE
    }

    private fun trailEndpoints(point: Point): List<Point> {
        val height = heightAt(point)
        if (height == 9) {
            return listOf(point)
        }
        return sequenceOf(NORTH, SOUTH, EAST, WEST)
            .map { direction -> point + direction }
            .filter { heightAt(it) == height + 1 }
            .flatMap(::trailEndpoints)
            .toList()
    }

    fun sumTrailheadScores(): Int = trailheads.map(::trailEndpoints).sumOf { it.distinct().size }

    fun sumTrailheadRatings(): Int = trailheads.map(::trailEndpoints).sumOf { it.size }
}

class Day10 {
    @Test
    fun `part 1 test data`() {
        val result = Topography(TEST_DATA).sumTrailheadScores()

        assertEquals(36, result)
    }

    @Test
    fun `part 1`() {
        val result = Topography(File("./inputs/day10.txt").readText()).sumTrailheadScores()

        assertEquals(796, result)
    }

    @Test
    fun `part 2 test data`() {
        val result = Topography(TEST_DATA).sumTrailheadRatings()

        assertEquals(81, result)
    }

    @Test
    fun `part 2`() {
        val result = Topography(File("./inputs/day10.txt").readText()).sumTrailheadRatings()

        assertEquals(1942, result)
    }
}