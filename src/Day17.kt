import java.lang.Math.ceil
import java.lang.Math.sqrt

data class TargetArea(val x0: Int, val x1: Int, val top: Int, val bottom: Int)
data class VSpeed(val initial: Int, val stepsToTarget: Int, val fallDown: Boolean)


fun main() {

    fun rangeVx(x0: Int, x1: Int): IntRange {
        //minx from square equation n2/2 +n/2 -x0-1
        val delta = 0.25 - 2.0 * (-x0 - 1.0)//b^2 - 4ac
        val startVx = ceil(-0.5 + sqrt(delta)).toInt()//(-b+sqrt(delta))/2a
        return IntRange(startVx, x1 + 1)//startx to x1
    }

    fun readArea(input: List<String>): TargetArea {
        val numbers = input[0].replace("..", ",")
            .replace("target area: x=", "")
            .replace(" y=", "")
            .split(",")
            .map { it.toInt() }
        val (x0, x1, y0, y1) = numbers
        return if (y0 < y1) TargetArea(x0, x1, y1, y0) else TargetArea(x0, x1, y0, y1)
    }


    fun calcXStepsToHit(startVx: Int, x0: Int, x1: Int): List<VSpeed> {
        val result = mutableListOf<VSpeed>()
        var distance = 0
        var dx = startVx
        var steps = 0
        do {
            steps++
            distance += dx
            dx--
            if (x0 <= distance && distance <= x1) {
                result.add(VSpeed(startVx, steps, dx == 0))
            }
            if (dx == 0) break
        } while (distance <= x1)
        return result
    }

    fun calcVyAndTop(startVy: Int, top: Int, bottom: Int, stepsToHit: List<Pair<Int, Boolean>>): Pair<Int, Int>? {
        var height = 0
        var dy = startVy
        var steps = 0
        var maxY = height
        val stepIter = stepsToHit.iterator()
        var stepToReachPair = stepIter.next()
        var stepToReach = stepToReachPair.first
        var fallsDown = stepToReachPair.second
        do {
            steps++
            height += dy
            if (height > maxY) maxY = height
            dy--
            if (steps == stepToReach || (fallsDown && steps > stepToReach)) {
                if (height >= bottom && height <= top) {
                    return startVy to maxY
                }
                if (!fallsDown && steps == stepToReach) {
                    if (!stepIter.hasNext()) {
                        break
                    }
                    stepToReachPair = stepIter.next()
                    stepToReach = stepToReachPair.first
                    fallsDown = stepToReachPair.second
                }
            }
        } while (height > bottom)
        return null
    }


    fun calcDxDy(startVy: Int, top: Int, bottom: Int, stepsToHit: List<Pair<Int, List<VSpeed>>>): Set<Pair<Int, Int>> {
        val result = mutableSetOf<Pair<Int, Int>>()
        val fallingDown = mutableSetOf<Int>()
        var height = 0
        var dy = startVy
        var steps = 0
        val stepIter = stepsToHit.iterator()
        var stepToReachPair = stepIter.next()
        var stepToReach: Int? = stepToReachPair.first
        var fallsDown = stepToReachPair.second.any { it.fallDown == true }
        fallingDown.addAll(stepToReachPair.second.filter { it.fallDown == true }.map { it.initial })
        do {
            steps++
            height += dy
            dy--
            if (steps == stepToReach) {
                if (height >= bottom && height <= top) {
                    stepToReachPair.second.map { it.initial }.forEach { result.add(it to startVy) }
                    fallingDown.forEach { result.add(it to startVy) }
                }
                if (!stepIter.hasNext()) {
                    stepToReach = null
                } else {
                    stepToReachPair = stepIter.next()
                    stepToReach = stepToReachPair.first
                    fallsDown = fallsDown || stepToReachPair.second.any { it.fallDown == true }
                    fallingDown.addAll(stepToReachPair.second.filter { it.fallDown == true }.map { it.initial })
                }
            } else if (fallingDown.isNotEmpty()) {
                if (height >= bottom && height <= top) {
                    fallingDown.forEach { result.add(it to startVy) }
                }
            }
        } while (height > bottom)
        return result
    }

    fun part1(input: List<String>): Int {
        val area: TargetArea = readArea(input)
        val lastVxAndStepsToHit = rangeVx(area.x0, area.x1)
            .flatMap { calcXStepsToHit(it, area.x0, area.x1) }
        val stepsToHitAndDx = lastVxAndStepsToHit
            .groupBy { it.stepsToTarget }
            .map { entry -> entry.key to entry.value.fold(false) { acc: Boolean, i: VSpeed -> acc || i.fallDown } }

        val stepsToHit = stepsToHitAndDx.sortedBy { it.first }
        val vyFromTo = IntRange(area.bottom, 100)
        val vyToHit = vyFromTo.mapNotNull { calcVyAndTop(it, area.top, area.bottom, stepsToHit) }

        return vyToHit.map { it.second }.maxOf { it }
    }

    fun part2(input: List<String>): Int {

        val area: TargetArea = readArea(input)
        val lastVxAndStepsToHit = rangeVx(area.x0, area.x1)
            .flatMap { calcXStepsToHit(it, area.x0, area.x1) }
        val stepsToHitAndDx = lastVxAndStepsToHit
            .groupBy { it.stepsToTarget }
            .toList()

        val stepsToHit = stepsToHitAndDx.sortedBy { it.first }
        val vyFromTo = IntRange(area.bottom, 100)
        val dxdy = vyFromTo
            .flatMap { calcDxDy(it, area.top, area.bottom, stepsToHit) }
            .toSet()


        return dxdy.size
    }


    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day17_test")) == 45)
    check(part2(readInput("Day17_test")) == 112)

    println(part1(readInput("Day17")))
    println(part2(readInput("Day17")))
}
