package org.ricall.day06

import org.junit.jupiter.api.Test
import org.ricall.day06.Direction.*
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

    fun turnRight() = when (this) {
        NORTH -> EAST
        EAST -> SOUTH
        SOUTH -> WEST
        WEST -> NORTH
    }
}

private data class Guard(val position: Point, val direction: Direction)

private class Lab {
    private val obstructions: List<Point>
    private val guard: Guard
    private val width: Int
    private val height: Int

    constructor(input: String) {
        val obstructions = mutableListOf<Point>()
        var guard: Guard? = null
        val lines = input.lines()
        lines.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                when (c) {
                    '#' -> obstructions.add(Point(x, y))
                    '^' -> guard = Guard(Point(x, y), NORTH)
                    '>' -> guard = Guard(Point(x, y), EAST)
                    'v' -> guard = Guard(Point(x, y), SOUTH)
                    '<' -> guard = Guard(Point(x, y), WEST)
                }
            }
        }
        check(guard != null)
        this.obstructions = obstructions.toList()
        this.guard = guard!!
        this.width = lines[0].length
        this.height = lines.size
    }

    private constructor(obstructions: List<Point>, guard: Guard, width: Int, height: Int) {
        this.obstructions = obstructions
        this.guard = guard
        this.width = width
        this.height = height
    }

    private fun isInLab(guard: Guard) = guard.position.x in 0 until width && guard.position.y in 0 until height

    private fun advance(guard: Guard): Guard {
        val position = guard.position + guard.direction
        if (!obstructions.contains(position)) {
            return Guard(position, guard.direction)
        }

        var direction = guard.direction
        while (true) {
            direction = direction.turnRight()
            val newPosition = guard.position + direction

            if (!obstructions.contains(newPosition)) {
                return Guard(newPosition, direction)
            }
        }
    }

    private fun getAllVisitedLocations(): Set<Point> {
        val visited = mutableSetOf<Point>()

        var guard = this.guard
        while (isInLab(guard)) {
            visited.add(guard.position)
            guard = advance(guard)
        }
        return visited
    }

    fun countVisitedLocations() = getAllVisitedLocations().size

    private fun isGuardLooping(): Boolean {
        val actions = mutableSetOf<Guard>()
        var guard = this.guard
        while (isInLab(guard)) {
            if (actions.contains(guard)) {
                return true
            }
            actions.add(guard)
            guard = advance(guard)
        }
        return false
    }

    private fun addObstruction(position: Point) =
        Lab(obstructions = obstructions + position, guard = guard, width = width, height = height)

    fun countLoopsByAddingASingleObstacle() = getAllVisitedLocations()
        .parallelStream()
        .map { position -> addObstruction(position).isGuardLooping() }
        .filter { it == true }
        .count()
}

private val TEST_DATA = """
    |....#.....
    |.........#
    |..........
    |..#.......
    |.......#..
    |..........
    |.#..^.....
    |........#.
    |#.........
    |......#...""".trimMargin()


class Day06 {
    @Test
    fun `part 1 test data`() {
        val result = Lab(TEST_DATA).countVisitedLocations()
        assertEquals(41, result)
    }

    @Test
    fun `part 1`() {
        val result = Lab(File("./inputs/day6.txt").readText()).countVisitedLocations()
        assertEquals(5095, result)
    }

    @Test
    fun `part 2 test data`() {
        val result = Lab(TEST_DATA).countLoopsByAddingASingleObstacle()
        assertEquals(6, result)
    }

    @Test
    fun `part 2`() {
        val result = Lab(File("./inputs/day6.txt").readText()).countLoopsByAddingASingleObstacle()
        assertEquals(1933, result)
    }
}