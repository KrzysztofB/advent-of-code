private val ALL_SEGMENTS = setOfChars("ABCDEFG")
private fun mutableSegments(): MutableSet<Char> {
    val s = mutableSetOf<Char>()
    s.addAll(ALL_SEGMENTS)
    return s
}

private fun setOfStrings(chars: String): Set<String> {
    return chars.split("").toSet()
}

private fun setOfChars(chars: String): Set<Char> {
    val charArray = chars.toCharArray().toTypedArray()

    return setOf(*charArray)
}

private val digitSegments = mapOf<String, Set<Char>>(
    "0" to setOfChars("ABCEFG"),
    "1" to setOfChars("CF"),
    "2" to setOfChars("ACDEG"),
    "3" to setOfChars("ACDFG"),
    "4" to setOfChars("CFBD"),
    "5" to setOfChars("ABDFG"),
    "6" to setOfChars("ABDEFG"),
    "7" to setOfChars("CFA"),
    "8" to setOfChars("ABCDEFG"),
    "9" to setOfChars("ABCDFG")
)

private val segmentCountPossibleDigits = digitSegments.toList().groupBy { it.second.size }

fun main() {

    fun taskPatterns(line: String): List<String> = line.split("|")[1].trim().split(" ")

    fun countUniqueLength(patterns: List<String>): Int {
        val uniqueLengths = setOf<Int>(2, 4, 3, 7)//panels in digits: 1,4,7,8
        val result = patterns
            .map { it.length }
            .filter { it in uniqueLengths }
            .count()
        return result
    }

    fun part1(input: List<String>): Int {
        val result = input
            .map { taskPatterns(it) }
            .map { countUniqueLength(it) }
            .sum()
        return result
    }
/*
10 cyfr
segmenty A..G
1: C,F
7: C,F,A
4: B,C,D,F
8: A..G

2[5]: A,C,D,E,G
3[5]: A,C,D,F,G
5[5]: A,B,D,F,G
3 jednakowe A,D,G różnica pojedyncza to B lub F
mamy dwie - to jeśli pozostałe 2 różne, to 2 i 5

0[6]: A,B,C, E,F,G
6[6]: A,B,D  E,F,G
9[6]: A,B,C, D,F,G
4 jednakowe ABFG różnica pojedyncza to D
mamy dwie

zapalone segmenty
A: 0,2,3,5,6,7,8,9 (brak w 1,4)
B: 0, 4, 5, 5, 8, 9

4 i 9 = 4 wspólne
4 i (6/0) = 3 wspólne

1 i 6 = 1 wspólny
1 i (0/9) = 2 wspólne

4 i 2 = 2 wspólne
4 i 3 = 3 wspólne
4 i 5 = = 3 wspólne

1 i 2 = 1 wspólny
1 i 3 = 2 wspólne
1 i 5 = 1 wspólny

7 i 2 = 2 wspólny
7 i 3 = 3 wspólne
7 i 5 = 2 wspólny

1 i 4 = 2 wspólne

1 i 7 = 2 wspólne

po znalezieniu segmentów można wygenerować nowe patterny?

 */




    fun decodeAnswer(line: String): Int {
        val (entryPatterns, outputPatterns) = line
            .split("|")
            .map { it.trim().split(" ") }
            .map { it.map { it.toCharArray().toSet() } }
        val allPatterns = (entryPatterns + outputPatterns).toSet()

        val wireMapping = mapOf<Char, MutableSet<Char>>(
            'a' to mutableSegments(),
            'b' to mutableSegments(),
            'c' to mutableSegments(),
            'd' to mutableSegments(),
            'e' to mutableSegments(),
            'f' to mutableSegments(),
            'g' to mutableSegments()
        )

        val decoders = allPatterns.map { it -> PatternDecoder(it, wireMapping) }
        do {
            val patternsReduced = decoders.any { it.reduceOptions() }
            var digitsReduced = false
            while (MultiplePatternLogic.reduceDigits(decoders)) {
                digitsReduced = true
            }
            var wiresReduced = false
            while (MultiplePatternLogic.reduceWires(wireMapping)) {
                wiresReduced = true
            }

        } while (patternsReduced || digitsReduced || wiresReduced)

        val xx = outputPatterns.map { op -> decoders.firstOrNull { it.pattern == op } }
            .map { if (it == null || it.possibleDigits.size != 1) "NULL" else "${it.possibleDigits.first()}" }
        val digitString = xx.joinToString(separator = "")
        return digitString.toInt()
    }

    fun part2(input: List<String>): Int {
        return input.map { decodeAnswer(it) }.sum()
    }

    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day08_test")) == 26)
    check(part2(readInput("Day08_test")) == 61229)

    println(part1(readInput("Day08")))
    println(part2(readInput("Day08")))
}


private class PatternDecoder(val pattern: Set<Char>, private val wireMapping: Map<Char, MutableSet<Char>>) {
    val possibleDigits = mutableSetOf<String>()
    val possibleSegments = mutableSetOf<Char>()
    val excludedSegments = mutableSetOf<Char>()

    init {
        possibleDigits.addAll(segmentCountPossibleDigits[pattern.size]!!.map { it.first })
        possibleDigits.forEach {
            possibleSegments.addAll(digitSegments.getOrDefault(it, emptySet()))
        }
        excludedSegments.addAll(ALL_SEGMENTS.subtract(possibleSegments))
        reduceOptions()
    }

    private fun wireMappingFromSegments(): Boolean {
        //from each mapping wire -> possible segments
        // remove segments not in all possible segments
        return wireMapping.filter { it.key in pattern }
            .map { it.value }
            .fold(false) { changedAcc: Boolean, segments: MutableSet<Char> ->
                segments.removeIf { it !in possibleSegments } || changedAcc
            }
    }


    private fun segmentsFromWireMapping(): Boolean {
        // collect all segments in pattern wires
        // remove from possible pattersne these not in collected
        val segmentsUpdate = pattern.fold(emptySet<Char>()) { accSegments: Set<Char>, patternChar: Char ->
            accSegments union wireMapping.getOrDefault(patternChar, emptySet())
        }
        return possibleSegments.removeIf { !segmentsUpdate.contains(it) }
    }

    private fun digitsFromSegments(): Boolean {
        // remove item from possible digits if possible segments don't allow to light it up
        val digitsChanged = possibleDigits.removeIf {
            !possibleSegments.containsAll(digitSegments[it]!!)
        }
        if (digitsChanged) {
            segmentsFromDigits()
        }
        return digitsChanged
    }

    private fun segmentsFromDigits(): Boolean {
        val allSegments = possibleDigits.map {
            digitSegments.getOrDefault(it, emptySet())
        }
            .reduce { acc: Set<Char>, digitSegments: Set<Char> -> acc union digitSegments }
        return possibleSegments.removeIf { it !in allSegments }
    }

    fun reduceOptions(): Boolean {
        val change1 = wireMappingFromSegments()
        val change2 = segmentsFromWireMapping()
        val change3 = digitsFromSegments()
        return change1 || change2 || change3
    }

    fun removeDigits(guessedDigits: Set<String>): Boolean {
        val changed = possibleDigits.removeIf { it in guessedDigits }
        check(possibleDigits.isNotEmpty(), { "no digits for $pattern" })
        if (changed) segmentsFromDigits()
        return changed
    }
}

private object MultiplePatternLogic {


    private fun fiveSegmentsPossible25(decoders: List<PatternDecoder>): Boolean {
        if (decoders.size != 2) return false
        val diffSize = decoders[0].pattern.subtract(decoders[1].pattern).size
        if (diffSize != 2) return false
        val without25 = setOf("0", "1", "3", "4", "6", "7", "8", "9")

        val change0 = decoders[0].removeDigits(without25)
        val change1 = decoders[1].removeDigits(without25)

        return change0 || change1
    }

    private fun decoderFor(digit: String, decoders: List<PatternDecoder>): List<PatternDecoder> {
        return decoders.filter { it.possibleDigits.size == 1 && it.possibleDigits.first() == digit }
    }

    private fun fiveSegmentsWith1(pattern1: Set<Char>, decoders: List<PatternDecoder>): Boolean {
//                1 i 2 = 1 wspólny segment
//                1 i 3 = 2 wspólne
//                1 i 5 = 1 wspólny
        return decoders.filter { it.pattern.size == 5 }
            .fold(false) { accChanged: Boolean, decoder: PatternDecoder ->
                when (commonCount(pattern1, decoder.pattern)) {
                    2 -> decoder.removeDigits(setOfStrings("012456789"))
                    1 -> decoder.removeDigits(setOfStrings("01346789"))
                    else -> false
                }
            }
    }

    private fun fiveSegmentsWith7(pattern7: Set<Char>, decoders: List<PatternDecoder>): Boolean {
//                7 i 2 = 2 wspólne segmenty
//                7 i 3 = 3 wspólne
//                7 i 5 = 2 wspólne
        return decoders.filter { it.pattern.size == 5 }
            .fold(false) { accChanged: Boolean, decoder: PatternDecoder ->
                when (commonCount(pattern7, decoder.pattern)) {
                    3 -> decoder.removeDigits(setOfStrings("012456789"))
                    2 -> decoder.removeDigits(setOfStrings("01346789"))
                    else -> false
                }
            }
    }

    private fun commonCount(patternA: Set<Char>, patternB: Set<Char>): Int =
        (patternA intersect patternB).size


    private fun reduce235(decoders: List<PatternDecoder>): Boolean {
        val toProcess = decoders.filter { it.pattern.size == 5 }

        val change25 = fiveSegmentsPossible25(toProcess)

        val change235with1 = decoderFor("1", decoders).map {
            fiveSegmentsWith1(it.pattern, decoders)
        }.reduce { acc, item -> acc || item }

        val change235with7 = decoderFor("7", decoders).map {
            fiveSegmentsWith7(it.pattern, decoders)
        }.reduce { acc, item -> acc || item }

        return change25 || change235with1 || change235with7
    }

    fun reduceDigits(decoders: List<PatternDecoder>): Boolean {
        val (guessedDigitDecoders, unknownDigitDecoders) = decoders.partition { it.possibleDigits.size == 1 }
        val guessedDigits = guessedDigitDecoders.map { it.possibleDigits.first() }.toSet()

        val changeFromGuessed = unknownDigitDecoders.fold(false) { accChanged: Boolean, decoder: PatternDecoder ->
            decoder.removeDigits(guessedDigits) || accChanged
        }

        val change235 = reduce235(decoders)
        return changeFromGuessed || change235
    }

    fun reduceWires(wireMapping: Map<Char, MutableSet<Char>>): Boolean {
        val guessedSegments = wireMapping.filter { it.value.size == 1 }.map { it.value.first() }.toSet()

        return if (guessedSegments.isEmpty()) false
        else wireMapping.filter { it.value.size > 1 }
            .map { it.value }
            .fold(false) { accChanged: Boolean, possibleSegments: MutableSet<Char> ->
                possibleSegments.removeIf { it in guessedSegments } || accChanged
            }
    }

}