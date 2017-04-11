import org.pcollections.ConsPStack

fun main(args: Array<String>) {
    val list = ConsPStack.from(listOf(2, 3))
    val newList = list + 1
//    println(list)
//    println(newList)
    println(newList.first())
    println(newList - 0)
    println(newList.subList(1))
}
