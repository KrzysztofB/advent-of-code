fun main() {

    fun parseLine(line: String): List<Int> {
        return line.trim().toCharArray().map { it - '0' }
    }

    fun addChange(counters: List<Int>, change: List<Int>): List<Int> {
        return counters.mapIndexed{ index, counter -> counter +  change[index] }
    }

    fun part1(input: Sequence<String>): Int {
        val oneCounts = input.fold(emptyList<Int>() to 0) { acc: Pair<List<Int>, Int>, line: String ->
            val change = parseLine(line)
            if(acc.first.isEmpty())
                change to 1
            else
                addChange(acc.first,change) to acc.second+1
        }
        val theshold = oneCounts.second / 2
        val gammaRateString = oneCounts.first.map { if(it > theshold) "1" else "0" }.joinToString(separator = "")
        val epsilonRateString = gammaRateString.map { if(it=='1') "0" else "1" }.joinToString(separator = "")

        return gammaRateString.toInt(2)*epsilonRateString.toInt(2)
    }

    fun part2(input: List<String>): Int {
        var data1 = input
        var position = 0
        do{
            data1 = select(data1, position, { a:Int,b:Int -> a>b }, '1')
            position++
        } while(data1.size >1)

        var data2 = input
        position = 0
        do{
            data2 = select(data2, position, { a:Int,b:Int -> a<b }, '0')
            position++
        } while(data2.size >1)

        return data1.getOrElse(0,{"0"}).toInt(2) * data2.getOrElse(0,{"0"}).toInt(2)
    }


    fun select(data:List<String>, position:Int, selector:(Int,Int)->Boolean, preferred: Char): List<String>{
        if(data.size<2) return data
        val parts = data.partition { it[position] == preferred }
        return if (parts.first.size == parts.second.size) parts.first
        else if (selector(parts.first.size, parts.second.size)) parts.first
        else parts.second
    }

    // test if implementation meets criteria from the description, like:
    check(part1(readSequence("Day03_test")) == 198)
    check(part2(readInput("Day03_test")) == 230)

    println(part1(readSequence("Day03")))
    println(part2(readInput("Day03")))
}


