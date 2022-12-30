import Models.Command
import Models.System
import Models.mk_dir
import Models.touch
import Models.cd
import Models.DoNothing
import java.io.File

fun main(args: Array<String>) {

    val lines = File("src/main/resources/day07.txt").readLines()

    val cd_regex = """\$ cd (\w+)""".toRegex()
    fun cdFromString(str: String): Command {
        val match = cd_regex.find(str)!!.groupValues
        return cd(match[1])
    }

    val dir_regex = """dir (\w+)""".toRegex()
    fun dirFromString(str: String): Command {
        val match = dir_regex.find(str)!!.groupValues
        return mk_dir(match[1])
    }

    val file_regex = """(\d+) ([\w.]+)""".toRegex()
    fun fileFromString(str: String): Command {
        val match = file_regex.find(str)!!.groupValues
        return touch(match[2], match[1].toInt())
    }

    val cd_top_string = """$ cd /"""
    val cd_up_string = """$ cd .."""

    val commands = lines.map { line ->
        if (line.matches(cd_regex)) cdFromString(line)
        else if (line.matches(dir_regex)) dirFromString(line)
        else if (line.matches(file_regex)) fileFromString(line)
        else if (line == cd_top_string) Models.cd_top
        else if (line == cd_up_string) Models.cd_up
        else DoNothing
    }

    val sys2 = commands.fold(System()){ system, command -> command.run(system)}

    sys2.print()
    println(sys2.dirBelow10k())
    println(sys2.bestToDelete())

}

object Models {

    interface Node{
        val name: String
        val size: Int
        fun print(depth: Int): String
    }
    class File(override val name: String, override val size: Int) : Node{
        override fun print(depth: Int): String {
            return "-" + name + " (file, size=$size)"
        }
    }

    data class Dir(override val name: String, val children: List<Node>) : Node{
        fun add(node: Node): Dir {
            return copy(children = children + node)
        }

        fun sizeBelow(): Boolean {
            return size < 100000
        }

        fun childrenBelow(): List<Dir>{
            val x = children.flatMap { if (it is Dir) it.childrenBelow() else listOf() }
            return children.filter { it is Dir && it.sizeBelow()} as List<Dir> + x
        }

        fun flatten(): List<Dir> {
            return (children.filter { it is Dir } as List<Dir>) + children.flatMap { if(it is Dir) it.flatten() else listOf() }
        }

        override val size: Int
            get() = children.sumOf { it.size }

        override fun print(depth: Int): String {
            val prefix = if (children.isEmpty()) "" else "\n"
            return "-" + name + " (dir, size=$size)" + children.joinToString(
                prefix = prefix,
                separator = "\n"
            ) { ("  ".repeat(depth)) + it.print(depth + 1) }
        }
    }

    data class System(
        val fileStructure: Dir = Dir("/", listOf()),
        val currentPath: List<String> = listOf()
        ){
        fun add(node: Node): System {
            fun loop(path: List<String>, dir: Dir): Dir{
                return if (path.isEmpty()){
                    dir.add(node)
                }else{
                    val head = path[0]
                    val updatedDir = loop(path.drop(1), dir.children.find { it.name == head } as Dir)
                    val updatedChildren = dir.children.filter { it.name != head } + updatedDir
                    dir.copy(children = updatedChildren)
                }
            }
            return copy(fileStructure = loop(currentPath, fileStructure))
        }

        fun dirBelow10k(): Int {
            return fileStructure.childrenBelow().sumOf { it.size }
        }

        fun bestToDelete(): Int {
            val currentlyUsed = fileStructure.size
            val total = 70000000
            val requiredForUpdate = 30000000
            val available = total - currentlyUsed
            val minimumDelete = requiredForUpdate - available

            return fileStructure.flatten().filter { it.size >= minimumDelete }.sortedBy { it.size }[0].size
        }

        fun print(){
            println("system")
            println(fileStructure.print(1))
        }
    }

    interface Command {
        fun run(system: System): System
    }
    object DoNothing : Command {
        override fun run(system: System): System {return  system }
    }

    class cd(val target: String) : Command {
        override fun run(system: System): System {
            return system.copy(currentPath = system.currentPath + target)
        }
    }
    object cd_up : Command {
        override fun run(system: System): System {
            return system.copy(currentPath = system.currentPath.dropLast(1))
        }
    }
    object cd_top : Command {
        override fun run(system: System): System {
            return system.copy(currentPath = listOf())
        }
    }
    class mk_dir(val name: String) : Command {
        override fun run(system: System): System {
            return system.add(Dir(name, listOf()))
        }
    }

    class touch(val name: String, val size: Int) : Command {
        override fun run(system: System): System {
            return system.add(File(name, size))
        }
    }

}