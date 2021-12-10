fun main() {

    val closingErrorScore = mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137
    )
    val openingToClosingMap = mapOf(
        '(' to ')',
        '[' to ']',
        '{' to '}',
        '<' to '>'
    )
    val closingValue = mapOf(
        ')' to 1,
        ']' to 2,
        '}' to 3,
        '>' to 4
    )

    /**
     * returns one of:
     * null - when opening and closing sequence match
     * Pair(first, emptyList) - first unmatched closing char
     * Pair('x', unmatchedOpeningSequence) - when no more closing and opening were unclosed
     */
    fun firstUnmatching(line: String): Pair<Char, List<Char>>? {
        val opening = "{([<"

        val stack = mutableListOf<Char>()

        for ((index, c) in line.withIndex()) {
            if (c in opening)
                stack.add(c)
            else
                if (openingToClosingMap[stack.last()] == c)
                    stack.removeLast()
                else
                    return c to emptyList()
        }
        return if (stack.isEmpty())
            null
        else
            'x' to stack
    }

    fun part1(input: List<String>): Int = input.mapNotNull { firstUnmatching(it) }
            .filter { it.second.isEmpty() }
            .map { closingErrorScore[it.first]!! }
            .sum()

    fun closingScore(openings: List<Char>): Long = openings.reversed()
        .asSequence()
        .map { openingToClosingMap[it] }
        .map { char -> closingValue[char]!! }
        .fold(0L) { sum: Long, charValue: Int ->
            sum * 5 + charValue
        }

    fun part2(input: List<String>): Long {
        val scores = input.mapNotNull { firstUnmatching(it) }
            .map { it.second }
            .filter { it.isNotEmpty() }
            .map { closingScore(it) }
            .sorted()
        check(scores.size % 2 == 1)
        val middleIndex = scores.size / 2

        return scores[middleIndex]
    }

    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day10_test")) == 26397)
    check(part2(readInput("Day10_test")) == 288957L)

    println(part1(readInput("Day10")))
    println(part2(readInput("Day10")))
}
