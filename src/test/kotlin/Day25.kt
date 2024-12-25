package org.ricall.day25

import org.junit.jupiter.api.Test
import org.ricall.day25.ShapeType.KEY
import org.ricall.day25.ShapeType.LOCK
import java.io.File
import kotlin.test.assertEquals

typealias Heights = List<Int>

private enum class ShapeType { LOCK, KEY }
private fun parseShape(input: String): Pair<ShapeType, Heights> {
    val lines = input.lines()
    val type = when (lines[0][0]) {
        '#' -> LOCK
        '.' -> KEY
        else -> error("Invalid shape")
    }
    val heights = lines[0].indices.map { x ->
        val height = lines.indices
            .joinToString("") { y -> if (lines[y][x] == '.') " " else "#" }
            .trim()
            .length

        if (type == KEY) lines.size - height - 1 else height - 1
    }
    return type to heights
}

private fun parseInput(input: String): Pair<List<Heights>, List<Heights>> {
    val shapes = input.split("\n\n").map(::parseShape)
    val locks = shapes.filter { (type, _) -> type == LOCK }.map { (_, heights) -> heights }
    val keys =  shapes.filter { (type, _) -> type == KEY }.map { (_, heights) -> heights }
    return locks to keys
}

private fun solvePartOne(input: String): Int {
    val (locks, keys) = parseInput(input)

    return locks.map { lock -> keys.count { key -> key.indices.all { key[it] >= lock[it] }} }.sum()
}

class Day25 {
    private val TEST_DATA = """
        |#####
        |.####
        |.####
        |.####
        |.#.#.
        |.#...
        |.....
        |
        |#####
        |##.##
        |.#.##
        |...##
        |...#.
        |...#.
        |.....
        |
        |.....
        |#....
        |#....
        |#...#
        |#.#.#
        |#.###
        |#####
        |
        |.....
        |.....
        |#.#..
        |###..
        |###.#
        |###.#
        |#####
        |
        |.....
        |.....
        |.....
        |#....
        |#.#..
        |#.#.#
        |#####
    """.trimMargin()

    @Test
    fun `part 1 test data`() {
        val result = solvePartOne(TEST_DATA)

        assertEquals(3, result)
    }

    @Test
    fun `part 1`() {
        val result = solvePartOne(File("./inputs/day25.txt").readText())

        assertEquals(3483, result)
    }
}