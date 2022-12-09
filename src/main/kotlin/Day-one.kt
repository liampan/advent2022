import java.io.File

fun main(args: Array<String>) {

    val lines = File("src/main/resources/day1.txt").readLines()

    fun getElves(lines: List<String>, acc: List<List<String>> = listOf()): List<List<String>> {
        val firstNewLineIndex = lines.indexOfFirst { it.isEmpty() }
        return if (firstNewLineIndex >= 0) {
            val elf: List<String> = lines.take(firstNewLineIndex)
            val rest = lines.drop(firstNewLineIndex + 1)
            val newAcc = listOf(*acc.toTypedArray(), elf);
            getElves(rest, newAcc)
        } else {
            return listOf(*acc.toTypedArray(), lines);
        }
    }

    val elves = getElves(lines)
    val elfTotals = elves.map { it.map { it.toInt() }.sum() }
    val biggest = elfTotals.maxOf { it }
    println(biggest)


    //part 2
    val top3 = elfTotals.sortedDescending().take(3).sum()
    println(top3)
}