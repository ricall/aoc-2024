import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class FindPairs {

    /**
     * Write a function in Java that takes an array of integers `k` and an integer `n`.
     * The function should return a list of pairs of integers from the array that add up to the value `n`.
     * Each pair should be represented as an array of two integers. The pairs should be unique and the order
     * of pairs or integers within pairs does not matter.
     *
     * @param k - An array of integers
     * @param n - An integer target sum
     * @return A list of unique pairs of integers that add up to `n`
     *
     * Example:
     *
     * Input:
     * int[] k = {1, 2, 3, 4, 3, 2}; {1,2,2,3,3,4}
     * int n = 5;
     *
     * Output:
     * [[1, 4], [2, 3]]
     *
     * Explanation:
     * - 1 + 4 = 5
     * - 2 + 3 = 5
     *
     * Constraints:
     * 1. The array `k` can contain negative numbers.
     * 2. Each pair in the output should be unique (i.e., no repeated pairs with the same numbers).
     * 3. You can assume that each element in the array can be used only once in a pair.
     * 4. The order of pairs or the order of numbers within each pair does not matter.
     */
    private fun findPairs(k: Array<Int>, n: Int): List<Pair<Int, Int>> {
        val visited = HashSet<Int>()
        val matches = ArrayList<Pair<Int, Int>>()
        val indexedValues = k.withIndex()

        k.forEachIndexed { firstIndex, firstValue ->
            if (visited.contains(firstValue)) {
                return@forEachIndexed
            }

            visited.add(firstValue)
            val searchValue = n - firstValue
            if (indexedValues.any { (secondIndex, secondValue) -> secondIndex != firstIndex &&  secondValue == searchValue }) {
                visited.add(searchValue)
                matches.add(firstValue to searchValue)
            }
        }
        return matches
    }

    @Test
    fun `basic find pairs`() {
        val result = findPairs(arrayOf(1, 2, 3, 4, 3, 2), 5)

        assertEquals(listOf(1 to 4, 2 to 3), result)
    }

    @Test
    fun `find pairs with duplicate second`() {
        val result = findPairs(arrayOf(1, 2, 3, 4, 4, 3, 3, 2), 5)

        assertEquals(listOf(1 to 4, 2 to 3), result)
    }

    @Test
    fun `no matching pairs`() {
        val result = findPairs(arrayOf(1, 2, 4, 5, 6), 4)

        assertEquals(emptyList<Pair<Int, Int>>(), result)
    }

    @Test
    fun `find pairs with negative values`() {
        val result = findPairs(arrayOf(-3, -2, -1, 0, 1, 2, 3, 4, 5, 6), 4)

        assertEquals(listOf(-2 to 6, -1 to 5, 0 to 4, 1 to 3), result)
    }
}