class Dijkstra(val grid: Array<IntArray>, val multiply: Int) {
    val width = grid[0].size
    val height = grid.size

    val explored = mutableSetOf<Pair<Int, Int>>()
    val frontier = mutableSetOf<Pair<Int, Int>>()

    fun gridValue(cords: Pair<Int, Int>): Int {
        val row = cords.first % height
        val col = cords.second % width
        return (grid[row][col] + cords.first / height + cords.second / width).let { if (it > 9) it - 9 else it }
    }

    private val distanceToNodeField =
        Array(multiply * height) { rowIdx -> IntArray(multiply * width) { Int.MAX_VALUE } }

    fun distanceToNode(node: Pair<Int, Int>): Int = distanceToNodeField[node.first][node.second]

    fun distanceToNode(node: Pair<Int, Int>, value: Int) {
        distanceToNodeField[node.first][node.second] = value
    }


    fun minPathDijkstra(): Int {
        val startNode = Pair(0, 0)
        val endNode = Pair(multiply * height - 1, multiply * width - 1)

        distanceToNode(startNode, 0)

        frontier.add(startNode)
        while (frontier.isNotEmpty()) {
            val nearestNode = frontier.minByOrNull { node -> distanceToNode(node) }!!// priority queue :)
            frontier.remove(nearestNode)
            explored.add(nearestNode)

            if (nearestNode == endNode) {
                return distanceToNode(nearestNode)
            }

            neighbours(nearestNode).filter { it !in explored }
                .forEach {
                    val alt = distanceToNode(nearestNode) + gridValue(it)
                    if (alt < distanceToNode(it)) {
                        distanceToNode(it, alt)
                    }
                    if (it !in frontier) {
                        frontier.add(it)
                    }
                }
        }
        return 0
    }

    private fun neighbours(node: Pair<Int, Int>) = sequenceOf(
        node.first - 1 to node.second,
        node.first + 1 to node.second,
        node.first to node.second - 1,
        node.first to node.second + 1
    ).filter { it.first >= 0 && it.first < multiply * height }
        .filter { it.second >= 0 && it.second < multiply * width }

}

fun main() {
    fun loadGrid(input: List<String>): Array<IntArray> {
        val width = input[0].trim().length
        val height = input.size
        val grid = Array(height) { lineIdx ->
            IntArray(width) { colIdx ->
                Character.getNumericValue(input[lineIdx][colIdx])
            }
        }
        return grid
    }

    fun part1(input: List<String>): Int {
        val grid = loadGrid(input)
        val dijkstra = Dijkstra(grid, 1)
        val result = dijkstra.minPathDijkstra()

        return result
    }

    fun part2(input: List<String>): Int {
        val grid = loadGrid(input)
        val dijkstra = Dijkstra(grid, 5)
        val result = dijkstra.minPathDijkstra()

        return result
    }

    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day15_test")) == 40)
    check(part2(readInput("Day15_test")) == 315)

    println(part1(readInput("Day15")))
    println(part2(readInput("Day15")))
}
