package rule

import org.reflections.Reflections
import java.util.ServiceLoader


interface Rule {
    fun validate(s: String): String
}

class Rule1 : Rule {
    override fun validate(s: String): String = s.hashCode().toString()
}

class Rule2 : Rule {
    override fun validate(s: String): String = s.reversed()
}

val s = "abc"

fun loadWithServiceLoader() {
    val loader = ServiceLoader.load(Rule::class.java)
    loader.map { it.validate(s) }.forEach { println(it) }
}

fun main(args: Array<String>) {
    val reflections = Reflections("rule")
    val rules = reflections.getSubTypesOf(Rule::class.java)
    rules.map { it.newInstance().validate(s) }.forEach { println(it) }
}
