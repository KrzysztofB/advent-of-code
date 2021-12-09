fun main() {

    fun loadTerrainAddBorders(input: List<String>): Array<IntArray> {
        val originalWidth = input[0].trim().length
        val width = originalWidth + 2
        val originalHeight = input.size
        val height = originalHeight + 2
        val terrain = Array<IntArray>(height) { lineIdx ->
            when (lineIdx) {
                0, height - 1 -> IntArray(width) { 9 }
                else -> IntArray(width) { colIdx ->
                    when (colIdx) {
                        0, width - 1 -> 9
                        else -> Character.getNumericValue(input[lineIdx - 1][colIdx - 1])
                    }
                }
            }
        }
        return terrain
    }

    fun lowestPoint(row: Int, column: Int, terrain: Array<IntArray>): Boolean {
        val value = terrain[row][column]
        return value < terrain[row][column - 1]
                && value < terrain[row][column + 1]
                && value < terrain[row - 1][column]
                && value < terrain[row + 1][column]
    }

    fun lowPoints(terrain: Array<IntArray>): List<Int> {
        val result = mutableListOf<Int>()
        for (row in 1..terrain.size - 2) {
            for (column in 1..terrain[0].size - 1)
                if (lowestPoint(row, column, terrain))
                    result.add(terrain[row][column])
        }
        return result
    }


    fun lowPointsCoords(terrain: Array<IntArray>): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        for (row in 1..terrain.size - 2) {
            for (column in 1..terrain[0].size - 1)
                if (lowestPoint(row, column, terrain))
                    result.add(row to column)
        }
        return result
    }

    fun part1(input: List<String>): Int {
        val terrain = loadTerrainAddBorders(input)

        return lowPoints(terrain).map { it + 1 }.sum()
    }

    fun basinSize(startCoord: Pair<Int, Int>, terrain: Array<IntArray>): Int {
        val basin = mutableSetOf<Pair<Int, Int>>()
        val toVisit = mutableSetOf<Pair<Int, Int>>()
        basin.add(startCoord)
        toVisit.add(startCoord)
        fun consider(row: Int, col: Int) {
            if (terrain[row][col] == 9) return
            val location = Pair(row, col)
            if (location in basin) return
            basin.add(location)
            toVisit.add(location)
        }
        while (toVisit.isNotEmpty()) {
            val location = toVisit.first()
            toVisit.remove(location)
            consider(location.first - 1, location.second)
            consider(location.first, location.second - 1)
            consider(location.first + 1, location.second)
            consider(location.first, location.second + 1)
        }

        return basin.size
    }

    fun findBasins(startCoords: List<Pair<Int, Int>>, terrain: Array<IntArray>): List<Int> {
        return startCoords.map { basinSize(it, terrain) }
            .sortedDescending()
            .take(3)
    }

    fun part2(input: List<String>): Int {
        val terrain = loadTerrainAddBorders(input)
        val lowCoords = lowPointsCoords(terrain)
        return findBasins(lowCoords, terrain)
            .reduce { acc: Int, basinSize: Int -> acc * basinSize }
    }


    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day09_test")) == 15)
    check(part2(readInput("Day09_test")) == 1134)

    println(part1(readInput("Day09")))
    println(part2(readInput("Day09")))
}
