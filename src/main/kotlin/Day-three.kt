import java.io.File

fun main(args: Array<String>) {

    val lines = File("src/main/resources/day3.txt").readLines()
    val asciiOffsetLower = 96
    val asciiOffsetUpper = 38

    fun getPriority(char: Char): Int {
        return if (char.isLowerCase()) char.code - asciiOffsetLower else char.code - asciiOffsetUpper
    }

    val partOne = lines.map {
        val first = it.take(it.length/2)
        val last = it.takeLast(it.length/2)
        val shared = first.toSet().intersect(last.toSet()).first().toChar()
        getPriority(shared)
    }.sum()

    println(partOne)

    val partTwo = lines.chunked(3).map { group ->
        val intersect = group.map { it.toSet() }.fold(group.first().toSet()){ acc, cur -> acc.intersect(cur) }.first()
        getPriority(intersect)
    }.sum()

    println(partTwo)
}