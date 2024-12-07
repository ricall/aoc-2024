package org.ricall.day07

import org.junit.jupiter.api.Test
import org.ricall.day07.Operator.*
import java.io.File
import kotlin.test.assertEquals

data class Calibration(val result: Long, val values: List<Long>) {
    fun apply(operator: Operator): Calibration {
        val (v1, v2) = values.slice(0..1)
        val newValues = mutableListOf(operator.apply(v1, v2))
        newValues.addAll(values.slice(2..<values.size))

        return Calibration(result = result, values = newValues)
    }
}

enum class Operator(val apply: (Long, Long) -> Long) {
    ADD({ x, y -> x + y }),
    MULTIPLY({ x, y -> x * y }),
    CONCAT({ x, y -> "${x}${y}".toLong()})
}

fun parseInput(text: String) = text.lines().filter(String::isNotBlank).map { line ->
    val (result, valueText) = line.split(':')
    val values = valueText.trim().split(' ').map { it.toLong() }

    Calibration(result = result.toLong(), values = values)
}

fun isValidCalibration(calibration: Calibration) : Boolean {
    if (calibration.values.size == 1) {
        return calibration.result == calibration.values[0]
    }

    return isValidCalibration(calibration.apply(ADD)) || isValidCalibration(calibration.apply(MULTIPLY))
}

fun isValidCalibrationWithAllOperators(calibration: Calibration) : Boolean {
    if (calibration.values.size == 1) {
        return calibration.result == calibration.values[0]
    }

    return isValidCalibrationWithAllOperators(calibration.apply(ADD))
            || isValidCalibrationWithAllOperators(calibration.apply(MULTIPLY))
            || isValidCalibrationWithAllOperators(calibration.apply(CONCAT))
}

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
        val result = parseInput(TEST_DATA)
            .filter(::isValidCalibration)
            .sumOf { it.result }

        assertEquals(3749, result)
    }

    @Test
    fun `part 1`() {
        val result = parseInput(File("./inputs/day7.txt").readText())
            .filter(::isValidCalibration)
            .sumOf { it.result }

        assertEquals(12940396350192, result)
    }

    @Test
    fun `part 2 test data`() {
        val result = parseInput(TEST_DATA)
            .filter(::isValidCalibrationWithAllOperators)
            .sumOf { it.result }

        assertEquals(11387, result)
    }

    @Test
    fun `part 2`() {
        val result = parseInput(File("./inputs/day7.txt").readText())
            .filter(::isValidCalibrationWithAllOperators)
            .sumOf { it.result }

        assertEquals(106016735664498, result)
    }
}