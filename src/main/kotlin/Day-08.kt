import java.io.File

fun main(args: Array<String>) {

    val lines = File("src/main/resources/day08.txt").readLines()


    val grid: List<List<Int>> = lines.map { row -> row.toList().map { it.digitToInt() } }
    val width = grid[0].size
    val height = grid.size
    val totalSeen = grid.flatMapIndexed { x, row ->
        row.mapIndexed { y, tree ->
                val left = grid[x].take(y)
                val right = grid[x].takeLast((width-y)-1)
                val column = grid.map { it[y] }
                val above = column.take(x)
                val below = column.takeLast((height-x)-1)
                fun seenFrom(l: List<Int>): Boolean{
                    return l.all { it < tree }
                }
                val seen = listOf(left, right, above, below).any{direction -> seenFrom(direction)}
                seen
        }
    }.count { it }

    val partTwo = grid.flatMapIndexed { x, row ->
        row.mapIndexed { y, tree ->
            val left = grid[x].take(y)
            val right = grid[x].takeLast((width-y)-1)
            val column = grid.map { it[y] }
            val above = column.take(x)
            val below = column.takeLast((height-x)-1)

            fun canSee(l: List<Int>): Int{
                return if (l.isEmpty()) 0
                else {
                    val t = l.takeWhile { it < tree }.size
                    val hitTreePlusOne = if(t == l.size) 0 else 1
                    t + hitTreePlusOne
                }
            }
            val visibleAbove = canSee(above.reversed())
            val visibleLeft = canSee(left.reversed())
            val visibleRight = canSee(right)
            val visibleBelow = canSee(below)

            visibleAbove * visibleLeft * visibleRight * visibleBelow
        }
    }.maxOf { it }

    println(partTwo)

}