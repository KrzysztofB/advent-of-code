

/*
110100101111111000101000

110100101111111000101000

00111000000000000110111101000101001010010001001000000000
11101110000000001101010000001100100000100011000001100000
VVVTTT.....

type ID 4 represent a literal value Nx5 bits first =0 mesns last 5pack
+ zeros to end
 */

data class Packet(val allBits:String, val startBit :Int=0){



    var version:Int = -1
    var typeId:Int = -1
    var length:Int = -1
    val children = mutableListOf<Packet>()
    val sumOfVersions:Int
        get() = sumVersionsCount()
    val value:Long
        get() = eval()

    private var stringValue: String = ""
    private var intValue = Long.MIN_VALUE

    private var lengthType:Char =  ' '
    private var childrenBitsDeclared: Int? = null
    private var childrenCountDeclared: Int? = null


    fun parse():Packet {
        version = bitsAsInt(startBit,3)
        typeId  = bitsAsInt(startBit+3, 3)
        if(typeId==4){
            val (literalsLength, literalsBits) = readLiteral(startBit + 6)
            stringValue = literalsBits
            intValue = literalsBits.toLong(2)
            length = 6 + literalsLength
            return this
        }

        lengthType = allBits[startBit+6]
        if(lengthType=='0'){
            childrenBitsDeclared = bitsAsInt(startBit+6+1, 15)
            var childrenBitsToRead:Int = childrenBitsDeclared!!
            var childStart = startBit + 6+ 1 + 15
            while(childrenBitsToRead>0){
                val child = Packet(allBits, childStart)
                child.parse()
                children.add(child)
                childrenBitsToRead = childrenBitsToRead - child.length
                childStart += child.length
            }
            length = 7+15+ (childrenBitsDeclared ?: Int.MIN_VALUE)
        } else {
            childrenCountDeclared = bitsAsInt(startBit+6+1, 11)
            var childrenToRead = childrenCountDeclared!!
            var childStart = startBit + 6 + 1 + 11
            while(childrenToRead>0){
                val child = Packet(allBits, childStart)
                child.parse()
                children.add(child)
                childrenToRead--
                childStart += child.length
            }
            length = 7+11+children.map { it.length }.sum()
        }
        return this
    }

    fun sumVersionsCount():Int{
        return version + children.map{ it.sumVersionsCount()}.sum()
    }

    fun eval(): Long = when(typeId){
            0 -> children.map { it.value }.sum()
            1 -> children.map { it.value }.fold(1){ acc, i -> acc * i }
            2 -> children.map { it.value }.minOf { it }
            3 -> children.map { it.value }.maxOf { it }
            4 -> intValue
            5 -> if (children[0].value > children[1].value) 1 else 0
            6 -> if (children[0].value < children[1].value) 1 else 0
            7 -> if (children[0].value == children[1].value) 1 else 0
            else -> Long.MIN_VALUE
        }




    private fun bitsAsInt(readFrom:Int, bitsToRead:Int):Int{
            return allBits.substring(readFrom, readFrom+bitsToRead).toInt(2)
    }

    fun literalBlock(startBit:Int): Pair<Boolean,String>{
        val hasNext = allBits[startBit] == '1'
        val bits = allBits.substring(startBit+1, startBit+5)
        return hasNext to bits
    }

    /**
     * returns number of bits read in literals and literal bits
     */
    tailrec fun readLiteral(startBit:Int, literalsLength:Int = 0, blockBits :String = ""):Pair<Int,String>{
        val (hasNext, bits) = literalBlock(startBit)
        //val parsed = bitsAsInt(startBit, 5)
        return if(hasNext){
            readLiteral(startBit+5, literalsLength+5, blockBits+bits)
        } else {
            literalsLength+5 to blockBits+bits
        }
    }

}

fun main() {

    fun hex2bin(value:String): String{
        return value
            .trim()
            .asSequence()
            .map { "$it".toInt(16) }
            .map {it.toString(2).padStart(4,'0')}
            .joinToString("")
    }

    fun skipZeros(bits: String, packetStart: Int, bitsRead: Int): Int {
        val toRead = 8 -  (bitsRead % 8)
        for(i in 0.until(toRead)){
            check(bits[packetStart+bitsRead+i] =='0')
        }
        return toRead
    }

    fun part1(input: List<String>): Int {
        val bits = hex2bin(input[0])
        val mainPacket = Packet(bits)
        mainPacket.parse()

        return mainPacket.sumOfVersions
    }

    fun part2(input: List<String>): Long {
        val bits = hex2bin(input[0])
        val mainPacket = Packet(bits)
        mainPacket.parse()

        return mainPacket.value
    }

    fun preTest(){
        val literal = Packet(hex2bin("D2FE28")).parse()
        check(literal.version == 6 && literal.typeId == 4 && literal.value == 2021L && literal.length == 21)

        val operatorLen = Packet(hex2bin("38006F45291200")).parse()
        check(operatorLen.version == 1 && operatorLen.typeId == 6 && operatorLen.length == 49)
        check(operatorLen.children.size == 2)
        check(operatorLen.children[0].length == 11 && operatorLen.children[1].length == 16)

        val operatorCount = Packet(hex2bin("EE00D40C823060")).parse()
        check(operatorCount.version == 7 && operatorCount.typeId == 3 && operatorCount.length == 51)
        check(operatorCount.children.size == 3)
        check(operatorCount.children[0].length == 11 && operatorCount.children[1].length == 11 && operatorCount.children[2].length == 11)
    }

    // test if implementation meets criteria from the description, like:
    preTest()

    check(part1(listOf("8A004A801A8002F478")) == 16)
    check(part1(listOf("620080001611562C8802118E34")) == 12)
    check(part1(listOf("C0015000016115A2E0802F182340")) == 23)
    check(part1(listOf("A0016C880162017C3686B18A3D4780")) == 31)

    check(part2(listOf("C200B40A82")) == 3L)
    check(part2(listOf("04005AC33890")) == 54L)
    check(part2(listOf("880086C3E88112")) == 7L)
    check(part2(listOf("CE00C43D881120")) == 9L)

    check(part2(listOf("D8005AC2A8F0")) == 1L)
    check(part2(listOf("F600BC2D8F")) == 0L)
    check(part2(listOf("9C005AC2F8F0")) == 0L)
    check(part2(listOf("9C0141080250320F1802104A08")) == 1L)


    println(part1(readInput("Day16")))
    println(part2(readInput("Day16")))
}
