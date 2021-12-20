import kotlin.math.abs

fun main() {

    fun loadScanner(input: List<String>, startLine: Int): Scanner? {
        if (startLine + 13 >= input.size) {
            return null
        }
        val scanner = input[startLine].replace("-", "").trim()
        val beacons = mutableListOf<RBeacon>()
        for (i in (startLine + 1).until(input.size)) {
            val beacon = RBeacon.from(input[i])
            if (beacon != null) {
                beacons.add(beacon)
            } else {
                break
            }
        }

        return Scanner(scanner, beacons)
    }

    fun load(input: List<String>): List<Scanner> {
        var startLine = 0
        val scanners = mutableListOf<Scanner>()
        do {
            val scanner = loadScanner(input, startLine)
            if (scanner != null) {
                scanners.add(scanner)
                startLine = startLine + scanner.beacons.size + 2
            }
        } while (scanner != null)

        return scanners
    }



    fun oneStepMappings(scanners: List<Scanner>): MutableList<Mapping> {
        var mappings = mutableListOf<Mapping>()
        for (i in 0.until(scanners.size)) {  //0.until
            for (j in (i + 1).until(scanners.size)) { //(i + 1).until
                val s0 = scanners[i]
                val s1 = scanners[j]

                val c = s0.common(s1)
                val u = Scanner.uniqueDistances(c)
                println("${s0.name} ${s1.name} ${c.size} ${u.size}")
                if (c.size >= Scanner.MIN_COMMON_PAIRS) {
                    val mapping = s0.findMappingFrom(s1, u)
                    mappings.add(mapping)
//                    val commonCount = s1.beacons.map {
//                        mapping.calc(it)
//                    }.filter {
//                        s0.beacons.contains(it)
//                    }.count()
//                    println("common after mapping = $commonCount")
                }
            }
        }
        return mappings
    }

    fun prepareMappings (scanners: List<Scanner>){
        val mappings = mutableMapOf<Pair<String,String>, Mapping>()
        val firstStep = oneStepMappings(scanners)
        firstStep.forEach { mappings.put(it.nameTo to it.nameFrom, it)  }
        firstStep.forEach {
            val reverseKey = it.nameFrom to it.nameTo
            if (!mappings.containsKey(reverseKey)) {
                mappings.put(reverseKey, it.reversed())
            }
        }
        val pairsToFind = scanners.filter{ it.name != scanners[0].name }
            .map { scanners[0].name to it.name }
        while(!mappings.keys.containsAll(pairsToFind)){


        }


    }

    fun composeMapping(nameTo: String, nameFrom: String, s: Scanner, mappings: MutableList<Mapping>) {
        if(nameTo == nameFrom) return
        val existing = mappings.firstOrNull{
            ((it.nameTo == nameTo) && (it.nameFrom == nameFrom))
                    || ((it.nameTo == nameFrom) && (it.nameFrom == nameTo))
        }
        if(existing!= null) {
            return
        }
        //mapping doesn't exist yet... compose one
        mappings.filter{ it.nameTo == nameTo || it.nameFrom == nameTo }
    }

    fun part1(input: List<String>): Int {
        val scanners = load(input)

        val mappings = oneStepMappings(scanners)
        val zeroName = scanners[0].name
        for(s in scanners){
            composeMapping(zeroName, s.name, s, mappings)
        }


        return 0
    }

    fun part2(input: List<String>): Int {

        return 0
    }


    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day19_test")) == 40)
//    check(part2(readInput("Day15_test")) == 315)
//
//    println(part1(readInput("Day15")))
//    println(part2(readInput("Day15")))
}

data class RBeacon(val x: Int, val y: Int, val z: Int) {

    companion object {
        fun from(line: String): RBeacon? {
            if (line.trim().isEmpty()) return null
            return line.trim()
                .split(",")
                .map { it.toInt() }
                .chunked(3)
                .map { RBeacon(it[0], it[1], it[2]) }
                .first()
        }
    }
}


class Scanner(val name: String, val beacons: List<RBeacon>) {
    override fun toString(): String {
        val buf = StringBuilder()
        buf.append(name)
        buf.append("\n")
        beacons.forEach {
            buf.append(it)
            buf.append("\n")
        }
        return buf.toString()
    }

    val distances: List<Distance>

    private fun distance(a: RBeacon, b: RBeacon): Triple<Int, Int, Int> {
        return listOf(
            abs(a.x - b.x),
            abs(a.y - b.y),
            abs(a.z - b.z)
        ).sorted().chunked(3).map { Triple(it[0], it[1], it[2]) }.first()
        // sort abs czy orientacja obrotowa?
    }

    init {
        val distanceList = mutableListOf<Distance>()
        for (i in 0.until(beacons.size))
            for (j in (i + 1).until(beacons.size)) {
                val a = beacons[i]
                val b = beacons[j]
                val d = distance(a, b)
                distanceList.add(Distance(d, a, b))
            }
        distances = distanceList
    }


    fun common(other: Scanner): List<Triple<Int, Int, Int>> {
        val commonDistances = mutableListOf<Triple<Int, Int, Int>>()
        val otherDistances = other.distances.map { it.dist }.toMutableList()
        distances.map { it.dist }.forEach {
            if (otherDistances.remove(it)) commonDistances.add(it)
        }
        return (commonDistances)//.size)//< MIN_COMMON_PAIRS)
    }

    fun vectorAbs(b0: RBeacon, b1: RBeacon) = Triple(
        abs(b1.x - b0.x),
        abs(b1.y - b0.y),
        abs(b1.z - b0.z)
    )

    fun vector(b0: RBeacon, b1: RBeacon) = Triple(
        (b1.x - b0.x),
        (b1.y - b0.y),
        (b1.z - b0.z)
    )

    private fun findMappingBy(s1: Scanner, uniqueDistance:Triple<Int, Int, Int>, uniques: Set<Triple<Int, Int, Int>>): Mapping {
        val d0 = distances.first { it.dist == uniqueDistance }
        val d1 = s1.distances.first { it.dist == uniqueDistance }
        val v0abs = vectorAbs(d0.b0, d0.b1)
        val v1abs = vectorAbs(d1.b0, d1.b1)
        val axisMapping = Mapping.findAxisMapping(v0abs, v1abs)
        //println("$v0abs -> ${axisMapping(v1abs)}")

        val v0 = vector(d0.b0, d0.b1)
        //which are same? d0.b0 matches d1.b0 or d1.b1...
        //lets assume d0.b0 ~ d1.b0, if not direction ,mapping retruns reverse= true
        val directionMapping = Mapping.findDirections(v0, vector(d1.b0, d1.b1), axisMapping)
        //println("$v0 -> ${directionMapping(v1)} was $v1")
        val reversedD1 = directionMapping.third
        val b0MatchInS1 = if (reversedD1) d1.b1 else d1.b0
        val completeMapping = Mapping.findTranslationMapping(d0.b0, b0MatchInS1, Pair(directionMapping.first, directionMapping.second))
//        println(completeMapping.first)
//        println("${d0.b0} -> ${completeMapping.second(b0MatchInS1)} was $b0MatchInS1")
//
//        val b1MatchInS1 = if (reversedD1) d1.b0 else d1.b1
//        println("${d0.b1} -> ${completeMapping.second(b1MatchInS1)} was $b1MatchInS1")

        val v1 = if(!reversedD1) vector(d1.b0, d1.b1) else vector(d1.b1, d1.b0)
        return Mapping(name, s1.name, completeMapping.second, v0, v1, d0.b0, b0MatchInS1)
    }

    fun findMappingFrom(s1: Scanner, uniqueDistances: List<Triple<Int, Int, Int>>): Mapping {
        val totalUnique = uniqueDistances.filter {
            ((it.first > 0)
                    && (it.first != it.second)
                    && (it.second != it.third)
                    && (it.third != it.first))
        }.toSet()
        println("---- ${name} ${s1.name} ----")
        val mappings = totalUnique.map { findMappingBy(s1, it, totalUnique) }
        return mappings[0]
    }


    companion object {
        const val MIN_COMMON_PAIRS = 66 // (12 by 2)

        fun uniqueDistances(list: List<Triple<Int, Int, Int>>): List<Triple<Int, Int, Int>> {
            val unique = list.distinct()
            if (unique.size == list.size) return list
            val multiple = list.toMutableList().also { it.removeAll(unique) }
            return unique.toMutableList().also { it.removeAll(multiple) }
        }
    }

}

data class Distance(val dist: Triple<Int, Int, Int>, val b0: RBeacon, val b1: RBeacon)

class Mapping(
    val nameTo: String, val nameFrom: String, val calc: (RBeacon) -> RBeacon,
    val v0: Triple<Int, Int, Int>, val v1: Triple<Int, Int, Int>,
    val b0: RBeacon, val b1: RBeacon
) {

    fun handleFromTo(nFrom: String, nTo: String): Mapping? {
        if (nameTo == nTo && nameFrom == nFrom) return this
        if (nameTo == nFrom && nameFrom == nTo) return reversed()
        return null
    }

    fun reversed(): Mapping {
        val v0abs = v0.let { Triple(abs(it.first), abs(it.second), abs(it.third)) }
        val v1abs = v1.let { Triple(abs(it.first), abs(it.second), abs(it.third)) }

        val axisMapping = Mapping.findAxisMapping(v1abs, v0abs)
        val directionMapping = Mapping.findDirections(v1, v0, axisMapping)
        val completeMapping = Mapping.findTranslationMapping(b1, b0, Pair(directionMapping.first, directionMapping.second))
        return Mapping(nameFrom, nameTo, completeMapping.second, v1, v0, b1, b0)
    }

    companion object {

        fun findAxisMapping(
            v0: Triple<Int, Int, Int>,
            v1: Triple<Int, Int, Int>
        ): Pair<String, (Triple<Int, Int, Int>) -> Triple<Int, Int, Int>> {
            fun selectFor(value: Int): Pair<String, (Triple<Int, Int, Int>) -> Int> =
                when (value) {
                    v1.first -> Pair("x", ::returnFirst)
                    v1.second -> Pair("y", ::returnSecond)
                    v1.third -> Pair("z", ::returnThird)
                    else -> throw IllegalArgumentException("Value $value not found in $v1")
                }

            val fx = selectFor(v0.first)
            val fy = selectFor(v0.second)
            val fz = selectFor(v0.third)

            val mapping: (Triple<Int, Int, Int>) -> Triple<Int, Int, Int> =
                { a -> Triple(fx.second(a), fy.second(a), fz.second(a)) }

            return "${fx.first},${fy.first},${fz.first}" to mapping
        }

        val ROTATIONS = listOf("x,y,z", "y,-x,z", "-x,-y,z", "-y,x,z",
        "x,-z,y", "x,-y,-z", "x,z,-y",
        "z,y,-x", "-x,y,-z", "-z,y,x",
        "y,-z,-x", "-x,-z, -y", "-y,-z,x",
            "y,z,x", "-y,z,-x",
            "y,x,-z", "-y,-x,-z").map { "($it)" }.toSet()

        /***
         * returns
         *  directionsChange as string, eg (-x, y,-z) or (x,z,-y)
         *  function to change (rotation)
         *  reversed = true when input v1 was reversed because of improper reverse vector
         */
        fun findDirections(
            v0: Triple<Int, Int, Int>,
            v1: Triple<Int, Int, Int>,
            axisMapping: Pair<String, (Triple<Int, Int, Int>) -> Triple<Int, Int, Int>>
        ): Triple<String, (Triple<Int, Int, Int>) -> Triple<Int, Int, Int>, Boolean> {
            val v1Rotated = axisMapping.second(v1)
            fun selectFor(expected: Int, value: Int): Pair<String, (Int) -> Int> =
                when (value) {
                    expected -> "" to ::signPass
                    else -> "-" to ::signChange
                }

            val fx = selectFor(v0.first, v1Rotated.first)
            val fy = selectFor(v0.second, v1Rotated.second)
            val fz = selectFor(v0.third, v1Rotated.third)

            val mapping: (Triple<Int, Int, Int>) -> Triple<Int, Int, Int> = { a ->
                val rotated = axisMapping.second(a)
                Triple(fx.second(rotated.first), fy.second(rotated.second), fz.second(rotated.third))
            }
            val description = axisMapping.first.split(",").chunked(3)
                .map { "(${fx.first}${it[0]},${fy.first}${it[1]},${fz.first}${it[2]})" }
                .first()
            if(description !in ROTATIONS) {
                //println("$description not in rotations")
                return findDirections(v0,Triple(-v1.first, -v1.second,-v1.third ),axisMapping).let{
                    Triple(it.first, it.second, !it.third)
                }
            }

            return Triple(description, mapping, false)
        }

        fun findTranslationMapping(
            b0: RBeacon,
            b1: RBeacon,
            directionMapping: Pair<String, (Triple<Int, Int, Int>) -> Triple<Int, Int, Int>>
        ): Pair<String, (RBeacon) -> RBeacon> {
//            val preparedB1 = directionMapping(
//                b1.let { Triple(it.x, it.y, it.z) }
//            ).let { RBeacon(it.first, it.second, it.third) }

            //  value -> expected
            //  value -> value - ( value - expected)
//            fun selectFor(expected: Int, value: Int): (Int) -> Int =
//                { it - (value - expected) }
//
//            val fx = selectFor(b0.x, preparedB1.x)
//            val fy = selectFor(b0.y, preparedB1.y)
//            val fz = selectFor(b0.z, preparedB1.z)

            fun signed(nnn: Int) = if (nnn >= 0) "+$nnn" else "$nnn"

            //translate by v1 to 0,0,0, rotate, translate by v0 to xyz
            val mapping: (RBeacon) -> RBeacon = {

                directionMapping.second(
                    it.let { Triple(it.x - b1.x, it.y - b1.y, it.z - b1.z) }
                ).let {
                    Triple(it.first + b0.x, it.second + b0.y, it.third + b0.z)
                }.let { RBeacon(it.first, it.second, it.third) }
                //RBeacon(fx(preparedA.x), fy(preparedA.y), fz(preparedA.z))
            }
            val description =
                "(x${signed(-b1.x)},y${signed(-b1.y)},z${signed(-b1.z)})\n${directionMapping.first}\n(x${signed(b0.x)},y${
                    signed(b0.y)
                },z${signed(b0.z)})"
//            val description =
//                "${directionMapping.first}"
            return description to mapping
        }

    }


}

fun signPass(a: Int) = a
fun signChange(a: Int) = -a

fun returnFirst(a: Triple<Int, Int, Int>) = a.first
fun returnSecond(a: Triple<Int, Int, Int>) = a.second
fun returnThird(a: Triple<Int, Int, Int>) = a.third

/*
scanner 0 scanner 1 66
scanner 1 scanner 3 66
scanner 1 scanner 4 66
scanner 2 scanner 4 66
------
scanner 0 scanner 2 3
scanner 0 scanner 3 0
scanner 0 scanner 4 15
scanner 1 scanner 2 15
scanner 3 scanner 4 15
scanner 2 scanner 3 3
 */