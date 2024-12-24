package org.ricall.day03

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private val MUL_REGEX = "mul[(](\\d{1,3}),(\\d{1,3})[)]".toRegex()
private val DO_DONT_REGEX = "do(n[']t){0,1}[(][)]".toRegex()

private fun evaluate(text: String) = MUL_REGEX.findAll(text).toList().fold(0) { acc, match ->
    val v1 = match.groupValues[1].toInt()
    val v2 = match.groupValues[2].toInt()

    acc + v1 * v2
}

private data class Statement(val index: Int, val statement: String)

private fun statementsFrom(text: String) = listOf(DO_DONT_REGEX, MUL_REGEX)
    .flatMap { it.findAll(text).map { match -> Statement(match.range.first, match.value) } }
    .sortedBy(Statement::index)
    .map(Statement::statement)

private fun evaluateWithConditionalStatements(text: String): Int {
    var enabled = true
    return statementsFrom(text).fold(0) { total, command ->
        when (command) {
            "do()" -> enabled = true
            "don't()" -> enabled = false
            else -> if (enabled) {
                return@fold total + evaluate(command)
            }
        }
        return@fold total
    }
}

private val TEST_DATA = "xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))"

class Day03 {
    @Test
    fun `part 1 test data`() {
        val result = evaluate(TEST_DATA)
        assertEquals(161, result)
    }

    @Test
    fun `part 1`() {
        val result = evaluate(File("./inputs/day3.txt").readText())
        assertEquals(187825547, result)
    }

    @Test
    fun `part 2 test data`() {
        val result = evaluateWithConditionalStatements(TEST_DATA)
        assertEquals(48, result)
    }

    @Test
    fun `part 2`() {
        val result = evaluateWithConditionalStatements(File("./inputs/day3.txt").readText())
        assertEquals(85508223, result)
    }
}