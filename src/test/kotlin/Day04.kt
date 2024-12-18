package org.ricall.day04

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import org.ricall.day04.Direction.*

private data class Point(val y: Int, val x: Int) {
    operator fun plus(other: Point) = Point(y + other.y, x + other.x)
    operator fun plus(direction: Direction) = Point(y + direction.vector.y, x + direction.vector.x)
}

private enum class Direction(val vector: Point) {
    NW(Point(-1, -1)),
    N(Point(-1, 0)),
    NE(Point(-1, 1)),
    E(Point(0, 1)),
    SE(Point(1, 1)),
    S(Point(1, 0)),
    SW(Point(1, -1)),
    W(Point(0, -1)),
}

private class Day04Search(text: String) {

    // ---------------- PART ONE

    private fun getXMASCount(position: Point) = Direction.entries
        .map { direction -> textAt(position, direction, 4)}
        .count { text -> text == "XMAS" }

    private fun countXMASAt(position: Point) = when(charAt(position)) {
        'X' -> getXMASCount(position)
        else -> 0
    }

    fun countXMAS() = totalFor(::countXMASAt)

    // ----------------- PART TWO

    private fun isMASCross(position: Point): Boolean {
        return listOf(
            textAt(position + NW, SE, 3),
            textAt(position + SW, NE, 3),
        ).all { word -> word == "MAS" || word == "SAM" }
    }

    private fun getMASCrossCountAt(position: Point) = when (isMASCross(position)) {
        true -> 1
        else -> 0
    }

    private fun smartCountMASCrossAt(position: Point) = when (charAt(position)) {
        'A' -> getMASCrossCountAt(position)
        else -> 0
    }

    fun countMASCrosses() = totalFor(::smartCountMASCrossAt)

    // ----------------- HELPERS

    private val lines = text.lines()

    private fun charAt(position: Point): Char {
        val (y, x) = position
        if (y < 0 || y >= lines.size || x < 0 || x >= lines[y].length) {
            return ' '
        }
        return lines[y][x]
    }

    private fun textAt(position: Point, direction: Direction, length: Int): String {
        var currentPoint = position

        return (0..<length).map {
            val ch = charAt(currentPoint)
            currentPoint += direction
            ch
        }.joinToString(separator = "")
    }

    private inline fun totalFor(totalAt: (Point) -> Int): Int {
        var total = 0
        for (y in lines.indices) {
            for (x in lines[y].indices) {
                total += totalAt(Point(y, x))
            }
        }
        return total
    }
}

class Day04 {
    private val TEST_DATA = """
    |MMMSXXMASM
    |MSAMXMSMSA
    |AMXSXMAAMM
    |MSAMASMSMX
    |XMASAMXAMM
    |XXAMMXXAMA
    |SMSMSASXSS
    |SAXAMASAAA
    |MAMMMXMMMM
    |MXMXAXMASX""".trimMargin()

    @Test
    fun `part 1 test data`() {
        val result = Day04Search(TEST_DATA).countXMAS()

        assertEquals(18, result)
    }

    @Test
    fun `part 1`() {
        val result = Day04Search(File("./inputs/day4.txt").readText()).countXMAS()

        assertEquals(2549, result)
    }

    @Test
    fun `part 2 test data`() {
        val result = Day04Search(TEST_DATA).countMASCrosses()

        assertEquals(9, result)
    }

    @Test
    fun `part 2`() {
        val result = Day04Search(File("./inputs/day4.txt").readText()).countMASCrosses()

        assertEquals(2003, result)
    }
}