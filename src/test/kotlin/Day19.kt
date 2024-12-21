package org.ricall.day19

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private class LinenLayout(input: String) {
    private val patterns: List<String>
    private val designs: List<String>
    private val cache = mutableMapOf<String, Long>()

    init {
        val parts = input.split("\n\n")
        patterns = parts[0].split(", ")
        designs = parts[1].lines()
    }

    private fun countLinenArrangements(steps: List<String>, design: String, count: Long = 0): Long =
        cache.getOrPut(design) {
            if (design.isEmpty()) {
                return 1
            }
            var arrangements = count
            for (step in steps) {
                if (design.startsWith(step)) {
                    arrangements += countLinenArrangements(steps, design.substring(step.length))
                }
            }
            cache[design] = arrangements
            arrangements
        }

    private fun numberOfArrangementsForDesigns(): List<Long> =
        designs.map { design -> countLinenArrangements(patterns, design) }

    fun validDesignPatternCount() = numberOfArrangementsForDesigns().count { it > 0 }

    fun totalTowelArrangements() = numberOfArrangementsForDesigns().sum()
}

class Day19 {
    private val TEST_DATA = """
        |r, wr, b, g, bwu, rb, gb, br
        |
        |brwrr
        |bggr
        |gbbr
        |rrbgbr
        |ubwu
        |bwurrg
        |brgr
        |bbrgwb""".trimMargin()

    @Test
    fun `part 1 test data`() {
        val result = LinenLayout(TEST_DATA).validDesignPatternCount()

        assertEquals(6, result)
    }

    @Test
    fun `part 1`() {
        val result = LinenLayout(File("./inputs/day19.txt").readText()).validDesignPatternCount()

        assertEquals(298, result)
    }

    @Test
    fun `part 2 test data`() {
        val result = LinenLayout(TEST_DATA).totalTowelArrangements()

        assertEquals(16, result)
    }

    @Test
    fun `part 2`() {
        val result = LinenLayout(File("./inputs/day19.txt").readText()).totalTowelArrangements()

        assertEquals(572248688842069, result)
    }
}