package org.ricall.day11

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class Day11 {
    private val TEST_DATA = "125 17"

    class StoneCounter(input: String) {
        private val stones: List<Long>
        private val transformCache = mutableMapOf<Long, List<Long>>()

        init {
            stones = parseInput(input)
            transformCache[0] = listOf(1)
        }

        fun observeStones(blinks: Int): Long {
            var stoneCounts = buildMap { stones.forEach { stone -> set(stone, (get(stone) ?: 0L) + 1) } }

            repeat(blinks) { stoneCounts = blink(stoneCounts) }
            return stoneCounts.values.sum()
        }

        private fun blink(stoneCounts: Map<Long, Long>) = buildMap<Long, Long> {
            stoneCounts.forEach { (stone, count) ->
                var transformedStones = transformCache[stone]
                if (transformedStones == null) {
                    val stoneString = stone.toString()
                    val length = stoneString.length
                    if (length % 2 == 0) {
                        transformedStones = listOf(
                            stoneString.substring(0, length / 2).toLong(),
                            stoneString.substring(length / 2).toLong()
                        )
                    } else {
                        transformedStones = listOf(stone * 2024)
                    }
                    transformCache[stone] = transformedStones
                }
                transformedStones.forEach { newStone -> set(newStone, (get(newStone) ?: 0) + count) }
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