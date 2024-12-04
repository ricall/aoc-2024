private data class Point(val y: Int, val x: Int) {
    operator fun plus(other: Point) = Point(y + other.y, x + other.x)
}

class Day04Search(text: String) {

    // ---------------- PART ONE

    private fun getXMASCount(position: Point) = listOf(NW, N, NE, E, SE, S, SW, W)
        .map { direction -> textAt(position, direction)}
        .count { text -> text == "XMAS" }

    private fun countXMASAt(position: Point) = when(charAt(position)) {
        'X' -> getXMASCount(position)
        else -> 0
    }

    fun countXMAS() = totalFor(::countXMASAt)

    // ----------------- PART TWO

    private fun isMASCross(position: Point): Boolean {
        return listOf(
            textAt(position + NW, SE, 3),
            textAt(position + SW, NE, 3),
        ).all { word -> word == "MAS" || word == "SAM" }
    }

    private fun getMASCrossCountAt(position: Point) = when (isMASCross(position)) {
        true -> 1
        else -> 0
    }

    private fun smartCountMASCrossAt(position: Point) = when (charAt(position)) {
        'A' -> getMASCrossCountAt(position)
        else -> 0
    }

    fun countMASCrosses() = totalFor(::smartCountMASCrossAt)

    // ----------------- HELPERS

    private val lines = text.lines()

    companion object {
        // Compass directions as Delta y,x
        private val NW = Point(-1, -1)
        private val N = Point(-1, 0)
        private val NE = Point(-1, 1)
        private val E = Point(0, 1)
        private val SE = Point(1, 1)
        private val S = Point(1, 0)
        private val SW = Point(1, -1)
        private val W = Point(0, -1)
    }

    private fun charAt(position: Point): Char {
        val (y, x) = position
        if (y < 0 || y >= lines.size || x < 0 || x >= lines[y].length) {
            return ' '
        }
        return lines[y][x]
    }

    private fun textAt(position: Point, direction: Point, length: Int = 4): String {
        var currentPoint = position

        return (0..<length).map {
            val ch = charAt(currentPoint)
            currentPoint += direction
            ch
        }.joinToString(separator = "")
    }

    private inline fun totalFor(totalAt: (Point) -> Int): Int {
        var total = 0
        for (y in lines.indices) {
            for (x in lines[y].indices) {
                val position = Point(y, x)
                total += totalAt(position)
            }
        }
        return total
    }
}