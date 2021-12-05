fun main() {

    fun loadGame(input: List<String>): Pair<List<UInt>, List<BingoBoard>> {
        val numbers = input.first().trim().split(",").map { it.toUInt() }
        val boards = mutableListOf<BingoBoard>()
        for (boardLine in 1 until input.size step 6) {
            if (boardLine + 5 >= input.size) break
            boards.add(BingoBoard.load(input, boardLine))
        }
        return numbers to boards.toList()
    }

    fun part1(input: List<String>): Int {
        val (numbers, boards) = loadGame(input)
        for (number in numbers) {
            val winner = boards.firstOrNull { it.markDrawn(number) }
            if (winner != null) {
                return (number * winner.sumUnmarked()).toInt()
            }
        }
        return 0
    }

    fun part2(input: List<String>): Int {
        val (numbers, boards) = loadGame(input)
        var lastWinner: Pair<UInt, BingoBoard>? = null
        for (number in numbers) {
            val winner = boards.filter { !it.won }.map { it to it.markDrawn(number) }
                .firstOrNull { it.second }
            if (winner != null) {
                lastWinner = number to winner.first
                //return (number * winner.sumUnmarked()).toInt()
            }
        }
        return lastWinner?.let { (lastWinner.first * lastWinner.second.sumUnmarked()).toInt() } ?: 0
    }


    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day04_test")) == 4512)
    check(part2(readInput("Day04_test")) == 1924)

    println(part1(readInput("Day04")))
    println(part2(readInput("Day04")))
}

class BingoBoard(val board: Array<MutableList<Field>>) {
    var won: Boolean = false

    override fun toString(): String {
        return board.map { it.toString() }.joinToString(separator = "\n")
    }

    fun markDrawn(number: UInt): Boolean {
        var found: Pair<Int, Int>? = null
        loop@ for (row in 0..4) for (col in 0..4) {
            val field = board[row][col]
            if (field.number == number) {
                field.drawn = true
                found = row to col
                break@loop
            }
        }

        return if (found != null) {
            won = won || hasBingo(found.first, found.second)
            won
        } else
            false
    }

    private fun hasBingo(row: Int, col: Int): Boolean {
        return (0..4).map { board[it][col] }.all { it.drawn } || board[row].all { it.drawn }
    }

    fun sumUnmarked(): UInt =
        board.flatMap { it }.filter { !it.drawn }.sumOf { it.number }


    companion object {
        fun load(input: List<String>, startLine: Int): BingoBoard {
            val board = Array(5) { mutableListOf<Field>() }
            for (lineIdx in 0..4) {
                val line = input[startLine + 1 + lineIdx].trim()
                line.split(" ").filter { it.isNotEmpty() }.forEach { board[lineIdx].add(Field(it.toUInt())) }
            }
            return BingoBoard(board)
        }

    }
}

class Field(var number: UInt) {
    var drawn: Boolean = false
    override fun toString(): String {
        return if (drawn) ".$number." else " $number "
    }
}
