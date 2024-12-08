import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private fun parseInput(lines: List<String>): Pair<Map<Int, List<Int>>, List<List<Int>>> {
    val rules = mutableListOf<Pair<Int, Int>>()
    val updates = mutableListOf<List<Int>>()
    var parseUpdates = false

    for (line in lines) {
        if (line.isBlank()) {
            parseUpdates = true
            continue
        }

        when (parseUpdates) {
            false -> {
                line.split("|").let {
                    require(it.size == 2)
                    rules.add(it[0].toInt() to it[1].toInt())
                }
            }
            else -> {
                updates.add(line.split(",").map { it.toInt() })
            }
        }
    }

    val pageOrdering = rules.groupBy { it.second }.mapValues { it.value.map { it.first } }
    return pageOrdering to updates
}

private fun sumValidMiddlePageNumbers(text: String): Int {
    val (pageOrdering, updates) = parseInput(text.lines())

    return updates
        .filter { update -> isValidUpdate(update, pageOrdering) }
        .sumOf(::middlePageNumber)
}

private fun isValidUpdate(update: List<Int>, pageOrdering: Map<Int, List<Int>>): Boolean {
    val printed = mutableSetOf<Int>()
    for (page in update) {
        val requiredPages = pageOrdering.getOrDefault(page, emptyList()).filter(update::contains)
        if (!requiredPages.all(printed::contains)) {
            return false
        }
        printed.add(page)
    }
    return true
}

private fun middlePageNumber(update: List<Int>) = update[update.size / 2]

private fun sumReorderedInvalidMiddlePageNumbers(text: String): Int {
    val (pageOrdering, updates) = parseInput(text.lines())

    return updates
        .filter { update -> !isValidUpdate(update, pageOrdering) }
        .map { update -> reorderUpdate(update, pageOrdering)}
        .sumOf(::middlePageNumber)
}

private fun reorderUpdate(update: List<Int>, pageOrdering: Map<Int, List<Int>>): List<Int> {
    val orderedPrint = mutableListOf<Int>()
    val candidates = mutableSetOf<Int>()
    candidates.addAll(update)

    while (candidates.isNotEmpty()) {
        val page = candidates.first { page ->
            pageOrdering.getOrDefault(page, emptyList())
                .filter(update::contains)
                .all(orderedPrint::contains)
        }
        orderedPrint.add(page)
        candidates.remove(page)
    }
    return orderedPrint
}

private val TEST_DATA = """
    |47|53
    |97|13
    |97|61
    |97|47
    |75|29
    |61|13
    |75|53
    |29|13
    |97|29
    |53|29
    |61|53
    |97|53
    |61|29
    |47|13
    |75|47
    |97|75
    |47|61
    |75|61
    |47|29
    |75|13
    |53|13
    |
    |75,47,61,53,29
    |97,61,53,29,13
    |75,29,13
    |75,97,47,61,53
    |61,13,29
    |97,13,75,29,47""".trimMargin("|")

class Day05 {
    @Test
    fun `part 1 test data`() {
        val result = sumValidMiddlePageNumbers(TEST_DATA)
        assertEquals(143, result)
    }

    @Test
    fun `part 1`() {
        val result = sumValidMiddlePageNumbers(File("./inputs/day5.txt").readText())
        assertEquals(5129, result)
    }

    @Test
    fun `part 2 test data`() {
        val result = sumReorderedInvalidMiddlePageNumbers(TEST_DATA)
        assertEquals(123, result)
    }

    @Test
    fun `part 2`() {
        val result = sumReorderedInvalidMiddlePageNumbers(File("./inputs/day5.txt").readText())
        assertEquals(4077, result)
    }
}