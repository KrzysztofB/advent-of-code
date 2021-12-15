import java.lang.Integer.min

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


    fun printDigitGrid(grid: Array<IntArray>, msg: String = "") {
        println("--- $msg ---")
        for (row in grid) {
            for (col in row) {
                print(col.toString(16))
            }
            println()
        }
    }
    fun printGrid(grid: Array<IntArray>, msg: String = "") {
        println("--- $msg ---")
        for (row in grid) {
            for (col in row) {
                print(col.toString().padStart(4))
            }
            println()
        }
    }

    //FAIL: counts only path DOWN & RIGHT
    fun cheapestPath(grid: Array<IntArray>){
        val maxCol = grid[0].indices.last
        val maxRow = grid.indices.last
        for(row in maxRow downTo 0)
        for(col in maxCol downTo 0)
        {
            val minCost =
                when{
                    (row==maxRow)&& (col==maxCol) -> 0
                    else -> min(
                        if(row==maxRow) Int.MAX_VALUE else grid[row+1][col],
                        if(col==maxCol) Int.MAX_VALUE else grid[row][col+1]
                    )
                }

            if((row==0)&&(col==0)) {
                grid[row][col] = minCost
            } else{
                grid[row][col] += minCost
            }
        }
    }

    fun part1(input: List<String>): Int {
        val grid = loadGrid(input)
        printGrid(grid)
        cheapestPath(grid)
        printGrid(grid)

        return grid[0][0]
    }

    fun part2(input: List<String>): Int {
        //val grid = loadGrid(input)
        return 0
    }

    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day15_test")) == 40)
    //check(part2(readInput("Day15_test")) == 195)

    println(part1(readInput("Day15")))
   // println(part2(readInput("Day15")))
}
