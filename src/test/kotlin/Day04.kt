import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

private val TEST_DATA = """
    |MMMSXXMASM
    |MSAMXMSMSA
    |AMXSXMAAMM
    |MSAMASMSMX
    |XMASAMXAMM
    |XXAMMXXAMA
    |SMSMSASXSS
    |SAXAMASAAA
    |MAMMMXMMMM
    |MXMXAXMASX""".trimMargin()

class Day04 {
    @Test
    fun `part 1 test data`() {
        val result = WordSearch(TEST_DATA).countXMAS()

        assertEquals(18, result)
    }

    @Test
    fun `part 1`() {
        val result = WordSearch(File("./inputs/day4.txt").readText()).countXMAS()

        assertEquals(2549, result)
    }

    @Test
    fun `part 2 test data`() {
        val result = WordSearch(TEST_DATA).countMASCrosses()

        assertEquals(9, result)
    }

    @Test
    fun `part 2`() {
        val result = WordSearch(File("./inputs/day4.txt").readText()).countMASCrosses()

        assertEquals(2003, result)
    }
}