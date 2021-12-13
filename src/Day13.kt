fun main() {
    fun loadDots(input: List<String>, dots: MutableSet<Pair<Int, Int>>): Int {
        for ((index, line) in input.withIndex()) {
            if (line.trim().isEmpty()) {
                return index + 1
            }
            val (x, y) = line.split(",").map { it.toInt() }
            dots.add(x to y)
        }
        throw IllegalArgumentException("No folds in file")
    }

    fun foldX(value: Int, dots: MutableSet<Pair<Int, Int>>) {
        val movedDots = mutableSetOf<Pair<Int, Int>>()
        dots.filter { it.first > value }.forEach {
            val newX = it.first - 2 * (it.first - value)
            movedDots.add(newX to it.second)
        }
        dots.removeAll { it.first >= value }
        dots.addAll(movedDots)
    }

    fun foldY(value: Int, dots: MutableSet<Pair<Int, Int>>) {
        val movedDots = mutableSetOf<Pair<Int, Int>>()
        dots.filter { it.second > value }.forEach {
            val newY = it.second - 2 * (it.second - value)
            movedDots.add(it.first to newY)
        }
        dots.removeAll { it.second >= value }
        dots.addAll(movedDots)
    }

    fun foldPage(foldInstruction: String, dots: MutableSet<Pair<Int, Int>>) {
        require(foldInstruction.startsWith("fold along "))
        val (xy, strFoldLine) = foldInstruction.replace("fold along ", "").split("=")
        if (xy == "x") foldX(strFoldLine.toInt(), dots)
        else foldY(strFoldLine.toInt(), dots)
    }

    fun printPage(dots: Set<Pair<Int, Int>>) {
        val maxX = dots.maxOf { it.first }
        val maxY = dots.maxOf { it.second }
        val grid = Array<CharArray>(maxY + 1) { lineIdx ->
            CharArray(maxX + 1) { ' ' }
        }
        dots.forEach { grid[it.second][it.first] = '#' }

        grid.map { it.joinToString("") }.forEach { println(it) }
    }

    fun part1(input: List<String>): Int {
        val dots = mutableSetOf<Pair<Int, Int>>()
        val startOfFolds = loadDots(input, dots)
        foldPage(input[startOfFolds], dots)
        return dots.size
    }

    fun part2(input: List<String>): Int {
        val dots = mutableSetOf<Pair<Int, Int>>()
        val startOfFolds = loadDots(input, dots)
        for (lineIdx in startOfFolds.until(input.size)) {
            foldPage(input[lineIdx], dots)
        }
        printPage(dots)
        return 0
    }

    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day13_test")) == 17)
    check(part2(readInput("Day13_test")) == 0)

    println(part1(readInput("Day13")))
    println(part2(readInput("Day13")))

}
