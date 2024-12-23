package org.ricall.day22

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private const val MAX_SECRET_PER_DAY = 2000

private const val BITMASK = 0xffffff
private fun nextSecretNumber(number: Int): Int {
    val part1 = number shl 6 xor number and BITMASK
    val part2 = part1 shr 5 xor part1 and BITMASK
    return part2 shl 11 xor part2 and BITMASK
}

private typealias SecretSequence = Sequence<Int>

private fun secretSequence(seed: Int) = sequence {
    var current = seed
    repeat(MAX_SECRET_PER_DAY) {
        val next = nextSecretNumber(current)
        yield(next)
        current = next
    }
}

private fun solvePartOneSingle(seed: Int) = secretSequence(seed).last().toLong()
private fun solvePartOne(input: String) = input.lines()
    .map(String::toInt)
    .sumOf(::solvePartOneSingle)

private typealias DeltaList = List<Int>
private typealias Price = Int

private fun convertSecretsToDeltaListToPrice(sequence: SecretSequence) = sequence.map { it % 10 }
    .zipWithNext { a, b -> b to b - a }
    .windowed(4) { (a, b, c, d) -> listOf(a.second, b.second, c.second, d.second) to d.first }
    .groupingBy { it.first }
    .fold({ _, value -> value.second }, { _, a, _ -> a })

private fun mergeDeltaLists(target: MutableMap<DeltaList, Price>, source: Map<DeltaList, Price>) = target.apply {
    source.forEach { (deltaList, price) -> merge(deltaList, price, Int::plus) }
}

private fun solvePartTwo(input: String) = input.lines()
    .map(String::toInt)
    .map(::secretSequence)
    .map(::convertSecretsToDeltaListToPrice)
    .fold(mutableMapOf(), ::mergeDeltaLists)
    .values
    .max()

class Day22 {

    @Test
    fun `part 1 basic mode`() {
        val results = secretSequence(123).take(10).toList()

        assertEquals(
            listOf(
                15887950,
                16495136,
                527345,
                704524,
                1553684,
                12683156,
                11100544,
                12249484,
                7753432,
                5908254,
            ), results
        )
    }

    @Test
    fun `part 1 test data`() {
        val result = solvePartOne(
            """
            |1
            |10
            |100
            |2024""".trimMargin()
        )

        assertEquals(37327623, result)
    }

    @Test
    fun `part 1`() {
        val result = solvePartOne(File("./inputs/day22.txt").readText())

        assertEquals(14623556510, result)
    }

    @Test
    fun `part 2 test data`() {
        val result = solvePartTwo(
            """
            |1
            |2
            |3
            |2024""".trimMargin()
        )

        assertEquals(23, result)
    }

    @Test
    fun `part 2`() {
        val result = solvePartTwo(File("./inputs/day22.txt").readText())

        assertEquals(1701, result)
    }
}