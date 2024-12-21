package org.ricall.day21

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.min
import kotlin.test.assertEquals

private data class Point(val x: Int, val y: Int) {
    fun addDx(amount: Int) = Point(x + amount, y)
    fun addDy(amount: Int) = Point(x, y + amount)
}

private class Keypad(buttons: String) {
    private val keypad = buildMap {
        buttons.chunked(3).forEachIndexed { y, line ->
            line.forEachIndexed { x, ch -> put(ch, Point(x, y)) }
        }
    }
    private val invalidButton = keypad['X']

    private var calculateMinimumButtonPresses: (String, Int) -> Long = this::minimumButtonCountFor
    fun withMinimumButtonLengthCalculator(calculator: (String, Int) -> Long): Keypad {
        this.calculateMinimumButtonPresses = calculator
        return this
    }

    fun minimumButtonCountFor(text: String, depth: Int = 1): Long = when(depth) {
        0 -> text.length.toLong()
        else ->text.fold(keypad['A']!! to 0L) { (current, total), ch ->
            val next = keypad[ch]!!
            next to total + minimumNumberOfButtonPresses(CacheableRequest(current, next, depth))
        }.second
    }

    private data class CacheableRequest(val start: Point, val end: Point, val depth: Int)
    private val cache = mutableMapOf<CacheableRequest, Long>()
    private fun minimumNumberOfButtonPresses(request: CacheableRequest) = cache.getOrPut(request) {
        val (start, end, depth) = request
        var result = Long.MAX_VALUE

        val todo = mutableListOf(start to "")
        while (todo.isNotEmpty()) {
            val (current, path) = todo.removeFirst()
            when (current) {
                end -> result = min(result, calculateMinimumButtonPresses("${path}A", depth - 1))
                invalidButton -> { /* IGNORE */ }
                else -> {
                    when {
                        current.x < end.x -> todo += current.addDx(1) to "$path>"
                        current.x > end.x -> todo += current.addDx(-1) to "$path<"
                    }
                    when {
                        current.y < end.y -> todo += current.addDy(1) to "${path}v"
                        current.y > end.y -> todo += current.addDy(-1) to "$path^"
                    }
                }
            }
        }
        result
    }
}

private fun createKeypad() = Keypad("789456123X0A")
    .withMinimumButtonLengthCalculator(Keypad("X^A<v>")::minimumButtonCountFor)

private fun scoreInputs(input: String, robotCount: Int = 3): Long {
    val keypad = createKeypad()
    return input.lines().sumOf { code ->
        val buttonCount = keypad.minimumButtonCountFor(code, robotCount)
        val codeAsNumber = code.substring(0, code.length - 1).toInt()

        codeAsNumber * buttonCount
    }
}

class Day21 {
    private val TEST_DATA = """
        |029A
        |980A
        |179A
        |456A
        |379A
    """.trimMargin()

    @Test
    fun `part 1 - single`() {
        val result = createKeypad().minimumButtonCountFor("029A", 3)

        assertEquals(68, result)
    }

    @Test
    fun `part 1 - test data`() {
        val result = scoreInputs(TEST_DATA)

        assertEquals(126384, result)
    }

    @Test
    fun `part 1`() {
        val result = scoreInputs(File("./inputs/day21.txt").readText())

        assertEquals(219366, result)
    }

    @Test
    fun `part 2 test data`() {
        val result = scoreInputs(TEST_DATA, 26)

        assertEquals(154115708116294, result)
    }

    @Test
    fun `part 2`() {
        val result = scoreInputs(File("./inputs/day21.txt").readText(), 26)

        assertEquals(271631192020464, result)
    }
}