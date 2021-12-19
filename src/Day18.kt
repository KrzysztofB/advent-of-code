
private enum class Side { LEFT, RIGHT }

private class Explosion {
    var leftDigitPair: SnailPair? = null
    var leftDigitOn: Side? = null
    var explosionParent: SnailPair? = null
    var explosionOn: Side? = null
    var rightDigitPair: SnailPair? = null
    var rightDigitOn: Side? = null

    fun shouldUpdateLeftDigit() = (explosionParent == null) && (explosionOn == null)
    fun shouldUpdateRightDigit() = (explosionParent != null) && (rightDigitPair == null)

    fun isExplosionEmpty() = (explosionParent == null) && (explosionOn == null)
    fun isRightEmpty() = (rightDigitPair == null) && (rightDigitOn == null)


}

private interface SnailNum {
    val isDigit: Boolean
    var left: SnailNum
    var right: SnailNum

    fun magnitude():Int
    fun copy(): SnailNum
    fun toString(builder: StringBuilder)
}

private class SnailPair(override var left: SnailNum, override var right: SnailNum) : SnailNum {
    override fun toString(builder: StringBuilder) {
        builder.append("[")
        left.toString(builder)
        builder.append(",")
        right.toString(builder)
        builder.append("]")
    }


    override fun toString(): String {
        val builder = StringBuilder()
        toString(builder)
        return builder.toString()
    }

    companion object {
        fun parse(allText: String, statIndex: Int = 0): Pair<SnailNum, Int> {
            if (allText[statIndex] != '[') return SnailValue.parse(allText, statIndex)

            val (left, commaPos) = parse(allText, statIndex + 1)
            check(allText[commaPos] == ',')
            val (right, endPos) = parse(allText, commaPos + 1)
            check(allText[endPos] == ']')
            return SnailPair(left, right) to (endPos + 1)
        }
    }

    override val isDigit: Boolean
        get() = false

    override fun magnitude(): Int =
       ( left.magnitude()*3 + right.magnitude()*2)

    override fun copy(): SnailNum = SnailPair(left.copy(), right.copy())


    fun explode(nestedIn: Int = 0): Boolean {
        val explosion = Explosion()
        traverseSlimes(nestedIn, explosion, this, Side.LEFT)// side has no meaning here
        if (explosion.explosionParent != null) {
            val exploded = replaceExploded(explosion.explosionParent!!, explosion.explosionOn)
            addExploded(explosion.leftDigitPair, explosion.leftDigitOn, exploded.left as SnailValue)
            addExploded(explosion.rightDigitPair, explosion.rightDigitOn, exploded.right as SnailValue)
            return true
        }
        return false
    }

    private fun trySplitValue(side: Side): Boolean {
        val childNode = if (side == Side.LEFT) left else right
        if (childNode.isDigit) {
            val toSplit = (childNode as SnailValue).value
            if (toSplit > 9) {
                val leftVal = SnailValue(toSplit / 2)
                val rightVal = SnailValue(toSplit / 2 + toSplit % 2)
                val newNode = SnailPair(leftVal, rightVal)
                if (side == Side.LEFT) left = newNode else right = newNode
                return true
            }
            return false
        } else {
            return (childNode as SnailPair).findAndSplit()
        }
    }

    private fun findAndSplit(): Boolean {
        return trySplitValue(Side.LEFT) || trySplitValue(Side.RIGHT)
    }


    fun split(): Boolean {
        return findAndSplit()
    }

    private fun addExploded(parent: SnailPair?, side: Side?, value: SnailValue) {
        if (parent != null && side != null) {
            if (side == Side.LEFT)
                parent.left = SnailValue((parent.left as SnailValue).value + value.value)
            else
                parent.right = SnailValue((parent.right as SnailValue).value + value.value)
        }
    }

    private fun replaceExploded(explosionParent: SnailPair, explosionOn: Side?): SnailPair {
        val toExplode = if (explosionOn == Side.LEFT)
            explosionParent.left
        else
            explosionParent.right
        val zeroNode = SnailValue(0)
        if (explosionOn == Side.LEFT)
            explosionParent.left = zeroNode
        else
            explosionParent.right = zeroNode
        return toExplode as SnailPair
    }

    private fun examineValue(nestedIn: Int, explosion: Explosion, side: Side) {
        val childNode = if (side == Side.LEFT) {
            left
        } else {
            right
        }
        if (childNode.isDigit) {
            if (explosion.shouldUpdateLeftDigit()) {
                explosion.leftDigitPair = this
                explosion.leftDigitOn = side
            } else if (explosion.shouldUpdateRightDigit()) {
                explosion.rightDigitPair = this
                explosion.rightDigitOn = side

            }
        } else {
            (childNode as SnailPair).traverseSlimes(nestedIn + 1, explosion, this, side)
        }
    }

    private fun traverseSlimes(nestedIn: Int, explosion: Explosion, parent: SnailPair, side: Side) {
        if (!explosion.isRightEmpty()) return

        if (nestedIn < 4 || !explosion.isExplosionEmpty()) {
            // save left number in case of explosion
            examineValue(nestedIn, explosion, Side.LEFT)
            examineValue(nestedIn, explosion, Side.RIGHT)
        } else {
            check(left.isDigit)
            check(right.isDigit)
            if (explosion.isExplosionEmpty()) {
                explosion.explosionParent = parent
                explosion.explosionOn = side
            }
        }
    }
}

private class SnailValue(val value: Int) : SnailNum {

    override val isDigit: Boolean
        get() = true
    override var left: SnailNum
        get() = TODO("Not yet implemented")
        set(_) {}
    override var right: SnailNum
        get() = TODO("Not yet implemented")
        set(_) {}

    override fun magnitude(): Int = value
    override fun copy(): SnailNum = SnailValue(value)


    override fun toString(builder: StringBuilder) {
        builder.append(value)
    }

    companion object {
        fun parse(allText: String, startIndex: Int): Pair<SnailNum, Int> {
            return SnailValue(allText.substring(startIndex, startIndex + 1).toInt()) to startIndex + 1
        }
    }
}

fun main() {

    fun loadSnailNumbers(input: List<String>): List<SnailNum> {
        return input.map {
            SnailPair.parse(it.trim()).first
        }
    }

    fun add(a: SnailNum, b:SnailNum): SnailNum{
        val added = SnailPair(a, b)
        //println("$added added")

        do {
            var changed = false
            while (added.explode()) {
                //println("$added exploded")
                changed = true
            }
            //println("$added exploded loop ended")
            if (added.split()) {
                //println("$added split")
                changed = true
            }
        } while (changed)
        return added
    }

    fun sumSnailNumbers(input: List<String>): SnailNum {
        val numbers = loadSnailNumbers(input)
        //numbers.forEach { println(it) }
        //println("-----------------------")
        var sum = numbers[0]
        for (idx in 1 until numbers.size) {
            sum = add(sum, numbers[idx])
        }
        return sum
    }


    fun part1(input: List<String>): Int {
        val num = sumSnailNumbers(input)

        return num.magnitude()
    }

    fun part2(input: List<String>): Int {
        val numbers = loadSnailNumbers(input)
        var maxMag = Int.MIN_VALUE
        for( a in numbers)
            for(b in numbers){
                if( a===b ) continue
                val c = add(a.copy(),b.copy())
                val cMag =  c.magnitude()
                if (cMag>maxMag) maxMag = cMag
            }
        return maxMag
    }

    fun preTest() {
        //part1(listOf("[[[[4,3],4],4],[7,[[8,4],9]]]","[1,1]"))
        check(
            sumSnailNumbers(
                listOf(
                    "[1,1]",
                    "[2,2]",
                    "[3,3]",
                    "[4,4]"
                )
            ).toString() == "[[[[1,1],[2,2]],[3,3]],[4,4]]"
        )
        check(
            sumSnailNumbers(
                listOf(
                    "[1,1]",
                            "[2,2]",
                            "[3,3]",
                            "[4,4]",
                            "[5,5]"
                )
            ).toString() == "[[[[3,0],[5,3]],[4,4]],[5,5]]"
        )
        check(
            sumSnailNumbers(
                listOf(
                    "[1,1]",
                    "[2,2]",
                    "[3,3]",
                    "[4,4]",
                    "[5,5]",
                    "[6,6]"
                )
            ).toString() == "[[[[5,0],[7,4]],[5,5]],[6,6]]"
        )
        check(
            sumSnailNumbers(
                listOf(
                    "[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]",
                            "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]",
                            "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]",
                            "[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]",
                            "[7,[5,[[3,8],[1,4]]]]",
                            "[[2,[2,2]],[8,[8,1]]]",
                            "[2,9]",
                            "[1,[[[9,3],9],[[9,0],[0,7]]]]",
                            "[[[5,[7,4]],7],1]",
                            "[[[[4,2],2],6],[8,7]]"
                )
            ).toString() == "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]"
        )
    }

    //preTest()

// test if implementation meets criteria from the description, like:

    check(part1(readInput("Day18_test")) == 4140)
    check(part2(readInput("Day18_test")) == 3993)

    println(part1(readInput("Day18")))
    println(part2(readInput("Day18")))
}
