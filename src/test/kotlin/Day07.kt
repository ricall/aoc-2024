package org.ricall.day07

import org.junit.jupiter.api.Test
import org.ricall.day07.Operator.*
import java.io.File
import kotlin.test.assertEquals

data class Calibration(val target: Long, val values: List<Long>)

enum class Operator(val evaluate: (Long, Long) -> Long) {
    ADD({ x, y -> x + y }),
    MULTIPLY({ x, y -> x * y }),
    CONCAT({ x, y ->
        var part = y
        var multiplier = 1
        while (part > 0) {
            part /= 10
            multiplier *= 10
        }
        x * multiplier + y
    })
}

fun parseInput(text: String) = text.lines().filter(String::isNotBlank).map { line ->
    val (target, valueText) = line.split(':')
    val values = valueText.trim().split(' ').map { it.toLong() }

    Calibration(target = target.toLong(), values = values)
}

fun checkCalibrationByRecursion(operators: List<Operator>, target: Long, current: Long, values: List<Long>): Boolean {
    if (values.isEmpty()) {
        return target == current
    }
    if (current > target) {
        return false
    }

    return operators.any { operator ->
        val newValues = values.toMutableList()
        val value = newValues.removeFirst()

        checkCalibrationByRecursion(operators, target, operator.evaluate(current, value), newValues)
    }
}

fun checkCalibration(operators: List<Operator>, calibration: Calibration): Boolean {
    val values = calibration.values.toMutableList()
    val current = values.removeFirst()

    return checkCalibrationByRecursion(operators, calibration.target, current, values)
}

fun sumOfValidCalibrationPartOne(text: String) = parseInput(text)
    .filter { checkCalibration(listOf(ADD, MULTIPLY), it) }
    .sumOf { it.target }

fun sumOfValidCalibrationPartTwo(text: String) = parseInput(text)
    .filter { checkCalibration(listOf(ADD, MULTIPLY, CONCAT), it) }
    .sumOf { it.target }

val TEST_DATA = """
    |190: 10 19
    |3267: 81 40 27
    |83: 17 5
    |156: 15 6
    |7290: 6 8 6 15
    |161011: 16 10 13
    |192: 17 8 14
    |21037: 9 7 18 13
    |292: 11 6 16 20""".trimMargin()

class Day07 {
    @Test
    fun `part 1 test data`() {
        val result = sumOfValidCalibrationPartOne(TEST_DATA)

        assertEquals(3749, result)
    }

    @Test
    fun `part 1`() {
        val result = sumOfValidCalibrationPartOne(File("./inputs/day7.txt").readText())

        assertEquals(12940396350192, result)
    }

    @Test
    fun `part 2 test data`() {
        val result = sumOfValidCalibrationPartTwo(TEST_DATA)

        assertEquals(11387, result)
    }

    @Test
    fun `part 2`() {
        val result = sumOfValidCalibrationPartTwo(File("./inputs/day7.txt").readText())

        assertEquals(106016735664498, result)
    }
}