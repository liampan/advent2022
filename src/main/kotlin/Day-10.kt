import java.io.File
import Models10.CPU
import Models10.Command

fun main(args: Array<String>) {

    val lines = File("src/main/resources/day10.txt").readLines()


    val out = lines.fold(CPU(1)) { cpu, str ->
        cpu.run(Command.fromStr(str))
    }

    println(out.sumSignalStrength())

}

object Models10 {

    data class CPU(val x: Int, val cycle: Int = 0, val register: List<Pair<Int, Int>> = listOf()) {
        fun run(command: Command): CPU {
            return command
                .execute(this)
        }

        override fun toString(): String {
            return "[${"%03d".format(cycle)}] x=${"%03d".format(x)} | $register"
        }

        fun tick(): CPU {
            printPixel()
            return copy(cycle = cycle + 1).register()
        }

        fun printPixel() {
            val current = cycle%40

            if (current%5 == 0 && current != 0 ) print("   ")

            if (listOf(x-1, x, x+1).contains(current))
                print("#")
            else
                print(" ")
            if ((cycle+1)%40== 0) println("") else print("")
        }

        private fun register(): CPU {
            return if (cycle == 20 || (cycle+20)%40 == 0) copy(register = register + Pair(cycle, x))
            else this
        }

        fun sumSignalStrength(): Int {
            return register.sumOf { it.first * it.second }
        }

    }

    interface Command {
        fun execute(cpu: CPU): CPU

        companion object {
            fun fromStr(str: String): Command {
                operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text)
                return when(str){
                    "noop" -> Noop
                    in Regex(Addx.regex.pattern) -> Addx(Addx.regex.find(str)!!.groupValues[1].toInt())
                    else -> throw java.lang.Exception("command: $str not parsed")
                }

            }
        }
    }

    object Noop : Command {
        override fun execute(cpu: CPU): CPU {
            return cpu.tick()
        }
    }

    data class Addx(val amount: Int) : Command {
        override fun execute(cpu: CPU): CPU {
            return cpu
                .tick()
                .tick()
                .copy(x = cpu.x + amount)
        }

        companion object {
            val regex = "addx (-?\\d+)".toRegex()
        }
    }


}