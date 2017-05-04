import kotlin.coroutines.experimental.buildIterator

sealed class Tree

object Empty : Tree()

data class Node(val value: Int, val left: Tree, val right: Tree) : Tree()

fun traverse(t : Tree): Iterator<Int> = buildIterator {
    when(t) {
        is Empty -> Unit
        is Node -> {
            traverse(t.left).forEach { yield(it) }
            traverse(t.right).forEach { yield(it) }
            yield(t.value)
        }
    }
}

fun main(args: Array<String>) {
    val n4 = Node(4,
                Node(3,
                        Node(1, Empty, Empty),
                        Node(2, Empty, Empty)),
                Empty)
    val seq = traverse(n4)
    seq.forEach {println(it)}
}




