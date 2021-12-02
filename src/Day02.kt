fun main() {

    fun parseCommand(line: String): Pair<Int, Int> {
        val tokens = line.split(" ")
        require(tokens.size == 2)
        val step = tokens[1].toInt()
        return when (tokens[0]) {
            "up" -> 0 to -step
            "down" -> 0 to step
            "forward" -> step to 0
            else -> throw IllegalArgumentException("Unknown command $line")
        }
    }

    fun part1(input: Sequence<String>): Int {
        val location = input.fold(0 to 0) { acc: Pair<Int, Int>, line: String ->
            val delta = parseCommand(line)
            (acc.first + delta.first) to (acc.second + delta.second)
        }
        return location.first * location.second
    }

    fun part2(input: Sequence<String>): Int {
        val location = input.fold(Triple(0,0,0)) { acc: Triple<Int, Int, Int>, line: String ->
            val change = parseCommand(line)
            val (oldHorizontal, oldDepth, oldAim) = acc
            val aim = oldAim + change.second
            Triple(oldHorizontal + change.first,oldDepth + change.first * aim,aim)
        }
        return location.first * location.second
    }

    // test if implementation meets criteria from the description, like:
    check(part1(readSequence("Day02_test")) == 150)
    check(part2(readSequence("Day02_test")) == 900)

    println(part1(readSequence("Day02")))
    println(part2(readSequence("Day02")))
}
