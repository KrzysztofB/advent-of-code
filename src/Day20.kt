private fun Array<CharArray>.copyGrid() = Array(size) { get(it).clone() }

private fun printGrid(grid: Array<CharArray>, msg: String = "") {
    println("--- $msg ---")
    for (row in grid) {
        for (col in row) {
            print(col)
        }
        println()
    }
}

fun main() {
    var outOfImage = '.'

    fun load(input: List<String>, padding: Int): Pair<String, Array<CharArray>> {
        val gridLines = input.subList(2,input.size)
        val paddedHeight = gridLines.size + padding+padding
        val paddedWidth = gridLines[0].length+padding+padding
        val grid = Array<CharArray>(paddedHeight) { lineIdx ->
            if(lineIdx>=padding&& lineIdx<paddedHeight-padding) {
                CharArray(paddedWidth) {
                    if(it>=padding&& it<paddedWidth-padding) {
                        gridLines[lineIdx - padding][it - padding]
                    }
                 else {'.' }
            } }else {
                CharArray(paddedWidth){ '.' }
            }
        }

        return input[0] to grid
    }

    fun rowPattern(rowIdx: Int, colIdx: Int, source: Array<CharArray>): Int {
        if (rowIdx < source.indices.first || rowIdx > source.indices.last) {
            return if( outOfImage=='.') 0 else 7
        }

        fun pixelValue(pixelColIdx: Int): Int {
            if (pixelColIdx < source[0].indices.first || pixelColIdx > source[0].indices.last) {
                return if( outOfImage=='.') 0 else 1
            }
            return if (source[rowIdx][pixelColIdx] == '#') 1 else 0
        }
        val p0 = pixelValue(colIdx - 1) * 4
        val p1 = pixelValue(colIdx) * 2
        val p2 = pixelValue(colIdx + 1)
        return p0 + p1 + p2
    }

    fun pattern(rowIdx: Int, colIdx: Int, source: Array<CharArray>): Int {
        val r0 = rowPattern(rowIdx - 1, colIdx, source) * 64
        val r1 = rowPattern(rowIdx, colIdx, source) * 8
        val r2 = rowPattern(rowIdx + 1, colIdx, source)
        return r0 + r1 + r2
    }

    fun evalGrid(algo: String, grid: Array<CharArray>, evalCount: Int = 2): Int {
        var source = grid
        var canvas = source.copyGrid()
        repeat(evalCount) {
            for (rowIdx in 0.until(source.size)) {
                for (colIdx in 0.until(source[0].size)) {
                    val algInput: Int = pattern(rowIdx, colIdx, source)
                    canvas[rowIdx][colIdx] = algo[algInput]
                }
            }

            // printGrid(canvas, it.toString())
            val tmp = source
            source = canvas
            canvas = tmp
            if(outOfImage == '.') outOfImage = algo[0]
            else if(outOfImage=='#') outOfImage = algo[511]
        }
        return source.fold(0) { accLine, line ->
            accLine + line.fold(0) { acc, c -> acc + (if (c == '#') 1 else 0) }
        }

    }

    fun part1(input: List<String>): Int {
        outOfImage = '.'
        val (algo, grid) = load(input,2)
        //printGrid(grid)

        val result = evalGrid(algo, grid, 2)
        return result
    }


    fun part2(input: List<String>): Int {
        outOfImage = '.'
        val (algo, grid) = load(input,50)
        //printGrid(grid)

        val result = evalGrid(algo, grid, 50)
        return result
    }


    fun preTest() {
        val padding = 50
        val (_, grid1) = load(listOf("###", "", "...", "#..", ".#."),padding)
        outOfImage = '.'
        //check(rowPattern(0,0,grid1)==0)
        check(rowPattern(1+padding, 0+padding, grid1) == 2)
        check(rowPattern(1+padding, 1+padding, grid1) == 4)
        check(rowPattern(2+padding, 0+padding, grid1) == 1)

        check(pattern(0+padding, 1+padding, grid1) == 4)
        check(pattern(1+padding, 1+padding, grid1) == 34)
        check(pattern(2+padding, 1+padding, grid1) == 256 + 16)


    }
    preTest()
    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day20_test")) == 35)
    check(part2(readInput("Day20_test")) == 3351)
//
     //println(part1(readInput("Day20")))// something wrong here....
    println(part2(readInput("Day20")))

}
