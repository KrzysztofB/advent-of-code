
fun main() {
    fun load(input: List<String>): Pair<String,Map<Pair<Char,Char>,Char>> {
        val template = input[0].trim()

        val insertionRules = mutableMapOf<Pair<Char,Char>,Char>()
        for(lineIdx in 2.until(input.size)){
            val line = input[lineIdx]
            val (ruleFrom,ruleTo) = line.replace(" -> ",":").split(":")
            val pair = ruleFrom[0] to ruleFrom[1]
            insertionRules.put(pair, ruleTo[0])
        }
        return template to insertionRules
    }

    fun incCharCount( _key:Char, value:Long?) = if(value==null) 1L else value+1L

    fun applyRules(template: String, insertionRules: Map<Pair<Char,Char>, Char>):String {

        val nextTemplate = StringBuffer()
        var previous = '@'
        for(i in 0.until(template.length)){
            val c = template[i]
            try {
                if (i == 0) {
                    nextTemplate.append(c)
                    continue
                }

                val toInsert = insertionRules.getOrDefault(previous to c, '@')
                if (toInsert != '@') {
                    nextTemplate.append(toInsert)
                }
                nextTemplate.append(c)
            }finally {
                previous = c
            }
        }

        return nextTemplate.toString()
    }



    fun calcValue(template: CharSequence): Int {
        val sorted = template.groupingBy { it }.eachCount().toList().sortedBy { it.second }
        return sorted.last().second - sorted.first().second
    }


    fun part1(input: List<String>): Int {
        val (template, insertionRules) = load(input)
        //println(template)
        var nextTemplate = template
        repeat(10){
            nextTemplate = applyRules(nextTemplate, insertionRules)
            //println(nextTemplate)
        }
        return calcValue(nextTemplate)    }

    fun applyAndCount(c0: Char, c1: Char, counters: MutableMap<Char, Long>, insertionRules: Map<Pair<Char,Char>, Char>,level: Int) {
        if(level==0) return
        val toInsert = insertionRules.getOrDefault(c0 to c1, '@')
        if (toInsert == '@') {
            return
        }
        applyAndCount(c0, toInsert, counters, insertionRules, level-1)
        counters.compute(toInsert, ::incCharCount)
        applyAndCount(toInsert,c1, counters, insertionRules, level-1)
    }

    fun part2(input: List<String>): Long {
        val counters = mutableMapOf<Char,Long>()
        val (template, insertionRules) = load(input)
        template.forEach { counters.compute(it, ::incCharCount)
        }
        for(i in 1.until(template.length)){
            applyAndCount(template[i-1], template[i], counters, insertionRules, 40)
        }
        val sorted = counters.toList().map{it.second}.sorted()
        return sorted.last() - sorted.first()

    }

    // test if implementation meets criteria from the description, like:
    check(part1(readInput("Day14_test")) == 1588)

    check(part2(readInput("Day14_test")) == 2188189693529L)
//
      //println(part1(readInput("Day14")))
    println(part2(readInput("Day14")))

}
