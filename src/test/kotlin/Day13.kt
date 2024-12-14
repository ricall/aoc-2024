package org.ricall.day13

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private val TEST_DATA = """
    |Button A: X+94, Y+34
    |Button B: X+22, Y+67
    |Prize: X=8400, Y=5400
    |
    |Button A: X+26, Y+66
    |Button B: X+67, Y+21
    |Prize: X=12748, Y=12176
    |
    |Button A: X+17, Y+86
    |Button B: X+84, Y+37
    |Prize: X=7870, Y=6450
    |
    |Button A: X+69, Y+23
    |Button B: X+27, Y+71
    |Prize: X=18641, Y=10279""".trimMargin()

private data class Vec2(val x: Long, val y: Long)
private data class Machine(val buttonA: Vec2, val buttonB: Vec2, val prize: Vec2)

private val BUTTON_REGEX = Regex("Button ([A-Z]+): X[+]([0-9]+), Y[+]([0-9]+)")
private val PRIZE_REGEX = Regex("Prize: X=([0-9]+), Y=([0-9]+)")
private val PART2_OFFSET = Vec2(10000000000000L, 10000000000000L)

private fun parseInput(input: String, offset: Vec2 = Vec2(0L, 0L)) = buildList {
    var buttons = mutableMapOf<String, Vec2>()
    val handleButton = { groups: List<String> -> buttons[groups[1]] = Vec2(groups[2].toLong(), groups[3].toLong()) }
    val handlePrize = { groups: List<String> ->
        add(Machine(buttons["A"]!!, buttons["B"]!!, Vec2(groups[1].toLong() + offset.x, groups[2].toLong() + offset.y) ))
        buttons = mutableMapOf()
    }
    input.lines().forEach { line ->
        listOf(BUTTON_REGEX to handleButton, PRIZE_REGEX to handlePrize).forEach { (regex, handler) ->
            regex.find(line)?.let { handler(it.groupValues) }
        }
    }
}

private fun findMinimumTokensPerMachine(machine: Machine): Long {
    val (buttonA, buttonB, prize) = machine

    val b = (prize.y * buttonA.x - prize.x * buttonA.y) / (buttonB.y * buttonA.x - buttonB.x * buttonA.y)
    val a = (prize.x - b * buttonB.x) / buttonA.x
    if (a * buttonA.x + b * buttonB.x == prize.x && a * buttonA.y + b * buttonB.y == prize.y) {
        return 3 * a + b
    }
    return 0
}

private fun findMinimumTokens(machines: List<Machine>) = machines.sumOf(::findMinimumTokensPerMachine)

class Day13 {
    @Test
    fun `part 1 test data`() {
        val machines = parseInput(TEST_DATA)
        val tokens = findMinimumTokens(machines)

        assertEquals(480, tokens)
    }

    @Test
    fun `part 1`() {
        val machines = parseInput(File("./inputs/day13.txt").readText())
        val tokens = findMinimumTokens(machines)

        assertEquals(32026, tokens)
    }

    @Test
    fun `part 2 test data`() {
        val machines = parseInput(TEST_DATA, PART2_OFFSET)
        val tokens = findMinimumTokens(machines)

        assertEquals(875318608908, tokens)
    }

    @Test
    fun `part 2`() {
        val machines = parseInput(File("./inputs/day13.txt").readText(), PART2_OFFSET)
        val tokens = findMinimumTokens(machines)

        assertEquals(89013607072065, tokens)
    }
}