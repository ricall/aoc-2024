package org.ricall.day11

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class Day11 {
    private val TEST_DATA = "125 17"

    class StoneCounter(input: String) {
        private val stones = parseInput(input)
        private val rules = mutableMapOf(0L to listOf(1L))

        fun observeStones(blinks: Int): Long {
            var stoneCounts = stones.groupBy { it }.mapValues { it.value.size.toLong() }

            repeat(blinks) { stoneCounts = blink(stoneCounts) }
            return stoneCounts.values.sum()
        }

        private fun blink(stoneCounts: Map<Long, Long>) = buildMap<Long, Long> {
            stoneCounts.forEach { (stone, count) ->
                rules.getOrPut(stone, {
                    val stoneAsString = stone.toString()
                    val length = stoneAsString.length
                    when (length % 2 == 0) {
                        true -> listOf(
                            stoneAsString.substring(0, length / 2).toLong(),
                            stoneAsString.substring(length / 2).toLong()
                        )
                        false -> listOf(stone * 2024)
                    }
                }).forEach { newStone ->
                    set(newStone, (get(newStone) ?: 0) + count)
                }
            }
        }

        private fun parseInput(text: String) = text.trim().split(" ").map { it.toLong() }
    }

    @Test
    fun `part 1 test data`() {
        val result = StoneCounter(TEST_DATA).observeStones(25)
        assertEquals(55312, result)
    }

    @Test
    fun `part 1`() {
        val result = StoneCounter(File("./inputs/day11.txt").readText()).observeStones(25)
        assertEquals(183620, result)
    }

    @Test
    fun `part 2`() {
        val result = StoneCounter(File("./inputs/day11.txt").readText()).observeStones(75)
        assertEquals(220377651399268, result)
    }
}