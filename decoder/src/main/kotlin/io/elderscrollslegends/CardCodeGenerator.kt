package io.elderscrollslegends

class CardCodeGenerator {
    fun generateCardCodeCombinations(): List<String> {
        val lowerLetters = CharArray(26) { (it + 97).toChar() }
        val upperLetters = CharArray(26) { (it + 65).toChar() }
        val allLetters = lowerLetters + upperLetters
        return combine(allLetters.toList(), allLetters.toList()) {a, b -> "$a$b"}.filter { it.matches(Regex("^[a-z].*")) }
    }

    private fun <T1, T2, R> combine(
        first: Iterable<T1>,
        second: Iterable<T2>,
        combiner: (T1, T2) -> R
    ): List<R> = first.flatMap { firstItem -> second.map { secondItem -> combiner(firstItem, secondItem) } }
}

