package org.ricall.day20

import org.junit.jupiter.api.Test
import java.io.File
import java.util.*
import kotlin.test.assertEquals

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
    operator fun plus(point: Point) = Point(x + point.x, y + point.y)
    operator fun plus(direction: Direction) = Point(x + direction.delta.x, y + direction.delta.y)
}
private fun manhattanDistance(start: Point, end: Point) = Math.abs(start.x - end.x) + Math.abs(start.y - end.y)

private data class Maze(val input: String) {
    private val width: Int
    private val height: Int
    private var maze: Set<Point>
    private var start: Point
    private val end: Point

    fun solve(): List<Point> {
        val visited = mutableSetOf<Position>()
        val unvisited = PriorityQueue<WeightedPosition>(compareBy { it.cost })

        unvisited.add(WeightedPosition(Position(start, Direction.EAST), 0, emptyList()))
        while (unvisited.isNotEmpty()) {
            val (state, cost, previous) = unvisited.remove()
            visited += state

            if (state.point == end) {
                val path = previous + end
                return path
            }

            unvisited += sequenceOf(
                WeightedPosition(
                    Position(state.point + state.direction, state.direction),
                    cost + 1,
                    previous + state.point
                ),
                WeightedPosition(Position(state.point, state.direction.turnLeft()), cost + 1000, previous),
                WeightedPosition(Position(state.point, state.direction.turnRight()), cost + 1000, previous),
            ).filter { it.position !in visited && !isWall(it.position.point) }
        }
        throw Exception("No path found")
    }

    private fun isWall(point: Point) = maze.contains(point)

    fun findCheats(path: List<Point>, cheatDuration: Int) = buildMap<Int, Int> {
        val addSavingCount = { saving: Int -> put(saving, getOrDefault(saving, 0) + 1) }
        path.forEachIndexed { step, current ->
            path.mapIndexedNotNull { stepIndex, point ->
                    val stepDistance = manhattanDistance(current, point)
                    if (stepDistance <= cheatDuration) stepIndex to stepDistance else null
                }
                .filter { it.first > step }
                .forEach { (stepIndex, stepDistance) ->
                    addSavingCount(path.size - (step + stepDistance + path.size - stepIndex))
                }
        }
    }

    init {
        var startPoint: Point? = null
        var endPoint: Point? = null
        maze = buildSet {
            val lines = input.lines()
            width = lines[0].length
            height = lines.size
            lines.mapIndexed { y, line ->
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

private fun solve(input: String, cheatDuration: Int) = Maze(input).run { findCheats(solve(), cheatDuration) }

class Day20 {
    private val TEST_DATA = """
        |###############
        |#...#...#.....#
        |#.#.#.#.#.###.#
        |#S#...#.#.#...#
        |#######.#.#.###
        |#######.#.#...#
        |#######.#.###.#
        |###..E#...#...#
        |###.#######.###
        |#...###...#...#
        |#.#####.#.###.#
        |#.#...#.#.#...#
        |#.#.#.#.#.#.###
        |#...#...#...###
        |###############""".trimMargin()

    @Test
    fun `part 1 test data`() {
        val cheats = solve(TEST_DATA, 2)
//        cheats.entries
//            .sortedBy { it.key }
//            .forEach { println("There are ${it.value} cheats to save ${it.key} picoseconds") }
        val result = cheats.entries.filter { it.key >= 40 }.sumOf { it.value }

        assertEquals(2, result)
    }

    @Test
    fun `part 1`() {
        val cheats = solve(File("./inputs/day20.txt").readText(), 2)
        val result = cheats.entries.filter { it.key >= 100 }.sumOf { it.value }

        assertEquals(1393, result)
    }

    @Test
    fun `part 2 test data`() {
        val cheats = solve(TEST_DATA, 20)
//        cheats.entries.sortedBy { it.key }
//            .filter { it.key >= 50 }
//            .forEach { println("There are ${it.value} cheats to save ${it.key} picoseconds") }
        val result = cheats.entries.filter { it.key >= 50 }.sumOf { it.value }

        assertEquals(285, result)
    }

    @Test
    fun `part 2`() {
        val cheats = solve(File("./inputs/day20.txt").readText(), 20)
        val result = cheats.entries.filter { it.key >= 100 }.sumOf { it.value }

        assertEquals(990096, result)
    }
}