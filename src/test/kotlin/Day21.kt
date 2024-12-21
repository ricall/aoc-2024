package org.ricall.day21

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private data class Point(val x: Int, val y: Int) {
    fun addDx(amount: Int) = Point(x + amount, y)
    fun addDy(amount: Int) = Point( x, y + amount)
}

private class Keypad(buttons: String, val pathLength: ((String) -> Long)? = null) {
    private val keypad = buildMap {
        buttons.chunked(3).forEachIndexed { y, line ->
            line.forEachIndexed { x, ch -> put(ch, Point(x, y)) }
        }
    }
    private val invalidButton = keypad['X']

    fun minimumButtonCountFor(text: String, depth: Int = 1): Long {
        if (depth == 0) {
            return text.length.toLong()
        }
        var current = keypad['A']!!
        var result = 0L
        for (ch in text) {
            val target = keypad[ch]!!
            result += minimumNumberOfButtonPresses(CacheableRequest(current, target, depth))
            current = target
        }
        return result
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
                end -> {
                    val buttonPresses = when(pathLength == null) {
                        true -> minimumButtonCountFor("${path}A", depth - 1)
                        false -> pathLength.invoke("${path}A")
                    }
                    result = Math.min(result, buttonPresses)
                }
                invalidButton -> {}
                else -> {
                    if (current.x < end.x) {
                        todo += current.addDx(1) to "$path>"
                    } else if (current.x > end.x) {
                        todo += current.addDx(-1) to "$path<"
                    }
                    if (current.y < end.y) {
                        todo += current.addDy(1) to "${path}v"
                    } else if (current.y > end.y) {
                        todo += current.addDy(-1) to "$path^"
                    }
                }
            }
        }
        result
    }
}

private fun minimumButtonCountFor(input: String, robotCount: Int = 2): Long {
    val directionalKeypad = Keypad("X^A<v>")
    val numericKeypad = Keypad("789456123X0A") { path -> directionalKeypad.minimumButtonCountFor(path, robotCount) }

    return input.lines().sumOf { code ->
        val buttonCount = numericKeypad.minimumButtonCountFor(code)
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
        val directionalKeypad = Keypad("X^A<v>")
        val numericKeypad = Keypad("789456123X0A", { path -> directionalKeypad.minimumButtonCountFor(path, 2)})

        val result = numericKeypad.minimumButtonCountFor("029A")

        assertEquals(68, result)
    }

    @Test
    fun `part 1 - test data`() {
        val result = minimumButtonCountFor(TEST_DATA)

        assertEquals(126384, result)
    }

    @Test
    fun `part 1`() {
        val result = minimumButtonCountFor(File("./inputs/day21.txt").readText())

        assertEquals(219366, result)
    }

    @Test
    fun `part 2 test data`() {
        val result = minimumButtonCountFor(TEST_DATA, 25)

        assertEquals(154115708116294, result)
    }

    @Test
    fun `part 2`() {
        val result = minimumButtonCountFor(File("./inputs/day21.txt").readText(), 25)

        assertEquals(271631192020464, result)
    }
}