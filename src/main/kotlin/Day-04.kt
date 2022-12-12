import java.io.File

fun main(args: Array<String>) {

    val lines = File("src/main/resources/day04.txt").readLines()

    val regex = """(\d+)-(\d+)""".toRegex()
    fun intRangeFrom(str: String): IntRange {
        val match = regex.find(str)!!.groupValues
        return IntRange(match[1].toInt(), match[2].toInt())
    }

    data class Pair(val range1: IntRange, val range2: IntRange){
        val overlap = range1.intersect(range2)
    }

    val pairs = lines.map { pair ->
        val assignments = pair.split(",")
        Pair(intRangeFrom(assignments[0]), intRangeFrom(assignments[1]))
    }

    val partOne = pairs.count { pair ->
        pair.overlap.size == pair.range1.count() || pair.overlap.size == pair.range2.count()
    }

    println(partOne)

    val partTwo = pairs.count{pair ->
        pair.overlap.isNotEmpty()
    }

    println(partTwo)
}
