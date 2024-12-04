import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals

private fun parseInput(input: InputStream) = BufferedReader(InputStreamReader(input))
    .readLines()
    .filter { it.isNotBlank() }
    .map { line ->
        line.split("\\s+".toRegex()).let {
            require(it.size == 2)
            it[0].toLong() to it[1].toLong()
        }
    }.unzip()

private fun distanceFor(input: InputStream): Long {
    val (left, right) = parseInput(input)
    return left.sorted().zip(right.sorted()).sumOf { (first, second) -> abs(first - second) }
}

private fun similarityFor(input: InputStream): Long {
    val (left, right) = parseInput(input)
    val frequencies = right.groupingBy { it }.eachCount()
    return left.sumOf { num -> num * frequencies.getOrDefault(num, 0) }
}

private val TEST_INPUT = """
    |3   4
    |4   3
    |2   5
    |1   3
    |3   9
    |3   3
    """.trimMargin()

class Day01 {
    @Test
    fun `verify testing is working`() {
        val distance = distanceFor(TEST_INPUT.byteInputStream())
        assertEquals(11, distance)
    }

    @Test
    fun `verify part1`() {
        val distance = distanceFor(FileInputStream("./inputs/day1.txt"))
        assertEquals(1765812, distance)
    }

    @Test
    fun `verify part2 test case`() {
        val similarity = similarityFor(TEST_INPUT.byteInputStream())
        assertEquals(31, similarity)
    }

    @Test
    fun `verify part2`() {
        val similarity = similarityFor(FileInputStream("./inputs/day1.txt"))
        assertEquals(20520794, similarity)
    }
}