fun main() {

    fun coordRange(a: Int, b: Int): Iterable<Int> {
        return if (a <= b) return a..b
        else IntProgression.fromClosedRange(a, b, -1)
    }

    fun pointsOnLine(coords: List<Int>, includeDiagonal: Boolean): Sequence<Pair<Int, Int>> = sequence {
        if (coords[0] == coords[2])
            for (y in coordRange(coords[1], coords[3])) yield(Pair(coords[0], y))
        else if (coords[1] == coords[3])
            for (x in coordRange(coords[0], coords[2])) yield(Pair(x, coords[1]))
        else if (includeDiagonal)
            yieldAll(coordRange(coords[0], coords[2]).zip(coordRange(coords[1], coords[3])))
    }

    fun countPoints(input: List<String>, includeDiagonal: Boolean): Int {
        val points = mutableMapOf<Pair<Int, Int>, Int>()
        for (line in input) {
            val numbers = line.replace(" -> ", ",").split(",").map { it.toInt() }
            check(numbers.size == 4)
            for (point in pointsOnLine(numbers, includeDiagonal)) {
                if (points.computeIfPresent(point, { _, v -> v + 1 }) == null) {
                    points[point] = 1
                }
            }
        }
        return points.values.filter { it >= 2 }.count()
    }

    fun part1(input: List<String>): Int {
        return countPoints(input, false)
    }

    fun part2(input: List<String>): Int {
        return countPoints(input, true)
    }


    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day05_test")) == 5)
    check(part2(readInput("Day05_test")) == 12)

    println(part1(readInput("Day05")))
    println(part2(readInput("Day05")))
}
