import java.io.File

fun main(args: Array<String>) {

    val lines = File("src/main/resources/day06.txt").readLines()
    val packetMarkerLength = 4
    val messageMarkerLength = 14

    fun findMarker(markerLength: Int, str: String, count: Int = 0): Int{
        return if (str.take(markerLength).toSet().size == markerLength) count + markerLength
        else findMarker(markerLength, str.drop(1), count + 1)
    }

    lines.map { println(findMarker(packetMarkerLength, it)) }
    lines.map { println(findMarker(messageMarkerLength, it)) }


}