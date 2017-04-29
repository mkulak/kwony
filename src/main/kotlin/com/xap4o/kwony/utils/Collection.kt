package com.xap4o.kwony.utils


inline fun <reified B> Iterable<*>.partitionAs(): Pair<List<B>, List<*>> {
    val (b, a) = partition { it is B }
    return (b as List<B>) to a
}

//fun <A, B : Any> Iterable<A>.partitionAs(klass: KClass<B>): Pair<List<B>, List<A>> {
//    val (b, a) = partition { klass.isInstance(it) }
//    return (b as List<B>) to a
//}



