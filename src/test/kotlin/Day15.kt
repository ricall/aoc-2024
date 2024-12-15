package org.ricall.day15

import org.junit.jupiter.api.Test
import org.ricall.day15.Direction.*
import org.ricall.day15.Object.*
import org.ricall.day15.Warehouse.ParseMode.*
import java.io.File
import kotlin.test.assertEquals

private val SMALL_TEST_DATA = """
    |########
    |#..O.O.#
    |##@.O..#
    |#...O..#
    |#.#.O..#
    |#...O..#
    |#......#
    |########
    |
    |<^^>>>vv<v>>v<<""".trimMargin()

private val SMALL_TEST_DATA2 = """
    |#######
    |#...#.#
    |#.....#
    |#..OO@#
    |#..O..#
    |#.....#
    |#######
    |
    |<vv<<^^<<^^""".trimMargin()

private val TEST_DATA = """
    |##########
    |#..O..O.O#
    |#......O.#
    |#.OO..O.O#
    |#..O@..O.#
    |#O#..O...#
    |#O..O..O.#
    |#.OO.O.OO#
    |#....O...#
    |##########
    |
    |<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
    |vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
    |><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
    |<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
    |^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
    |^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
    |>^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
    |<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
    |^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
    |v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^""".trimMargin()

private data class Vec2(val x: Int, val y: Int) {
    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
}

private enum class Object {
    WALL,
    EMPTY,
    ROBOT,
    BOX,
    BOX_LEFT,
    BOX_RIGHT,
}

private enum class Direction(val delta: Vec2) {
    UP(Vec2(0, -1)),
    DOWN(Vec2(0, 1)),
    LEFT(Vec2(-1, 0)),
    RIGHT(Vec2(1, 0));
}

private class Warehouse(input: String, parseMode: ParseMode = NORMAL) {
    enum class ParseMode { NORMAL, DOUBLE_WIDTH }
    private val map = mutableListOf<MutableList<Object>>()
    private var robot: Vec2
    private val moves = mutableListOf<Direction>()

    fun moveRobot(): Warehouse {
        for (move in moves) {
            if (processMove(listOf(robot), move)) {
                robot += move.delta
            }
        }
        return this
    }

    private fun processMove(points: List<Vec2>, move: Direction): Boolean {
        val movePoints = {
            points.forEach { point ->
                val (x, y) = point + move.delta
                map[y][x] = map[point.y][point.x]
                map[point.y][point.x] = EMPTY
            }
            true
        }

        val adjacent = buildSet<Vec2> {
            points.forEach { point ->
                val newPoint = point + move.delta
                val value = map[newPoint.y][newPoint.x]
                add(newPoint)
                if (move == UP || move == DOWN) {
                    if (value == BOX_LEFT) {
                        add(newPoint + RIGHT.delta)
                    }
                    if (value == BOX_RIGHT) {
                        add(newPoint + LEFT.delta)
                    }
                }
            }
        }
        if (adjacent.all { map[it.y][it.x] == EMPTY }) {
            return movePoints()
        }
        if (adjacent.any { map[it.y][it.x] == WALL }) {
            return false
        }

        val nonEmptyAdjacent = adjacent.filter { map[it.y][it.x] != EMPTY }
        if (processMove(nonEmptyAdjacent, move)) {
            return movePoints()
        }
        return false
    }

    fun sumBoxLocations() = map.mapIndexed { y, line ->
        line.mapIndexed { x, ch ->
            when (ch) {
                BOX_LEFT, BOX -> x + 100 * y
                else -> 0
            }
        }.sum()
    }.sum()

    override fun toString() = buildString {
        for (line in map) {
            for (ch in line) {
                append(
                    when (ch) {
                        EMPTY -> '.'
                        WALL -> '#'
                        BOX -> 'O'
                        BOX_LEFT -> '['
                        BOX_RIGHT -> ']'
                        ROBOT -> '@'
                    }
                )
            }
            append('\n')
        }
    }

    init {
        val (gridText, movesText) = input.split("\n\n")
        var robotLocation: Vec2? = null

        gridText.lines().forEachIndexed { y, line ->
            map.add(buildList {
                line.forEachIndexed { x, ch ->
                    when (ch) {
                        '#' -> {
                            add(WALL)
                            if (parseMode == DOUBLE_WIDTH) {
                                add(WALL)
                            }
                        }

                        'O' -> {
                            if (parseMode == NORMAL) {
                                add(BOX)
                            } else {
                                add(BOX_LEFT)
                                add(BOX_RIGHT)
                            }
                        }

                        '@' -> {
                            add(ROBOT)
                            if (parseMode == NORMAL) {
                                robotLocation = Vec2(x, y)
                            } else {
                                robotLocation = Vec2(x * 2, y)
                                add(EMPTY)
                            }
                        }

                        else -> {
                            add(EMPTY)
                            if (parseMode == DOUBLE_WIDTH) {
                                add(EMPTY)
                            }
                        }
                    }
                }
            }.toMutableList())
        }

        movesText.lines().forEach { line ->
            moves.addAll(line.map { ch ->
                when (ch) {
                    '<' -> LEFT
                    '^' -> UP
                    'v' -> DOWN
                    '>' -> RIGHT
                    else -> throw IllegalArgumentException("Unknown direction $ch")
                }
            })
        }
        robot = robotLocation ?: throw IllegalArgumentException("No robot found")
    }

}

class Day15 {
    @Test
    fun `part 1 small test data`() {
        val map = Warehouse(SMALL_TEST_DATA)
            .moveRobot()
        val result = map.sumBoxLocations()

        println(map)
        assertEquals(2028, result)
    }

    @Test
    fun `part 1 test data`() {
        val result = Warehouse(TEST_DATA)
            .moveRobot()
            .sumBoxLocations()
        assertEquals(10092, result)
    }

    @Test
    fun `part 1`() {
        val result = Warehouse(File("./inputs/day15.txt").readText())
            .moveRobot()
            .sumBoxLocations()
        assertEquals(1479679, result)
    }

    @Test
    fun `part 2 small test data`() {
        val result = Warehouse(SMALL_TEST_DATA2, DOUBLE_WIDTH)
            .moveRobot()
            .sumBoxLocations()
        assertEquals(618, result)
    }

    @Test
    fun `part 2 test data`() {
        val result = Warehouse(TEST_DATA, DOUBLE_WIDTH)
            .moveRobot()
            .sumBoxLocations()
        assertEquals(9021, result)
    }

    @Test
    fun `part 2`() {
        val result = Warehouse(File("./inputs/day15.txt").readText(), DOUBLE_WIDTH)
            .moveRobot()
            .sumBoxLocations()
        assertEquals(1509780, result)
    }
}