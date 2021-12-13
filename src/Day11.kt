fun main() {
    fun loadGrid(input: List<String>): Array<IntArray> {
        val width = input[0].trim().length
        val height = input.size
        val grid = Array<IntArray>(height) { lineIdx ->
            IntArray(width) { colIdx ->
                Character.getNumericValue(input[lineIdx][colIdx])
            }
        }
        return grid
    }


    fun printGrid(grid: Array<IntArray>, msg: String = "") {
        println("--- $msg ---")
        for (row in grid) {
            for (col in row) {
                print(col.toString(16))
            }
            println()
        }
    }

    fun increaseLevels(grid: Array<IntArray>) {
        for (row in grid)
            for (colIdx in row.indices) {
                row[colIdx]++
            }
    }

    fun fire(rowIdx: Int, colIdx: Int, grid: Array<IntArray>) {
        if (rowIdx in grid.indices) {
            val row = grid[rowIdx]
            if (colIdx in row.indices) {
                if (row[colIdx] > 0)
                    row[colIdx]++
            }
        }
    }

    fun fireNeighbours(rowIdx: Int, colIdx: Int, grid: Array<IntArray>) {
        fire(rowIdx - 1, colIdx - 1, grid)
        fire(rowIdx - 1, colIdx, grid)
        fire(rowIdx - 1, colIdx + 1, grid)

        fire(rowIdx, colIdx - 1, grid)
        fire(rowIdx, colIdx + 1, grid)

        fire(rowIdx + 1, colIdx - 1, grid)
        fire(rowIdx + 1, colIdx, grid)
        fire(rowIdx + 1, colIdx + 1, grid)
    }

    fun countFlashes(grid: Array<IntArray>): Int {
        var flashedCount = 0
        do {
            var flashedInLoop = 0
            for ((rowIdx, row) in grid.withIndex())
                for (colIdx in row.indices) {
                    if (row[colIdx] > 9) {
                        flashedInLoop++
                        row[colIdx] = 0
                        fireNeighbours(rowIdx, colIdx, grid)
                    }
                }
            flashedCount += flashedInLoop
            //printGrid(grid, "flashed $flashedInLoop total $flashedCount")
        } while (flashedInLoop > 0)
        return flashedCount
    }

    fun part1(input: List<String>): Int {
        val grid = loadGrid(input)
        var flashes = 0
        repeat(100) {
            increaseLevels(grid)
            flashes += countFlashes(grid)
            //println("######  AFTER STEP ${it+1}  ######")
        }
        return flashes
    }

    fun part2(input: List<String>): Int {
        val grid = loadGrid(input)
        val gridSize = grid.size * grid[0].size
        var step = 0
        do {
            increaseLevels(grid)
            val flashes = countFlashes(grid)
            step++
            //println("######  AFTER STEP ${it+1}  ######")
        } while (flashes < gridSize)
        return step
    }

    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day11_test")) == 1656)
    check(part2(readInput("Day11_test")) == 195)

    println(part1(readInput("Day11")))
    println(part2(readInput("Day11")))
}
