package org.ricall.day02

import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.math.abs
import kotlin.test.assertEquals

private fun parseInput(input: InputStream) = BufferedReader(InputStreamReader(input))
    .readLines()
    .filter { it.isNotBlank() }
    .map { it.split("\\s+".toRegex()).map { it.toInt() } }

private fun predicateForLevel(predicate: (Int, Int) -> Boolean) =
    { level: List<Int> -> level.zipWithNext().all { (f, s) -> predicate(f, s) } }

private val areLevelsIncreasing = predicateForLevel { f, s -> f < s }
private val areLevelsDecreasing = predicateForLevel { f, s -> f > s }
private val areStepsWithin3 = predicateForLevel { f, s -> abs(f - s) <= 3 }

private fun isTheLevelSafe(level: List<Int>) =
    (areLevelsIncreasing(level) || areLevelsDecreasing(level)) && areStepsWithin3(level)

private fun isTheLevelSafeWithProblemDampener(level: List<Int>) =
    isTheLevelSafe(level) || level.indices.any { idx -> isTheLevelSafe(level.filterIndexed { i, _ -> i != idx }) }

private val TEST_DATA = """
    |7 6 4 2 1
    |1 2 7 8 9
    |9 7 6 2 1
    |1 3 2 4 5
    |8 6 4 4 1
    |1 3 6 7 9
    """.trimMargin()

class Day02 {
    @Test
    fun `part one test data`() {
        val safeCount = parseInput(TEST_DATA.byteInputStream()).count(::isTheLevelSafe)
        assertEquals(2, safeCount)
    }

    @Test
    fun `part one`() {
        val safeCount = parseInput(FileInputStream("./inputs/day2.txt")).count(::isTheLevelSafe)
        assertEquals(402, safeCount)
    }

    @Test
    fun `part two test data`() {
        val safeCount = parseInput(TEST_DATA.byteInputStream()).count(::isTheLevelSafeWithProblemDampener)
        assertEquals(4, safeCount)
    }

    @Test
    fun `part two`() {
        val safeCount = parseInput(FileInputStream("./inputs/day2.txt")).count(::isTheLevelSafeWithProblemDampener)
        assertEquals(455, safeCount)
    }
}