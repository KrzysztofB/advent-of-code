fun main() {
    fun loadGraph(input: List<String>): Map<String, Node> {
        val nodes = mutableMapOf<String, Node>()
        for (line in input) {
            val (name1, name2) = line.trim().split("-")
            val node1 = nodes.getOrPut(name1, { Node(name1) })
            val node2 = nodes.getOrPut(name2, { Node(name2) })
            node1.link(node2)
            node2.link(node1)
        }
        return nodes.toMap()
    }

    fun part1(input: List<String>): Int {
        val nodes = loadGraph(input)

        val startNode = nodes["start"]!!

        val foundPaths = mutableListOf<List<Node>>()
        startNode.findPaths(foundPaths)
        return foundPaths.size
    }

    fun part2(input: List<String>): Int {
        val nodes = loadGraph(input)

        val startNode = nodes["start"]!!

        val foundPaths = mutableListOf<List<Node>>()
        startNode.findPaths2(foundPaths)

        return foundPaths.size
    }

    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day12_test")) == 10)
//    check(part1(readInput("Day12_test2")) == 19)
//    check(part1(readInput("Day12_test3")) == 226)

    check(part2(readInput("Day12_test")) == 36)
//    check(part2(readInput("Day12_test2")) == 103)
//    check(part2(readInput("Day12_test3")) == 3509)

    println(part1(readInput("Day12")))
    println(part2(readInput("Day12")))
}

class Node(val name: String) {
    private val links = mutableListOf<Node>()

    override fun toString(): String {
        return name
    }

    fun link(linkedNode: Node) {
        links.add(linkedNode)
    }

    fun findPaths(foundPaths: MutableList<List<Node>>, path: MutableList<Node> = mutableListOf<Node>()) {
        path.add(this)
        try {
            if (isEndNode(this)) {
                foundPaths.add(path.toList())
                //printPath(path)
                return
            }
            for (nextNode in links.filter { canBeVisited(it, path) }) {
                nextNode.findPaths(foundPaths, path)
            }
        } finally {
            path.removeLast()
        }
    }

    fun findPaths2(foundPaths: MutableList<List<Node>>, path: MutableList<Node> = mutableListOf<Node>()) {
        path.add(this)
        try {
            if (isEndNode(this)) {
                foundPaths.add(path.toList())
                //printPath(path)
                return
            }
            for (nextNode in links.filter { canBeVisited2(it, path) }) {
                nextNode.findPaths2(foundPaths, path)
            }
        } finally {
            path.removeLast()
        }
    }


    companion object {
        fun pathToString(path: List<Node>): String =
            path.map { it.name }.joinToString("-")

        fun printPath(path: List<Node>) =
            println(pathToString(path))

        fun isStartNode(node: Node) = node.name == "start"
        fun isEndNode(node: Node) = node.name == "end"
        fun isSingleVisitNode(node: Node) = node.name.first().isLowerCase()
        fun isMultiVisitNode(node: Node) = node.name.first().isUpperCase()

        fun canBeVisited(node: Node, path: MutableList<out Node>) =
            !(isSingleVisitNode(node) && path.contains((node)))

        fun canBeVisited2(node: Node, path: List<Node>): Boolean = when {
            isMultiVisitNode(node) -> true
            isStartNode(node) -> false
            path.contains(node) && wasSmallVisitedTwice(path) -> false
            else -> true
        }

        private fun wasSmallVisitedTwice(path: List<Node>): Boolean {
            return path.asSequence()
                .map { it.name }
                .filter { it.first().isLowerCase() }
                .sorted()
                .fold(0 to "") { acc: Pair<Int, String>, s: String ->
                    if (acc.second == s)
                        (acc.first + 1) to s
                    else
                        acc.first to s
                }.first > 0
        }
    }
}