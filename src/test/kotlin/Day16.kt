package org.ricall.day16

import org.junit.jupiter.api.Test
import java.io.File
import java.lang.Integer.MAX_VALUE
import java.util.PriorityQueue
import kotlin.test.assertEquals

private val TEST_DATA = """
    |###############
    |#.......#....E#
    |#.#.###.#.###.#
    |#.....#.#...#.#
    |#.###.#####.#.#
    |#.#.#.......#.#
    |#.#.#####.###.#
    |#...........#.#
    |###.#.#####.#.#
    |#...#.....#.#.#
    |#.#.#.###.#.#.#
    |#.....#...#.#.#
    |#.###.#.#.#.#.#
    |#S..#.....#...#
    |###############""".trimMargin()

private enum class Direction(val delta: Point) {
    NORTH(Point(0, -1)),
    EAST(Point(1, 0)),
    SOUTH(Point(0, 1)),
    WEST(Point(-1, 0));

    fun turnLeft() = when (this) {
        NORTH -> WEST
        WEST -> SOUTH
        SOUTH -> EAST
        EAST -> NORTH
    }

    fun turnRight() = when (this) {
        NORTH -> EAST
        EAST -> SOUTH
        SOUTH -> WEST
        WEST -> NORTH
    }
}
private data class Position(val point: Point, val direction: Direction)
private data class WeightedPosition(val position: Position, val cost: Int, val path: List<Point>)
private data class Point(val x: Int, val y: Int) {
    operator fun plus(direction: Direction) = Point(x + direction.delta.x, y + direction.delta.y)
}
private data class Result(val cost: Int, val bestPath: List<Point>, val bestPathPoints: Set<Point>)

private class Maze(input: String) {
    private val maze: Set<Point>
    private val start: Point
    private val end: Point

    fun solve(): Result {
        val visited = mutableSetOf<Position>()
        val unvisited = PriorityQueue<WeightedPosition>(compareBy { it.cost })

        val bestPaths = mutableSetOf<Point>()
        var bestPath = emptyList<Point>()
        var bestPathCost = MAX_VALUE

        unvisited.add(WeightedPosition(Position(start, Direction.EAST), 0, emptyList()))
        while (unvisited.isNotEmpty()) {
            val (state, cost, previous) = unvisited.remove()
            visited += state

            if (state.point == end) {
                val path = previous + end
                bestPaths += path

                if (cost < bestPathCost) {
                    bestPath = path
                    bestPathCost = cost
                }
            }

            unvisited += sequenceOf(
                WeightedPosition(Position(state.point + state.direction, state.direction), cost + 1, previous + state.point),
                WeightedPosition(Position(state.point, state.direction.turnLeft()), cost + 1000, previous),
                WeightedPosition(Position(state.point, state.direction.turnRight()), cost + 1000, previous),
            ).filter { it.position !in visited && !isWall(it.position.point) }
        }
        return Result(bestPathCost, bestPath, bestPaths)
    }

    private fun isWall(point: Point) = maze.contains(point)

    init {
        var startPoint: Point? = null
        var endPoint: Point? = null
        maze = buildSet {
            input.lines().mapIndexed { y, line ->
                line.toCharArray().mapIndexed { x, c ->
                    when (c) {
                        'S' -> startPoint = Point(x, y)
                        'E' -> endPoint = Point(x, y)
                        '#' -> add(Point(x, y))
                        else -> {}
                    }
                }
            }
        }
        start = startPoint!!
        end = endPoint!!
    }
}

class Day16 {
    @Test
    fun `part 1 test data`() {
        val result = Maze(TEST_DATA).solve()

        assertEquals(7036, result.cost)
    }

    @Test
    fun `part 1`() {
        val result = Maze(File("./inputs/day16.txt").readText()).solve()

        assertEquals(99448, result.cost)
    }

    @Test
    fun `part 2 test data`() {
        val result = Maze(TEST_DATA).solve()

        assertEquals(7036, result.cost)
        assertEquals(45, result.bestPathPoints.size)
    }

    @Test
    fun `part 2`() {
        val result = Maze(File("./inputs/day16.txt").readText()).solve()

        assertEquals(99448, result.cost)
        assertEquals(498, result.bestPathPoints.size)
    }
}