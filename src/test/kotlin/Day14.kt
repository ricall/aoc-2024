package org.ricall.day12

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private val TEST_DATA = """
    |p=0,4 v=3,-3
    |p=6,3 v=-1,-3
    |p=10,3 v=-1,2
    |p=2,0 v=2,-1
    |p=0,0 v=1,3
    |p=3,0 v=-2,-2
    |p=7,6 v=-1,-3
    |p=3,0 v=-1,-2
    |p=9,3 v=2,3
    |p=7,3 v=-1,2
    |p=2,4 v=2,-3
    |p=9,5 v=-3,-3""".trimMargin()

private data class Vec2(val x: Int, val y: Int)
private data class Robot(var p: Vec2, val v: Vec2)

private val PARSE_LINE = Regex("p=([0-9]+),([0-9]+) v=([-0-9]+),([-0-9]+)")

private class Room(val width: Int, val height: Int, input: String) {
    private val robots: List<Robot> = parseInput(input)

    fun advance(time: Int): Room {
        robots.forEach { it.p = Vec2((it.p.x + it.v.x * time).mod(width), (it.p.y + it.v.y * time).mod(height)) }
        return this
    }

    fun getSafetyFactor(): Int {
        val midX = width / 2
        val midY = height / 2
        return robots
            .filter { it.p.x != midX && it.p.y != midY }
            .groupBy { robot ->
                val k1 = when (robot.p.x) {
                    in (0..midX) -> "L"
                    in (midX..width) -> "R"
                    else -> "X"
                }
                val k2 = when (robot.p.y) {
                    in (0..midY) -> "U"
                    in (midY..height) -> "D"
                    else -> "X"
                }
                "$k1$k2"
            }
            .values.fold(1) { number, robots -> number * robots.size }
    }

    fun hasChristmasTree(): Boolean {
        robots.forEach { robot -> if (robots.count { it.p == robot.p } > 1) return false }
        return true
    }

    override fun toString() = buildString {
        (0..<height).forEach { y ->
            (0..<width).forEach { x ->
                val p = Vec2(x, y)
                val sum = robots.count { robot -> robot.p == p }
                append(if (sum == 0) '.' else sum)
            }
            append('\n')
        }
    }

    private fun parseInput(input: String): List<Robot> = input.lines().map { line ->
        PARSE_LINE.find(line)?.groupValues?.let { (_, px, py, vx, vy) ->
            Robot(Vec2(px.toInt(), py.toInt()), Vec2(vx.toInt(), vy.toInt()))
        } ?: throw IllegalArgumentException("Invalid input: $line")
    }
}

class Day14 {
    @Test
    fun `part 1 test data`() {
        val result = Room(11, 7, TEST_DATA)
            .advance(100)
            .getSafetyFactor()

        assertEquals(12, result)
    }

    @Test
    fun `part 1`() {
        val result = Room(101, 103, File("./inputs/day14.txt").readText())
            .advance(100)
            .getSafetyFactor()

        assertEquals(229868730, result)
    }

    @Test
    fun `part 2`() {
        val room = Room(101, 103, File("./inputs/day14.txt").readText())
        (1..1_000_000).forEach { index ->
            if (room.advance(1).hasChristmasTree()) {
                println("Found christmas tree in room after $index steps:")
                println(room)
                assertEquals(7861, index)
                return
            }
        }
        assertEquals(0, 1)
    }
}