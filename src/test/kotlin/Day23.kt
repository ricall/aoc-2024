package org.ricall.day23

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private fun parseConnections(input: String) = input.lines()
    .flatMap { it.split("-").let { (a,b) -> listOf(a to b, b to a) } }
    .groupBy({ it.first }, { it.second })

private fun solvePartOne(input: String): Int {
    val connections = parseConnections(input)
    val results = buildSet {
        for ((computer, others) in connections) {
            for (i in 0..<others.lastIndex) {
                val candidate1 = others[i]
                for (j in i + 1..others.lastIndex) {
                    val candidate2 = others[j]
                    if (candidate1 in connections.getValue(candidate2)) {
                        add(setOf(computer, candidate1, candidate2))
                    }
                }
            }
        }
    }
    return results.count { it.any { it.startsWith("t") } }
}

private fun solvePartTwo(input: String): String {
    val connections = parseConnections(input)
    val groups = mutableSetOf<MutableSet<String>>()
    for ((computer, others) in connections) {
        for (group in groups) {
            if (others.containsAll(group) && group.all { computer in connections.getValue(it) }) {
                group.add(computer)
            }
        }
        for (node in others) {
            groups.add(mutableSetOf(computer, node))
        }
    }
    return groups.maxBy { it.size }.sorted().joinToString(",")
}

class Day23 {
    private val TEST_DATA = """
        |kh-tc
        |qp-kh
        |de-cg
        |ka-co
        |yn-aq
        |qp-ub
        |cg-tb
        |vc-aq
        |tb-ka
        |wh-tc
        |yn-cg
        |kh-ub
        |ta-co
        |de-co
        |tc-td
        |tb-wq
        |wh-td
        |ta-ka
        |td-qp
        |aq-cg
        |wq-ub
        |ub-vc
        |de-ta
        |wq-aq
        |wq-vc
        |wh-yn
        |ka-de
        |kh-ta
        |co-tc
        |wh-qp
        |tb-vc
        |td-yn""".trimMargin()

    @Test
    fun `part 1 test data`() {
        val result = solvePartOne(TEST_DATA)

        assertEquals(7, result)
    }

    @Test
    fun `part 1`() {
        val result = solvePartOne(File("./inputs/day23.txt").readText())

        assertEquals(1170, result)
    }

    @Test
    fun `part 2 test data`() {
        val result = solvePartTwo(TEST_DATA)

        assertEquals("co,de,ka,ta", result)
    }

    @Test
    fun `part 2`() {
        val result = solvePartTwo(File("./inputs/day23.txt").readText())

        assertEquals("bo,dd,eq,ik,lo,lu,ph,ro,rr,rw,uo,wx,yg", result)
    }
}