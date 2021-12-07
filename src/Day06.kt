fun main() {

    fun calcNextDay(fish: MutableMap<Int, Long>) {
        val ready = fish.getOrDefault(0, 0L)
        (1..8).forEach {
            fish[it - 1] = fish.getOrDefault(it, 0L)
        }
        fish[8] = ready
        fish[6] = ready + fish.getOrDefault(6, 0L)
    }

    fun count(fish: MutableMap<Int, Long>): Long = fish.values.sum()

    fun calcToDay(input: List<String>, lastDay: Int): Long {
        val fish = mutableMapOf<Int, Long>()

        input[0].split(",")
            .map { it.toInt() }
            .groupBy { it }
            .forEach { fish.put(it.key, it.value.size.toLong()) }
        var day = 0
        do {
            day++
            calcNextDay(fish)
        } while (day < lastDay)
        val result = count(fish)
        return result
    }

    fun part1(input: List<String>) = calcToDay(input, 80)


    fun part2(input: List<String>) = calcToDay(input, 256)


    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day06_test")) == 5934L)

    println(part1(readInput("Day06")))
    println(part2(readInput("Day06")))
}
