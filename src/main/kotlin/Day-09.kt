import Models09.Motion
import Models09.Rope
import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.sign


fun main(args: Array<String>) {

    val lines = File("src/main/resources/day09.txt").readLines()


    val out = lines.fold(Rope.init(10)) { rope, str ->
        Motion.fromStr(str).move(rope)
    }

    println(out.knots.last().visited.size)
}

object Models09 {

    data class Rope(val knots: List<Knot>){
        fun pull(): Rope {
            fun loop(pulled: List<Knot>, toPull: List<Knot>): List<Knot>{
                return if(toPull.isEmpty()) return pulled
                else {
                    loop(
                        pulled = pulled + toPull[0].pulledBy(pulled.last()),
                        toPull = toPull.drop(1)
                    )
                }
            }

            return Rope(loop(knots.take(1), knots.drop(1)))
        }

        companion object {
            fun init(length: Int): Rope {
                return Rope(arrayOfNulls<Knot>(length).mapIndexed { i, _ -> Knot.init(i) })
            }
        }
    }

    data class Knot(private val i: Int, val x: Int, val y: Int, val visited: Set<Pair<Int, Int>>) {

        override fun toString(): String {
            return "Knot-$i(x=$x, y=$y)"
        }

        fun pulledBy(head: Knot): Knot {
            val dx = head.x - x
            val dy = head.y - y

            return if (dx.absoluteValue <= 1 && dy.absoluteValue <= 1)
                this
            else move(dx.sign, dy.sign)
        }

        fun move(xBy: Int = 0, yBy: Int = 0): Knot {
            val newX = x + xBy
            val newY = y + yBy
            return copy(
                x = newX,
                y = newY,
                visited = visited + Pair(newX, newY)
            )
        }

        companion object {
            fun init(i: Int) = Knot(i, 0, 0, setOf(Pair(0, 0)))
        }

    }

    interface Motion {
        val amount: Int
        fun apply(knot: Knot): Knot
        fun move(rope: Rope): Rope{
            fun loop(count: Int, r: Rope): Rope {
                return if (count == 0) r
                else loop(count -1,
                    r.copy(
                        knots = listOf(apply(r.knots[0])) + r.knots.drop(1)
                    ).pull()
                )
            }
            return loop(amount, rope)
        }

        companion object {
            val regex = """(L|R|U|D) (\d+)""".toRegex()
            fun fromStr(str: String): Motion {
                val match = regex.find(str)!!.groupValues
                val amount = match[2].toInt()
                return when(match[1]){
                    "R" -> Right(amount)
                    "L" -> Left(amount)
                    "U" -> Up(amount)
                    "D" -> Down(amount)
                    else -> throw java.lang.Exception("bad motion: $str")
                }

            }
        }
    }

    data class Right(override val amount: Int) : Motion {
        override fun apply(knot: Knot): Knot {
            println("right")
            return knot.move(xBy = 1)
        }
    }

    data class Left(override val amount: Int) : Motion {
        override fun apply(knot: Knot): Knot {
            println("left")
            return knot.move(xBy = -1)
        }
    }

    data class Up(override val amount: Int) : Motion {
        override fun apply(knot: Knot): Knot {
            println("up")
            return knot.move(yBy = 1)
        }
    }

    data class Down(override val amount: Int) : Motion {
        override fun apply(knot: Knot): Knot {
            println("down")
            return knot.move(yBy = -1)
        }
    }

}