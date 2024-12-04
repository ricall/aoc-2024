class Day04Search(text: String) {
    private val lines = text.lines()

    private fun getXMASCount(position: Pair<Int, Int>) = listOf(
        stringAt(position, Pair(-1, -1)),
        stringAt(position, Pair(-1, 0)),
        stringAt(position, Pair(-1, 1)),
        stringAt(position, Pair(0, 1)),
        stringAt(position, Pair(1, 1)),
        stringAt(position, Pair(1, 0)),
        stringAt(position, Pair(1, -1)),
        stringAt(position, Pair(0, -1)),
    ).count { word -> word == "XMAS" }

    private fun countXMASAt(position: Pair<Int, Int>) = when(charAt(position)) {
        'X' -> getXMASCount(position)
        else -> 0
    }

    fun countXMAS() = visitAll(::countXMASAt)

    // -----------------

    private fun findMAS(position: Pair<Int, Int>): Boolean {
        val (y, x) = position
        return listOf(
            stringAt(Pair(y - 1, x - 1), Pair(1, 1), 3),
            stringAt(Pair(y + 1, x - 1), Pair(-1, 1), 3),
        ).all { word -> word == "MAS" || word == "SAM" }
    }

    private fun countMASCrossAt(position: Pair<Int, Int>) = when (findMAS(position)) {
        true -> 1
        else -> 0
    }

    fun countMASCrosses() = visitAll { position ->
        when (charAt(position)) {
            'A' -> countMASCrossAt(position)
            else -> 0
        }
    }

    // -----------------

    private fun charAt(position: Pair<Int, Int>): Char {
        val (y, x) = position
        if (y < 0 || y >= lines.size || x < 0 || x >= lines[y].length) {
            return ' '
        }
        return lines[y][x]
    }

    private fun stringAt(position: Pair<Int, Int>, delta: Pair<Int, Int>, length: Int = 4): String {
        var (y, x) = position
        val (dy, dx) = delta

        return (0..<length).map {
            val ch = charAt(Pair(y, x))
            y += dy
            x += dx

            ch
        }.joinToString(separator = "")
    }

    private inline fun visitAll(sink: (Pair<Int, Int>) -> Int): Int {
        var total = 0
        for (y in lines.indices) {
            for (x in lines[y].indices) {
                val position = Pair(y, x)
                total += sink(position)
            }
        }
        return total
    }
}