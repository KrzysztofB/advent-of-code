fun main() {
    fun load(input: List<String>): Pair<String,Map<String,Char>> {
        val template = input[0].trim()

        val insertionRules = mutableMapOf<String,Char>()
        for(lineIdx in 2.until(input.size)){
            val line = input[lineIdx]
            val (ruleFrom,ruleTo) = line.replace(" -> ",":").split(":")
            insertionRules.put(ruleFrom, ruleTo[0])
        }
        return template to insertionRules
    }

    fun <T> incCount( _key:T, value:Long?) = if(value==null) 1L else value+1L

    fun doubledFinalCount(template: String, pairCount: MutableMap<String, Long>):Map<Char,Long> {
        val charCounter = (pairCount.asSequence()
            .flatMap { sequenceOf(it.key[0] to it.value, it.key[1] to it.value) }
                + sequenceOf(template.first() to 1L, template.last() to 1L))
            .groupingBy { it.first }
            .fold(0L, { acc:Long, charCount: Pair<Char,Long> -> acc+charCount.second} )
        return charCounter
    }

    fun finalResult(charCounts: Map<Char,Long>): Long {
        val sorted = charCounts.asSequence().sortedBy { it.value }
        return sorted.last().value/2L - sorted.first().value/2L
    }

    fun updateCounter(provious:Long?, update:Long):Long? =
        ((provious?:0L)+update).let { if(it==0L) null else it }


    fun applyRules(pairCount: MutableMap<String, Long>, insertionRules: Map<String, Char>) {
        val change= mutableListOf<Pair<String, Long>>()
        insertionRules.asSequence().forEach {
            val count = pairCount.get(it.key)?: 0L
            if(count>0){
                change.add(Pair( String(charArrayOf(it.key[0], it.value)),count))
                change.add(Pair( String(charArrayOf(it.value, it.key[1])),count))
                change.add(Pair( it.key, -count))
            }
        }

        change.forEach{
            pairCount.compute(it.first, { _:String, value:Long? -> updateCounter(value, it.second) })
        }
    }

    fun part1(input: List<String>): Long {
        val (template, insertionRules) = load(input)
        val pairCount = mutableMapOf<String,Long>()
        for(i in 1.until(template.length)){
            val pair = template.substring(i-1, i+1)
            pairCount.compute(pair, ::incCount)
        }
        repeat(10){
            applyRules(pairCount, insertionRules)
        }
        val charCounts = doubledFinalCount(template, pairCount)

        return finalResult(charCounts)
    }

    fun part2(input: List<String>): Long {
        val (template, insertionRules) = load(input)
        val pairCount = mutableMapOf<String,Long>()
        for(i in 1.until(template.length)){
            val pair = template.substring(i-1, i+1)
            pairCount.compute(pair, ::incCount)
        }
        repeat(40){
            applyRules(pairCount, insertionRules)
        }
        val charCounts = doubledFinalCount(template, pairCount)

        return finalResult(charCounts)
    }
    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day14_test")) == 1588L)
    //check(part1(readInput("Day14")) == 3408L)
    check(part2(readInput("Day14_test")) == 2188189693529L)

    println(part1(readInput("Day14")))
    println(part2(readInput("Day14")))

}
