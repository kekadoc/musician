package com.kekadoc.project.musician.core.util

fun interface ResultCallback<Q, R> {

    sealed class State<Q, R> {
        data class Process<Q, R>(val query: Q) : State<Q, R>()
        data class Error<Q, R>(val fail: Throwable) : State<Q, R>()
        data class Success<Q, R>(val result: R) : State<Q, R>()
    }

    fun onResult(result: State<Q, R>)

}

fun <Q, R> ResultCallback<Q, R>.run(query: Q) {
    onResult(ResultCallback.State.Process(query))
}
fun <Q, R> ResultCallback<Q, R>.fail(e: Throwable) {
    onResult(ResultCallback.State.Error(e))
}
fun <Q, R> ResultCallback<Q, R>.success(result: R) {
    onResult(ResultCallback.State.Success(result))
}

fun <T, K, V> Iterable<T>.toMap(builder: ((T) -> Pair<K, V>)): Map<K, V> {
    val map = mutableMapOf<K, V>()
    for (value in this) {
        val pair = builder(value)
        map[pair.first] = pair.second
    }
    return map
}