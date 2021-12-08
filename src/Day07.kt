import java.lang.Math.abs

fun main() {

    fun stepsToFuel(distance: Int): Int {
        return (1 + distance) * distance / 2
    }

    fun totalFuel(list: List<Pair<Int, Int>>, destination: Int): Int {
        val result = list.fold(0) { acc: Int, moving: Pair<Int, Int> ->
            val (location, count) = moving
            val distance = abs(location - destination)
            val fuelForOne = stepsToFuel(distance)
            acc + fuelForOne * count
        }
        return result
    }

    fun median(list: List<Int>) = list.let {
        if (it.size % 2 == 0)
            (it[it.size / 2] + it[(it.size - 1) / 2]) / 2
        else
            it[it.size / 2]
    }

    fun part2(input: List<String>): Int {
        val position = input[0].split(",").map { it.toInt() }.sorted()
        val locationsCounted = position.groupBy { it }.map { it.key to it.value.size }.sortedBy { it.first }
        val result = IntRange(locationsCounted.first().first, locationsCounted.last().first)
            .map { totalFuel(locationsCounted, it) }
            .minOf { it }
        return result
    }


    fun part1(input: List<String>): Int {
        val position = input[0].split(",").map { it.toInt() }.sorted()
        val m = median(position)
        val result = position.map { abs(it - m) }.sum()
        return result
    }

    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day07_test")) == 37)
    check(part2(readInput("Day07_test")) == 168)

    println(part1(readInput("Day07")))
    println(part2(readInput("Day07")))
}
