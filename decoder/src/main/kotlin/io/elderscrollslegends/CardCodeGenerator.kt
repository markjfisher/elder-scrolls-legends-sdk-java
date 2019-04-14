package io.elderscrollslegends

class CardCodeGenerator {
    fun generateCardCodeCombinations(): List<String> {
        val upperLetters = CharArray(26) { (it + 65).toChar() }
        val lowerLetters = CharArray(26) { (it + 97).toChar() }
        val allLetters = upperLetters + lowerLetters
        return combine(allLetters.toList(), allLetters.toList()) {a, b -> "$a$b"}
    }

    private fun <T1, T2, R> combine(
        first: Iterable<T1>,
        second: Iterable<T2>,
        combiner: (T1, T2) -> R
    ): List<R> = first.flatMap { firstItem -> second.map { secondItem -> combiner(firstItem, secondItem) } }
}

