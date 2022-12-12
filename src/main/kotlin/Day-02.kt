import java.io.File


fun main(args: Array<String>) {

    val lines = File("src/main/resources/day02.txt").readLines()

    // (they play, I play)
    val games = lines.map {
        val t =it.split(' ')
        RPS.Round(t.first(), t.last()).resolve()
    }

    println(games.sum())

    //part 2
    // (they play, outcome)
    val part2 = lines.map {
        val t = it.split(' ')
        val theirMove = RPS.Moves.apply(t.first())
        val outcome = RPS.OutComes.apply(t.last())
        val iPlay = outcome.from(theirMove)
        iPlay.score + outcome.score
    }

    println(part2.sum())


}

object RPS {

    const val win = 6
    const val draw = 3
    const val lost = 0

    data class Round(val theyPlay: Move, val iPlay: Move){
        fun resolve(): Int{
            return iPlay.vs(theyPlay) + iPlay.score
        }
        constructor(them: String, me: String): this(Moves.apply(them), Moves.apply(me))

    }

    sealed interface Move {
        val score: Int
        fun vs(move: Move): Int
    }

    object Moves {
        fun apply(s: String): Move{
            return when(s){
                "A", "X" -> Rock
                "B", "Y" -> Paper
                "C", "Z" -> Scissors
                else -> throw Exception("bad input: $s")
            }
        }
    }

    object Rock : Move {
        override val score = 1
        override fun vs(move: Move): Int {
            return when(move){
                Rock -> draw
                Paper -> lost
                Scissors -> win
            }
        }
    }
    object Paper : Move {
        override val score = 2
        override fun vs(move: Move): Int {
            return when(move){
                Rock -> win
                Paper -> draw
                Scissors -> lost
            }
        }
    }
    object Scissors : Move {
        override val score = 3
        override fun vs(move: Move): Int {
            return when(move){
                Rock -> lost
                Paper -> win
                Scissors -> draw
            }
        }
    }

    sealed interface OutCome {
        val score: Int
        fun from(theyPlay: Move): Move
    }

    object OutComes {
        fun apply(s: String): OutCome{
            return when(s){
                "X" -> Loose
                "Y" -> Draw
                "Z" -> Win
                else -> throw Exception("bad input: $s")
            }
        }
    }

    object Win : OutCome {
        override val score: Int = win
        override fun from(theyPlay: Move): Move {
            return when(theyPlay){
                Rock -> Paper
                Paper -> Scissors
                Scissors -> Rock
            }
        }
    }

    object Loose : OutCome {
        override val score: Int = lost
        override fun from(theyPlay: Move): Move {
            return when(theyPlay){
                Rock -> Scissors
                Paper -> Rock
                Scissors -> Paper
            }
        }
    }

    object Draw: OutCome {
        override val score: Int = draw
        override fun from(theyPlay: Move): Move {
            return theyPlay
        }
    }
}