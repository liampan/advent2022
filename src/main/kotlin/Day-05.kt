import java.io.File

fun main(args: Array<String>) {

    val lines = File("src/main/resources/day05.txt").readLines()

    class Command(val count: Int, val from: Int, val to: Int)

    data class State(val stacks: Map<Int, List<Char>>) {
        fun execute(command: Command, isNewerCrane: Boolean = false): State {
            val crates: List<Char> = stacks[command.from]!!.take(command.count)
            val cratesToMove = if (isNewerCrane) crates else crates.reversed()

            val newStackmap = stacks
                .plus(command.from to stacks[command.from]!!.drop(command.count))
                .plus(command.to to cratesToMove + stacks[command.to]!!)

            return this.copy(stacks = newStackmap)
        }

        fun prettyPrint() {
            val sorted = stacks.toSortedMap()
            sorted.map {
                print(it.key)
                print(" | ")
                println(it.value.reversed())
            }
            print("answer: ")
            println(sorted.map { it.value.first() }.joinToString(""))
        }

    }

    val commandRegex = """move (\d+) from (\d+) to (\d+)""".toRegex()
    val commands = lines.filter { it.startsWith("move") }.map { line ->
        val match = commandRegex.find(line)!!.groupValues
        Command(match[1].toInt(), match[2].toInt(), match[3].toInt())
    }


    val stackMap: Map<Int, List<Char>> = lines
        .filterNot { it.startsWith("move") || it.isEmpty() }
        .flatMap {
            it.mapIndexed { a, b -> Pair(a, b) }.filter { indexAndChar ->
                indexAndChar.second.isDigit() || indexAndChar.second.isLetter()
            }
        }.groupBy { it.first }.map { it.value.map { it.second } }
        .associate { line ->
            line.last().digitToInt() to line.dropLast(1)
        }

    val startState = State(stackMap)

    //part One
    val endState = commands.fold(startState){state, command -> state.execute(command)}
    endState.prettyPrint()

    //part Two
    val endState2 = commands.fold(startState){state, command -> state.execute(command, isNewerCrane = true)}
    endState2.prettyPrint()
}