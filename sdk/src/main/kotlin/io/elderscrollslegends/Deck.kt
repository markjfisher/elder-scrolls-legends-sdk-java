package io.elderscrollslegends

class Deck(cards: List<Card> = emptyList()) {
    // Convert List<Card> to Map<count, List<Card>>, grouping the incoming card list by the count of them
    private val cardGroups = cards
        .groupBy { it.id }                                     // Map<cardId, List<Card>>
        .map { it.value.size to it.value.first() }             // List<Pair<count, Card>>
        .groupBy { it.first }                                  // Map<count, List<Pair<count, Card>>>
        .map { it.key to it.value.map { p -> p.second }.sortedBy { card -> card.name } }      // List<Pair<count, List<Card (sorted by name)>>
        .toMap()                                               // Map<count, List<Card>>

    fun of(count: Int) = cardGroups.getOrDefault(count, emptyList())
}
